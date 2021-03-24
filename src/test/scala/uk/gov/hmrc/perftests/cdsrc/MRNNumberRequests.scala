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

object MRNNumberRequests extends ServicesConfiguration with RequestUtils {

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


  def loginWithAuthLoginStubMRN(enrolmentKey: String = "", identifierName: String = "", identifierValue: String = ""): HttpRequestBuilder = {
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
      .formParam("additionalInfo.emailVerified", "")
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
      .formParam("enrolment[0].taxIdentifier[0].value", "AA12345678901234Z")
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
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(303))
  }

  def getTheMRNPage : HttpRequestBuilder = {
    http("get The MRN page")
      .get(s"$baseUrl/$route/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What is your Movement Reference Number (MRN)?"))
  }

  def postTheMRNPage : HttpRequestBuilder = {
    http("post The MRN page")
      .post(s"$baseUrl/$route/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/importer-eori-entry": String))
  }

  def getTheMRNImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route/importer-eori-entry": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postTheMRNImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route/importer-eori-entry": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/declarant-eori-entry": String))
  }

  def getTheMRNDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route/declarant-eori-entry": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postTheMRNDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route/declarant-eori-entry": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-declaration-details": String))
  }

  def getTheMRNCheckDeclarationPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these details are correct"))
  }

  def postTheMRNCheckDeclarationPage : HttpRequestBuilder = {
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/who-is-the-declarant": String))
  }

  def getTheMRNWhoIsDeclarantPage : HttpRequestBuilder = {
    http("get the MRN who is declarant page")
      .get(s"$baseUrl/$route/who-is-the-declarant": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Who is making this claim?"))
  }

  def postTheMRNWhoIsDeclarantPage : HttpRequestBuilder = {
    http("post the MRN who is declarant page")
      .post(s"$baseUrl/$route/who-is-the-declarant": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-who-is-making-the-claim", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-claimant-details-as-individual": String))
  }

  def getTheMRNEnterClaimantDetailsAsIndividualPage : HttpRequestBuilder = {
    http("get the MRN enter claimant details as individual page ")
      .get(s"$baseUrl/$route/enter-claimant-details-as-individual": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your claimant details as an individual"))
  }

  def postTheMRNEnterClaimantDetailsAsIndividualPage : HttpRequestBuilder = {
    http("post the MRN enter claimant details as individual page")
      .post(s"$baseUrl/$route/enter-claimant-details-as-individual": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-individual.individual-full-name", "Automation Central LTD")
      .formParam("enter-claimant-details-individual.individual-email", "someemail@mail.com")
      .formParam("enter-claimant-details-individual.individual-phone-number", "+4420723934397")
      .formParam("nonUkAddress-line1", "10 Automation Road")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Coventry")
      .formParam("postcode", "CV3 6EA")
      .formParam("countryCode", "GB")
      .formParam("enter-claimant-details-individual.add-company-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-claimant-details-as-company": String))
  }

  def getTheMRNEnterClaimantDetailsAsCampanyPage : HttpRequestBuilder = {
    http("get the MRN enter claimant details as company page")
      .get(s"$baseUrl/$route/enter-claimant-details-as-company": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your claimant details as an importer company"))
  }

  def postTheMRNEnterClaimantDetailsAsCampanyPage : HttpRequestBuilder = {
    http("post the MRN enter claimant details as company page")
      .post(s"$baseUrl/$route/enter-claimant-details-as-company": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-importer-company.importer-company-name", "Automation Central LTD")
      .formParam("enter-claimant-details-importer-company.importer-email", "someemail@mail.com")
      .formParam("enter-claimant-details-importer-company.importer-phone-number", "+4420723934397")
      .formParam("nonUkAddress-line1", "10 Automation Road")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Coventry")
      .formParam("postcode", "CV3 6EA")
      .formParam("countryCode", "GB")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/choose-basis-for-claim": String))
  }

  def getTheMRNChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("get the MRN choose basis of claim page")
      .get(s"$baseUrl/$route/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the basis for claim"))
  }

  def postTheMRNChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("post the MRN choose basis of claim page")
      .post(s"$baseUrl/$route/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "3")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-commodity-details": String))
  }

  def getTheMRNEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("get the MRN enter commodity details page")
      .get(s"$baseUrl/$route/enter-commodity-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter details of the commodities you would like reimbursing for"))
  }

  def postTheMRNEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("post the MRN enter commodity details page")
      .post(s"$baseUrl/$route/enter-commodity-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-commodities-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/select-duties": String))
  }

  def getTheMRNSelectDutiesPage : HttpRequestBuilder = {
    http("get the MRN select duties page")
      .get(s"$baseUrl/$route/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))
  }

  def postTheMRNSelectDutiesPage : HttpRequestBuilder = {
    http("post the MRN select duties page")
      .post(s"$baseUrl/$route/select-duties": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[0]", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/start-claim": String))
  }

  def getTheMRNStartClaimPage : HttpRequestBuilder = {
    http("get the MRN start claim page")
      .get(s"$baseUrl/$route/start-claim": String)
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
      .check(header("Location").is(s"/$route/check-claim": String))
  }

  def getTheMRNCheckClaimPage : HttpRequestBuilder = {
    http("get the MRN check claim page")
      .get(s"$baseUrl/$route/check-claim": String)
      .check(status.is(200))
      .check(regex("Your reimbursement claim totals"))
  }

  def postTheMRNCheckClaimPage : HttpRequestBuilder = {
    http("post the MRN check claim page")
      .post(s"$baseUrl/$route/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-these-bank-details-are-correct": String))
  }

  def getTheMRNCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("get the MRN check these bank details are correct page")
      .get(s"$baseUrl/$route/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))
  }

  def postTheMRNCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("post the MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
//      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(200))
      //.check(header("Location").is(s"/$route/supporting-evidence/upload-supporting-evidence": String))
  }






































}
