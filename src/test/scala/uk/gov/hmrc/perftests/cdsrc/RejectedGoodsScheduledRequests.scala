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
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object RejectedGoodsScheduledRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/rejected-goods"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getRejectedGoodsScheduledSelectClaimTypePage : HttpRequestBuilder = {
    http("get select claim type page")
      .get(s"$baseUrl/$route/select-claim-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Start a new claim"))
  }

  def postRejectedGoodsScheduledSelectClaimTypePage : HttpRequestBuilder = {
    http("post rejected goods scheduled select claim type page")
      .post(s"$baseUrl/$route/select-claim-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-claim-type",  "RejectedGoods")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))
  }

  def getRejectedGoodsScheduledChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("get the rejected goods scheduled choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Choose how many MRNs you want to submit in this claim"))
  }

  def postRejectedGoodsScheduledChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("post the rejected scheduled goods choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("rejected-goods.choose-how-many-mrns", "Scheduled")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-movement-reference-number": String))
  }

  def getRejectedGoodsScheduledMRNPage : HttpRequestBuilder = {
    http("get rejected goods scheduled the MRN page")
      .get(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the first MRN"))
  }

  def postRejectedGoodsScheduledMRNPage : HttpRequestBuilder = {
    http("post rejected goods scheduled the MRN page")
      .post(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number.rejected-goods", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-importer-eori": String))
  }

  def getRejectedGoodsScheduledImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the rejected goods scheduled MRN importer eori entry page")
      .get(s"$baseUrl/$route1/scheduled/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postRejectedGoodsScheduledImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the rejected goods scheduled MRN importer eori entry page")
      .post(s"$baseUrl/$route1/scheduled/enter-importer-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-declarant-eori": String))
  }

  def getRejectedGoodsScheduledDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the rejected goods scheduled MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/scheduled/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postRejectedGoodsScheduledDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the rejected goods scheduled MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/scheduled/enter-declarant-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-declaration-details": String))
  }

  def getRejectedGoodsScheduledCheckDeclarationPage : HttpRequestBuilder = {
    http("get the rejected goods scheduled MRN check declaration details page")
      .get(s"$baseUrl/$route1/scheduled/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postRejectedGoodsScheduledCheckDeclarationPage : HttpRequestBuilder = {
    http("post rejected goods scheduled check declaration details page")
      .post(s"$baseUrl/$route1/scheduled/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/upload-mrn-list": String))
  }

  def getRejectedGoodsScheduledUploadMrnListPage : HttpRequestBuilder = {
    http("get rejected goods scheduled upload mrn list page")
      .get(s"$baseUrl/$route1/scheduled/upload-mrn-list": String)
      .check(status.is(303))
  }

  def getRejectedGoodsScheduledDocumentUploadChooseFilesPage : HttpRequestBuilder = {
    http("get rejected goods scheduled document upload choose files page")
      .get(s"$baseUrl/$route/scheduled/scheduled-document-upload/choose-files": String)
      .check(status.is(303))
  }

  def getRejectedGoodsUploadDocumentsChooseFilePage : HttpRequestBuilder = {
    http("get rejected goods scheduled upload documents choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(saveFileUploadUrl)
      .check(saveCallBack)
      .check(saveAmazonDate)
      .check(saveSuccessRedirect)
      .check(saveAmazonCredential)
      .check(saveUpscanIniateResponse)
      .check(saveUpscanInitiateRecieved)
      .check(saveRequestId)
      .check(saveAmazonAlgorithm)
      .check(saveKey)
      .check(saveAmazonSignature)
      .check(saveErrorRedirect)
      .check(saveSessionId)
      .check(savePolicy)
      .check(status.is(200))
      .check(regex("Add a document which shows all the MRNs in this claim"))
  }

  def postRejectedGoodsScheduledUploadDocumentsChooseFilePagePage : HttpRequestBuilder = {
    http("post rejected goods scheduled upload documents choose file page")
      .post("${fileUploadAmazonUrl}")
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryjoqtomO5urVl5B6N")
      .asMultipartForm
      .bodyPart(StringBodyPart("x-amz-meta-callback-url", "${callBack}"))
      .bodyPart(StringBodyPart("x-amz-date", "${amazonDate}"))
      .bodyPart(StringBodyPart("success_action_redirect", "${successRedirect}"))
      .bodyPart(StringBodyPart("x-amz-credential", "${amazonCredential}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-response", "${upscanInitiateResponse}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-received", "${upscanInitiateReceived}"))
      .bodyPart(StringBodyPart("x-amz-meta-request-id", "${requestId}"))
      .bodyPart(StringBodyPart("x-amz-algorithm", "${amazonAlgorithm}"))
      .bodyPart(StringBodyPart("key", "${key}"))
      .bodyPart(StringBodyPart("x-amz-signature", "${amazonSignature}"))
      .bodyPart(StringBodyPart("error_action_redirect", "${errorRedirect}"))
      .bodyPart(StringBodyPart("x-amz-meta-original-filename", "validFile.png"))
      .bodyPart(StringBodyPart("acl", "private"))
      .bodyPart(StringBodyPart("x-amz-meta-session-id", "${sessionId}"))
      .bodyPart(StringBodyPart("x-amz-meta-consuming-service", "cds-reimbursement-claim-frontend"))
      .bodyPart(StringBodyPart("policy", "${policy}"))
      .bodyPart(RawFileBodyPart("file", "data/validFile.png"))
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  }

  def getRejectedGoodsScheduledDocumentUploadProgressPage : HttpRequestBuilder = {
    http("get upload progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postRejectedGoodsScheduledDocumentUploadProgressPage : HttpRequestBuilder = {
    http("post upload progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postRejectedGoodsScheduledDocumentUploadProgressPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }

  def getRejectedGoodsScheduledUploadDocumentsSummaryPage : HttpRequestBuilder = {
    http("get rejected goods scheduled upload documents summary page")
      .get(s"$baseUrl" + "${selectPage}")
      .check(status.is(200))
      .check(regex("You have successfully uploaded a document showing all the MRNs in this claim"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))
  }

  def postRejectedGoodsScheduledUploadDocumentsSummaryPage : HttpRequestBuilder = {
    http("post rejected goods scheduled upload documents summary page")
      //.post(s"$baseUrl" + "${supportEvidencePageType}")
      .post(s"$baseUrl/$route/upload-documents/summary": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("schedule-document.check-your-answers", "false")
      .check(status.is(303))
      //.check(header("Location").is(s"/$route/scheduled/claimant-details": String))
  }

  def getRejectedGoodsScheduledClaimantDetailsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled MRN claimant details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def getRejectedGoodsScheduledContactDetailsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled change contact details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))
  }

  def postRejectedGoodsScheduledChangeContactDetailsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled change contact details page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claimant-details": String))
  }

  def postRejectedGoodsScheduledClaimDetailsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled claim details page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-basis-for-claim": String))
  }

  def getRejectedGoodsScheduledChooseBasisForClaimPage : HttpRequestBuilder = {
    http("get rejected goods scheduled choose basis for claim page")
      .get(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))
  }

  def postRejectedGoodsScheduledChooseBasisForClaimPage : HttpRequestBuilder = {
    http("post rejected goods scheduled choose basis for claim page")
      .post(s"$baseUrl/$route1/scheduled/choose-basis-for-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim.rejected-goods", "SpecialCircumstances")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-special-circumstances": String))
  }

  def getRejectedGoodsScheduledSpecialCircumstancesPage : HttpRequestBuilder = {
    http("get rejected goods scheduled special circumstances page")
      .get(s"$baseUrl/$route1/scheduled/enter-special-circumstances": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter any special circumstances"))
  }

  def postRejectedGoodsScheduledSpecialCircumstancesPage : HttpRequestBuilder = {
    http("post rejected goods scheduled special circumstances page")
      .post(s"$baseUrl/$route1/scheduled/enter-special-circumstances" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-special-circumstances.rejected-goods", "reason")
      .check(status.is(303))
      //.check(header("Location").is(s"/$route1/scheduled/choose-disposal-method": String))
  }

  def getRejectedGoodsScheduledChooseDisposalMethodPage : HttpRequestBuilder = {
    http("get rejected goods scheduled choose disposal method page")
      .get(s"$baseUrl/$route1/scheduled/choose-disposal-method": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose how you will dispose of the goods"))
  }

  def postRejectedGoodsScheduledChooseDisposalMethodPage : HttpRequestBuilder = {
    http("post rejected goods scheduled choose disposal method page")
      .post(s"$baseUrl/$route1/scheduled/choose-disposal-method" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-method-of-disposal.rejected-goods", "PlacedInCustomsWarehouse")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-rejected-goods-details": String))
  }

  def getRejectedGoodsScheduledEnterRejectedDetailsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled enter rejected goods details page")
      .get(s"$baseUrl/$route1/scheduled/enter-rejected-goods-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide details of the rejected goods"))
  }

  def postRejectedGoodsScheduledEnterRejectedDetailsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled enter rejected goods details page")
      .post(s"$baseUrl/$route1/scheduled/enter-rejected-goods-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-rejected-goods-details.rejected-goods", "Any")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/select-duty-types": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duty types page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duty types you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duty types page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
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
      .check(header("Location").saveAs("action3"))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/uk-duty": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesUkDutyPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties uk duty page")
      .get(session => {
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      })
      .check(status.is(200))
      .check(regex("Select the UK duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesUkDutyPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties uk duty page")
      .post(session => {
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      })
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/eu-duty": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesEuDutyPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties eu duty page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .check(status.is(200))
      .check(regex("Select the EU duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesEuDutyPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties eu duty page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "A50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/beer": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesBeerPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties beer page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/beer": String)
      .check(status.is(200))
      .check(regex("Select the beer duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesBeerPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties beer page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/beer": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "440")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/wine": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesWinePage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/wine": String)
      .check(status.is(200))
      .check(regex("Select the wine duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesWinePage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/wine": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "413")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/made-wine": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesMadeWinePage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties made wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/made-wine": String)
      .check(status.is(200))
      .check(regex("Select the made-wine duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesMadeWinePage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties made wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/made-wine": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "423")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/low-alcohol-beverages": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties alcohol page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/low-alcohol-beverages": String)
      .check(status.is(200))
      .check(regex("Select the low alcohol beverages duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties alcohol page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/low-alcohol-beverages": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "435")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/spirits": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesSpiritsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties spirits page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/spirits": String)
      .check(status.is(200))
      .check(regex("Select the spirits duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesSpiritsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties spirits page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/spirits": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "462")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/cider-perry": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties cider page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/cider-perry": String)
      .check(status.is(200))
      .check(regex("Select the cider and perry duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties cider page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/cider-perry": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "483")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/hydrocarbon-oils": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties hydrocarbon oils page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/hydrocarbon-oils": String)
      .check(status.is(200))
      .check(regex("Select the hydrocarbon oil duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties hydrocarbon oils page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/hydrocarbon-oils": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "551")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/biofuels": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties biofuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/biofuels": String)
      .check(status.is(200))
      .check(regex("Select the biofuels duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties biofuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/biofuels": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "589")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/miscellaneous-road-fuels": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties road fuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/miscellaneous-road-fuels": String)
      .check(status.is(200))
      .check(regex("Select the miscellaneous road fuels duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties road fuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/miscellaneous-road-fuels": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "592")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/tobacco": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesTobaccoPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties tobacco page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/tobacco": String)
      .check(status.is(200))
      .check(regex("Select the tobacco products duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesTobaccoPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties tobacco page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/tobacco": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "611")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/climate-change-levy": String))
  }

  def getRejectedGoodsScheduledMrnSelectDutiesClimatePage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties climate change levy page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/climate-change-levy": String)
      .check(status.is(200))
      .check(regex("Select the Climate Change Levy duties you want to claim for all MRNs in the file you uploaded"))
  }

  def postRejectedGoodsScheduledMrnSelectDutiesClimatePage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties climate change levy page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/climate-change-levy": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "99A")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim": String))
  }

  def getRejectedGoodsScheduledMrnEnterClaimPage : HttpRequestBuilder = {
    http("get rejected goods scheduled MRN enter claim page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim": String)
      .check(status.is(303))
  }

  def getRejectedGoodsScheduledMrnUkDutyPage : HttpRequestBuilder = {
    http("get rejected goods scheduled enter claim uk duty tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/uk-duty/A00": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under UK Duty A00"))
  }

  def postRejectedGoodsScheduledMrnUkDutyPage : HttpRequestBuilder = {
    http("post rejected goods scheduled enter claim uk duty tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/uk-duty/A00": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "3.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/eu-duty/A50": String))
  }

  def getRejectedGoodsScheduledMrnEuDutyPage: HttpRequestBuilder = {
    http("get rejected goods scheduled enter claim eu duty tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/eu-duty/A50": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under EU Duty A50"))
  }

  def postRejectedGoodsScheduledMrnEuDutyPage : HttpRequestBuilder = {
    http("post rejected goods scheduled enter claim eu duty tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/eu-duty/A50": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "4.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/beer/440": String))
  }

  def getRejectedGoodsScheduledMrnBeerPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties beer tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/beer/440": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Beer 440"))
  }

  def postRejectedGoodsScheduledMrnBeerPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties beer tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/beer/440": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/wine/413": String))
  }

  def getRejectedGoodsScheduledMrnWinePage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties wine tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/wine/413": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Wine 413"))
  }

  def postRejectedGoodsScheduledMrnWinePage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties wine tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/wine/413": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/made-wine/423": String))
  }

  def getRejectedGoodsScheduledMrnMadeWinePage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties made wine tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/made-wine/423": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Made-wine 423"))
  }

  def postRejectedGoodsScheduledMrnMadeWinePage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties made wine tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/made-wine/423": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/low-alcohol-beverages/435": String))
  }

  def getRejectedGoodsScheduledMrnLowAlcoholPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties alcohol tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/low-alcohol-beverages/435": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Low alcohol beverages 435"))
  }

  def postRejectedGoodsScheduledMrnLowAlcoholPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties alcohol tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/low-alcohol-beverages/435": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/spirits/462": String))
  }

  def getRejectedGoodsScheduledMrnSpiritsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties spirits tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/spirits/462": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Spirits 462"))
  }

  def postRejectedGoodsScheduledMrnSpiritsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties spirits tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/spirits/462": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/cider-perry/483": String))
  }

  def getRejectedGoodsScheduledMrnCiderPerryPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties cider tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/cider-perry/483": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Cider and perry 483"))
  }

  def postRejectedGoodsScheduledMrnCiderPerryPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties cider tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/cider-perry/483": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/hydrocarbon-oils/551": String))
  }

  def getRejectedGoodsScheduledMrnHydroOilsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties hydrocarbon oils tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/hydrocarbon-oils/551": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Hydrocarbon oil 551"))
  }

  def postRejectedGoodsScheduledMrnHydroOilsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties hydrocarbon oils tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/hydrocarbon-oils/551": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/biofuels/589": String))
  }

  def getRejectedGoodsScheduledMrnBiofuelsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties biofuels tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/biofuels/589": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Biofuels 589"))
  }

  def postRejectedGoodsScheduledMrnBiofuelsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties biofuels tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/biofuels/589": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/miscellaneous-road-fuels/592": String))
  }

  def getRejectedGoodsScheduledMrnRoadFuelsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties miscellaneous road tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/miscellaneous-road-fuels/592": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Miscellaneous road fuels 592"))
  }

  def postRejectedGoodsScheduledMrnRoadFuelsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties miscellaneous road tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/miscellaneous-road-fuels/592": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/tobacco/611": String))
  }

  def getRejectedGoodsScheduledMrnTobaccoPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties tobacco tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/tobacco/611": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Tobacco products 611"))
  }

  def postRejectedGoodsScheduledMrnTobaccoPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties tobacco tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/tobacco/611": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/climate-change-levy/99A": String))
  }

  def getRejectedGoodsScheduledMrnClimateLevyPage : HttpRequestBuilder = {
    http("get rejected goods scheduled select duties climate change tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/climate-change-levy/99A": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Climate Change Levy 99A"))
  }

  def postRejectedGoodsScheduledMrnClimateLevyPage : HttpRequestBuilder = {
    http("post rejected goods scheduled select duties climate change tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/climate-change-levy/99A": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim-scheduled.rejected-goods.paid-amount", "10")
      .formParam("enter-claim-scheduled.rejected-goods.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-claim": String))
  }

  def getRejectedGoodsScheduledCheckClaimPage : HttpRequestBuilder = {
    http("get rejected goods scheduled check claim page")
      .get(s"$baseUrl/$route1/scheduled/check-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the repayment claim totals for all MRNs"))
  }

  def postRejectedGoodsScheduledCheckClaimPage : HttpRequestBuilder = {
    http("post rejected goods scheduled check claim page")
      .post(s"$baseUrl/$route1/scheduled/check-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-inspection-date": String))
  }

  def getRejectedGoodsScheduledInspectionDatePage : HttpRequestBuilder = {
    http("get rejected goods scheduled inspection date page")
      .get(s"$baseUrl/$route1/scheduled/enter-inspection-date": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us when the goods will be available until for inspection"))
  }

  def postRejectedGoodsScheduledInspectionDatePage : HttpRequestBuilder = {
    http("post rejected goods scheduled inspection date page")
      .post(s"$baseUrl/$route1/scheduled/enter-inspection-date" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-inspection-date.rejected-goods.day", "19")
      .formParam("enter-inspection-date.rejected-goods.month", "10")
      .formParam("enter-inspection-date.rejected-goods.year", "2000")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/inspection-address/choose-type": String))
  }

  def getRejectedGoodsScheduledInspectionAddressChoosePage : HttpRequestBuilder = {
    http("get rejected goods scheduled inspection address choose type page")
      .get(s"$baseUrl/$route1/scheduled/inspection-address/choose-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose an address for the inspection"))
  }

  def postRejectedGoodsScheduledInspectionAddressChoosePage : HttpRequestBuilder = {
    http("post rejected goods scheduled inspection address choose type page")
      .post(s"$baseUrl/$route1/scheduled/inspection-address/choose-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("inspection-address.type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-bank-details": String))
  }

  def getRejectedGoodsScheduledCheckBankDetailsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled check bank details page")
      .get(s"$baseUrl/$route1/scheduled/check-bank-details": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
  }

  def getRejectedGoodsScheduledBankAccountTypePage : HttpRequestBuilder = {
    http("get rejected goods scheduled bank account type page")
      .get(s"$baseUrl/$route1/scheduled/choose-bank-account-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postRejectedGoodsScheduledBankAccountTypePage : HttpRequestBuilder = {
    http("post rejected goods scheduled bank account type page")
      .post(s"$baseUrl/$route1/scheduled/choose-bank-account-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Business")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-bank-account-details": String))
  }

  def getRejectedGoodsScheduledEnterBankDetailsPage : HttpRequestBuilder = {
    http("get rejected goods scheduled enter bank details page")
      .get(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your bank account details"))
  }

  def postRejectedGoodsScheduledEnterBankDetailsPage : HttpRequestBuilder = {
    http("post rejected goods scheduled enter bank details page")
      .post(s"$baseUrl/$route1/scheduled/enter-bank-account-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-bank-details": String))
  }

  def getRejectedGoodsScheduledChooseFileTypePage : HttpRequestBuilder = {
    http("get rejected goods scheduled choose file type page")
      .get(s"$baseUrl/$route1/scheduled/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))
  }

  def postRejectedGoodsScheduledChooseFileTypesPage : HttpRequestBuilder = {
    http("post rejected goods scheduled choose file type page")
      .post(s"$baseUrl/$route1/scheduled/choose-file-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-files": String))
  }

  def getRejectedGoodsScheduledChooseFilesPage : HttpRequestBuilder = {
    http("get rejected goods scheduled choose files page")
      .get(s"$baseUrl/$route1/scheduled/choose-files": String)
      .check(status.is(303))
  }

  def getRejectedGoodsScheduledChooseFilePage : HttpRequestBuilder = {
    http("get rejected goods scheduled choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(status.is(200))
      .check(regex("Upload (.*)"))
  }

  def getRejectedGoodsScheduledUploadDocumentsChooseFilePage : HttpRequestBuilder = {
    http("get rejected goods scheduled upload documents choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(saveFileUploadUrl)
      .check(saveCallBack)
      .check(saveAmazonDate)
      .check(saveSuccessRedirect)
      .check(saveAmazonCredential)
      .check(saveUpscanIniateResponse)
      .check(saveUpscanInitiateRecieved)
      .check(saveRequestId)
      .check(saveAmazonAlgorithm)
      .check(saveKey)
      .check(saveAmazonSignature)
      .check(saveErrorRedirect)
      .check(saveSessionId)
      .check(savePolicy)
      .check(status.is(200))
      //      //.check(saveCsrfToken())
      //      //.check(header("Location").saveAs("action"))
      //      .check(regex("""form action="(.*)" method""").saveAs("actionlll"))
      //      .check(regex("""supporting-evidence/scan-progress/(.*)">""").saveAs("action1"))
      .check(regex("Upload (.*)"))
  }

  def postRejectedGoodsScheduledUploadDocumentsChooseFilePage : HttpRequestBuilder = {
    http("post rejected goods scheduled upload support evidence page")
      .post("${fileUploadAmazonUrl}")
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryjoqtomO5urVl5B6N")
      .asMultipartForm
      .bodyPart(StringBodyPart("x-amz-meta-callback-url", "${callBack}"))
      .bodyPart(StringBodyPart("x-amz-date", "${amazonDate}"))
      .bodyPart(StringBodyPart("success_action_redirect", "${successRedirect}"))
      .bodyPart(StringBodyPart("x-amz-credential", "${amazonCredential}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-response", "${upscanInitiateResponse}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-received", "${upscanInitiateReceived}"))
      .bodyPart(StringBodyPart("x-amz-meta-request-id", "${requestId}"))
      .bodyPart(StringBodyPart("x-amz-algorithm", "${amazonAlgorithm}"))
      .bodyPart(StringBodyPart("key", "${key}"))
      .bodyPart(StringBodyPart("x-amz-signature", "${amazonSignature}"))
      .bodyPart(StringBodyPart("error_action_redirect", "${errorRedirect}"))
      .bodyPart(StringBodyPart("x-amz-meta-original-filename", "validFile.png"))
      .bodyPart(StringBodyPart("acl", "private"))
      .bodyPart(StringBodyPart("x-amz-meta-session-id", "${sessionId}"))
      .bodyPart(StringBodyPart("x-amz-meta-consuming-service", "cds-reimbursement-claim-frontend"))
      .bodyPart(StringBodyPart("policy", "${policy}"))
      .bodyPart(RawFileBodyPart("file", "data/validFile.png"))
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  }

  def getRejectedGoodsScheduledScanProgressWaitPage : HttpRequestBuilder = {
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postRejectedGoodsScheduledScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postRejectedGoodsScheduledScanProgressWaitPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").is(s"/$route/upload-documents/summary": String))
        .check(header("Location").optional.saveAs("selectPage1")))
    ).actionBuilders
  }

  def getRejectedGoodsScheduledDocumentsSummaryPage1 : HttpRequestBuilder = {
    http("get rejected goods scheduled upload documents summary page1")
      .get(s"$baseUrl/$route/upload-documents/summary": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))
  }

  def postRejectedGoodsScheduledDocumentsSummaryPage : HttpRequestBuilder = {
    http("post rejected goods scheduled upload documents page")
      .post(s"$baseUrl/$route/upload-documents/summary" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))
      //.check(header("Location").is(s"/$route1/single/check-your-answers": String))
  }

  def getRejectedGoodsScheduledCheckYourAnswersPage : HttpRequestBuilder = {
    http("get rejected goods scheduled check your answers page")
      .get(s"$baseUrl/$route1/scheduled/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))
  }

  def postRejectedGoodsScheduledCheckYourAnswersPage : HttpRequestBuilder = {
    http("post rejected goods scheduled submit claim page")
      .post(s"$baseUrl/$route1/scheduled/submit-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claim-submitted": String))
  }

  def getRejectedGoodsScheduledClaimSubmittedPage : HttpRequestBuilder = {
    http(("get rejected goods scheduled claim submitted page"))
      .get(s"$baseUrl/$route1/scheduled/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
  }
}
