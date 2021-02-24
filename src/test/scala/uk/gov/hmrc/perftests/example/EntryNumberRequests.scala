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

package uk.gov.hmrc.perftests.example

import io.gatling.core.Predef._
import io.gatling.core.action.builder.PauseBuilder
import io.gatling.core.check.CheckBuilder
import io.gatling.core.feeder.Random
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object EntryNumberRequests extends ServicesConfiguration {

    val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
    val route: String = "claim-for-reimbursement-of-import-duties"

    val authUrl: String = baseUrlFor("auth-login-stub")
    val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
    val redirect1 = s"$baseUrl/$route/start"
    val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[HttpCheck, Response, CharSequence, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

    def getAuthLoginPage : HttpRequestBuilder = {
      http("Navigate to auth login stub page")
        .get(s"$authUrl/auth-login-stub/gg-sign-in")
        .check(status.is(200))
        .check(saveCsrfToken())
        .check(regex("Authority Wizard").exists)
        .check(regex("CredID").exists)
    }


  def loginWithAuthLoginStub(enrolmentKey: String = "", identifierName: String = "", identifierValue: String = ""): HttpRequestBuilder = {
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

  def getCdsrStartPage1 : HttpRequestBuilder = {
    http("Get cdsr start page")
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(200))
      .check(regex("Claim for reimbursement of import duties").exists)
  }

  def getCdsrStartPage : HttpRequestBuilder = {
    http("post cdsr start page")
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(303))
  }

  def getstartMRNPage : HttpRequestBuilder = {
    http("get MRN page")
      .get(s"$baseUrl/$route/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      //.check(header("Location").is(s"$baseUrl/$route/enter-movement-reference-number": String))
  }

  def getMRNPage : HttpRequestBuilder = {
    http("get MRN page")
      .get(s"$baseUrl/$route/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What is your Movement Reference Number (MRN)?"))
  }

  def postMRNPage : HttpRequestBuilder = {
    http("post MRN page")
      .post(s"$baseUrl/$route/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "666541198B49856762")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-declaration-details": String))
  }

  def getEnterDeclarationDetails : HttpRequestBuilder = {
    http("get declaration details page")
      .get(s"$baseUrl/$route/enter-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter Declaration Details"))
  }

  def postEnterDeclarationDetails : HttpRequestBuilder = {
    http("post declaration details page")
      .post(s"$baseUrl/$route/enter-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declaration-details-day", "01")
      .formParam("enter-declaration-details-month", "01")
      .formParam("enter-declaration-details-year", "2020")
      .formParam("enter-declaration-details.place-of-import", "london")
      .formParam("enter-declaration-details.importer-name", "john")
      .formParam("enter-declaration-details.importer-email-address", "test@test.com")
      .formParam("enter-declaration-details.importer-phone-number", "0783635281")
      .formParam("enter-declaration-details.declarant-name", "steeve")
      .formParam("enter-declaration-details.declarant-email-address", "steeve@test.com")
      .formParam("enter-declaration-details.declarant-phone-number", "0783635281")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/who-is-the-declarant": String))
  }

  def getWhoIsDeclarantPage : HttpRequestBuilder = {
    http("get who is declarant page")
      .get(s"$baseUrl/$route/who-is-the-declarant": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Who is making this claim?"))
  }

  def postWhoIsDeclarantPage : HttpRequestBuilder = {
    http("post who is declarant page")
      .post(s"$baseUrl/$route/who-is-the-declarant": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-who-is-making-the-claim", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-claimant-details-as-individual": String))
  }

  def getEnterClaimantDetailsAsIndividualPage : HttpRequestBuilder = {
    http("get enter claimant details as individual page")
      .get(s"$baseUrl/$route/enter-claimant-details-as-individual": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your claimant details"))
  }

  def postEnterClaimantDetailsAsIndividualPage : HttpRequestBuilder = {
    http("post enter claimant details as individual page")
      .post(s"$baseUrl/$route/enter-claimant-details-as-individual": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-individual.importer-full-name", "James")
      .formParam("enter-claimant-details-individual.importer-email", "james@test.com")
      .formParam("enter-claimant-details-individual.importer-phone-number", "07836362762")
      .formParam("nonUkAddress-building", "26")
      .formParam("nonUkAddress-line1", "Wharry court")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "London")
      .formParam("nonUkAddress-line5", "")
      .formParam("postcode", "ne7 7ty")
      .formParam("countryCode-name", "Australia")
      .formParam("countryCode", "AU")
      .formParam("enter-claimant-details-individual.add-company-details", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-claimant-details-as-company": String))
  }

  def getEnterClaimantDetailsAsCompanyPage : HttpRequestBuilder = {
    http("get enter claimant details as company page")
      .get(s"$baseUrl/$route/enter-claimant-details-as-company": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your claimant details as an importer company"))
  }

  def postEnterClaimantDetailsAsCompanyPage : HttpRequestBuilder = {
    http("post enter claimant details as company")
      .post(s"$baseUrl/$route/enter-claimant-details-as-company": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-importer-company.importer-company-name", "Infotech")
      .formParam("enter-claimant-details-importer-company.importer-email", "test@test.com")
      .formParam("enter-claimant-details-importer-company.importer-phone-number", "00371790133")
      .formParam("nonUkAddress-building", "39")
      .formParam("nonUkAddress-line1", "street")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Denver")
      .formParam("nonUkAddress-line5", "")
      .formParam("nonUkAddress-line6", "")
      .formParam("countryCode-name", "Italy")
      .formParam("countryCode", "IT")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-reason-for-claim-and-basis": String))
  }

  def getEnterReasonForClaimAndBasisPage : HttpRequestBuilder = {
    http("get enter reason for claim and basis page")
      .get(s"$baseUrl/$route/enter-reason-for-claim-and-basis": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the reason and or basis for claim"))
  }

  def postEnterReasonForClaimAndBasisPage : HttpRequestBuilder = {
    http("post enter reason for claim and basis page")
      .post(s"$baseUrl/$route/enter-reason-for-claim-and-basis": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-reason-and-basis-for-claim.basis", "2")
      .formParam("select-reason-and-basis-for-claim.reason", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-commodity-details": String))
  }

  def getEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("get enter commodity details page")
      .get(s"$baseUrl/$route/enter-commodity-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter details of the commodities you would like reimbursing for"))
  }

  def postEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("post enter commodity details page")
      .post(s"$baseUrl/$route/enter-commodity-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-commodities-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-uk-duty-amounts": String))
  }

  def getEnterUkDutyAmountsPage : HttpRequestBuilder = {
    http("get enter uk duty amounts page")
      .get(s"$baseUrl/$route/enter-uk-duty-amounts": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter UK Duty Amounts to Calculate Your Claim"))
  }

  def postEnterUkDutyAmountsPage : HttpRequestBuilder = {
    http("post enter uk duty amounts page")
      .post(s"$baseUrl/$route/enter-uk-duty-amounts": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-duty-and-claim-amounts[0].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[0].paid", "1000")
      .formParam("enter-duty-and-claim-amounts[0].claim", "100")
      .formParam("enter-duty-and-claim-amounts[1].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[1].paid", "")
      .formParam("enter-duty-and-claim-amounts[1].claim", "")
      .formParam("enter-duty-and-claim-amounts[2].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[2].paid", "")
      .formParam("enter-duty-and-claim-amounts[2].claim", "")
      .formParam("enter-duty-and-claim-amounts[3].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[3].paid", "")
      .formParam("enter-duty-and-claim-amounts[3].claim", "")
      .formParam("enter-duty-and-claim-amounts[4].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[4].paid", "")
      .formParam("enter-duty-and-claim-amounts[4].claim", "")
      .formParam("enter-duty-and-claim-amounts[5].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[5].paid", "")
      .formParam("enter-duty-and-claim-amounts[5].claim", "")
      .formParam("enter-duty-and-claim-amounts[6].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts[6].paid", "")
      .formParam("enter-duty-and-claim-amounts[6].claim", "")
      .formParam("enter-duty-and-claim-amounts.make-eu-duty-claim", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-eu-duty-amounts": String))
  }

  def getEnterEuDutyAmountsPage : HttpRequestBuilder = {
    http("get enter eu duty amounts page")
      .get(s"$baseUrl/$route/enter-eu-duty-amounts": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter EU duty amounts to calculate your claim"))
  }

  def postEnterEuDutyAmountsPage : HttpRequestBuilder = {
    http("post enter eu duty amounts page")
      .post(s"$baseUrl/$route/enter-eu-duty-amounts": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-duty-and-claim-amounts-eu[0].tax-code", "0")
      .formParam("enter-duty-and-claim-amounts-eu[0].paid", "1000")
      .formParam("enter-duty-and-claim-amounts-eu[0].claim", "10")
      .formParam("enter-duty-and-claim-amounts-eu[1].tax-code", "1")
      .formParam("enter-duty-and-claim-amounts-eu[1].paid", "")
      .formParam("enter-duty-and-claim-amounts-eu[1].claim", "")
      .formParam("enter-duty-and-claim-amounts-eu[2].tax-code", "2")
      .formParam("enter-duty-and-claim-amounts-eu[2].paid", "")
      .formParam("enter-duty-and-claim-amounts-eu[2].claim", "")
      .formParam("enter-duty-and-claim-amounts-eu[3].tax-code", "3")
      .formParam("enter-duty-and-claim-amounts-eu[3].paid", "")
      .formParam("enter-duty-and-claim-amounts-eu[3].claim", "")
      .formParam("enter-duty-and-claim-amounts-eu[4].tax-code", "4")
      .formParam("enter-duty-and-claim-amounts-eu[4].paid", "")
      .formParam("enter-duty-and-claim-amounts-eu[4].claim", "")
      .formParam("enter-duty-and-claim-amounts-eu[5].tax-code", "5")
      .formParam("enter-duty-and-claim-amounts-eu[5].paid", "")
      .formParam("enter-duty-and-claim-amounts-eu[5].claim", "")
      .formParam("enter-duty-and-claim-amounts-eu[6].tax-code", "6")
      .formParam("enter-duty-and-claim-amounts-eu[6].paid", "")
      .formParam("enter-duty-and-claim-amounts-eu[6].claim", "")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-reimbursement-claim-total": String))
  }

  def getCheckReimbursementClaimTotalPage : HttpRequestBuilder = {
    http("get check reimbursement claim total page")
      .get(s"$baseUrl/$route/check-reimbursement-claim-total": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Your Reimbursement Claim Total"))
  }

  def postCheckReimbursementClaimTotalPage : HttpRequestBuilder = {
    http("post check reimbursement claim total page")
      .post(s"$baseUrl/$route/check-reimbursement-claim-total": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/enter-bank-account-details": String))
  }

  def getEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("get enter bank account details page")
      .get(s"$baseUrl/$route/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter bank account details"))
  }

  def PostEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("post enter bank account details page")
      .post(s"$baseUrl/$route/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-details.account-name", "NatWest")
      .formParam("enter-bank-details.is-business-account[]", "0")
      .formParam("enter-bank-details-sort-code-1", "02")
      .formParam("enter-bank-details-sort-code-2", "19")
      .formParam("enter-bank-details-sort-code-3", "45")
      .formParam("enter-bank-details.account-number", "12345678")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/supporting-evidence/upload-supporting-evidence": String))
  }

  def getUploadSupportEvidencePage : HttpRequestBuilder = {
    http("get upload support evidence page")
      .get(s"$baseUrl/$route/supporting-evidence/upload-supporting-evidence": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("""action="(.*)"""").saveAs("action"))
      .check(regex("""supporting-evidence/scan-progress/(.*)">""").saveAs("action1"))
      .check(regex("Upload files to support your claim"))
  }

  def postUploadSupportEvidencePage(fileType : String) : HttpRequestBuilder = {

    val cdsrheaders = Map (
      "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
      "Content-type" -> "multipart/form-data; boundary=----WebKitFormBoundarykkh211cVUdtCgAae")

    val file = System.getProperty("user.dir") + "/src/test/resources/data/" + fileType

    http("post upload support evidence page")
      .post("${action}": String)
      .formParam("csrfToken", "${csrfToken}")
      .formUpload("file", file)
      .headers(cdsrheaders)
      .check(status.is(303))
  }

  def getScanProgressWaitPage : HttpRequestBuilder = {
    Thread.sleep(4)
    http("get scan progress wait page")
      .get(s"$baseUrl/$route/upload-supporting-evidence/scan-progress/{action1}": String)
      .check(status.is(200))
      .check(regex("Wait a few seconds and then select ‘continue’"))
  }

  def constPause = new PauseBuilder(60 seconds, None)


  def postScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl/$route/upload-supporting-evidence/scan-progress/{action1}": String)
      .check(status.is(303))
      .check(header("Location").is(s"/$route/upload-supporting-evidence/scan-progress?id={action1}": String))
  }

  def postScanProgressWaitPage1 : HttpRequestBuilder = {
    http(" post scan  progress wait page1")
      .post(s"$baseUrl/$route/upload-supporting-evidence/scan-progress?id={action1}": String)
      .check(status.is(303))
      .check(header("Location").is(s"/$route/upload-supporting-evidence/scan-progress/{action1}": String))
  }

  def getSelectSupportingEvidencePage : HttpRequestBuilder = {
    http("get select supporting evidence page")
      .get(s"$baseUrl/$route/upload-supporting-evidence/select-supporting-evidence-type/{action1}": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the description of the file you just uploaded"))
  }

  def postSelectSupportingEvidencePage : HttpRequestBuilder = {
    http("post select supporting evidence page")
      .post(s"$baseUrl/$route/upload-supporting-evidence/select-supporting-evidence-type/{action1}": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.choose-document-type", "3")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/supporting-evidence/check-your-answers": String))
  }

  def getCheckYourAnswersPage : HttpRequestBuilder = {
    http("get check your answers page")
      .get(s"$baseUrl/$route/supporting-evidence/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Confirm these are the files you want to submit"))
  }

  def postCheckYourAnswersPage : HttpRequestBuilder = {
    http("post check your answers page")
      .post(s"$baseUrl/$route/supporting-evidence/check-your-answers": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-answers-accept-send": String))
  }

  def getReviewYourClaimPage : HttpRequestBuilder = {
    http("get check answers and send page")
      .get(s"$baseUrl/$route/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Review your claim"))
  }

  def postReviewYourClaimPage : HttpRequestBuilder = {
    http("post check answers and send page")
      .post(s"$baseUrl/$route/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-your-answers.declaration[]", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-answers-accept-send": String)) // ToDo - need to change url
  }
 //Todo Submitted page
  def getSubmittedPage : HttpRequestBuilder = {
    http(("get submitted page"))
      .get(s"$baseUrl/$route/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim submitted"))
  }
  //Todo post submitted page
  def postSubmittedPage : HttpRequestBuilder = {
    http("post submitted page")
      .post(s"$baseUrl/$route/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      //.formParam("check-your-answers.declaration[]", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-answers-accept-send": String)) // ToDo - need to change url
  }

  //Todo signout page
  //Todo feedback survey page

  // Todo- check pages
  //wait page  - done
  // scan progress page - done
  // description page - done
  // check your answers - done
  // review and claim - done
  //submitted page - not yet

}
