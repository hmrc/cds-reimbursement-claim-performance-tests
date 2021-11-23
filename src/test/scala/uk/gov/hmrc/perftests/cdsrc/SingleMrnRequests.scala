/*
 * Copyright 2021 HM Revenue & Customs
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
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object SingleMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-for-reimbursement-of-import-duties"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[HttpCheck, Response, CharSequence, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getMRNAuthLoginPage : HttpRequestBuilder = {
    http("Navigate to auth login stub page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in")
      .check(status.is(200))
      .check(regex("Authority Wizard").exists)
      .check(regex("CredID").exists)
  }


  def loginWithAuthLoginStubMRN(eoriValue: String =  "", enrolmentKey: String = "", identifierName: String = "", identifierValue: String = ""): HttpRequestBuilder = {
    http("Login with user credentials")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("authorityId", "")
      .formParam("gatewayToken", "")
      .formParam("redirectionUrl", redirect1)
      .formParam("credentialStrength", "weak")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Individual")
      .formParam("usersName", "")
      .formParam("email", "user@test.com")
      .formParam("credentialRole", "User")
      .formParam("oauthTokens.accessToken", "")
      .formParam("oauthTokens.refreshToken", "")
      .formParam("oauthTokens.idToken", "")
      .formParam("additionalInfo.profile", "")
      .formParam("additionalInfo.groupProfile", "")
      .formParam("additionalInfo.emailVerified", "N/A")
      .formParam("nino", "")
      .formParam("groupIdentifier", "")
      .formParam("agent.agentId", "")
      .formParam("agent.agentCode", "")
      .formParam("agent.agentFriendlyName", "")
      .formParam("unreadMessageCount", "")
      .formParam("mdtp.sessionId", "")
      .formParam("mdtp.deviceId", "")
      .formParam("presets-dropdown", "IR-SA")
      .formParam("enrolment[0].name", "HMRC-CUS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "EORINumber")
      .formParam("enrolment[0].taxIdentifier[0].value", {s"$eoriValue"})
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[1].name", "")
      .formParam("enrolment[1].taxIdentifier[0].name", "")
      .formParam("enrolment[1].taxIdentifier[0].value", "")
      .formParam("enrolment[1].state", "Activated")
      .formParam("enrolment[2].name", "")
      .formParam("enrolment[2].taxIdentifier[0].name", "")
      .formParam("enrolment[2].taxIdentifier[0].value", "")
      .formParam("enrolment[2].state", "Activated")
      .formParam("enrolment[3].name", "")
      .formParam("enrolment[3].taxIdentifier[0].name", "")
      .formParam("enrolment[3].taxIdentifier[0].value", "")
      .formParam("enrolment[3].state", "Activated")
      .formParam("itmp.givenName", "")
      .formParam("itmp.middleName", "")
      .formParam("itmp.familyName", "")
      .formParam("itmp.dateOfBirth", "")
      .formParam("itmp.address.line1", "")
      .formParam("itmp.address.line2", "")
      .formParam("itmp.address.line3", "")
      .formParam("itmp.address.line4", "")
      .formParam("itmp.address.line5", "")
      .formParam("itmp.address.postCode", "")
      .formParam("itmp.address.countryName", "")
      .formParam("itmp.address.countryCode", "")
      .check(status.is(303))
      .check(header("Location").is(s"$baseUrl/$route/start": String))
  }

  def getMRNCdsrStartPage : HttpRequestBuilder = {
      http("post cdsr start page")
        .get(s"$baseUrl/$route/start/": String)
        .check(status.is(303))
  }

  def getTheMRNCheckEoriDetailsPage : HttpRequestBuilder = {
    http("get check eori details page")
      .get(s"$baseUrl/$route/check-eori-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the EORI associated with the user ID is correct"))
  }

  def postTheMRNCheckEoriDetailsPage : HttpRequestBuilder = {
    http("post check eori details page")
      .post(s"$baseUrl/$route/check-eori-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-eori-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/choose-how-many-mrns": String))
  }

  def getChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("get the choose how many mrns page")
      .get(s"$baseUrl/$route/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("How many MRNs do you want to submit in this claim?"))
  }

  def postChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("post the choose how many mrns page")
      .post(s"$baseUrl/$route/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-number-of-claims", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-movement-reference-number": String))
  }

  def getTheMRNPage : HttpRequestBuilder = {
    http("get The MRN page")
      .get(s"$baseUrl/$route/single/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us your Movement Reference Number (.*)"))
  }

  def postTheMRNPage : HttpRequestBuilder = {
    http("post The MRN page")
      .post(s"$baseUrl/$route/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-importer-eori": String))
  }

  def getTheMRNImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route/single/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postTheMRNImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route/single/enter-importer-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-declarant-eori": String))
  }

  def getTheMRNDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route/single/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postTheMRNDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route/single/enter-declarant-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-declaration-details": String))
  }

  def getTheMRNCheckDeclarationPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route/single/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postTheMRNCheckDeclarationPage : HttpRequestBuilder = {
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route/single/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/who-is-the-declarant": String))
  }

  def getTheMRNWhoIsDeclarantPage : HttpRequestBuilder = {
    http("get the MRN who is declarant page")
      .get(s"$baseUrl/$route/single/who-is-the-declarant": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Who is making this claim?"))
  }

  def postTheMRNWhoIsDeclarantPage : HttpRequestBuilder = {
    http("post the MRN who is declarant page")
      .post(s"$baseUrl/$route/single/who-is-the-declarant": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-who-is-making-the-claim", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/claimant-details/check": String))
  }

  def getTheMrnClaimantDetailsCheckPage : HttpRequestBuilder = {
    http("get the MRN claimant details check page")
      .get(s"$baseUrl/$route/single/claimant-details/check": String)
      .check(status.is(200))
      .check(regex("Check your details as registered with CDS"))
  }

  def getTheMrnChangeContactDetailsPage : HttpRequestBuilder = {
    http("get the MRN change contact details page")
      .get(s"$baseUrl/$route/single/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))
  }

  def postTheMrnChangeContactDetailsPage : HttpRequestBuilder = {
    http("post the MRN change contact details page")
      .post(s"$baseUrl/$route/single/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/claimant-details/check": String))
  }

  def getTheMrnClaimantDetailsCheckPage1 : HttpRequestBuilder = {
    http("get the MRN claimant details page from details contact page")
      .get(s"$baseUrl/$route/single/claimant-details/check": String)
      .check(status.is(200))
      .check(regex("Check your details as registered with CDS"))
  }

  def postTheMrnClaimantDetailsCheckPage : HttpRequestBuilder = {
    http("post the MRN claimant details check page")
      .post(s"$baseUrl/$route/single/claimant-details/check": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claimant-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/claim-northern-ireland": String))
  }

  def getTheMRNClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("get the claim northern ireland page")
      .get(s"$baseUrl/$route/single/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your goods imported to or moved through Northern Ireland?"))
  }

  def postTheMRNClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("post the claim northern ireland page")
      .post(s"$baseUrl/$route/single/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/choose-basis-for-claim": String))

  }

  def getTheMRNChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("get the MRN choose basis of claim page")
      .get(s"$baseUrl/$route/single/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the basis for claim"))
  }

  def postTheMRNChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("post the MRN choose basis of claim page")
      .post(s"$baseUrl/$route/single/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-duplicate-movement-reference-number": String))
  }

  def getTheDuplicateMRNPage : HttpRequestBuilder = {
    http("get the duplicate enter movement reference number page")
      .get(s"$baseUrl/$route/single/enter-duplicate-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us your duplicate Movement Reference Number (.*)"))
  }

  def postTheDuplicateMRNPage : HttpRequestBuilder = {
    http("post the duplicate enter movement reference number page")
      .post(s"$baseUrl/$route/single/enter-duplicate-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-duplicate-movement-reference-number", "20AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-duplicate-declaration-details": String))
  }

  def getTheMRNCheckDuplicateDeclarationPage : HttpRequestBuilder = {
    http("get the MRN check duplicate declaration details page")
      .get(s"$baseUrl/$route/single/check-duplicate-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct for the duplicate MRN"))
  }

  def postTheMRNCheckDuplicateDeclarationPage : HttpRequestBuilder = {
    http("post the MRN duplicate check declaration details page")
      .post(s"$baseUrl/$route/single/check-duplicate-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-commodity-details": String))
  }

  def getTheMRNEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("get the MRN enter commodity details page")
      .get(s"$baseUrl/$route/single/enter-commodity-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us the reason for this claim"))
  }

  def postTheMRNEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("post the MRN enter commodity details page")
      .post(s"$baseUrl/$route/single/enter-commodity-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-commodities-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/select-duties": String))
  }

  def getTheMRNSelectDutiesPage : HttpRequestBuilder = {
    http("get the MRN select duties page")
      .get(s"$baseUrl/$route/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))
  }

  def postTheMRNSelectDutiesPage : HttpRequestBuilder = {
    http("post the MRN select duties page")
      .post(s"$baseUrl/$route/single/select-duties": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A95")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/start-claim": String))
  }

  def getTheMRNStartClaimPage : HttpRequestBuilder = {
    http("get the MRN start claim page")
      .get(s"$baseUrl/$route/single/start-claim": String)
      .check(status.is(303))
      .check(header("Location").saveAs("action3"))
  }

  def getTheMRNEnterClaimPage : HttpRequestBuilder = {
    http("get the MRN enter claim page")
      .get(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .check(status.is(200))
      .check(regex("Claim details for A95 - Provisional Countervailing Duty"))
  }

  def postTheMRNEnterClaimPage : HttpRequestBuilder = {
    http("post the MRN enter claim page")
      .post(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim", "39")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-claim": String))
  }

  def getTheMRNCheckClaimPage : HttpRequestBuilder = {
    http("get the MRN check claim page")
      .get(s"$baseUrl/$route/single/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the reimbursement claim total for your MRN"))
  }

  def postTheMRNCheckClaimPage : HttpRequestBuilder = {
    http("post the MRN check claim page")
      .post(s"$baseUrl/$route/single/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/select-reimbursement-method": String))
  }

  def getSelectReimbursementMethodPage : HttpRequestBuilder = {
    http("get select reimbursement method page")
      .get(s"$baseUrl/$route/single/select-reimbursement-method": String)
      .check(status.is(200))
      .check(regex("Select reimbursement method"))
  }

  def postSelectReimbursementMethodPage: HttpRequestBuilder = {
    http("post select reimbursement method page")
      .post(s"$baseUrl/$route/single/select-reimbursement-method": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("reimbursement-method", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-these-bank-details-are-correct": String))
  }

  def getTheMRNCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("get the MRN check these bank details are correct page")
      .get(s"$baseUrl/$route/single/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))
  }

  def getTheMRNBankAccountTypePage : HttpRequestBuilder = {
    http("get the MRN bank account type")
      .get(s"$baseUrl/$route/single/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postTheMRNBankAccountTypePage : HttpRequestBuilder = {
    http("post the MRN bank account type")
      .post(s"$baseUrl/$route/single/bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-bank-account-details": String))
  }

  def getTheMRNEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("get the MRN enter bank account details page")
      .get(s"$baseUrl/$route/single/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter bank account details"))
  }

  def postTheMRNEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("post the MRN enter bank account details page")
      .post(s"$baseUrl/$route/single/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-details.account-name", "Halifax")
      .formParam("enter-bank-details.sort-code", "123456")
      .formParam("enter-bank-details.account-number", "23456789")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-these-bank-details-are-correct": String))
  }

  def postTheMRNCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("post the MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
      .check(status.is(200))
  }

}
