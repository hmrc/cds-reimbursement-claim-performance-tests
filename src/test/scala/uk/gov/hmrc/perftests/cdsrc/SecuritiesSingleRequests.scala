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

object SecuritiesSingleRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/securities"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken: CheckBuilder[CssCheckType, NodeSelector] =
    css("input[name='csrfToken']", "value").optional.saveAs("csrfToken")

  def getSecuritiesSelectClaimTypePage: HttpRequestBuilder =
    http("get select claim type page")
      .get(s"$baseUrl/$route/choose-claim-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(css("title").is("Start a new claim - Claim back import duty and VAT - GOV.UK"))

  def postSecuritiesSelectClaimTypePage: HttpRequestBuilder =
    http("post securities select claim type page")
      .post(s"$baseUrl/$route/choose-claim-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-claim-type", "Securities")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-movement-reference-number": String))

  def getSecuritiesEnterMovementReferenceNumberPage: HttpRequestBuilder =
    http("get securities enter movement reference number page")
      .get(s"$baseUrl/$route1/enter-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Movement Reference Number (MRN)")).is(true))

  def postSecuritiesEnterMovementReferenceNumberPage(MRN: String): HttpRequestBuilder =
    http("post securities enter movement reference number page")
      .post(s"$baseUrl/$route1/enter-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", MRN)
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-reason-for-security": String))

  def getSecuritiesReasonForSecurityPage: HttpRequestBuilder =
    http("get securities choose reason for security page")
      .get(s"$baseUrl/$route1/choose-reason-for-security": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Why was a security deposit or guarantee needed?")).is(true))

  def postSecuritiesReasonForSecurityPage(
    reasonForSecurity: String
  ): HttpRequestBuilder = {

    val reasonForSecurityUrl = reasonForSecurity match {
      case "Authorised-use (Great Britain) or end-use (Northern Ireland) relief" => "EndUseRelief"
      case "Inward-processing relief (IPR)"                                      => "InwardProcessingRelief"
      //case "Manual override of duty amount" => "ManualOverrideDeposit"
      //case "Missing document: Community System of Duty Relief" => "CommunitySystemsOfDutyRelief"
      case "Missing proof of origin"                                             => "MissingPreferenceCertificate"
      //case "Missing document: quota license" => "MissingLicenseQuota"
      //case "Revenue Dispute or Inland Pre-Clearance (IPC)" => "OutwardProcessingRelief"
      case "Temporary Admission (2 months)"                                      => "TemporaryAdmission2M"
      case "Temporary Admission (24 months)"                                     => "TemporaryAdmission2Y"
      case "Temporary Admission (3 months)"                                      => "TemporaryAdmission3M"
      case "Temporary Admission (6 months)"                                      => "TemporaryAdmission6M"
      // case "UKAP Entry Price" => "UKAPEntryPrice"
      //case "UKAP Safeguard Duties" => "UKAPSafeguardDuties"
      // case _ => throw new IllegalArgumentException("Location not found " + reasonForSecurity)
    }

    http("post securities choose reason for security page")
      .post(s"$baseUrl/$route1/choose-reason-for-security": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-reason-for-security.securities", reasonForSecurityUrl)
      .check(status.is(303))
  }

  def postSecuritiesSingleReasonForSecurityPage: HttpRequestBuilder =
    http("post securities single choose reason for security page")
      .post(s"$baseUrl/$route1/select-securities": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(header("Location").is(s"/$route1/single/check-mrn": String))

  def getSecuritiesHaveYourDocumentsReady: HttpRequestBuilder =
    http("get the supporting documents ready page")
      .get(s"$baseUrl/$route1/have-your-documents-ready": String)
      .check(status.in(200, 303))
      .check(bodyString.transform(_.contains("Have your supporting documents ready")).is(true))

  def getSecuritiesTotalImportDischargedPage: HttpRequestBuilder =
    http("get securities check total import discharged page")
      .get(s"$baseUrl/$route1/check-total-import-discharged": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Can you account for all of the imported goods?")).is(true))

  def postSecuritiesTotalImportDischargedForBod4Page: HttpRequestBuilder =
    http("post securities check total import discharged for bod4 page")
      .post(s"$baseUrl/$route1/check-total-import-discharged": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-total-import-discharged", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/upload-bill-of-discharge4": String))

  def getSecuritiesBODChooseFileTypePage: HttpRequestBuilder =
    http("get BOD upload documents choose file page")
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
      .check(status.in(200, 303))
      .check(regex("""form action="(.*)" method""").saveAs("actionlll"))
      .check(regex("""supporting-evidence/scan-progress/(.*)">""").saveAs("action1"))
      .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))

  def postSecuritiesBODChooseFileTypePage: HttpRequestBuilder =
    http("post BOD upload support evidence page")
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
      .bodyPart(RawFileBodyPart("file", "data/pixel.jpg"))
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/add-other-documents": String))

  def postSecuritiesTotalImportDischargedForBod3Page: HttpRequestBuilder =
    http("post securities check total import discharged page")
      .post(s"$baseUrl/$route1/check-total-import-discharged": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-total-import-discharged", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/upload-bill-of-discharge3": String))

  def getSecuritiesBod3MandatoryCheckPage: HttpRequestBuilder =
    http("get securities bod3 mandatory check page")
      .get(s"$baseUrl/$route1/bod3-mandatory-check": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Bill of discharge (BOD) form"))

  def getAddOtherDocuments: HttpRequestBuilder =
    http("add other documents to support your claim")
      .get(s"$baseUrl/$route1/add-other-documents": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))

  def postAddOtherDocuments: HttpRequestBuilder =
    http("post add other documents to support your claim")
      .post(s"$baseUrl/$route1/add-other-documents": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("add-other-documents", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-payee-type": String))

  def getEnterAdditionalDetails: HttpRequestBuilder =
    http("add other documents to support your claim")
      .get(s"$baseUrl/$route1/enter-additional-details": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))

  def postEnterAdditionalDetails: HttpRequestBuilder =
    http("add other documents to support your claim")
      .post(s"$baseUrl/$route1/enter-additional-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-additional-details", "Test")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/change-contact-details": String))

  def getSecuritiesCheckDeclarationDetailsPage: HttpRequestBuilder =
    http("get securities check declaration details page")
      .get(s"$baseUrl/$route1/single/check-mrn": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))

  def postSecuritiesCheckDeclarationDetailsPage: HttpRequestBuilder =
    http("post securities check declaration details page")
      .post(s"$baseUrl/$route1/single/check-mrn": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/confirm-full-repayment": String))

  def getSecuritiesTACheckDeclarationDetailsPage: HttpRequestBuilder =
    http("get securities check declaration details page")
      .get(s"$baseUrl/$route1/single/check-mrn": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))

  def postSecuritiesTACheckDeclarationDetailsPage: HttpRequestBuilder =
    http("post securities TA next check declaration details page")
      .post(s"$baseUrl/$route1/single/check-mrn": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/have-your-documents-ready": String))

  def getSecuritiesConfirmPaymentExportMethodPage: HttpRequestBuilder =
    http("get securities Confirm Payment export method page")
      .get(s"$baseUrl/$route1/single/confirm-full-repayment": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Tell us what you’re claiming")).is(true))

  def postSecuritiesConfirmPaymentExportMethodPage: HttpRequestBuilder =
    http("post securities Confirm Payment export method page")
      .post(s"$baseUrl/$route1/single/confirm-full-repayment": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("confirm-full-repayment-2", "false")
      .formParam("choose-export-method", "ExportedInSingleShipment")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/partial-claims": String))

  def getSecuritiesPartialClaimPage: HttpRequestBuilder =
    http("get securities Partial Claim page")
      .get(s"$baseUrl/$route1/partial-claims": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Partial claims")).is(true))

  def postSecuritiesPartialClaimPage: HttpRequestBuilder =
    http("post securities Partial Claim page")
      .post(s"$baseUrl/$route1/partial-claims": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("partial-claims", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/select-duties": String))

  def getSecuritiesSelectDutyPage: HttpRequestBuilder =
    http("get securities Select Duty page")
      .get(s"$baseUrl/$route1/select-duties": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What do you want to claim?")).is(true))

  def postSecuritiesSelectDutyPage: HttpRequestBuilder =
    http("post securities Select Duty page")
      .post(s"$baseUrl/$route1/select-duties": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duties", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-claim/ABC0123456/A00": String))

  def getSecuritiesConfirmDutyRepaymentPage: HttpRequestBuilder =
    http("get securities duty repayments page")
      .get(s"$baseUrl/$route1/enter-claim/ABC0123456/A00": String)
      .check(saveCsrfToken)
      .check(status.is(303))

  def postSecuritiesEnterClaimRedTaxCodePage: HttpRequestBuilder =
    http("post securities enter claim tax code page")
      .post(s"$baseUrl/$route1/select-duties/ABC0123456": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.in(303, 200))

  def postSecuritiesEnterClaimTaxCodePage: HttpRequestBuilder =
    http("post securities enter claim tax code page")
      .post(s"$baseUrl/$route1/enter-claim/ABC0123456/A00": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim-amount", "90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/confirm-full-repayment/ABC0123456": String))

  def getSecuritiesCheckClaimPage: HttpRequestBuilder =
    http("get securities check claim page")
      .get(s"$baseUrl/$route1/single/check-claim": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))

  def postSecuritiesCheckClaimPage: HttpRequestBuilder =
    http("post securities check claim page")
      .post(s"$baseUrl/$route1/single/check-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))

  def getSecuritiesExportMethodPage: HttpRequestBuilder =
    http("get securities export method page")
      .get(s"$baseUrl/$route1/export-method": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What did you do with the goods?")).is(true))

  def postSecuritiesExportMethodPage: HttpRequestBuilder =
    http("post securities export method page")
      .post(s"$baseUrl/$route1/export-method": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-export-method[]", "ExportedInSingleOrMultipleShipments")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-export-movement-reference-number": String))

  def getSecuritiesExportMRNPage: HttpRequestBuilder =
    http("get securities export MRN page")
      .get(s"$baseUrl/$route1/enter-export-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))

  def postSecuritiesExportMRNPage: HttpRequestBuilder =
    http("post securities export MRN page")
      .post(s"$baseUrl/$route1/enter-export-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-export-movement-reference-number", "41ABCDEFGHIJKLMNO1")
      .formParam("enter-export-movement-reference-number", "false")
      .check(status.is(303))

  def getSecuritiesChoosePayeeTypePage: HttpRequestBuilder =
    http("get the securities choose payee type page")
      .get(s"$baseUrl/$route1/choose-payee-type": String)
      .check(status.in(200, 303))

  def getSecuritiesEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get securities enter bank account details page")
      .get(s"$baseUrl/$route1/enter-bank-account-details": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))
      .check(bodyString.transform(_.contains("Enter the UK-based bank account details")).is(true))

  def postSecuritiesEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post securities enter bank account details page")
      .post(s"$baseUrl/$route/securities/enter-bank-account-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.in(200, 303))

  def postSecuritiesMoreDetailsPage: HttpRequestBuilder =
    http("post securities more details page")
      .post(s"$baseUrl/$route/securities/enter-bank-account-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.in(200, 303, 400))

  def getSecuritiesChooseFileTypePage: HttpRequestBuilder =
    http("get securities choose file type page")
      .get(s"$baseUrl/$route1/choose-file-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))

  def postSecuritiesChooseFileTypePage: HttpRequestBuilder =
    http("post securities choose file type page")
      .post(s"$baseUrl/$route1/choose-file-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-file-type", "ImportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-files": String))

  def getSecuritiesChooseFilesPage: HttpRequestBuilder =
    http("get securities choose files page")
      .get(s"$baseUrl/$route1/choose-files": String)
      .check(status.is(303))

  def getSecuritiesCustomsDocumentsChooseFilePage: HttpRequestBuilder =
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

  def postSecuritiesUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
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
      .bodyPart(RawFileBodyPart("file", "data/pixel.jpg"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  def getSecuritiesSingleScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("#{UpscanResponseSuccess}")
      .check(status.in(303, 200))

  def getSecuritiesClaimantDetailsPage: HttpRequestBuilder =
    http("get securities claimant details page")
      .get(s"$baseUrl/$route1/claimant-details/change-contact-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Who should we contact about this claim?"))

  def postSecuritiesClaimantDetailsPage: HttpRequestBuilder =
    http("post securities claimant details page")
      .post(s"$baseUrl/$route1/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/claimant-details/lookup-address": String))

  def getSecuritiesClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get Securities claimant details page from details contact page")
      .get(s"$baseUrl/$route1/claimant-details": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))
      .check(header("Location").is(s"/$route1/claimant-details/lookup-address": String))

  def getSecuritiesCheckYourAnswersPage: HttpRequestBuilder =
    http("get securities check your answers page")
      .get(s"$baseUrl/$route1/check-your-answers": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))

  def postSecuritiesCheckYourAnswersPage: HttpRequestBuilder =
    http("post securities submit claim page")
      .post(s"$baseUrl/$route1/submit-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))

  def getSecuritiesClaimSubmittedPage: HttpRequestBuilder =
    http("get securities claim submitted page")
      .get(s"$baseUrl/$route1/claim-submitted": String)
      .check(status.in(200, 303))

}
