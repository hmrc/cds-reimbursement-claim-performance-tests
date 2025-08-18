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

object OverPaymentsSingleMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/overpayments"
  val overPaymentsV2: String                = baseUrlFor("cds-reimbursement-claim-frontend") + s"/claim-back-import-duty-vat/start"
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")


  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getOverPaymentsMRNCdsrStartPage: HttpRequestBuilder =
    http("post cdsr start page")
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(303))

  def getOverPaymentsMRNCheckEoriDetailsPage: HttpRequestBuilder =
    http("get check eori details page")
      .get(s"$baseUrl/$route/check-eori-details": String)
      .check(saveCsrfToken())
      .check(status.in(200,303))
      .check(regex("Check these EORI details are correct"))

  def postOverPaymentsMRNCheckEoriDetailsPage: HttpRequestBuilder =
    http("post check eori details page")
      .post(s"$baseUrl/$route/check-eori-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-eori-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/select-claim-type": String))

  def getOverPaymentsSelectClaimTypePage: HttpRequestBuilder =
    http("get select claim type page")
      .get(s"$baseUrl/$route/choose-claim-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(css("title").is("Start a new claim - Claim back import duty and VAT - GOV.UK"))

  def postOverPaymentsSelectClaimTypePage: HttpRequestBuilder =
    http("post select claim type page")
      .post(s"$baseUrl/$route/select-claim-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-claim-type", "C285")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))

  def getOverpaymentsChooseHowManyMrnsPage: HttpRequestBuilder =
    http("get the choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(bodyString.transform(_.contains("Single or multiple Movement Reference Numbers (MRNs)")).is(true))

  def postOverpaymentsChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post the choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("overpayments.choose-how-many-mrns", "Individual")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/have-your-documents-ready": String))

  def getOverpaymentsHaveYourDocumentsReady: HttpRequestBuilder =
    http("get the supporting documents ready page")
      .get(s"$baseUrl/$route1/single/have-your-documents-ready": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Have your supporting documents ready")).is(true))

  def getOverpaymentsMRNPage: HttpRequestBuilder =
    http("get The MRN page")
      .get(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Movement Reference Number (MRN)")).is(true))

  def postOverpaymentsMRNPage: HttpRequestBuilder =
    http("post The MRN page")
      .post(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-importer-eori": String))

  def getOverpaymentsMRNImporterEoriEntryPage: HttpRequestBuilder =
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What is the importer’s EORI number?")).is(true))

  def postOverpaymentsMRNImporterEoriEntryPage: HttpRequestBuilder =
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-declarant-eori": String))

  def getOverpaymentsMRNDeclarantEoriEntryPage: HttpRequestBuilder =
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What is the declarant’s EORI number?")).is(true))

  def postOverpaymentsMRNDeclarantEoriEntryPage: HttpRequestBuilder =
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-mrn": String))

  def getOverpaymentsMRNCheckDeclarationPage: HttpRequestBuilder =
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/single/check-mrn": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))


  def postOverpaymentsMRNCheckDeclarationPage: HttpRequestBuilder =
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route1/single/check-mrn": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claimant-details": String))







  def getOverpaymentsMRNClaimantDetailsPage: HttpRequestBuilder =
    http("get the MRN claimant details page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def getOverpaymentsMrnChangeContactDetailsPage: HttpRequestBuilder =
    http("get the MRN change contact details page")
      .get(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))

  def postOverpaymentsMrnChangeContactDetailsPage: HttpRequestBuilder =
    http("post the MRN change contact details page")
      .post(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claimant-details": String))

  def getOverpaymentsMrnClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get the MRN claimant details page from details contact page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def postOverpaymentsMrnClaimantDetailsCheckPage: HttpRequestBuilder =
    http("post the MRN claimant details page")
      .post(s"$baseUrl/$route1/single/claimant-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("claimant-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claim-northern-ireland": String))

  def getOverpaymentsMRNClaimNorthernIrelandPage: HttpRequestBuilder =
    http("get the claim northern ireland page")
      .get(s"$baseUrl/$route1/single/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your goods moved through or imported to Northern Ireland?"))

  def postOverpaymentsMRNClaimNorthernIrelandPage: HttpRequestBuilder =
    http("post the claim northern ireland page")
      .post(s"$baseUrl/$route1/single/claim-northern-ireland": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("claim-northern-ireland", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-basis-for-claim": String))

  def getOverpaymentsMRNChooseBasisOfClaimPage: HttpRequestBuilder =
    http("get the MRN choose basis of claim page")
      .get(s"$baseUrl/$route1/single/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))

  def postOverpaymentsMRNChooseBasisOfClaimPage: HttpRequestBuilder =
    http("post the MRN choose basis of claim page")
      .post(s"$baseUrl/$route1/single/choose-basis-for-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-basis-for-claim", "DuplicateEntry")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-duplicate-movement-reference-number": String))

  def getOverpaymentsDuplicateMRNPage: HttpRequestBuilder =
    http("get the duplicate enter movement reference number page")
      .get(s"$baseUrl/$route1/single/enter-duplicate-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the duplicate MRN"))

  def postOverpaymentsDuplicateMRNPage: HttpRequestBuilder =
    http("post the duplicate enter movement reference number page")
      .post(s"$baseUrl/$route1/single/enter-duplicate-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-duplicate-movement-reference-number", "20AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-duplicate-declaration-details": String))

  def getOverpaymentsMRNCheckDuplicateDeclarationPage: HttpRequestBuilder =
    http("get the MRN check duplicate declaration details page")
      .get(s"$baseUrl/$route1/single/check-duplicate-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
  //.check(regex("Check these declaration details are correct for the duplicate MRN"))

  def postOverpaymentsMRNCheckDuplicateDeclarationPage: HttpRequestBuilder =
    http("post the MRN duplicate check declaration details page")
      .post(s"$baseUrl/$route1/single/check-duplicate-declaration-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-additional-details": String))

  def getOverpaymentsMRNEnterCommodityDetailsPage: HttpRequestBuilder =
    http("get the MRN enter commodity details page")
      .get(s"$baseUrl/$route1/single/enter-additional-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide additional details about this claim"))

  def postOverpaymentsMRNEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post the MRN enter commodity details page")
      .post(s"$baseUrl/$route1/single/enter-additional-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-additional-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/select-duties": String))

  def getOverpaymentsMRNSelectDutiesPage: HttpRequestBuilder =
    http("get the MRN select duties page")
      .get(s"$baseUrl/$route1/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))

  def postOverpaymentsMRNSelectDutiesPage: HttpRequestBuilder =
    http("post the MRN select duties page")
      .post(s"$baseUrl/$route1/single/select-duties": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duties[]", "A95")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-claim": String))

  def getOverpaymentsMRNStartClaimPage: HttpRequestBuilder =
    http("get the MRN start claim page")
      .get(s"$baseUrl/$route1/single/enter-claim": String)
      .check(status.is(303))
      .check(header("Location").saveAs("action3"))

  def getOverpaymentsMRNEnterClaimPage: HttpRequestBuilder =
    http("get the MRN enter claim page")
      .get { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .check(status.is(200))
      .check(regex("A95 - Provisional Countervailing Duty"))

  def postOverpaymentsMRNEnterClaimPage: HttpRequestBuilder =
    http("post the MRN enter claim page")
      .post { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim", "39")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-claim": String))

  def getOverpaymentsMRNCheckClaimPage: HttpRequestBuilder =
    http("get the MRN check claim page")
      .get(s"$baseUrl/$route1/single/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the repayment claim total for the MRN"))

  def postOverpaymentsMRNCheckClaimPage: HttpRequestBuilder =
    http("post the MRN check claim page")
      .post(s"$baseUrl/$route1/single/check-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-payee-type": String))

  def getOverpaymentsSelectReimbursementMethodPage: HttpRequestBuilder =
    http("get choose repayment method page")
      .get(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .check(status.is(200))
      .check(regex("Choose repayment method"))

  def postOverpaymentsSelectReimbursementMethodPage: HttpRequestBuilder =
    http("post choose repayment method page")
      .post(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("reimbursement-method", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-file-type": String))

  def getOverpaymentsCheckBankDetailsPage: HttpRequestBuilder =
    http("get overpayments check bank details page")
      .get(s"$baseUrl/$route1/single/check-bank-details": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))

  def postOverpaymentsCheckBankDetailsPage: HttpRequestBuilder =
    http("post overpayments check bank details page")
      .post(s"$baseUrl/$route1/single/check-bank-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-file-type": String))

  def getOverpaymentsBankAccountTypePage: HttpRequestBuilder =
    http("get overpayments bank account type page")
      .get(s"$baseUrl/$route1/single/bank-account-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))

  def postOverpaymentsBankAccountTypePage: HttpRequestBuilder =
    http("post overpayments bank account type page")
      .post(s"$baseUrl/$route1/single/bank-account-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-bank-account-type", "Business")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-bank-account-details": String))

  def getOverpaymentsEnterBankDetailsPage: HttpRequestBuilder =
    http("get overpayments enter bank details page")
      .get(s"$baseUrl/$route1/single/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your bank account details"))

  def postOverpaymentsEnterBankDetailsPage: HttpRequestBuilder =
    http("post overpayments enter bank details page")
      .post(s"$baseUrl/$route1/single/enter-bank-account-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-bank-details": String))

  def getOverpaymentsChooseFileTypePage: HttpRequestBuilder =
    http("get overpayments choose file type page")
      .get(s"$baseUrl/$route1/single/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))

  def postOverpaymentsChooseFileTypesPage: HttpRequestBuilder =
    http("post overpayments choose file type page")
      .post(s"$baseUrl/$route1/single/choose-file-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-files": String))

  def getOverpaymentsChooseFilesPage: HttpRequestBuilder =
    http("get overpayments choose files page")
      .get(s"$baseUrl/$route1/single/choose-files": String)
      .check(status.is(303))

  def getOverpaymentsUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
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
      .check(status.is(200))
      .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))
      .check(regex("Upload (.*)"))

  def postOverpaymentsUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
    http("post upload support evidence page")
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
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  def getOverpaymentsScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("#{UpscanResponseSuccess}")
      .check(status.in(303, 200))

  def getOverpaymentsFileVerificationStatusPage: List[ActionBuilder] =
    asLongAs(session => session("fileStatus").asOption[String].forall(s => s == "WAITING" || s == "NOT_UPLOADED"))(
      pause(1.second).exec(
        http("get the file verification status page")
          .get(s"$baseUrlUploadCustomsDocuments" + "#{fileVerificationUrl}")
          .check(status.is(200))
          .check(jsonPath("$.fileStatus").in("WAITING", "ACCEPTED", "NOT_UPLOADED").saveAs("fileStatus"))
      )
    ).actionBuilders

  def getOverpaymentsContinueToHostPage: HttpRequestBuilder =
    http("get overpayments continue to host page")
      .get(s"$baseUrl/upload-customs-documents/continue-to-host": String)
      .check(status.is(303))
      .check(header("Location").is(s"$baseUrl/$route1/single/check-your-answers": String))

  def getOverpaymentsCheckYourAnswersPage: HttpRequestBuilder =
    http("get overpayments check your answers page")
      .get(s"$baseUrl/$route1/single/check-your-answers": String)
      .check(status.is(303))

  def postOverpaymentsCheckYourAnswersPage: HttpRequestBuilder =
    http("post overpayments submit claim page")
      .post(s"$baseUrl/$route1/single/submit-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/choose-payee-type": String))

  def getOverpaymentsClaimSubmittedPage: HttpRequestBuilder =
    http("get overpayments claim submitted page")
      .get(s"$baseUrl/$route1/single/claim-submitted": String)
      .check(status.is(303))
}
