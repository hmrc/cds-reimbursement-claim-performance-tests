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
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object SingleMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String   = "claim-back-import-duty-vat"
  val route1: String  = "claim-back-import-duty-vat/overpayments"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken: CheckBuilder[RegexCheckType, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getMRNAuthLoginPage: HttpRequestBuilder =
    http("Navigate to auth login stub page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in")
      .check(status.is(200))
      .check(regex("Authority Wizard").exists)
      .check(saveCsrfToken)
      .check(regex("CredID").exists)

  def loginWithAuthLoginStubMRN(
    eoriValue: String = "",
    enrolmentKey: String = "",
    identifierName: String = "",
    identifierValue: String = ""
  ): HttpRequestBuilder =
    http("Login with user credentials")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("authorityId", _ => "")
      .formParam("gatewayToken", _ => "")
      .formParam("redirectionUrl", _ => redirect1)
      .formParam("credentialStrength", _ => "strong")
      .formParam("excludeGnapToken", _ => "false")
      .formParam("confidenceLevel", _ => "50")
      .formParam("affinityGroup", _ => "Individual")
      .formParam("usersName", _ => "")
      .formParam("email", _ => "user@test.com")
      .formParam("credentialRole", _ => "User")
      .formParam("oauthTokens.accessToken", _ => "")
      .formParam("oauthTokens.refreshToken", _ => "")
      .formParam("oauthTokens.idToken", _ => "")
      .formParam("additionalInfo.profile", _ => "")
      .formParam("additionalInfo.groupProfile", _ => "")
      .formParam("additionalInfo.emailVerified", _ => "N/A")
      .formParam("nino", _ => "")
      .formParam("groupIdentifier", _ => "")
      .formParam("agent.agentId", _ => "")
      .formParam("agent.agentCode", _ => "")
      .formParam("agent.agentFriendlyName", _ => "")
      .formParam("unreadMessageCount", _ => "")
      .formParam("mdtp.sessionId", _ => "")
      .formParam("mdtp.deviceId", _ => "")
      .formParam("presets-dropdown", _ => "SA")
      .formParam("enrolment[0].name", _ => "HMRC-CUS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", _ => "EORINumber")
      .formParam("enrolment[0].taxIdentifier[0].value", _ => s"$eoriValue")
      .formParam("enrolment[0].state", _ => "Activated")
      .formParam("enrolment[1].name", _ => "")
      .formParam("enrolment[1].taxIdentifier[0].name", _ => "")
      .formParam("enrolment[1].taxIdentifier[0].value", _ => "")
      .formParam("enrolment[1].state", _ => "Activated")
      .formParam("enrolment[2].name", _ => "")
      .formParam("enrolment[2].taxIdentifier[0].name", _ => "")
      .formParam("enrolment[2].taxIdentifier[0].value", _ => "")
      .formParam("enrolment[2].state", _ => "Activated")
      .formParam("enrolment[3].name", _ => "")
      .formParam("enrolment[3].taxIdentifier[0].name", _ => "")
      .formParam("enrolment[3].taxIdentifier[0].value", _ => "")
      .formParam("enrolment[3].state", _ => "Activated")
      .formParam("itmp.givenName", _ => "")
      .formParam("itmp.middleName", _ => "")
      .formParam("itmp.familyName", _ => "")
      .formParam("itmp.dateOfBirth", _ => "")
      .formParam("itmp.address.line1", _ => "")
      .formParam("itmp.address.line2", _ => "")
      .formParam("itmp.address.line3", _ => "")
      .formParam("itmp.address.line4", _ => "")
      .formParam("itmp.address.line5", _ => "")
      .formParam("itmp.address.postCode", _ => "")
      .formParam("itmp.address.countryName", _ => "")
      .formParam("itmp.address.countryCode", _ => "")
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"$baseUrl/$route/start": String))

  def getMRNCdsrStartPage: HttpRequestBuilder =
    http("post cdsr start page")
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(303))

  def getTheMRNCheckEoriDetailsPage: HttpRequestBuilder =
    http("get check eori details page")
      .get(s"$baseUrl/$route/check-eori-details": String)
      .check(saveCsrfToken)
      .check(status.in(200, 303))
      .check(regex("Check your EORI number"))

  def postTheMRNCheckEoriDetailsPage: HttpRequestBuilder =
    http("post check eori details page")
      .post(s"$baseUrl/$route/check-eori-details": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("check-eori-details", _ => "true")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/claim-back-import-duty-vat/choose-claim-type": String))

  def getSelectClaimTypePage: HttpRequestBuilder =
    http("get select claim type page")
      .get(s"$baseUrl/$route/choose-claim-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Start a new claim"))

  def postSelectClaimTypePage: HttpRequestBuilder =
    http("post select claim type page")
      .post(s"$baseUrl/$route/choose-claim-type": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("choose-claim-type", _ => "C285")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/choose-how-many-mrns": String))

  def getChooseHowManyMrnsPage: HttpRequestBuilder =
    http("get the choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(regex("Single or multiple Movement Reference Numbers (MRNs)"))

  def getHaveYourDocumentsReady: HttpRequestBuilder =
    http("get Have Your Documents Ready page")
      .get(s"$baseUrl/$route1/have-your-documents-ready": String)
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(regex("Have your supporting documents ready"))

  def postChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post the choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("overpayments.choose-how-many-mrns", _ => "Individual")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/v2/single/enter-movement-reference-number": String))

  def getTheMRNPage: HttpRequestBuilder =
    http("get The MRN page")
      .get(s"$baseUrl/$route1/v2/single/enter-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Movement Reference Number (MRN)"))

  def postTheMRNPage: HttpRequestBuilder =
    http("post The MRN page")
      .post(s"$baseUrl/$route1/v2/single/enter-movement-reference-number": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-movement-reference-number", _ => "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/v2/single/enter-importer-eori": String))

  def getTheMRNImporterEoriEntryPage: HttpRequestBuilder =
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("What is the importer’s EORI number?"))

  def postTheMRNImporterEoriEntryPage: HttpRequestBuilder =
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-importer-eori-number", _ => "GB123456789012345")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/enter-declarant-eori": String))

  def getTheMRNDeclarantEoriEntryPage: HttpRequestBuilder =
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("What is the declarant’s EORI number?"))

  def postTheMRNDeclarantEoriEntryPage: HttpRequestBuilder =
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-declarant-eori-number", _ => "GB123456789012345")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/check-mrn": String))

  def postTheMRNChooseBasisOfClaimPage: HttpRequestBuilder =
    http("post the MRN choose basis of claim page")
      .post(s"$baseUrl/$route1/single/choose-basis-for-claim": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("select-basis-for-claim", _ => "DuplicateEntry")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/enter-duplicate-movement-reference-number": String))

  def getTheDuplicateMRNPage: HttpRequestBuilder =
    http("get the duplicate enter movement reference number page")
      .get(s"$baseUrl/$route1/single/enter-duplicate-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Duplicate Movement Reference Number (MRN)"))

  def postTheDuplicateMRNPage: HttpRequestBuilder =
    http("post the duplicate enter movement reference number page")
      .post(s"$baseUrl/$route1/single/enter-duplicate-movement-reference-number": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-duplicate-movement-reference-number", _ => "20AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/check-duplicate-mrn": String))

  def getTheMRNCheckDuplicateDeclarationPage: HttpRequestBuilder =
    http("get the MRN check duplicate declaration details page")
      .get(s"$baseUrl/$route1/single/check-duplicate-mrn": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Check the duplicate Movement Reference Number (MRN)"))

  def postTheMRNCheckDuplicateDeclarationPage: HttpRequestBuilder =
    http("post the MRN duplicate check declaration details page")
      .post(s"$baseUrl/$route1/single/check-duplicate-mrn": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("check-declaration-details", _ => "true")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/enter-additional-details": String))

  def getTheMRNEnterCommodityDetailsPage: HttpRequestBuilder =
    http("get the MRN enter commodity details page")
      .get(s"$baseUrl/$route1/single/enter-additional-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Tell us more about your claim"))

  def postTheMRNEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post the MRN enter commodity details page")
      .post(s"$baseUrl/$route1/single/enter-additional-details": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-additional-details", _ => "phones")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/select-duties": String))

  def getTheMRNSelectDutiesPage: HttpRequestBuilder =
    http("get the MRN select duties page")
      .get(s"$baseUrl/$route1/single/select-duties": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Claim details What do you want to claim?"))

  def postTheMRNSelectDutiesPage: HttpRequestBuilder =
    http("post the MRN select duties page")
      .post(s"$baseUrl/$route1/single/select-duties": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("select-duties[]", _ => "A95")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/enter-claim/A95": String))

  def getTheMRNStartClaimPage: HttpRequestBuilder =
    http("get the MRN start claim page")
      .get(s"$baseUrl/$route1/single/enter-claim/A95": String)
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).saveAs("action3"))

  def getTheMRNEnterClaimPage: HttpRequestBuilder =
    http("get the MRN enter claim page")
      .get { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .check(status.is(200))
      .check(regex("Claim details for A95 - Provisional Countervailing Duty"))

  def postTheMRNEnterClaimPage: HttpRequestBuilder =
    http("post the MRN enter claim page")
      .post { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-claim", _ => "39")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/check-claim": String))

  def getTheMRNCheckClaimPage: HttpRequestBuilder =
    http("get the MRN check claim page")
      .get(s"$baseUrl/$route1/single/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the repayment claim total for the MRN"))

  def postTheMRNCheckClaimPage: HttpRequestBuilder =
    http("post the MRN check claim page")
      .post(s"$baseUrl/$route1/single/check-claim": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("check-claim-summary", _ => "true")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/choose-repayment-method": String))

  def getTheMRNClaimantDetailsPage: HttpRequestBuilder =
    http("get the MRN claimant details page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def getTheMrnChangeContactDetailsPage: HttpRequestBuilder =
    http("get the MRN change contact details page")
      .get(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))

  def postTheMrnChangeContactDetailsPage: HttpRequestBuilder =
    http("post the MRN change contact details page")
      .post(s"$baseUrl/$route1/single/claimant-details/change-contact-details": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-contact-details.contact-name", _ => "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", _ => "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", _ => "+4420723934397")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/claimant-details": String))

  def getTheMrnClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get the MRN claimant details page from details contact page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def postTheMrnClaimantDetailsCheckPage: HttpRequestBuilder =
    http("post the MRN claimant details page")
      .post(s"$baseUrl/$route1/single/claimant-details": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("claimant-details", _ => "true")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/claim-northern-ireland": String))

  def getTheMRNClaimNorthernIrelandPage: HttpRequestBuilder =
    http("get the claim northern ireland page")
      .get(s"$baseUrl/$route1/single/claim-northern-ireland": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Were your goods moved through or imported to Northern Ireland?"))

  def postTheMRNClaimNorthernIrelandPage: HttpRequestBuilder =
    http("post the claim northern ireland page")
      .post(s"$baseUrl/$route1/single/claim-northern-ireland": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("claim-northern-ireland", _ => "true")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/choose-basis-for-claim": String))

  def getSelectReimbursementMethodPage: HttpRequestBuilder =
    http("get choose repayment method page")
      .get(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .check(status.is(200))
      .check(regex("How would you like to be repaid?"))

  def postSelectReimbursementMethodPage: HttpRequestBuilder =
    http("post choose repayment method page")
      .post(s"$baseUrl/$route1/single/choose-repayment-method": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("reimbursement-method", _ => "1")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/check-these-bank-details-are-correct": String))

  def getTheMRNCheckTheseBankDetailsAreCorrectPage: HttpRequestBuilder =
    http("get the MRN check these bank details are correct page")
      .get(s"$baseUrl/$route1/single/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))

  def getTheMRNBankAccountTypePage: HttpRequestBuilder =
    http("get the MRN bank account type")
      .get(s"$baseUrl/$route1/single/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))

  def postTheMRNBankAccountTypePage: HttpRequestBuilder =
    http("post the MRN bank account type")
      .post(s"$baseUrl/$route1/single/bank-account-type": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("select-bank-account-type", _ => "Personal")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/enter-bank-account-details": String))

  def getTheMRNEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get the MRN enter bank account details page")
      .get(s"$baseUrl/$route1/single/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter your bank account details"))

  def postTheMRNEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post the MRN enter bank account details page")
      .post(s"$baseUrl/$route1/single/enter-bank-account-details": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("enter-bank-account-details.account-name", _ => "Halifax")
      .formParam("enter-bank-account-details.sort-code", _ => "123456")
      .formParam("enter-bank-account-details.account-number", _ => "23456789")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/check-these-bank-details-are-correct": String))

  def postTheMRNCheckTheseBankDetailsAreCorrectPage: HttpRequestBuilder =
    http("post the MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
      .check(status.is(200))

  def getSelectSelectSupportingEvidenceTypePage: HttpRequestBuilder =
    http("get select supporting evidence type page")
      .get(s"$baseUrl/$route1/single/supporting-evidence/select-supporting-evidence-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))

  def postSelectSupportingEvidenceTypePage: HttpRequestBuilder =
    http("post select supporting evidence type page")
      .post(s"$baseUrl/$route1/single/supporting-evidence/select-supporting-evidence-type": String)
      .formParam("csrfToken", session => session("csrfToken").as[String])
      .formParam("choose-file-type", _ => "AirWayBill")
      .check(status.is(303))
      .check(header("Location".asInstanceOf[CharSequence]).is(s"/$route1/single/supporting-evidence/choose-files": String))

  def getSupportingEvidenceChooseFilesPage: HttpRequestBuilder =
    http("get supporting evidence choose files page")
      .get(s"$baseUrl/$route1/single/supporting-evidence/choose-files": String)
      .check(status.is(303))
}
