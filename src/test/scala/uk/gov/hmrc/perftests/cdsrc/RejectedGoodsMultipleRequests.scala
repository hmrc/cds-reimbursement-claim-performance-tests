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

object RejectedGoodsMultipleRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/rejected-goods"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getRejectedGoodsMultipleSelectClaimTypePage : HttpRequestBuilder = {
    http("get multiple select claim type page")
      .get(s"$baseUrl/$route/select-claim-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Start a new claim"))
  }

  def postRejectedGoodsMultipleSelectClaimTypePage : HttpRequestBuilder = {
    http("post rejected goods select claim type page")
      .post(s"$baseUrl/$route/select-claim-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-claim-type",  "RejectedGoods")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))
  }

  def getRejectedGoodsMultipleChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("get the rejected goods choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Choose how many MRNs you want to submit in this claim"))
  }

  def postRejectedGoodsMultipleChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("post the rejected goods choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("rejected-goods.choose-how-many-mrns", "Multiple")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-movement-reference-number": String))
  }

  def getRejectedGoodsMultipleMRNPage : HttpRequestBuilder = {
    http("get The MRN page")
      .get(s"$baseUrl/$route1/multiple/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the first MRN"))
  }

  def postRejectedGoodsMultipleMRNPage : HttpRequestBuilder = {
    http("post The MRN page")
      .post(s"$baseUrl/$route1/multiple/enter-movement-reference-number/1": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number.rejected-goods", "01AAAAAAAAAAAAAAA2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-importer-eori": String))
  }

  def getRejectedGoodsMultipleImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route1/multiple/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postRejectedGoodsMultipleImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route1/multiple/enter-importer-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB000000000000002")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-declarant-eori": String))
  }

  def getRejectedGoodsMultipleDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/multiple/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postRejectedGoodsMultipleDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/multiple/enter-declarant-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB000000000000002")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-declaration-details": String))
  }

  def getRejectedGoodsMultipleCheckDeclarationPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/multiple/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postRejectedGoodsMultipleCheckDeclarationPage : HttpRequestBuilder = {
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route1/multiple/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-movement-reference-number/2": String))
  }

  def getRejectedGoodsMultipleMRN2Page : HttpRequestBuilder = {
    http("get enter movement reference number second page")
      .get(s"$baseUrl/$route1/multiple/enter-movement-reference-number/2": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the second MRN"))
  }

  def postRejectedGoodsMultipleMRN2Page : HttpRequestBuilder = {
    http("post enter movement reference number second page")
      .post(s"$baseUrl/$route1/multiple/enter-movement-reference-number/2": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number.rejected-goods", "02AAAAAAAAAAAAAAA2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-movement-reference-numbers": String))
  }

  def getRejectedGoodsCheckMRNsPage : HttpRequestBuilder = {
    http("get check movement reference numbers page")
      .get(s"$baseUrl/$route1/multiple/check-movement-reference-numbers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("All the MRNs entered in this claim"))
  }

  def postRejectedGoodsCheckMRNsPage : HttpRequestBuilder = {
    http("post check movement reference numbers page")
      .post(s"$baseUrl/$route1/multiple/check-movement-reference-numbers": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-movement-reference-numbers.rejected-goods", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claimant-details": String))
  }

  def getRejectedGoodsMultipleClaimantDetailsPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/multiple/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def getRejectedGoodsMultipleContactDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods change contact details page")
      .get(s"$baseUrl/$route1/multiple/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))
  }

  def postRejectedGoodsMultipleChangeContactDetailsPage : HttpRequestBuilder = {
    http("post the rejected goods change contact details page")
      .post(s"$baseUrl/$route1/multiple/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claimant-details": String))
  }

  def getRejectedGoodsMultipleClaimantDetailsPage1 : HttpRequestBuilder = {
    http("get the rejected goods claimant details page from details contact page")
      .get(s"$baseUrl/$route1/multiple/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def postRejectedGoodsMultipleClaimDetailsPage : HttpRequestBuilder = {
    http("post rejected goods claim details page")
      .post(s"$baseUrl/$route1/multiple/claimant-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-basis-for-claim": String))
  }

  def getRejectedGoodsMultipleChooseBasisForClaimPage : HttpRequestBuilder = {
    http("get the rejected goods choose basis for claim page")
      .get(s"$baseUrl/$route1/multiple/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))
  }

  def postRejectedGoodsMultipleChooseBasisForClaimPage : HttpRequestBuilder = {
    http("post rejected goods choose basis for claim page")
      .post(s"$baseUrl/$route1/multiple/choose-basis-for-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim.rejected-goods", "SpecialCircumstances")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-special-circumstances": String))
  }

  def getRejectedGoodsMultipleSpecialCircumstancesPage : HttpRequestBuilder = {
    http("get the rejected goods special circumstances page")
      .get(s"$baseUrl/$route1/multiple/enter-special-circumstances": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter any special circumstances"))
  }

  def postRejectedGoodsMultipleSpecialCircumstancesPage : HttpRequestBuilder = {
    http("post rejected goods special circumstances page")
      .post(s"$baseUrl/$route1/multiple/enter-special-circumstances" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-special-circumstances.rejected-goods", "reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-disposal-method": String))
  }

  def getRejectedGoodsMultipleChooseDisposalMethodPage : HttpRequestBuilder = {
    http("get the rejected goods choose disposal method page")
      .get(s"$baseUrl/$route1/multiple/choose-disposal-method": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose how you will dispose of the goods"))
  }

  def postRejectedGoodsMultipleChooseDisposalMethodPage : HttpRequestBuilder = {
    http("post rejected goods choose disposal method page")
      .post(s"$baseUrl/$route1/multiple/choose-disposal-method" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-method-of-disposal.rejected-goods", "PlacedInCustomsWarehouse")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-rejected-goods-details": String))
  }

  def getRejectedGoodsMultipleEnterRejectedDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods enter rejected goods details page")
      .get(s"$baseUrl/$route1/multiple/enter-rejected-goods-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide details of the rejected goods"))
  }

  def postRejectedGoodsMultipleEnterRejectedDetailsPage : HttpRequestBuilder = {
    http("post rejected goods enter rejected goods details page")
      .post(s"$baseUrl/$route1/multiple/enter-rejected-goods-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-rejected-goods-details.rejected-goods", "Any")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties": String))
  }

  def getRejectedGoodsMultipleSelectDutiesPage : HttpRequestBuilder = {
    http("get the rejected goods select duties page")
      .get(s"$baseUrl/$route1/multiple/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for under first MRN"))
  }

  def postRejectedGoodsMultipleSelectDutiesOnePage : HttpRequestBuilder = {
    http("post rejected goods select duties one page")
      .post(s"$baseUrl/$route1/multiple/select-duties/1" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-claim/1/A90": String))
  }

  def getRejectedGoodsMultipleEnterClaimDutyOnePage : HttpRequestBuilder = {
    http("get the rejected goods enter claim page")
      .get(s"$baseUrl/$route1/multiple/enter-claim/1/A90": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A90 - Definitive Countervailing Duty under first MRN"))
  }

  def postRejectedGoodsMultipleEnterClaimDutyOnePage : HttpRequestBuilder = {
    http("post rejected goods enter claim page")
      .post(s"$baseUrl/$route1/multiple/enter-claim/1/A90" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim.rejected-goods.claim-amount", "67")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties/2": String))
  }

  def getRejectedGoodsMultipleSelectDutiesTwoPage : HttpRequestBuilder = {
    http("get the rejected goods select duties two page")
      .get(s"$baseUrl/$route1/multiple/select-duties/2": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for under second MRN"))
  }

  def postRejectedGoodsMultipleSelectDutiesTwoPage : HttpRequestBuilder = {
    http("post rejected goods select duties two page")
      .post(s"$baseUrl/$route1/multiple/select-duties/2" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A95")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-claim/2/A95": String))
  }

  def getRejectedGoodsMultipleEnterClaimDutyTwoPage : HttpRequestBuilder = {
    http("get the rejected goods enter claim two page")
      .get(s"$baseUrl/$route1/multiple/enter-claim/2/A95": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A95 - Provisional Countervailing Duty under second MRN"))
  }

  def postRejectedGoodsMultipleEnterClaimDutyTwoPage : HttpRequestBuilder = {
    http("post rejected goods enter claim page")
      .post(s"$baseUrl/$route1/multiple/enter-claim/2/A95" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim.rejected-goods.claim-amount", "23")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-claim": String))
  }

  def getRejectedGoodsMultipleCheckClaimPage : HttpRequestBuilder = {
    http("get the rejected goods check claim page")
      .get(s"$baseUrl/$route1/multiple/check-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the repayment claim totals for all MRNs"))
  }

  def postRejectedGoodsMultipleCheckClaimPage : HttpRequestBuilder = {
    http("post rejected goods check claim page")
      .post(s"$baseUrl/$route1/multiple/check-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim.rejected-goods", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-inspection-date": String))
  }

  def getRejectedGoodsMultipleInspectionDatePage : HttpRequestBuilder = {
    http("get the rejected goods inspection date page")
      .get(s"$baseUrl/$route1/multiple/enter-inspection-date": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us when the goods will be available until for inspection"))
  }

  def postRejectedGoodsMultipleInspectionDatePage : HttpRequestBuilder = {
    http("post rejected goods inspection date page")
      .post(s"$baseUrl/$route1/multiple/enter-inspection-date" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-inspection-date.rejected-goods.day", "19")
      .formParam("enter-inspection-date.rejected-goods.month", "10")
      .formParam("enter-inspection-date.rejected-goods.year", "2000")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/inspection-address/choose-type": String))
  }

  def getRejectedGoodsMultipleInspectionAddressChoosePage : HttpRequestBuilder = {
    http("get the rejected goods inspection address choose type page")
      .get(s"$baseUrl/$route1/multiple/inspection-address/choose-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose an address for the inspection"))
  }

  def postRejectedGoodsMultipleInspectionAddressChoosePage : HttpRequestBuilder = {
    http("post rejected goods inspection address choose type page")
      .post(s"$baseUrl/$route1/multiple/inspection-address/choose-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("inspection-address.type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-bank-details": String))
  }

  def getRejectedGoodsMultipleCheckBankDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods check bank details page")
      .get(s"$baseUrl/$route1/multiple/check-bank-details": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
  }

  def getRejectedGoodsMultipleBankAccountTypePage : HttpRequestBuilder = {
    http("get the rejected goods bank account type page")
      .get(s"$baseUrl/$route1/multiple/bank-account-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postRejectedGoodsMultipleBankAccountTypePage : HttpRequestBuilder = {
    http("post rejected goods bank account type page")
      .post(s"$baseUrl/$route1/multiple/bank-account-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Business")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-bank-account-details": String))
  }

  def getRejectedGoodsMultipleEnterBankDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods enter bank details page")
      .get(s"$baseUrl/$route1/multiple/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your bank account details"))
  }

  def postRejectedGoodsMultipleEnterBankDetailsPage : HttpRequestBuilder = {
    http("post rejected goods enter bank details page")
      .post(s"$baseUrl/$route1/multiple/enter-bank-account-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-bank-details": String))
  }

  def getRejectedGoodsMultipleChooseFileTypePage : HttpRequestBuilder = {
    http("get the rejected goods choose file type page")
      .get(s"$baseUrl/$route1/multiple/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))
  }

  def postRejectedGoodsMultipleChooseFileTypesPage : HttpRequestBuilder = {
    http("post rejected goods choose file type page")
      .post(s"$baseUrl/$route1/multiple/choose-file-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-files": String))
  }

  def getRejectedGoodsMultipleChooseFilesPage : HttpRequestBuilder = {
    http("get the rejected goods choose files page")
      .get(s"$baseUrl/$route1/multiple/choose-files": String)
      .check(status.is(303))
  }

  def getRejectedGoodsMultipleChooseFilePage : HttpRequestBuilder = {
    http("get the rejected goods choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(status.is(200))
      .check(regex("Upload (.*)"))
  }

  def getRejectedGoodsMultipleUploadDocumentsChooseFilePage : HttpRequestBuilder = {
    http("get upload documents choose file page")
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
      //      .check(regex("""form action="(.*)" method""").saveAs("actionlll"))
      //      .check(regex("""supporting-evidence/scan-progress/(.*)">""").saveAs("action1"))
      .check(regex("Upload (.*)"))
  }

  def postRejectedGoodsMultipleUploadDocumentsChooseFilePage : HttpRequestBuilder = {
    http("post upload support evidence page")
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

  def getRejectedGoodsMultipleScanProgressWaitPage : HttpRequestBuilder = {
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postRejectedGoodsMultipleScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postRejectedGoodsMultipleScanProgressWaitPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }

  def getRejectedGoodsMultipleDocumentsSummaryPage : HttpRequestBuilder = {
    http("get the rejected goods upload documents summary page")
      .get(s"$baseUrl/$route/upload-documents/summary": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))
  }

  def postRejectedGoodsMultipleDocumentsSummaryPage : HttpRequestBuilder = {
    http("post rejected goods upload documents page")
      .post(s"$baseUrl/$route/upload-documents/summary" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))
      //.check(header("Location").is(s"/$route1/single/check-your-answers": String))
  }

  def getRejectedGoodsMultipleCheckYourAnswersPage : HttpRequestBuilder = {
    http("get the rejected goods check your answers page")
      .get(s"$baseUrl/$route1/multiple/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))
  }

  def postRejectedGoodsMultipleCheckYourAnswersPage : HttpRequestBuilder = {
    http("post rejected goods submit claim page")
      .post(s"$baseUrl/$route1/multiple/submit-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claim-submitted": String))

  }

  def getRejectedGoodsMultipleClaimSubmittedPage : HttpRequestBuilder = {
    http(("get rejected goods claim submitted page"))
      .get(s"$baseUrl/$route1/multiple/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
  }
}
