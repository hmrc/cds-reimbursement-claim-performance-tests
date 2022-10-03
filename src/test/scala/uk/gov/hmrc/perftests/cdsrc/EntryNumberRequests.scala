/*
 * Copyright 2022 HM Revenue & Customs
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
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object EntryNumberRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/overpayments"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getCdsrStartPage1: HttpRequestBuilder =
    http("Get cdsr start page")
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(200))
      .check(regex("Claim for reimbursement of import duties").exists)

  def getCdsrStartPage: HttpRequestBuilder =
    http("post cdsr start page")
      .get(s"$baseUrl/$route/start": String)
      .check(status.is(303))

  def getCheckEoriDetailsPage: HttpRequestBuilder =
    http("get check eori details page")
      .get(s"$baseUrl/$route/check-eori-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check if this is the correct EORI"))

  def postCheckEoriDetailsPage: HttpRequestBuilder =
    http("post check eori details page")
      .post(s"$baseUrl/$route/check-eori-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-eori-details", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/select-number-of-claims": String))

  def getSelectNumberOfClaimsPage: HttpRequestBuilder =
    http("get the select number of claims page")
      .get(s"$baseUrl/$route/select-number-of-claims": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Select number of claims"))

  def postSelectNumberOfClaimsPage: HttpRequestBuilder =
    http("post the select number of claims page")
      .post(s"$baseUrl/$route/select-number-of-claims": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-number-of-claims", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-movement-reference-number": String))

  def getStartMRNPage: HttpRequestBuilder =
    http("get MRN page")
      .get(s"$baseUrl/$route/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))

  def getMRNPage: HttpRequestBuilder =
    http("get MRN page")
      .get(s"$baseUrl/$route/single/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("What is your Movement Reference Number (MRN)?"))

  def postMRNPage: HttpRequestBuilder =
    http("post MRN page")
      .post(s"$baseUrl/$route/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "666541198B49856762")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-declaration-details": String))

  def getEnterDeclarationDetails: HttpRequestBuilder =
    http("get declaration details page")
      .get(s"$baseUrl/$route/single/enter-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter declaration details"))

  def postEnterDeclarationDetails: HttpRequestBuilder =
    http("post declaration details page")
      .post(s"$baseUrl/$route/single/enter-declaration-details": String)
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
      .check(header("Location").is(s"/$route/single/who-is-the-declarant": String))

  def getWhoIsDeclarantPage: HttpRequestBuilder =
    http("get who is declarant page")
      .get(s"$baseUrl/$route/single/who-is-the-declarant": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Who is making this claim?"))

  def postWhoIsDeclarantPage: HttpRequestBuilder =
    http("post who is declarant page")
      .post(s"$baseUrl/$route/single/who-is-the-declarant": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-who-is-making-the-claim", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-your-details-as-registered-with-cds": String))

  def getEnterYourDetailsAsRegisteredWithCdsPage: HttpRequestBuilder =
    http("get enter your details as registered with cds page")
      .get(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your details as registered with CDS"))

  def postEnterYourDetailsAsRegisteredWithCdsPage: HttpRequestBuilder =
    http("post enter your details as registered with cds page")
      .post(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-as-registered-with-cds.individual-full-name", "James")
      .formParam("enter-claimant-details-as-registered-with-cds.individual-email", "james@test.com")
      .formParam("nonUkAddress-line1", "Wharry court")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "London")
      .formParam("postcode", "ne7 7ty")
      .formParam("countryCode", "GB")
      .formParam("enter-claimant-details-as-registered-with-cds.add-company-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-your-contact-details": String))

  def getEnterYourContactDetailsPage: HttpRequestBuilder =
    http("get enter your contact details page")
      .get(s"$baseUrl/$route/single/enter-your-contact-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your contact details"))

  def postEnterYourContactDetailsPage: HttpRequestBuilder =
    http("post enter your contact details page")
      .post(s"$baseUrl/$route/single/enter-your-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-your-contact-details.contact-name", "Infotech")
      .formParam("enter-your-contact-details.contact-email", "test@test.com")
      .formParam("enter-your-contact-details.contact-phone-number", "00371790133")
      .formParam("nonUkAddress-line1", " 39 street")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Denver")
      .formParam("postcode", "cv4 4ah")
      .formParam("countryCode", "IT")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-reason-for-claim-and-basis": String))

  def getEnterReasonForClaimAndBasisPage: HttpRequestBuilder =
    http("get enter reason for claim and basis page")
      .get(s"$baseUrl/$route/single/enter-reason-for-claim-and-basis": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the reason and or basis for claim"))

  def postEnterReasonForClaimAndBasisPage: HttpRequestBuilder =
    http("post enter reason for claim and basis page")
      .post(s"$baseUrl/$route/single/enter-reason-for-claim-and-basis": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-reason-and-basis-for-claim.basis", "2")
      .formParam("select-reason-and-basis-for-claim.reason", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-commodity-details": String))

  def getEnterCommodityDetailsPage: HttpRequestBuilder =
    http("get enter commodity details page")
      .get(s"$baseUrl/$route/single/enter-commodity-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us the reason for this claim"))

  def postEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post enter commodity details page")
      .post(s"$baseUrl/$route/single/enter-commodity-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-commodities-details", "phones")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/select-duties": String))

  def getSelectDutiesPage: HttpRequestBuilder =
    http("get select duties page")
      .get(s"$baseUrl/$route/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))

  def postSelectDutiesPage: HttpRequestBuilder =
    http("post select duties page")
      .post(s"$baseUrl/$route/single/select-duties": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/start-claim": String))

  def getStartClaimPage: HttpRequestBuilder =
    http("get start claim page")
      .get(s"$baseUrl/$route/single/start-claim": String)
      .check(status.is(303))
      .check(header("Location").saveAs("action3"))

  def getEnterClaimPage: HttpRequestBuilder =
    http("get enter claim page")
      .get { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .check(status.is(200))
      .check(regex("Enter the claim amount for duty A00 - Customs Duty"))

  def postEnterClaimPage: HttpRequestBuilder =
    http("post enter claim page")
      .post { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim.paid-amount", "1000")
      .formParam("enter-claim.claim-amount", "123")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-claim": String))

  def getCheckClaimPage: HttpRequestBuilder =
    http("get check claim page")
      .get(s"$baseUrl/$route/single/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the repayment claim totals for all MRNs"))

  def postCheckClaimPage: HttpRequestBuilder =
    http("post check claim page")
      .post(s"$baseUrl/$route/single/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-bank-account-details": String))

  def getEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get enter bank account details page")
      .get(s"$baseUrl/$route/single/enter-bank-account-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter bank account details"))

  def PostEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post enter bank account details page")
      .post(s"$baseUrl/$route/single/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-details.account-name", "NatWest")
      .formParam("enter-bank-details[]", "true")
      .formParam("enter-bank-details.sort-code", "123456")
      .formParam("enter-bank-details.account-number", "12345678")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/supporting-evidence/upload-supporting-evidence": String))

  def getUploadDocumentsChooseFilePage: HttpRequestBuilder =
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
      .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))
      //.check(regex("""supporting-evidence/scan-progress/(.*)">""").saveAs("action1"))
      .check(regex("Add documents to support your claim"))

  def postUploadDocumentsChoosefilesPage: HttpRequestBuilder =
    http("post upload documents choose file page")
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

  def getScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.in(303, 200))

  def constPause = new PauseBuilder(60 seconds, None)

  def postScanProgressWaitPage: HttpRequestBuilder =
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))

  def postScanProgressWaitPage1: List[ActionBuilder] =
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(
        http(" post scan progressing wait page1")
          .get(s"$baseUrl" + "${scanPage}")
          .check(status.in(303, 200))
          .check(header("Location").optional.saveAs("selectPage"))
      )
    ).actionBuilders

  def getUploadCustomsDocumentsPage: HttpRequestBuilder =
    http("get upload customs documents page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents")
      .check(status.is(303))

  def getUploadCustomsDocumentsChooseFilesPage =
    http("get upload customs documents choose files page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-files")
      .check(status.is(200))
  // .check(headerRegex("Set-Cookie", """mdtp=(.*)""").saveAs("mdtpCookie"))

  def getUploadCustomsDocumentsChooseFilePage: HttpRequestBuilder =
    http("get upload customs documents choose files page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-file": String)
      .check(status.is(200))
      .check(regex("Add documents to support your claim"))

  def getFileVerificationStatusPage: List[ActionBuilder] =
    asLongAs(session => session("fileStatus").asOption[String].forall(s => s == "WAITING" || s == "NOT_UPLOADED"))(
      pause(1.second).exec(
        http("get the file verification status page")
          .get(s"$baseUrlUploadCustomsDocuments" + "${fileVerificationUrl}")
          .check(status.is(200))
          .check(jsonPath("$.fileStatus").in("WAITING", "ACCEPTED", "NOT_UPLOADED").saveAs("fileStatus"))
      )
    ).actionBuilders

  def getFileVerificationStatusPage1: List[ActionBuilder] =
    asLongAs(session => session("fileStatus1").asOption[String].forall(s => s == "WAITING" || s == "NOT_UPLOADED"))(
      pause(1.second).exec(
        http("get the file verification status page")
          .get(s"$baseUrlUploadCustomsDocuments" + "${fileVerificationUrl}")
          .check(status.is(200))
          .check(jsonPath("$.fileStatus").in("WAITING", "ACCEPTED", "NOT_UPLOADED").saveAs("fileStatus1"))
      )
    ).actionBuilders

  def getFileVerifyBody: HttpRequestBuilder =
    http("get page")
      .get { session =>
        val Location = session.attributes("fileVerificationUrl")
        s"$baseUrl$Location"

      }
      .check(jsonPath("$.fileStatus").is("NOT_UPLOADED"))

  def getUploadDocumentsSummaryPage: HttpRequestBuilder =
    http("get upload documents summary page")
      .get(s"$baseUrl/$route/upload-documents/summary": String)
      .check(status.is(303))
      //.check(regex("You(.*)added 1 document to your claim"))

  def postUploadDocumentsSummaryPage: HttpRequestBuilder =
    http("post upload documents summary page")
      .post(s"$baseUrl/$route/upload-documents/summary": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choice", "no")
      .check(status.is(303))
      .check(header("Location").is(s"/upload-customs-documents/continue-to-host": String))

  def getCheckAnswersAcceptSendPage: HttpRequestBuilder =
    http("get check answers and send page")
      .get(s"$baseUrl/$route1/single/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))

  def postCheckAnswersAcceptSendPage: HttpRequestBuilder =
    http("post check answers and send page")
      .post(s"$baseUrl/$route1/single/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claim-submitted": String))

  def getClaimSubmittedPage: HttpRequestBuilder =
    http("get submitted page")
      .get(s"$baseUrl/$route1/single/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))

}
