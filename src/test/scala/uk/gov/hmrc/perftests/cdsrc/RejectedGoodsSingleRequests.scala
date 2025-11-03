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

object RejectedGoodsSingleRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/rejected-goods"


  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getRejectedGoodsSelectClaimTypePage: HttpRequestBuilder =
    http("get select claim type page")
      .get(s"$baseUrl/$route/choose-claim-type": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(css("title").is("Start a new claim - Claim back import duty and VAT - GOV.UK"))

  def postRejectedGoodsSelectClaimTypePage: HttpRequestBuilder =
    http("post rejected goods select claim type page")
      .post(s"$baseUrl/$route/choose-claim-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-claim-type", "RejectedGoods")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))

  def getRejectedGoodsChooseHowManyMrnsPage: HttpRequestBuilder =
    http("get the rejected goods choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.in(200,303))
  .check(bodyString.transform(_.contains("Single or multiple Movement Reference Numbers (MRNs)")).is(true))

  def postRejectedGoodsChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post the rejected goods choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("rejected-goods.choose-how-many-mrns", "Individual")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/have-your-documents-ready": String))

  def getRejectedGoodsHaveYourDocumentsReady: HttpRequestBuilder =
    http("get the rejected goods supporting documents ready page")
      .get(s"$baseUrl/$route1/single/have-your-documents-ready": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Have your supporting documents ready")).is(true))


  def postRejectedGoodsHaveYourDocumentsReady: HttpRequestBuilder =
    http("post the rejected goods supporting documents ready page")
      .post(s"$baseUrl/$route1/single/have-your-documents-ready": String)
     .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))


  def getRejectedGoodsMRNPage: HttpRequestBuilder =
    http("get The MRN page")
      .get(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Movement Reference Number (MRN)")).is(true))


  def postRejectedGoodsMRNPage: HttpRequestBuilder =
    http("post The MRN page")
      .post(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
     .check(header("Location").is(s"/$route1/single/enter-importer-eori": String))

  def getRejectedGoodsImporterEoriEntryPage: HttpRequestBuilder =
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("What is the importer’s EORI number?")).is(true))

  def postRejectedGoodsImporterEoriEntryPage: HttpRequestBuilder =
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-declarant-eori": String))

  def getRejectedGoodsDeclarantEoriEntryPage: HttpRequestBuilder =
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("What is the declarant’s EORI number?")).is(true))

  def postRejectedGoodsDeclarantEoriEntryPage: HttpRequestBuilder =
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-mrn": String))

  def getRejectedGoodsCheckDeclarationPage: HttpRequestBuilder =
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/single/check-mrn": String)
      .check(saveCsrfToken())
      .check(status.in(303,200))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))

  def postRejectedGoodsCheckDeclarationPage: HttpRequestBuilder =
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route1/single/check-mrn": String)
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-basis-for-claim": String))

  def getRejectedGoodsChooseBasisForClaimPage: HttpRequestBuilder =
    http("get the rejected goods choose basis for claim page")
      .get(s"$baseUrl/$route1/single/choose-basis-for-claim": String)
     .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Why are you making this claim?")).is(true))

  def postRejectedGoodsChooseBasisForClaimPage: HttpRequestBuilder =
    http("post rejected goods choose basis for claim page")
      .post(s"$baseUrl/$route1/single/choose-basis-for-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-basis-for-claim.rejected-goods", "SpecialCircumstances")
      .check(status.is(303))
     .check(header("Location").is(s"/$route1/single/enter-special-circumstances": String))

  def getRejectedGoodsSpecialCircumstancesPage: HttpRequestBuilder =
    http("get the rejected goods special circumstances page")
      .get(s"$baseUrl/$route1/single/enter-special-circumstances": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Enter any special circumstances")).is(true))

  def postRejectedGoodsSpecialCircumstancesPage: HttpRequestBuilder =
    http("post rejected goods special circumstances page")
      .post(s"$baseUrl/$route1/single/enter-special-circumstances": String)
     .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-special-circumstances.rejected-goods", "reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-disposal-method": String))

  def getRejectedGoodsChooseDisposalMethodPage: HttpRequestBuilder =
    http("get the rejected goods choose disposal method page")
      .get(s"$baseUrl/$route1/single/choose-disposal-method": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("How will you dispose of the goods?")).is(true))

  def postRejectedGoodsChooseDisposalMethodPage: HttpRequestBuilder =
    http("post rejected goods choose disposal method page")
      .post(s"$baseUrl/$route1/single/choose-disposal-method": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-method-of-disposal.rejected-goods", "PlacedInCustomsWarehouse")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-rejected-goods-details": String))

  def getRejectedGoodsEnterRejectedDetailsPage: HttpRequestBuilder =
    http("get the rejected goods enter rejected goods details page")
      .get(s"$baseUrl/$route1/single/enter-rejected-goods-details": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
     .check(bodyString.transform(_.contains("Tell us more about your claim")).is(true))

  def postRejectedGoodsEnterRejectedDetailsPage: HttpRequestBuilder =
    http("post rejected goods enter rejected goods details page")
      .post(s"$baseUrl/$route1/single/enter-rejected-goods-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-rejected-goods-details.rejected-goods", "Any")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/select-duties": String))


  def getRejectedGoodsSelectDutiesPage: HttpRequestBuilder =
    http("get the rejected goods select duties page")
      .get(s"$baseUrl/$route1/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
     .check(bodyString.transform(_.contains("What do you want to claim?")).is(true))

  def postRejectedGoodsSelectDutiesPage: HttpRequestBuilder =
    http("post rejected goods select duties page")
      .post(s"$baseUrl/$route1/single/select-duties": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duties[]", "A90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-claim": String))

  def getRejectedGoodsEnterClaimPage: HttpRequestBuilder =
    http("get the rejected goods enter claim page")
      .get(s"$baseUrl/$route1/single/enter-claim": String)
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-claim/A90": String))

  def getRejectedGoodsEnterClaimDutyPage: HttpRequestBuilder =
    http("get the rejected goods enter claim tax duty page")
      .get(s"$baseUrl/$route1/single/enter-claim/A90": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("A90 - Definitive Countervailing Duty")).is(true))

  def postRejectedGoodsEnterClaimDutyPage: HttpRequestBuilder =
    http("post rejected goods enter claim duty page")
      .post(s"$baseUrl/$route1/single/enter-claim/A90": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.rejected-goods.claim-amount", "£90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-claim": String))

  def getRejectedGoodsCheckClaimPage: HttpRequestBuilder =
    http("get the rejected goods check claim page")
      .get(s"$baseUrl/$route1/single/check-claim": String)
      .check(status.in(200,303))


  def postRejectedGoodsCheckClaimPage: HttpRequestBuilder =
    http("post rejected goods check claim page")
      .post(s"$baseUrl/$route1/single/check-claim": String)
     .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-claim.rejected-goods", "true")
      .check(status.is(303))
     .check(header("Location").is(s"/$route1/single/enter-inspection-date": String))

  def getRejectedGoodsInspectionDatePage: HttpRequestBuilder =
    http("get the rejected goods inspection date page")
      .get(s"$baseUrl/$route1/single/enter-inspection-date": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Making your goods available for inspection")).is(true))

  def postRejectedGoodsInspectionDatePage: HttpRequestBuilder =
    http("post rejected goods inspection date page")
      .post(s"$baseUrl/$route1/single/enter-inspection-date": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-inspection-date.rejected-goods.day", "19")
      .formParam("enter-inspection-date.rejected-goods.month", "10")
      .formParam("enter-inspection-date.rejected-goods.year", "2025")
      .check(status.is(303))
     .check(header("Location").is(s"/$route1/single/inspection-address/choose-type": String))

  def getRejectedGoodsInspectionAddressChoosePage: HttpRequestBuilder =
    http("get the rejected goods inspection address choose type page")
      .get(s"$baseUrl/$route1/single/inspection-address/choose-type": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Choose an address for the inspection")).is(true))

  def postRejectedGoodsInspectionAddressChoosePage: HttpRequestBuilder =
    http("post rejected goods inspection address choose type page")
      .post(s"$baseUrl/$route1/single/inspection-address/choose-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("inspection-address.type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-payee-type": String))

  def getRejectedGoodsChoosePayeeTypePage: HttpRequestBuilder =
    http("get the rejected goods choose payee type page")
      .get(s"$baseUrl/$route1/single/choose-payee-type": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Who will the repayment be made to?")).is(true))

  def postRejectedGoodsChoosePayeeTypePage: HttpRequestBuilder =
    http("post the rejected goods choose payee type page")
      .post(s"$baseUrl/$route1/single/choose-payee-type": String)
      .formParam("choose-payee-type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-repayment-method": String))

  def getRejectedGoodsRepaymentMethodPage: HttpRequestBuilder =
    http("get the rejected goods choose repayment method page")
      .get(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
     .check(bodyString.transform(_.contains("How would you like to be repaid?")).is(true))

  def postRejectedGoodsRepaymentMethodPage: HttpRequestBuilder =
    http("post rejected goods choose repayment method page")
      .post(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("reimbursement-method-bank-transfer", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-bank-account-details": String))


  def getRejectedGoodsEnterBankDetailsPage: HttpRequestBuilder =
    http("get the rejected goods enter bank details page")
      .get(s"$baseUrl/$route1/single/enter-bank-account-details": String)
     .check(saveCsrfToken())
      .check(status.in(200,303))
     .check(bodyString.transform(_.contains("Enter the UK-based bank account details")).is(true))

  def postRejectedGoodsEnterBankDetailsPage: HttpRequestBuilder =
    http("post rejected goods enter bank details page")
      .post(s"$baseUrl/$route1/single/enter-bank-account-details": String)
     .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.is(303))
     .check(header("Location").is(s"/$route1/single/choose-file-type": String))

  def getRejectedGoodsChooseFileTypePage: HttpRequestBuilder =
    http("get the rejected goods choose file type page")
      .get(s"$baseUrl/$route1/single/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
     .check(bodyString.transform(_.contains("Add supporting documents to your claim")).is(true))

  def postRejectedGoodsChooseFileTypesPage: HttpRequestBuilder =
    http("post rejected goods choose file type page")
      .post(s"$baseUrl/$route1/single/choose-file-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-files": String))

  def getRejectedGoodsChooseFilesPage: HttpRequestBuilder =
    http("get the rejected goods choose files page")
      .get(s"$baseUrl/$route1/single/choose-files": String)
      .check(status.is(303))

  def getRejectedGoodsChooseFilePage: HttpRequestBuilder =
    http("get the rejected goods choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Upload (.*)")).is(true))

  def getRejectedGoodsUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
    http("get upload documents choose file page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-file": String)
      .check(saveFileUploadUrl)
      .check(saveCallBack)
      .check(saveAmazonDate)
      .check(saveSuccessRedirect)
      .check(saveAmazonCredential)
      .check(saveUpscanInitiateResponse)
      .check(saveUpscanInitiateReceived)
      .check(saveRequestId)
      .check(saveAmazonAlgorithm)
      .check(saveKey)
      .check(saveAmazonSignature)
      .check(saveErrorRedirect)
      .check(saveSessionId)
      .check(savePolicy)
      .check(status.in(200,303))
    .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))
      .check(regex("Upload (.*)"))


  def postRejectedGoodsUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
    http("Post Upload Support Evidence Page")
      .post("#{fileUploadAmazonUrl}")
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryjoqtomO5urVl5B6N")
      .asMultipartForm
      .bodyPart(StringBodyPart("x-amz-meta-callback-url", "#{callBack}"))
      .bodyPart(StringBodyPart("x-amz-date", "#{amazonDate}"))
      .bodyPart(StringBodyPart("success_action_redirect", "#{successRedirect}"))
      .bodyPart(StringBodyPart("x-amz-credential", "#{amazonCredential}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-response", "#{upscanInitiateResponse}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-received", "#{upscanInitiateReceived}"))
      .bodyPart(StringBodyPart("x-amz-meta-request-id", "#{requestId}"))
      .bodyPart(StringBodyPart("x-amz-algorithm", "#{amazonAlgorithm}"))
      .bodyPart(StringBodyPart("key", "#{key}"))
      .bodyPart(StringBodyPart("x-amz-signature", "#{amazonSignature}"))
      .bodyPart(StringBodyPart("error_action_redirect", "#{errorRedirect}"))
      .bodyPart(StringBodyPart("x-amz-meta-original-filename", "validFile.png"))
      .bodyPart(StringBodyPart("acl", "private"))
      .bodyPart(StringBodyPart("x-amz-meta-session-id", "#{sessionId}"))
      .bodyPart(StringBodyPart("x-amz-meta-consuming-service", "cds-reimbursement-claim-frontend"))
      .bodyPart(StringBodyPart("policy", "#{policy}"))
      .bodyPart(RawFileBodyPart("file", "data/testImage95.jpg"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  def getRejectedGoodsSingleScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("#{UpscanResponseSuccess}")
      .check(status.in(303, 200))

  def postRejectedGoodsSingleScanProgressWaitPage: HttpRequestBuilder =
    http("post scan progress wait page")
      .post(s"$baseUrl" + "#{actionlll}")
     .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))


  def postRejectedGoodsSingleScanProgressWaitPage1: List[ActionBuilder] =
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(
        http(" post scan progressing wait page1")
          .get(s"$baseUrl" + "#{scanPage}")
          .check(status.in(303, 200))
          .check(header("Location").optional.saveAs("selectPage"))
      )
    ).actionBuilders

  def getRejectedGoodsDocumentsSummaryPage: HttpRequestBuilder =
    http("get the rejected goods upload documents summary page")
      .get(s"$baseUrl/$route/upload-documents/summary": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(regex("You have added 1 document to your claim"))

  def postRejectedGoodsDocumentsSummaryPage: HttpRequestBuilder =
    http("post rejected goods upload documents page")
      .post(s"$baseUrl/$route/upload-documents/summary": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))


  def getRejectedGoodsContinueToHostPage: HttpRequestBuilder   =
    http("get the rejected goods continue to host page")
      .get(s"$baseUrl/upload-customs-documents/continue-to-host": String)
      .check(status.is(303))
      .check(header("Location").is(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String))


  def getRejectedGoodsClaimantDetailsPage: HttpRequestBuilder =
    http("get the contact details page")
      .get(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .check(saveCsrfToken())
      .check(status.is(303))
      .check(regex("Who should we contact about this claim?"))

  def postRejectedGoodsChangeContactDetailsPage: HttpRequestBuilder =
    http("post the rejected goods change contact details page")
      .post(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claimant-details/lookup-address": String))

  def getRejectedGoodsClaimantDetailsPage1: HttpRequestBuilder =
    http("get the rejected goods claimant details page from details contact page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(status.in(200,303))
      //.check(bodyString.transform(_.contains("Who should we contact about this claim?")).is(true))

  def postRejectedGoodsClaimDetailsPage: HttpRequestBuilder =
    http("post rejected goods claim details page")
      .post(s"$baseUrl/$route1/single/claimant-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-your-answers": String))

  def getRejectedGoodsCheckYourAnswersPage: HttpRequestBuilder =
    http("get the rejected goods check your answers page")
      .get(s"$baseUrl/$route1/single/check-your-answers": String)
      .check(status.is(303))

  def postRejectedGoodsCheckYourAnswersPage: HttpRequestBuilder =
    http("post rejected goods submit claim page")
      .post(s"$baseUrl/$route1/single/submit-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))

  def getRejectedGoodsClaimSubmittedPage: HttpRequestBuilder =
    http("get rejected goods claim submitted page")
      .get(s"$baseUrl/$route1/single/claim-submitted": String)
      .check(status.is(303))


}

