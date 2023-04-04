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
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object SecuritiesSingleRequests extends ServicesConfiguration with RequestUtils{

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/securities"


  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getSecuritiesSelectClaimTypePage: HttpRequestBuilder =
    http("get select claim type page")
      .get(s"$baseUrl/$route/select-claim-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Start a new claim"))

  def postSecuritiesSelectClaimTypePage: HttpRequestBuilder =
    http("post securities select claim type page")
      .post(s"$baseUrl/$route/select-claim-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-claim-type", "Securities")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-movement-reference-number": String))

  def getSecuritiesEnterMovementReferenceNumberPage: HttpRequestBuilder =
    http("get securities enter movement reference number page")
      .get(s"$baseUrl/$route1/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the import MRN"))

  def postSecuritiesEnterMovementReferenceNumberPage(MRN :String): HttpRequestBuilder =
    http("post securities enter movement reference number page")
      .post(s"$baseUrl/$route1/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", MRN)
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-reason-for-security": String))

  def getSecuritiesReasonForSecurityPage: HttpRequestBuilder =
    http("get securities choose reason for security page")
      .get(s"$baseUrl/$route1/choose-reason-for-security": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select why a security deposit or guarantee was required"))

  def postSecuritiesReasonForSecurityPage(trueOrFalse: Boolean = true, reasonForSecurity : String): HttpRequestBuilder = {

    val reasonForSecurityUrl = reasonForSecurity match {
      case "Account Sales" => "AccountSales"
      case "End Use Relief" => "EndUseRelief"
      case "Inward Processing Relief" => "InwardProcessingRelief"
      case "Manual override of duty amount" => "ManualOverrideDeposit"
      case "Missing document: Community System of Duty Relief" => "CommunitySystemsOfDutyRelief"
      case "Missing document: preference" => "MissingPreferenceCertificate"
      case "Missing document: quota license" => "MissingLicenseQuota"
      case "Revenue Dispute or Inland Pre-Clearance (IPC)" => "OutwardProcessingRelief"
      case "Temporary Admissions (2 months Expiration)" => "TemporaryAdmission2M"
      case "Temporary Admissions (2 years Expiration)" => "TemporaryAdmission2Y"
      case "Temporary Admissions (3 months Expiration)" => "TemporaryAdmission3M"
      case "Temporary Admissions (6 months Expiration)" => "TemporaryAdmission6M"
      case "UKAP Entry Price" => "UKAPEntryPrice"
      case "UKAP Safeguard Duties" => "UKAPSafeguardDuties"
      case _ => throw new IllegalArgumentException("Location not found " + reasonForSecurity)
    }

    val url = if (trueOrFalse) s"/$route1/check-total-import-discharged": String else s"/$route1/select-securities": String

    http("post securities choose reason for security page")
      .post(s"$baseUrl/$route1/choose-reason-for-security": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-reason-for-security.securities", reasonForSecurityUrl )
      .check(status.is(303))
      .check(header("Location").is(url))
   }

  def postSecuritiesReasonForSecurityForErrorPage(trueOrFalse: Boolean = true, reasonForSecurity : String): HttpRequestBuilder = {

    val reasonForSecurityUrl = reasonForSecurity match {
      case "Account Sales" => "AccountSales"
      case "End Use Relief" => "EndUseRelief"
      case "Inward Processing Relief" => "InwardProcessingRelief"
      case "Manual override of duty amount" => "ManualOverrideDeposit"
      case "Missing document: Community System of Duty Relief" => "CommunitySystemsOfDutyRelief"
      case "Missing document: preference" => "MissingPreferenceCertificate"
      case "Missing document: quota license" => "MissingLicenseQuota"
      case "Revenue Dispute or Inland Pre-Clearance (IPC)" => "OutwardProcessingRelief"
      case "Temporary Admissions (2 months Expiration)" => "TemporaryAdmission2M"
      case "Temporary Admissions (2 years Expiration)" => "TemporaryAdmission2Y"
      case "Temporary Admissions (3 months Expiration)" => "TemporaryAdmission3M"
      case "Temporary Admissions (6 months Expiration)" => "TemporaryAdmission6M"
      case "UKAP Entry Price" => "UKAPEntryPrice"
      case "UKAP Safeguard Duties" => "UKAPSafeguardDuties"
      case _ => throw new IllegalArgumentException("Location not found " + reasonForSecurity)
    }

    val url = if (trueOrFalse) s"/$route1/error/claim-invalid-086": String else s"/$route1/error/claim-invalid-072": String

    http("post securities choose reason for security for error page")
      .post(s"$baseUrl/$route1/choose-reason-for-security": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-reason-for-security.securities", reasonForSecurityUrl )
      .check(status.is(303))
      .check(header("Location").is(url))
  }

  def getSecuritiesAcc14Error086Page: HttpRequestBuilder =
    http("get securities Acc14 claim invalid 086 page")
      .get(s"$baseUrl/$route1/error/claim-invalid-086": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("There is a problem with your claim"))

  def getSecuritiesAcc14Error072Page: HttpRequestBuilder =
    http("get securities Acc14 claim invalid 072 page")
      .get(s"$baseUrl/$route1/error/claim-invalid-072": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("There is a problem with your claim"))

  def postSecuritiesReasonForSecurityForTP104ErrorPage(trueOrFalse: Boolean = true, reasonForSecurity : String): HttpRequestBuilder = {

    val reasonForSecurityUrl = reasonForSecurity match {
      case "Account Sales" => "AccountSales"
      case "End Use Relief" => "EndUseRelief"
      case "Inward Processing Relief" => "InwardProcessingRelief"
      case "Manual override of duty amount" => "ManualOverrideDeposit"
      case "Missing document: Community System of Duty Relief" => "CommunitySystemsOfDutyRelief"
      case "Missing document: preference" => "MissingPreferenceCertificate"
      case "Missing document: quota license" => "MissingLicenseQuota"
      case "Revenue Dispute or Inland Pre-Clearance (IPC)" => "OutwardProcessingRelief"
      case "Temporary Admissions (2 months Expiration)" => "TemporaryAdmission2M"
      case "Temporary Admissions (2 years Expiration)" => "TemporaryAdmission2Y"
      case "Temporary Admissions (3 months Expiration)" => "TemporaryAdmission3M"
      case "Temporary Admissions (6 months Expiration)" => "TemporaryAdmission6M"
      case "UKAP Entry Price" => "UKAPEntryPrice"
      case "UKAP Safeguard Duties" => "UKAPSafeguardDuties"
      case _ => throw new IllegalArgumentException("Location not found " + reasonForSecurity)
    }

    val url = if (trueOrFalse) s"/$route1/error/claim-invalid-TPI04": String else s"/$route1/error/claim-invalid-072": String

    http("post securities choose reason for security for error page")
      .post(s"$baseUrl/$route1/choose-reason-for-security": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-reason-for-security.securities", reasonForSecurityUrl )
      .check(status.is(303))
      .check(header("Location").is(url))
  }

  def getSecuritiesTPI04ErrorPage: HttpRequestBuilder =
    http("get securities TPI04 error page")
      .get(s"$baseUrl/$route1/error/claim-invalid-TPI04": String)
      //.check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("There is a problem with your claim"))

  def getSecuritiesTotalImportDischargedPage: HttpRequestBuilder =
    http("get securities check total import discharged page")
      .get(s"$baseUrl/$route1/check-total-import-discharged": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Have you discharged all of the imported goods?"))

  def postSecuritiesTotalImportDischargedForBod4Page: HttpRequestBuilder =
    http("post securities check total import discharged for bod4 page")
      .post(s"$baseUrl/$route1/check-total-import-discharged": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-total-import-discharged", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/bod4-mandatory-check": String))

  def getSecuritiesBod4MandatoryCheckPage: HttpRequestBuilder =
    http("get securities bod4 mandatory check page")
      .get(s"$baseUrl/$route1/bod4-mandatory-check": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("End-Use bill of discharge (.*)"))

  def postSecuritiesTotalImportDischargedForBod3Page: HttpRequestBuilder =
    http("post securities check total import discharged page")
      .post(s"$baseUrl/$route1/check-total-import-discharged": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-total-import-discharged", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/bod3-mandatory-check": String))

  def getSecuritiesBod3MandatoryCheckPage: HttpRequestBuilder =
    http("get securities bod3 mandatory check page")
      .get(s"$baseUrl/$route1/bod3-mandatory-check": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Inward Processing bill of discharge (.*)"))


  def postSecuritiesBod4MandatoryCheckPage: HttpRequestBuilder =
    http("post securities bod4 mandatory check page")
      .post(s"$baseUrl/$route1/bod4-mandatory-check": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("bill-of-discharge", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/select-securities": String))

  def postSecuritiesBod3MandatoryCheckPage: HttpRequestBuilder =
    http("post securities bod3 mandatory check page")
      .post(s"$baseUrl/$route1/bod3-mandatory-check": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("bill-of-discharge", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/select-securities": String))

  def getSecuritiesSelectSecuritiesPage: HttpRequestBuilder =
    http("get securities select securities page")
      .get(s"$baseUrl/$route1/select-securities": String)
      //.check(saveCsrfToken())
      .check(status.is(303))

  def getSecuritiesSelectSecurities1Page: HttpRequestBuilder =
    http("get securities select securities 1 of 5 page")
      .get(s"$baseUrl/$route1/select-securities/ABC0123456": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Include this security deposit in your claim?"))

  def postSecuritiesSelectSecurities1Page: HttpRequestBuilder =
    http("post securities select securities 1 of 5 page")
      .post(s"$baseUrl/$route1/select-securities/ABC0123456": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-securities", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/select-securities/DEF6543213": String))

  def getSecuritiesSelectSecurities2Page: HttpRequestBuilder =
    http("get securities select securities 2 of 5 page")
      .get(s"$baseUrl/$route1/select-securities/DEF6543213": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Include this security deposit in your claim?"))

  def postSecuritiesSelectSecurities2Page: HttpRequestBuilder =
    http("post securities select securities 2 of 5 page")
      .post(s"$baseUrl/$route1/select-securities/DEF6543213": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-securities", "true")
      .check(status.is(303))
     // .check(header("Location").is(s"/$route1/select-securities/DEF6543212": String))

  def getSecuritiesCheckDeclarationDetailsPage: HttpRequestBuilder =
    http("get securities check declaration details page")
      .get(s"$baseUrl/$route1/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))

  def postSecuritiesCheckDeclarationDetailsPage(trueOrFalse : Boolean = true): HttpRequestBuilder = {

    val url = if (trueOrFalse) s"/$route1/claimant-details": String else s"/$route1/export-method": String

    http("post securities check declaration details page")
      .post(s"$baseUrl/$route1/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(url))
  }

  def getSecuritiesExportMethodPage: HttpRequestBuilder =
    http("get securities export method page")
      .get(s"$baseUrl/$route1/export-method": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us what you did with the goods"))

  def postSecuritiesExportMethodPage: HttpRequestBuilder =
    http("post securities export method page")
      .post(s"$baseUrl/$route1/export-method": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-export-method", "ExportedInSingleShipment")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-export-movement-reference-number": String))

  def getSecuritiesExportMRNPage: HttpRequestBuilder =
    http("get securities export MRN page")
      .get(s"$baseUrl/$route1/enter-export-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the export MRN in the declaration"))

  def postSecuritiesExportMRNPage: HttpRequestBuilder =
    http("post securities export MRN page")
      .post(s"$baseUrl/$route1/enter-export-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-export-movement-reference-number", "41ABCDEFGHIJKLMNO1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/claimant-details": String))

  def getSecuritiesClaimantDetailsPage: HttpRequestBuilder =
    http("get securities claimant details page")
      .get(s"$baseUrl/$route1/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def postSecuritiesClaimantDetailsPage: HttpRequestBuilder =
    http("post securities claimant details page")
      .post(s"$baseUrl/$route1/claimant-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/confirm-full-repayment": String))

  def getSecuritiesConfirmFullRepaymentPage: HttpRequestBuilder =
    http("get securities confirm full repayments page")
      .get(s"$baseUrl/$route1/confirm-full-repayment": String)
      //.check(saveCsrfToken())
      .check(status.is(303))

  def getSecuritiesConfirmFullRepayment1of2Page: HttpRequestBuilder =
    http("get securities confirm full repayment 1 of 2 page")
      .get(s"$baseUrl/$route1/confirm-full-repayment/ABC0123456": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Do you want to claim back all of this security deposit?"))

  def postSecuritiesConfirmFullRepayment1of2Page: HttpRequestBuilder =
    http("post securities confirm full repayment 1 of 2  page")
      .post(s"$baseUrl/$route1/confirm-full-repayment/ABC0123456": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("confirm-full-repayment", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/confirm-full-repayment/DEF6543213": String))

  def getSecuritiesConfirmFullRepayment2of2Page: HttpRequestBuilder =
    http("get securities confirm full repayment 2 of 2 page")
      .get(s"$baseUrl/$route1/confirm-full-repayment/DEF6543213": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Do you want to claim back all of this security deposit?"))

  def postSecuritiesConfirmFullRepayment2of2Page: HttpRequestBuilder =
    http("post securities confirm full repayment 2 of 2  page")
      .post(s"$baseUrl/$route1/confirm-full-repayment/DEF6543213": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("confirm-full-repayment", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/select-duties/DEF6543213": String))

  def getSecuritiesSelectDutiesPage: HttpRequestBuilder =
    http("get securities select duties page")
      .get(s"$baseUrl/$route1/select-duties/DEF6543213": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))

  def postSecuritiesSelectDutiesPage: HttpRequestBuilder =
    http("post securities select duties  page")
      .post(s"$baseUrl/$route1/select-duties/DEF6543213": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-claim/DEF6543213": String))

  def getSecuritiesEnterClaimPage: HttpRequestBuilder =
    http("get securities enter claim page")
      .get(s"$baseUrl/$route1/enter-claim/DEF6543213": String)
      //.check(saveCsrfToken())
      .check(status.is(303))

  def getSecuritiesEnterClaimTaxCodePage: HttpRequestBuilder =
    http("get securities enter claim tax code page")
      .get(s"$baseUrl/$route1/enter-claim/DEF6543213/A00": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A00 - Customs Duty"))

  def postSecuritiesEnterClaimTaxCodePage: HttpRequestBuilder =
    http("post securities enter claim tax code page")
      .post(s"$baseUrl/$route1/enter-claim/DEF6543213/A00": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim.securities.claim-amount", "90")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/check-claim": String))

  def getSecuritiesCheckClaimPage: HttpRequestBuilder =
    http("get securities check claim page")
      .get(s"$baseUrl/$route1/check-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the claim details for these security deposits"))

  def postSecuritiesCheckClaimPage: HttpRequestBuilder =
    http("post securities check claim page")
      .post(s"$baseUrl/$route1/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/check-bank-details": String))

  def getSecuritiesCheckBankDetailsPage: HttpRequestBuilder =
    http("get securities check bank details page")
      .get(s"$baseUrl/$route1/check-bank-details": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))

  def getSecuritiesLetterOfAuthorityPage: HttpRequestBuilder =
    http("get securities letter of authority page")
      .get(s"$baseUrl/$route1/letter-of-authority-confirmation": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Is a Letter of Authority required?"))

  def postSecuritiesLetterOfAuthorityPage: HttpRequestBuilder =
    http("post securities letter of authority page")
      .post(s"$baseUrl/$route1/letter-of-authority-confirmation": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("bank_account_letter_of_authority", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-bank-account-type": String))

  def getSecuritiesChooseBankAccountTypePage: HttpRequestBuilder =
    http("get securities choose bank account type page")
      .get(s"$baseUrl/$route1/choose-bank-account-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))

  def postSecuritiesChooseBankAccountTypePage: HttpRequestBuilder =
    http("post securities choose bank account type page")
      .post(s"$baseUrl/$route1/choose-bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Personal")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/enter-bank-account-details": String))

  def getSecuritiesEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get securities enter bank account details page")
      .get(s"$baseUrl/$route1/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your bank account details"))

  def postSecuritiesEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post securities enter bank account details page")
      .post(s"$baseUrl/$route1/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "101010")
      .formParam("enter-bank-account-details.account-number", "12345678")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/check-bank-details": String))

  def getSecuritiesChooseFileTypePage: HttpRequestBuilder =
    http("get securities choose file type page")
      .get(s"$baseUrl/$route1/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))

  def postSecuritiesChooseFileTypePage: HttpRequestBuilder =
    http("post securities choose file type page")
      .post(s"$baseUrl/$route1/choose-file-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "ImportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-files": String))

  def getSecuritiesChooseFilesPage: HttpRequestBuilder =
    http("get securities choose files page")
      .get(s"$baseUrl/$route1/choose-files": String)
      //.check(saveCsrfToken())
      .check(status.is(303))

  def getSecuritiesCustomsDocumentsChooseFilePage: HttpRequestBuilder =
    http("get upload documents choose file page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-file": String)
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
      .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))
      .check(regex("Upload (.*)"))

  def postSecuritiesUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
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
      .bodyPart(RawFileBodyPart("file", "data/testImage95.jpg"))
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  def getSecuritiesSingleScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.in(303, 200))

  def getSecuritiesCheckYourAnswersPage : HttpRequestBuilder =
    http("get securities check your answers page")
      .get(s"$baseUrl/$route1/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))

  def postSecuritiesCheckYourAnswersPage : HttpRequestBuilder =
    http("post securities submit claim page")
      .post(s"$baseUrl/$route1/submit-claim" : String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/claim-submitted": String))

  def getSecuritiesClaimSubmittedPage : HttpRequestBuilder =
    http(("get securities claim submitted page"))
      .get(s"$baseUrl/$route1/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
}
