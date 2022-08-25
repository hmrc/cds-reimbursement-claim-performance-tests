/*
 * Copyright 2022 HM Revenue & Customs
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
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object RejectedGoodsSingleRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/rejected-goods"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getRejectedGoodsSelectClaimTypePage : HttpRequestBuilder = {
    http("get select claim type page")
      .get(s"$baseUrl/$route/select-claim-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose type of claim"))
  }

  def postRejectedGoodsSelectClaimTypePage : HttpRequestBuilder = {
    http("post rejected goods select claim type page")
      .post(s"$baseUrl/$route/select-claim-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-claim-type",  "RejectedGoods")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))
  }

  def getRejectedGoodsChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("get the rejected goods choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Choose how many MRNs you want to submit in this claim"))
  }

  def postRejectedGoodsChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("post the rejected goods choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("rejected-goods.choose-how-many-mrns", "Individual")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-movement-reference-number": String))
  }

  def getRejectedGoodsMRNPage : HttpRequestBuilder = {
    http("get The MRN page")
      .get(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the MRN"))
  }

  def postRejectedGoodsMRNPage : HttpRequestBuilder = {
    http("post The MRN page")
      .post(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-importer-eori": String))
  }

  def getRejectedGoodsImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postRejectedGoodsImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-declarant-eori": String))
  }

  def getRejectedGoodsDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postRejectedGoodsDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-declaration-details": String))
  }

  def getRejectedGoodsCheckDeclarationPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/single/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postRejectedGoodsCheckDeclarationPage : HttpRequestBuilder = {
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route1/single/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claimant-details": String))
  }

  def getRejectedGoodsClaimantDetailsPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def getRejectedGoodsContactDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods change contact details page")
      .get(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))
  }

  def postRejectedGoodsChangeContactDetailsPage : HttpRequestBuilder = {
    http("post the rejected goods change contact details page")
      .post(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details-rejected-goods.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details-rejected-goods.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details-rejected-goods.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claimant-details": String))
  }

  def getRejectedGoodsClaimantDetailsPage1 : HttpRequestBuilder = {
    http("get the rejected goods claimant details page from details contact page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def postRejectedGoodsClaimDetailsPage : HttpRequestBuilder = {
    http("post rejected goods claim details page")
      .post(s"$baseUrl/$route1/single/claimant-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-basis-for-claim": String))
  }

  def getRejectedGoodsChooseBasisForClaimPage : HttpRequestBuilder = {
    http("get the rejected goods choose basis for claim page")
      .get(s"$baseUrl/$route1/single/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the basis for claim"))
  }

  def postRejectedGoodsChooseBasisForClaimPage : HttpRequestBuilder = {
    http("post rejected goods choose basis for claim page")
      .post(s"$baseUrl/$route1/single/choose-basis-for-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim.rejected-goods", "SpecialCircumstances")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-special-circumstances": String))
  }

  def getRejectedGoodsSpecialCircumstancesPage : HttpRequestBuilder = {
    http("get the rejected goods special circumstances page")
      .get(s"$baseUrl/$route1/single/enter-special-circumstances": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter any special circumstances"))
  }

  def postRejectedGoodsSpecialCircumstancesPage : HttpRequestBuilder = {
    http("post rejected goods special circumstances page")
      .post(s"$baseUrl/$route1/single/enter-special-circumstances" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-special-circumstances.rejected-goods", "reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-disposal-method": String))
  }

  def getRejectedGoodsChooseDisposalMethodPage : HttpRequestBuilder = {
    http("get the rejected goods choose disposal method page")
      .get(s"$baseUrl/$route1/single/choose-disposal-method": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose what you intend to do with the goods"))
  }

  def postRejectedGoodsChooseDisposalMethodPage : HttpRequestBuilder = {
    http("post rejected goods choose disposal method page")
      .post(s"$baseUrl/$route1/single/choose-disposal-method" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-method-of-disposal.rejected-goods", "PlacedInCustomsWarehouse")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-rejected-goods-details": String))
  }

  def getRejectedGoodsEnterRejectedDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods enter rejected goods details page")
      .get(s"$baseUrl/$route1/single/enter-rejected-goods-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide details of the rejected goods"))
  }

  def postRejectedGoodsEnterRejectedDetailsPage : HttpRequestBuilder = {
    http("post rejected goods enter rejected goods details page")
      .post(s"$baseUrl/$route1/single/enter-rejected-goods-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-rejected-goods-details.rejected-goods", "Any")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/select-duties": String))
  }

  def getRejectedGoodsSelectDutiesPage : HttpRequestBuilder = {
    http("get the rejected goods select duties page")
      .get(s"$baseUrl/$route1/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))
  }

  def postRejectedGoodsSelectDutiesPage : HttpRequestBuilder = {
    http("post rejected goods select duties page")
      .post(s"$baseUrl/$route1/single/select-duties" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-claim": String))
  }

  def getRejectedGoodsEnterClaimPage : HttpRequestBuilder = {
    http("get the rejected goods enter claim page")
      .get(s"$baseUrl/$route1/single/enter-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A90 - Definitive Countervailing Duty"))
  }

  def postRejectedGoodsEnterClaimPage : HttpRequestBuilder = {
    http("post rejected goods enter claim page")
      .post(s"$baseUrl/$route1/single/enter-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim.rejected-goods.claim-amount", "£90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-claim": String))
  }

  def getRejectedGoodsCheckClaimPage : HttpRequestBuilder = {
    http("get the rejected goods check claim page")
      .get(s"$baseUrl/$route1/single/check-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the claim total for the MRN"))
  }

  def postRejectedGoodsCheckClaimPage : HttpRequestBuilder = {
    http("post rejected goods check claim page")
      .post(s"$baseUrl/$route1/single/check-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim.rejected-goods", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-inspection-date": String))
  }

  def getRejectedGoodsInspectionDatePage : HttpRequestBuilder = {
    http("get the rejected goods inspection date page")
      .get(s"$baseUrl/$route1/single/enter-inspection-date": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us when the goods will be available until for inspection"))
  }

  def postRejectedGoodsInspectionDatePage : HttpRequestBuilder = {
    http("post rejected goods inspection date page")
      .post(s"$baseUrl/$route1/single/enter-inspection-date" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-inspection-date.rejected-goods.day", "19")
      .formParam("enter-inspection-date.rejected-goods.month", "10")
      .formParam("enter-inspection-date.rejected-goods.year", "2000")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/inspection-address/choose-type": String))
  }

  def getRejectedGoodsInspectionAddressChoosePage : HttpRequestBuilder = {
    http("get the rejected goods inspection address choose type page")
      .get(s"$baseUrl/$route1/single/inspection-address/choose-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose an address for the inspection"))
  }

  def postRejectedGoodsInspectionAddressChoosePage : HttpRequestBuilder = {
    http("post rejected goods inspection address choose type page")
      .post(s"$baseUrl/$route1/single/inspection-address/choose-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("inspection-address.type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-repayment-method": String))
  }

  def getRejectedGoodsRepaymentMethodPage : HttpRequestBuilder = {
    http("get the rejected goods choose repayment method page")
      .get(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose repayment method"))
  }

  def postRejectedGoodsRepaymentMethodPage : HttpRequestBuilder = {
    http("post rejected goods choose repayment method page")
      .post(s"$baseUrl/$route1/single/choose-repayment-method" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-payment-method.rejected-goods.single", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-bank-details": String))
  }

  def getRejectedGoodsCheckBankDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods check bank details page")
      .get(s"$baseUrl/$route1/single/check-bank-details": String)
      //.check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
  }

  def getRejectedGoodsBankAccountTypePage : HttpRequestBuilder = {
    http("get the rejected goods bank account type page")
      .get(s"$baseUrl/$route1/single/bank-account-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postRejectedGoodsBankAccountTypePage : HttpRequestBuilder = {
    http("post rejected goods bank account type page")
      .post(s"$baseUrl/$route1/single/bank-account-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Business")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-bank-account-details": String))
  }

  def getRejectedGoodsEnterBankDetailsPage : HttpRequestBuilder = {
    http("get the rejected goods enter bank details page")
      .get(s"$baseUrl/$route1/single/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your bank account details"))
  }

  def postRejectedGoodsEnterBankDetailsPage : HttpRequestBuilder = {
    http("post rejected goods enter bank details page")
      .post(s"$baseUrl/$route1/single/enter-bank-account-details" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-bank-details": String))
  }

  def getRejectedGoodsChooseFileTypePage : HttpRequestBuilder = {
    http("get the rejected goods choose file type page")
      .get(s"$baseUrl/$route1/single/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the type of supporting documents you are uploading"))
  }

  def postRejectedGoodsChooseFileTypesPage : HttpRequestBuilder = {
    http("post rejected goods choose file type page")
      .post(s"$baseUrl/$route1/single/choose-file-type" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-files": String))
  }

  def getRejectedGoodsChooseFilesPage : HttpRequestBuilder = {
    http("get the rejected goods choose files page")
      .get(s"$baseUrl/$route1/single/choose-files": String)
      .check(status.is(303))
  }

  def getRejectedGoodsChooseFilePage : HttpRequestBuilder = {
    http("get the rejected goods choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(status.is(200))
      //.check(regex("Upload letter of authority"))
      .check(regex("Upload (.*)"))
  }

  def getRejectedGoodsUploadCustomsDocumentsChooseFilePage : HttpRequestBuilder = {
    http("get upload documents choose file page")
      .get(s"$baseUrl/upload-customs-documents/choose-file": String)
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
      .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))
      .check(regex("Upload (.*)"))
  }

  def postRejectedGoodsUploadCustomsDocumentsChooseFilePage : HttpRequestBuilder = {
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
      .bodyPart(RawFileBodyPart("file", "data/ValidImageTest.jpg"))
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  }

  def getRejectedGoodsSingleScanProgressWaitPage : HttpRequestBuilder = {
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.in(303, 200))
//      .check(saveCsrfToken())
//      .check(regex("We are checking your document"))
//      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postRejectedGoodsSingleScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postRejectedGoodsSingleScanProgressWaitPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }

  def getRejectedGoodsDocumentsSummaryPage : HttpRequestBuilder = {
    http("get the rejected goods upload documents summary page")
      .get(s"$baseUrl/$route/upload-documents/summary": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))
  }

  def postRejectedGoodsDocumentsSummaryPage : HttpRequestBuilder = {
    http("post rejected goods upload documents page")
      .post(s"$baseUrl/$route/upload-documents/summary" : String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))
      //.check(header("Location").is(s"/$route1/single/check-your-answers": String))
  }

  def getRejectedGoodsCheckYourAnswersPage : HttpRequestBuilder = {
    http("get the rejected goods check your answers page")
      .get(s"$baseUrl/$route1/single/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))
  }

  def postRejectedGoodsCheckYourAnswersPage : HttpRequestBuilder = {
    http("post rejected goods submit claim page")
      .post(s"$baseUrl/$route1/single/submit-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claim-submitted": String))

  }

  def getRejectedGoodsClaimSubmittedPage : HttpRequestBuilder = {
    http(("get rejected goods claim submitted page"))
      .get(s"$baseUrl/$route1/single/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
  }
}
