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
      .check(saveCsrfToken())
      .check(regex("Authority Wizard").exists)
      .check(regex("CredID").exists)
  }


  def loginWithAuthLoginStubMRN(eoriValue: String =  "", enrolmentKey: String = "", identifierName: String = "", identifierValue: String = ""): HttpRequestBuilder = {
    http("Login with user credentials")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("csrfToken", "${csrfToken}")
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
      .check(regex("Check if this is the correct EORI"))
  }

  def postTheMRNCheckEoriDetailsPage : HttpRequestBuilder = {
    http("post check eori details page")
      .post(s"$baseUrl/$route/check-eori-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-eori-details", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/select-number-of-claims": String))
  }

  def getTheSelectNumberOfClaimsPage : HttpRequestBuilder = {
    http("get the select number of claims page")
      .get(s"$baseUrl/$route/select-number-of-claims": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Select number of claims"))
  }

  def postTheSelectNumberOfClaimsPage : HttpRequestBuilder = {
    http("post the select number of claims page")
      .post(s"$baseUrl/$route/select-number-of-claims": String)
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
      .check(regex("What is your Movement Reference Number (MRN)?"))
  }

  def postTheMRNPage : HttpRequestBuilder = {
    http("post The MRN page")
      .post(s"$baseUrl/$route/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/importer-eori-entry": String))
  }

  def getTheMRNImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route/single/importer-eori-entry": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postTheMRNImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route/single/importer-eori-entry": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/declarant-eori-entry": String))
  }

  def getTheMRNDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route/single/declarant-eori-entry": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postTheMRNDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route/single/declarant-eori-entry": String)
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
      .check(regex("Check these details are correct"))
  }

  def postTheMRNCheckDeclarationPage : HttpRequestBuilder = {
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route/single/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "0")
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
      .check(header("Location").is(s"/$route/single/claimant-details": String))
  }

  def getTheMrnClaimantDetailsPage : HttpRequestBuilder = {
    http("get the MRN claimant details page")
      .get(s"$baseUrl/$route/single/claimant-details": String)
      .check(status.is(200))
      .check(regex("Check your details as registered with CDS"))
  }

  def getTheMRNEnterYourDetailsAsRegisteredCdsPage : HttpRequestBuilder = {
    http("get the MRN enter your details as registered with cds")
      .get(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your details as registered with CDS"))
  }

  def postTheMRNEnterYourDetailsAsRegisteredCdsPage : HttpRequestBuilder = {
    http("post the MRN enter your details as registered with cds")
      .post(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-as-registered-with-cds.individual-full-name", "IT Solutions LTD")
      .formParam("enter-claimant-details-as-registered-with-cds.individual-email", "someemail@mail.com")
      .formParam("nonUkAddress-line1", "19 Bricks Road")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Newcastle")
      .formParam("postcode", "NE12 5BT")
      .formParam("countryCode", "GB")
      .formParam("enter-claimant-details-as-registered-with-cds.add-company-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-your-contact-details": String))
  }

  def getTheMRNEnterYourContactDetailsPage : HttpRequestBuilder = {
    http("get the MRN enter your contact details page")
      .get(s"$baseUrl/$route/single/enter-your-contact-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your contact details"))
  }

  def postTheMRNEnterYourContactDetailsPage : HttpRequestBuilder = {
    http("post the MRN enter your contact details page")
      .post(s"$baseUrl/$route/single/enter-your-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-your-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-your-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-your-contact-details.contact-phone-number", "+4420723934397")
      .formParam("nonUkAddress-line1", "11 Mount Road")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "London")
      .formParam("postcode", "E10 7PP")
      .formParam("countryCode", "GB")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/claim-northern-ireland": String))
  }

  def getTheMRNClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("get the claim northern ireland page")
      .get(s"$baseUrl/$route/single/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your commodities (.*) imported or moved through Northern Ireland?"))
  }

  def postTheMRNClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("post the claim northern ireland page")
      .post(s"$baseUrl/$route/single/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "0")
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
      .check(regex("Check these details are correct"))
  }

  def postTheMRNCheckDuplicateDeclarationPage : HttpRequestBuilder = {
    http("post the MRN duplicate check declaration details page")
      .post(s"$baseUrl/$route/single/check-duplicate-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "0")
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
      .formParam("select-duties[]", "A80")
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
      .check(regex("Enter the claim amount for duty A80 - Definitive Anti-Dumping Duty"))
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
      .check(regex("Check the reimbursement claim totals for all MRNs"))
  }

  def postTheMRNCheckClaimPage : HttpRequestBuilder = {
    http("post the MRN check claim page")
      .post(s"$baseUrl/$route/single/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "0")
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

  def postTheMRNCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("post the MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
      .check(status.is(200))
  }

}
