/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.cdsrc

import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt


object OverPaymentsScheduledMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/overpayments"
  val overPaymentsV2: String = baseUrlFor("cds-reimbursement-claim-frontend") + s"/claim-back-import-duty-vat/test-only"
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def postOverpaymentsScheduledChooseHowManyMrnsPage : HttpRequestBuilder =
    http("post overpayments scheduled choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("overpayments.choose-how-many-mrns", "Scheduled")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-movement-reference-number": String))

  def getOverpaymentsScheduledMRNPage : HttpRequestBuilder =
    http("get The MRN page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the first Movement Reference Number (.*)"))

  def postOverpaymentsScheduledMrnPage: HttpRequestBuilder =
    http("post overpayments scheduled MRN page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-importer-eori": String))

  def getOverpaymentsScheduledImporterEoriEntryPage : HttpRequestBuilder =
    http("get the overpayments scheduled MRN importer eori entry page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))

  def postOverpaymentsScheduledImporterEoriEntryPage : HttpRequestBuilder =
    http("post the overpayments scheduled MRN importer eori entry page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-importer-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-declarant-eori": String))

  def getOverpaymentsScheduledDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get overpayments scheduled MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postOverpaymentsScheduledDeclarantEoriEntryPage : HttpRequestBuilder =
    http("post overpayments scheduled MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-declarant-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/check-declaration-details": String))

  def getOverpaymentsScheduledMrnCheckDeclarationPage: HttpRequestBuilder =
    http("get overpayments scheduled check declaration details page")
      .get(s"$baseUrl/$route1/v2/scheduled/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))

  def postOverpaymentsScheduledMrnCheckDeclarationPage: HttpRequestBuilder =
    http("post overpayments scheduled check declaration details page")
      .post(s"$baseUrl/$route1/v2/scheduled/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/upload-mrn-list": String))

  def getOverpaymentsScheduledUploadMrnListPage : HttpRequestBuilder =
    http("get overpayments scheduled upload mrn list page")
      .get(s"$baseUrl/$route1/v2/scheduled/upload-mrn-list": String)
      .check(status.is(303))

  def getOverpaymentsScheduledClaimantDetailsPage : HttpRequestBuilder =
    http("get overpayments scheduled MRN claimant details page")
      .get(s"$baseUrl/$route1/v2/scheduled/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def getOverpaymentsScheduledContactDetailsPage : HttpRequestBuilder =
    http("get overpayments scheduled change contact details page")
      .get(s"$baseUrl/$route1/v2/scheduled/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))

  def postOverpaymentsScheduledChangeContactDetailsPage : HttpRequestBuilder =
    http("post overpayments scheduled change contact details page")
      .post(s"$baseUrl/$route1/v2/scheduled/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/claimant-details": String))

  def getOverpaymentsScheduledMrnClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get overpayments scheduled claimant details page from details contact page")
      .get(s"$baseUrl/$route1/v2/scheduled/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def postOverpaymentsScheduledMrnClaimantDetailsCheckPage: HttpRequestBuilder =
    http("post overpayments scheduled claimant details check page")
      .post(s"$baseUrl/$route1/v2/scheduled/claimant-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/claim-northern-ireland": String))

  def getOverpaymentsScheduledMrnClaimNorthernIrelandPage: HttpRequestBuilder =
    http("get overpayments scheduled claim northern ireland page")
      .get(s"$baseUrl/$route1/v2/scheduled/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your goods moved through or imported to Northern Ireland?"))

  def postOverpaymentsScheduledMrnClaimNorthernIrelandPage: HttpRequestBuilder =
    http("post overpayments scheduled claim northern ireland page")
      .post(s"$baseUrl/$route1/v2/scheduled/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/choose-basis-for-claim": String))

  def getOverpaymentsScheduledChooseBasisForClaimPage : HttpRequestBuilder =
    http("get overpayments scheduled choose basis for claim page")
      .get(s"$baseUrl/$route1/v2/scheduled/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))

  def postOverpaymentsScheduledChooseBasisForClaimPage : HttpRequestBuilder =
    http("post overpayments scheduled choose basis for claim page")
      .post(s"$baseUrl/$route1/v2/scheduled/choose-basis-for-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "DutySuspension")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-additional-details": String))

  def getOverpaymentsScheduledEnterAdditionalDetailsPage : HttpRequestBuilder =
    http("get overpayments scheduled enter additional details page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-additional-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide additional details about this claim"))

  def postOverpaymentsScheduledMrnEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post overpayments Scheduled enter commodity details page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-additional-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-additional-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/select-duty-types": String))

  def getOverpaymentsScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("get overpayments scheduled select duty types page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/select-duty-types": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duty types you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("post overpayments scheduled select duty types page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/select-duty-types": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-types[]", "uk-duty")
      .formParam("select-duty-types[]", "eu-duty")
      .formParam("select-duty-types[]", "beer")
      .formParam("select-duty-types[]", "wine")
      .formParam("select-duty-types[]", "made-wine")
      .formParam("select-duty-types[]", "low-alcohol-beverages")
      .formParam("select-duty-types[]", "spirits")
      .formParam("select-duty-types[]", "cider-perry")
      .formParam("select-duty-types[]", "hydrocarbon-oils")
      .formParam("select-duty-types[]", "biofuels")
      .formParam("select-duty-types[]", "miscellaneous-road-fuels")
      .formParam("select-duty-types[]", "tobacco")
      .formParam("select-duty-types[]", "climate-change-levy")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/uk-duty": String))

  def getOverpaymentsScheduledMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("get overpayments select duties uk duty page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/uk-duty": String)
      .check(status.is(200))
      .check(regex("Select the UK duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("post overpayments select duties uk duty page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/uk-duty": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/uk-duty/A00": String))

  def getOverpaymentsScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("get overpayments select duties eu duty page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/eu-duty": String)
      .check(status.is(200))
      .check(regex("Select the EU duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("post overpayments select duties eu duty page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/eu-duty": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "A50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/eu-duty/A50": String))

  def getOverpaymentsScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("get overpayments select duties beer page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/beer": String)
      .check(status.is(200))
      .check(regex("Select the beer duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("post overpayments select duties beer page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/beer": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "440")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/beer/440": String))

  def getOverpaymentsScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("get overpayments select duties wine page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/wine": String)
      .check(status.is(200))
      .check(regex("Select the wine duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("post overpayments select duties wine page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/wine": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "413")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/wine/413": String))

  def getOverpaymentsScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("get overpayments select duties made wine page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/made-wine": String)
      .check(status.is(200))
      .check(regex("Select the made-wine duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("post overpayments select duties made wine page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/made-wine": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "423")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/made-wine/423": String))

  def getOverpaymentsScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("get overpayments select duties alcohol page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/low-alcohol-beverages": String)
      .check(status.is(200))
      .check(regex("Select the low alcohol beverages duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("post overpayments select duties alcohol page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/low-alcohol-beverages": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "435")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/low-alcohol-beverages/435": String))

  def getOverpaymentsScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("get overpayments select duties spirits page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/spirits": String)
      .check(status.is(200))
      .check(regex("Select the spirits duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("post overpayments select duties spirits page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/spirits": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "462")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/spirits/462": String))

  def getOverpaymentsScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("get overpayments select duties cider page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/cider-perry": String)
      .check(status.is(200))
      .check(regex("Select the cider and perry duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("post overpayments select duties cider page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/cider-perry": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "483")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/cider-perry/483": String))

  def getOverpaymentsScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("get overpayments select duties hydrocarbon oils page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/hydrocarbon-oils": String)
      .check(status.is(200))
      .check(regex("Select the hydrocarbon oil duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("post overpayments select duties hydrocarbon oils page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/hydrocarbon-oils": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "551")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/hydrocarbon-oils/551": String))

  def getOverpaymentsScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("get overpayments select duties biofuels page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/biofuels": String)
      .check(status.is(200))
      .check(regex("Select the biofuels duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("post overpayments select duties biofuels page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/biofuels": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "589")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/biofuels/589": String))

  def getOverpaymentsScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("get overpayments select duties road fuels page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/miscellaneous-road-fuels": String)
      .check(status.is(200))
      .check(regex("Select the miscellaneous road fuels duties you want to claim for all MRNs in the file you uploaded")
      )

  def postOverpaymentsScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("post overpayments select duties road fuels page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/miscellaneous-road-fuels": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "592")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/miscellaneous-road-fuels/592": String))

  def getOverpaymentsScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("get overpayments select duties tobacco page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/tobacco": String)
      .check(status.is(200))
      .check(regex("Select the tobacco products duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("post overpayments select duties tobacco page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/tobacco": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "611")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/tobacco/611": String))

  def getOverpaymentsScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("get overpayments select duties climate change levy page")
      .get(s"$baseUrl/$route1/v2/scheduled/select-duties/climate-change-levy": String)
      .check(status.is(200))
      .check(regex("Select the Climate Change Levy duties you want to claim for all MRNs in the file you uploaded"))

  def postOverpaymentsScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("post overpayments select duties climate change levy page")
      .post(s"$baseUrl/$route1/v2/scheduled/select-duties/climate-change-levy": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "99A")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-claim/climate-change-levy/99A": String))

  def getOverpaymentsScheduledMrnEnterClaimPage: HttpRequestBuilder =
    http("get overpayments scheduled enter claim page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim": String)
      .check(status.is(303))

  def getOverpaymentsScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("get overpayments select duties uk duty tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/uk-duty/A00": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under UK Duty A00"))

  def postOverpaymentsScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("post overpayments select duties uk duty tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/uk-duty/A00": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "3.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/eu-duty": String))

  def getOverpaymentsScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("get overpayments select duties eu duty tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/eu-duty/A50": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under EU Duty A50"))

  def postOverpaymentsScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("post overpayments select duties eu duty tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/eu-duty/A50": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "4.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/beer": String))

  def getOverpaymentsScheduledMrnBeerPage: HttpRequestBuilder =
    http("get overpayments select duties beer tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/beer/440": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Beer 440"))

  def postOverpaymentsScheduledMrnBeerPage: HttpRequestBuilder =
    http("post overpayments select duties beer tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/beer/440": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/wine": String))

  def getOverpaymentsScheduledMrnWinePage: HttpRequestBuilder =
    http("get overpayments select duties wine tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/wine/413": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Wine 413"))

  def postOverpaymentsScheduledMrnWinePage: HttpRequestBuilder =
    http("post overpayments select duties wine tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/wine/413": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/made-wine": String))

  def getOverpaymentsScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("get overpayments select duties made wine tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/made-wine/423": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Made-wine 423"))

  def postOverpaymentsScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("post overpayments select duties made wine tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/made-wine/423": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/low-alcohol-beverages": String))

  def getOverpaymentsScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("get overpayments select duties alcohol tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/low-alcohol-beverages/435": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Low alcohol beverages 435"))

  def postOverpaymentsScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("post overpayments select duties alcohol tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/low-alcohol-beverages/435": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/spirits": String))

  def getOverpaymentsScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("get overpayments select duties spirits tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/spirits/462": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Spirits 462"))

  def postOverpaymentsScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("post overpayments select duties spirits tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/spirits/462": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/cider-perry": String))

  def getOverpaymentsScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("get overpayments select duties cider tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/cider-perry/483": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Cider and perry 483"))

  def postOverpaymentsScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("post overpayments select duties cider tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/cider-perry/483": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/hydrocarbon-oils": String))

  def getOverpaymentsScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("get overpayments select duties hydrocarbon oils tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/hydrocarbon-oils/551": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Hydrocarbon oil 551"))

  def postOverpaymentsScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("post overpayments select duties hydrocarbon oils tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/hydrocarbon-oils/551": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/biofuels": String))

  def getOverpaymentsScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("get overpayments select duties biofuels tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/biofuels/589": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Biofuels 589"))

  def postOverpaymentsScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("post overpayments select duties biofuels tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/biofuels/589": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/miscellaneous-road-fuels": String))

  def getOverpaymentsScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("get overpayments select duties miscellaneous road tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/miscellaneous-road-fuels/592": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Miscellaneous road fuels 592"))

  def postOverpaymentsScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("post overpayments select duties miscellaneous road tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/miscellaneous-road-fuels/592": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/tobacco": String))

  def getOverpaymentsScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("get overpayments select duties tobacco tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/tobacco/611": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Tobacco products 611"))

  def postOverpaymentsScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("post overpayments select duties tobacco tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/tobacco/611": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/select-duties/climate-change-levy": String))

  def getOverpaymentsScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("get select duties climate change tax page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-claim/climate-change-levy/99A": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Climate Change Levy 99A"))

  def postOverpaymentsScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("post Overpayments select duties climate change tax page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-claim/climate-change-levy/99A": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/check-claim": String))

  def getOverpaymentsScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("get overpayments scheduled MRN check claim page")
      .get(s"$baseUrl/$route1/v2/scheduled/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the repayment totals for this claim"))

  def postOverpaymentsScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("post overpayments scheduled MRN check claim page")
      .post(s"$baseUrl/$route1/v2/scheduled/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/choose-payee-type": String))

  def getOverpaymentScheduledChoosePayeeTypePage: HttpRequestBuilder =
    http("get the rejected goods choose payee type page")
      .get(s"$baseUrl/$route1/v2/scheduled/choose-payee-type": String)
      .check(status.is(303))

  def getOverpaymentsScheduledMrnCheckBankDetailsPage: HttpRequestBuilder =
    http("get overpayments scheduled MRN check these bank details are correct page")
      .get(s"$baseUrl/$route1/v2/scheduled/check-bank-details": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))

  def getOverpaymentsScheduledMRNBankAccountTypePage: HttpRequestBuilder =
    http("get overpayments scheduled bank account type")
      .get(s"$baseUrl/$route1/v2/scheduled/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))

  def postOverpaymentsScheduledMRNBankAccountTypePage: HttpRequestBuilder =
    http("post overpayments scheduled bank account type")
      .post(s"$baseUrl/$route1/v2/scheduled/bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Personal")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/enter-bank-account-details": String))

  def getOverpaymentsScheduledMRNEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get overpayments scheduled enter bank account details page")
      .get(s"$baseUrl/$route1/v2/scheduled/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter your bank account details"))

  def postOverpaymentsScheduledEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post overpayments scheduled enter bank account details page")
      .post(s"$baseUrl/$route1/v2/scheduled/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Natwest")
      .formParam("enter-bank-account-details.sort-code", "456789")
      .formParam("enter-bank-account-details.account-number", "45678901")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/check-bank-details": String))

  def getOverpaymentsScheduledChooseFileTypePage : HttpRequestBuilder =
    http("get overpayments scheduled choose file type page")
      .get(s"$baseUrl/$route1/v2/scheduled/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))

  def postOverpaymentsScheduledChooseFileTypesPage : HttpRequestBuilder =
    http("post overpayments rejected goods scheduled choose file type page")
      .post(s"$baseUrl/$route1/v2/scheduled/choose-file-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "PackingList")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/choose-files": String))

  def getOverpaymentsScheduledChooseFilesPage: HttpRequestBuilder =
    http("get overpayments scheduled choose files page")
      .get(s"$baseUrl/$route1/v2/scheduled/choose-files": String)
      .check(status.is(303))

  def getOverpaymentsScheduledCheckYourAnswersPage: HttpRequestBuilder =
    http("get overpayments scheduled check your answers page")
      .get(s"$baseUrl/$route1/v2/scheduled/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))

  def postOverpaymentsScheduledCheckYourAnswersPage: HttpRequestBuilder =
    http("post overpayments scheduled submit claim page")
      .post(s"$baseUrl/$route1/v2/scheduled/submit-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/scheduled/claim-submitted": String))

  def getOverpaymentsScheduledClaimSubmittedPage: HttpRequestBuilder =
    http("get overpayments scheduled claim submitted page")
      .get(s"$baseUrl/$route1/v2/scheduled/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))




































}
