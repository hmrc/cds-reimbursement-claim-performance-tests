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
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.css.CssCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import jodd.lagarto.dom.NodeSelector
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object OverPaymentsScheduledMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/overpayments"
  val overPaymentsV2: String                = baseUrlFor("cds-reimbursement-claim-frontend") + s"/claim-back-import-duty-vat/test-only"
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken: CheckBuilder[CssCheckType, NodeSelector] = css("input[name='csrfToken']", "value").optional.saveAs("csrfToken")

  def postOverpaymentsScheduledChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post overpayments scheduled choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("overpayments.choose-how-many-mrns", "Scheduled")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/have-your-documents-ready": String))

  def getOverpaymentsScheduledHaveYourDocumentsReady: HttpRequestBuilder =
    http("get the supporting documents ready page")
      .get(s"$baseUrl/$route1/scheduled/have-your-documents-ready": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Files you need for this claim")).is(true))

  def getOverpaymentsScheduledMRNPage: HttpRequestBuilder =
    http("get The MRN page")
      .get(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("First Movement Reference Number (MRN)")).is(true))

  def postOverpaymentsScheduledMrnPage: HttpRequestBuilder =
    http("post overpayments scheduled MRN page")
      .post(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-importer-eori": String))

  def getOverpaymentsScheduledImporterEoriEntryPage: HttpRequestBuilder =
    http("get the overpayments scheduled MRN importer eori entry page")
      .get(s"$baseUrl/$route1/scheduled/enter-importer-eori": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What is the importer’s EORI number?")).is(true))

  def postOverpaymentsScheduledImporterEoriEntryPage: HttpRequestBuilder =
    http("post the overpayments scheduled MRN importer eori entry page")
      .post(s"$baseUrl/$route1/scheduled/enter-importer-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-declarant-eori": String))

  def getOverpaymentsScheduledDeclarantEoriEntryPage: HttpRequestBuilder =
    http("get overpayments scheduled MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/scheduled/enter-declarant-eori": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What is the declarant’s EORI number?")).is(true))

  def postOverpaymentsScheduledDeclarantEoriEntryPage: HttpRequestBuilder =
    http("post overpayments scheduled MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/scheduled/enter-declarant-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-mrn": String))

  def getOverpaymentsScheduledMrnCheckDeclarationPage: HttpRequestBuilder =
    http("get overpayments scheduled check declaration details page")
      .get(s"$baseUrl/$route1/scheduled/check-mrn": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))

  def postOverpaymentsScheduledMrnCheckDeclarationPage: HttpRequestBuilder =
    http("post overpayments scheduled check declaration details page")
      .post(s"$baseUrl/$route1/scheduled/check-mrn": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/upload-mrn-list": String))

  def getOverpaymentsScheduledUploadMrnListPage: HttpRequestBuilder =
    http("get overpayments scheduled upload mrn list page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-files": String)
      .check(saveCsrfToken)
      .check(status.is(303))

  def getOverpaymentsScheduledChooseBasisForClaimPage: HttpRequestBuilder =
    http("get overpayments scheduled choose basis for claim page")
      .get(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Why are you making this claim?")).is(true))

  def postOverpaymentsScheduledChooseBasisForClaimPage: HttpRequestBuilder =
    http("post overpayments scheduled choose basis for claim page")
      .post(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-basis-for-claim", "DutySuspension")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-additional-details": String))

  def getOverpaymentsScheduledEnterAdditionalDetailsPage: HttpRequestBuilder =
    http("get overpayments scheduled enter additional details page")
      .get(s"$baseUrl/$route1/scheduled/enter-additional-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Tell us more about your claim")).is(true))

  def postOverpaymentsScheduledMrnEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post overpayments Scheduled enter commodity details page")
      .post(s"$baseUrl/$route1/scheduled/enter-additional-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-additional-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/select-duty-types": String))

  def getOverpaymentsScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("get overpayments scheduled select duty types page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Import tax to claim")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("post overpayments scheduled select duty types page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-types[]", "uk-duty")
      .formParam("select-duty-types[]", "eu-duty")
      .formParam("select-duty-types[]", "excise-duty")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/uk-duty": String))

  def getOverpaymentsScheduledMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("get overpayments select duties uk duty page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/uk-duty": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which UK duties do you want to claim for?")).is(true))

  def postOverpaymentsMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("post overpayments select duties uk duty page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/uk-duty": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/uk-duty/A00": String))

  def getOverpaymentsScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("get overpayments select duties uk duty tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/uk-duty/A00": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("A00 - Customs Duty")).is(true))

  def postOverpaymentsScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("post overpayments select duties uk duty tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/uk-duty/A00": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "3.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/eu-duty": String))

  def getOverpaymentsScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("get overpayments select duties eu duty page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which EU duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("post overpayments select duties eu duty page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "A50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/eu-duty/A50": String))

  def getOverpaymentsScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("get overpayments select duties eu duty tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/eu-duty/A50": String)
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(bodyString.transform(_.contains("A50 - Customs Duty")).is(true))

  def postOverpaymentsScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("post overpayments select duties eu duty tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/eu-duty/A50": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "4.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty": String))

  def getScheduledMrnExciseSelectDutiesPage: HttpRequestBuilder =
    http("get overpayments scheduled select duty types page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise duties")).is(true))


  def postScheduledMrnExciseSelectDutiesPage: HttpRequestBuilder =
    http("post overpayments scheduled select excise duty types page")
      .post(s"$baseUrl/$route1/scheduled/select-excise-categories")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-excise-categories[]", "beer")
      .formParam("select-excise-categories[]", "wine")
      .formParam("select-excise-categories[]", "made-wine")
      .formParam("select-excise-categories[]", "low-alcohol-beverages")
      .formParam("select-excise-categories[]", "spirits")
      .formParam("select-excise-categories[]", "cider-perry")
      .formParam("select-excise-categories[]", "other-fermented-products")
      .formParam("select-excise-categories[]", "hydrocarbon-oils")
      .formParam("select-excise-categories[]", "biofuels")
      .formParam("select-excise-categories[]", "miscellaneous-road-fuels")
      .formParam("select-excise-categories[]", "tobacco")
      .formParam("select-excise-categories[]", "climate-change-levy")
      .check(status.in(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/beer": String))


  def getOverpaymentsScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("get overpayments select duties beer page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/beer": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which beer duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("post overpayments select duties beer page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/beer": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "440")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/440": String))

  def getOverpaymentsScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("get overpayments select duties wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/wine": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which wine duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("post overpayments select duties wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/wine": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "413")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/413": String))

  def getOverpaymentsScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("get overpayments select duties made wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/made-wine": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which made-wine duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("post overpayments select duties made wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/made-wine": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "423")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/423": String))

  def getOverpaymentsScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("get overpayments select duties alcohol page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/low-alcohol-beverages": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which low alcohol beverages duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("post overpayments select duties alcohol page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/low-alcohol-beverages": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "435")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/435": String))

  def getOverpaymentsScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("get overpayments select duties spirits page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/spirits": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which spirits duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("post overpayments select duties spirits page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/spirits": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "462")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/462": String))

  def getOverpaymentsScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("get overpayments select duties cider page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/cider-perry": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which cider and perry duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("post overpayments select duties cider page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/cider-perry": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "483")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/483": String))

  def getOverpaymentsScheduledMrnSelectDutiesOtherFermentedProductsPage: HttpRequestBuilder =
    http("get overpayments select duties other fermented products page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/other-fermented-products": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which other fermented products duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesOtherFermentedProductsPage: HttpRequestBuilder =
    http("post overpayments select duties other fermented products page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/other-fermented-products": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "334")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/334": String))

  def getOverpaymentsScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("get overpayments select duties hydrocarbon oils page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/hydrocarbon-oils": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which hydrocarbon oil duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("post overpayments select duties hydrocarbon oils page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/hydrocarbon-oils": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "551")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/551": String))

  def getOverpaymentsScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("get overpayments select duties biofuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/biofuels": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which biofuels duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("post overpayments select duties biofuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/biofuels": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "589")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/589": String))

  def getOverpaymentsScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("get overpayments select duties road fuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/miscellaneous-road-fuels": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which miscellaneous road fuels duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("post overpayments select duties road fuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/miscellaneous-road-fuels": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "592")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/592": String))

  def getOverpaymentsScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("get overpayments select duties tobacco page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/tobacco": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which tobacco products duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("post overpayments select duties tobacco page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/tobacco": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "611")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/611": String))

  def getOverpaymentsScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("get overpayments select duties climate change levy page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/climate-change-levy": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which Climate Change Levy duties do you want to claim for?")).is(true))

  def postOverpaymentsScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("post overpayments select duties climate change levy page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/climate-change-levy": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "99A")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/99A": String))

  def getOverpaymentsScheduledMrnEnterClaimPage: HttpRequestBuilder =
    http("get overpayments scheduled enter claim page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim": String)
      .check(status.is(303))
  

  def getOverpaymentsScheduledMrnBeerPage: HttpRequestBuilder =
    http("get overpayments select duties beer tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/440": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 440 Beer")).is(true))


  def postOverpaymentsScheduledMrnBeerPage: HttpRequestBuilder =
    http("post overpayments select duties beer tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/440": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/wine": String))

  def getOverpaymentsScheduledMrnWinePage: HttpRequestBuilder =
    http("get overpayments select duties wine tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/413": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 413 Wine")).is(true))

  def postOverpaymentsScheduledMrnWinePage: HttpRequestBuilder =
    http("post overpayments select duties wine tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/413": String)
      .formParam("csrfToken","#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/made-wine": String))

  def getOverpaymentsScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("get overpayments select duties made wine tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/423": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 423 Made-wine")).is(true))

  def postOverpaymentsScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("post overpayments select duties made wine tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/423": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/low-alcohol-beverages": String))

  def getOverpaymentsScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("get overpayments select duties alcohol tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/435": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 435 Low alcohol beverages")).is(true))

  def postOverpaymentsScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("post overpayments select duties alcohol tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/435": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/spirits": String))

  def getOverpaymentsScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("get overpayments select duties spirits tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/462": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 462 Spirits")).is(true))

  def postOverpaymentsScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("post overpayments select duties spirits tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/462": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/cider-perry": String))

  def getOverpaymentsScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("get overpayments select duties cider tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/483": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 483 Cider and perry")).is(true))

  def postOverpaymentsScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("post overpayments select duties cider tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/483": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/other-fermented-products": String))

  def getOverpaymentsScheduledMrnOtherFermentedProductsPage: HttpRequestBuilder =
    http("get overpayments select duties other fermented products tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/334": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 334 Other fermented products")).is(true))

  def postOverpaymentsScheduledMrnOtherFermentedProductsPage: HttpRequestBuilder =
    http("post overpayments select duties other fermented products tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/334": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/hydrocarbon-oils": String))

  def getOverpaymentsScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("get overpayments select duties hydrocarbon oils tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/551": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 551 Hydrocarbon oil")).is(true))

  def postOverpaymentsScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("post overpayments select duties hydrocarbon oils tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/551": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/biofuels": String))

  def getOverpaymentsScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("get overpayments select duties biofuels tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/589": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 589 Biofuels")).is(true))

  def postOverpaymentsScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("post overpayments select duties biofuels tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/589": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/miscellaneous-road-fuels": String))

  def getOverpaymentsScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("get overpayments select duties miscellaneous road tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/592": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 592 Miscellaneous road fuels")).is(true))

  def postOverpaymentsScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("post overpayments select duties miscellaneous road tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/592": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/tobacco": String))

  def getOverpaymentsScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("get overpayments select duties tobacco tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/611": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 611 Tobacco products")).is(true))

  def postOverpaymentsScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("post overpayments select duties tobacco tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/611": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/climate-change-levy": String))

  def getOverpaymentsScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("get select duties climate change tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/99A": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 99A Climate Change Levy")).is(true))

  def postOverpaymentsScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("post Overpayments select duties climate change tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/99A": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-claim": String))

  def getOverpaymentsScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("get overpayments scheduled MRN check claim page")
      .get(s"$baseUrl/$route1/scheduled/check-claim": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the repayment totals for this claim")).is(true))

  def postOverpaymentsScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("post overpayments scheduled MRN check claim page")
      .post(s"$baseUrl/$route1/scheduled/check-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-payee-type": String))

  def getOverpaymentScheduledChoosePayeeTypePage: HttpRequestBuilder =
    http("get the rejected goods choose payee type page")
      .get(s"$baseUrl/$route1/scheduled/choose-payee-type": String)
      .check(saveCsrfToken)
      .check(status.in(303,200))
      .check(bodyString.transform(_.contains("Who will the repayment be made to?")).is(true))


  def postOverpaymentsScheduledMRNChoosePayeeTypePage: HttpRequestBuilder =
    http("post overpayments scheduled bank account type")
      .post(s"$baseUrl/$route1/scheduled/choose-payee-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-payee-type", "Consignee")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-bank-account-details": String))

  def getOverpaymentsScheduledMRNEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get overpayments scheduled enter bank account details page")
      .get(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Enter the UK-based bank account details")).is(true))

  def postOverpaymentsScheduledEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post overpayments scheduled enter bank account details page")
      .post(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Natwest")
      .formParam("enter-bank-account-details.sort-code", "456789")
      .formParam("enter-bank-account-details.account-number", "45678901")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-file-type": String))

  def getOverpaymentsScheduledChooseFileTypePage: HttpRequestBuilder =
    http("get overpayments scheduled choose file type page")
      .get(s"$baseUrl/$route1/scheduled/choose-file-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Add supporting documents to your claim")).is(true))

  def postOverpaymentsScheduledChooseFileTypesPage: HttpRequestBuilder =
    http("post overpayments rejected goods scheduled choose file type page")
      .post(s"$baseUrl/$route1/scheduled/choose-file-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-file-type", "PackingList")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-files": String))

  def getOverpaymentsScheduledChooseFilesPage: HttpRequestBuilder =
    http("get overpayments scheduled choose files page")
      .get(s"$baseUrl/$route1/scheduled/choose-files": String)
      .check(status.is(303))


  def getOverpaymentsScheduledContactDetailsPage: HttpRequestBuilder =
    http("get overpayments scheduled change contact details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Who should we contact about this claim?")).is(true))

  def postOverpaymentsScheduledChangeContactDetailsPage: HttpRequestBuilder =
    http("post overpayments scheduled change contact details page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claimant-details": String))

  def getOverpaymentsScheduledMrnClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get overpayments scheduled claimant details page from details contact page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))


  def postOverpaymentsScheduledMrnClaimantDetailsCheckPage: HttpRequestBuilder =
    http("post overpayments scheduled claimant details check page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-your-answers": String))

  def getOverpaymentsScheduledCheckYourAnswersPage: HttpRequestBuilder =
    http("get overpayments scheduled check your answers page")
      .get(s"$baseUrl/$route1/scheduled/check-your-answers": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))


  def postOverpaymentsScheduledCheckYourAnswersPage: HttpRequestBuilder =
    http("post overpayments scheduled submit claim page")
      .post(s"$baseUrl/$route1/scheduled/submit-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/upload-mrn-list": String))

  def getOverpaymentsScheduledClaimSubmittedPage: HttpRequestBuilder =
    http("get overpayments scheduled claim submitted page")
      .get(s"$baseUrl/$route1/scheduled/claim-submitted": String)
      .check(status.in(200,303))



}
