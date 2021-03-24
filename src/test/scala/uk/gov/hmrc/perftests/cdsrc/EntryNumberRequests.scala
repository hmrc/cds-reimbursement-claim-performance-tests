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
import io.gatling.core.action.builder.{ActionBuilder, PauseBuilder}
import io.gatling.core.check.CheckBuilder
import io.gatling.core.feeder.Random
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object EntryNumberRequests extends ServicesConfiguration with RequestUtils {

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

  def getStartMRNPage : HttpRequestBuilder = {
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
      .check(regex("Enter declaration details"))
  }

  def postEnterDeclarationDetails : HttpRequestBuilder = {
    http("post declaration details page")
      .post(s"$baseUrl/$route/enter-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declaration-details.day", "01")
      .formParam("enter-declaration-details.month", "01")
      .formParam("enter-declaration-details.year", "2020")
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
      .formParam("enter-claimant-details-individual.individual-full-name", "James")
      .formParam("enter-claimant-details-individual.individual-email", "james@test.com")
      .formParam("enter-claimant-details-individual.individual-phone-number", "07836362762")
      .formParam("nonUkAddress-line1", "Wharry court")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "London")
      .formParam("postcode", "ne7 7ty")
      //.formParam("countryCode-name", "Australia")
      .formParam("countryCode", "AU")
      .formParam("enter-claimant-details-individual.add-company-details", "true")
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
      .formParam("nonUkAddress-line1", " 39 street")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Denver")
      .formParam("postcode", "cv4 4ah")
      //.formParam("countryCode-name", "Italy")
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
      .check(header("Location").is(s"/$route/select-duties": String))
  }

  def getSelectDutiesPage : HttpRequestBuilder = {
    http("get select duties page")
      .get(s"$baseUrl/$route/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))
  }

  def postSelectDutiesPage : HttpRequestBuilder = {
    http("post select duties page")
      .post(s"$baseUrl/$route/select-duties": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[0]", "0")
      //.formParam("select-duties[1]", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/start-claim": String))

  }

  def getStartClaimPage : HttpRequestBuilder = {
    http("get start claim page")
      .get(s"$baseUrl/$route/start-claim": String)
      .check(status.is(303))
      //.check(regex("""form action="(.*)" method""").saveAs("action3"))
      .check(header("Location").saveAs("action3"))
     // .check(regex("""/claim-for-reimbursement-of-import-duties/enter-claim/(.*)">""").saveAs("action4"))
  }

  def getEnterClaimPage : HttpRequestBuilder = {
    http("get enter claim page")
      .get(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      //.get(s"$baseUrl/{action3}": String)
      //.check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the claim amount for duty A00 - Customs Duty"))
     // .check(header("Location").saveAs("action4"))
    // .check(regex("""/claim-for-reimbursement-of-import-duties/enter-claim/(.*)">""").saveAs("action4"))
  }

  def postEnterClaimPage : HttpRequestBuilder = {
    http("post enter claim page")
      .post(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim.paid-amount", "1000")
      .formParam("enter-claim.claim-amount", "123")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/check-claim": String))
  }

  def getCheckClaimPage : HttpRequestBuilder = {
    http("get check claim page")
      .get(s"$baseUrl/$route/check-claim": String)
      //.check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Your reimbursement claim totals"))

  }

  def postCheckClaimPage : HttpRequestBuilder = {
    http("post check claim page")
      .post(s"$baseUrl/$route/check-claim": String)
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
      .formParam("enter-bank-details[]", "true")
      .formParam("enter-bank-details.sort-code", "123456")
      .formParam("enter-bank-details.account-number", "12345678")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/supporting-evidence/upload-supporting-evidence": String))
  }

  def getUploadSupportEvidencePage : HttpRequestBuilder = {
    http("get upload support evidence page")
      .get(s"$baseUrl/$route/supporting-evidence/upload-supporting-evidence": String)
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
      .check(regex("Upload files to support your claim"))
  }

  def postUploadSupportEvidencePage : HttpRequestBuilder = {
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
      .bodyPart(RawFileBodyPart("file", "data/validFile.png"))
      //              alternative way to upload file:
      //                .bodyPart(RawFileBodyPart("file", "data/NewArrangement.xml")
      //                .fileName("NewArrangement.xml")
      //                .transferEncoding("binary"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  }

  def getScanProgressWaitPage : HttpRequestBuilder = {
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Wait a few seconds and then select ‘continue’"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def constPause = new PauseBuilder(60 seconds, None)


  def postScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postScanProgressWaitPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }


  def getSelectSupportingEvidencePage : HttpRequestBuilder = {
    http("get select supporting evidence page")
      .get(s"$baseUrl" + "${selectPage}")
      .check(status.is(200))
      .check(regex("Select the description of the file you just uploaded"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))
  }

  def postSelectSupportingEvidencePage : HttpRequestBuilder = {
    http("post select supporting evidence page")
      .post(s"$baseUrl" + "${supportEvidencePageType}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.choose-document-type", "5")
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

  def getCheckAnswersAcceptSendPage : HttpRequestBuilder = {
    http("get check answers and send page")
      .get(s"$baseUrl/$route/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your application"))
  }

  def postCheckAnswersAcceptSendPage : HttpRequestBuilder = {
    http("post check answers and send page")
      .post(s"$baseUrl/$route/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/claim-submitted": String))
  }

  def getClaimSubmittedPage : HttpRequestBuilder = {
    http(("get submitted page"))
      .get(s"$baseUrl/$route/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
  }

  //Todo signOut page
  //Todo feedback survey page
}
