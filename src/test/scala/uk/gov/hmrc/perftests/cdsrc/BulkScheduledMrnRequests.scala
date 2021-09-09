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
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object BulkScheduledMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-for-reimbursement-of-import-duties"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[HttpCheck, Response, CharSequence, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def postBulkScheduledSelectNumberOfClaimsPage : HttpRequestBuilder = {
    http("post the select number of claims page")
      .post(s"$baseUrl/$route/select-number-of-claims": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-number-of-claims", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/enter-movement-reference-number": String))
  }

  def getBulkScheduledMrnPage : HttpRequestBuilder = {
    http("get Scheduled MRN page")
      .get(s"$baseUrl/$route/scheduled/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the lead Movement Reference Number (.*)"))
  }

  def postBulkScheduledMrnPage : HttpRequestBuilder = {
    http("post Scheduled MRN page")
      .post(s"$baseUrl/$route/scheduled/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/check-declaration-details": String))
  }

  def getBulkScheduledMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("get Scheduled MRN check declaration details page")
      .get(s"$baseUrl/$route/scheduled/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these details are correct"))
  }

  def postBulkScheduledMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("post Scheduled MRN check declaration details page")
      .post(s"$baseUrl/$route/scheduled/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/scheduled-document-upload/upload": String))
  }

  def getScheduledDocumentUploadPage : HttpRequestBuilder = {
    http("get scheduled document upload page")
      .get(s"$baseUrl/$route/scheduled/scheduled-document-upload/upload": String)
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
      .check(regex("Add a scheduled document"))
  }

  def postScheduledDocumentUploadPage : HttpRequestBuilder = {
    http("post scheduled document upload page")
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

  def getScheduledDocumentUploadProgressPage : HttpRequestBuilder = {
    http("get upload progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postScheduledDocumentUploadProgressPage : HttpRequestBuilder = {
    http("post upload progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postScheduledDocumentUploadProgressPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }

  def getScheduledDocumentUploadReviewPage : HttpRequestBuilder = {
    http("get scheduled document upload review page")
      .get(s"$baseUrl" + "${selectPage}")
      .check(status.is(200))
      .check(regex("You have successfully uploaded a scheduled document"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))
  }

  def postScheduledDocumentUploadReviewPage : HttpRequestBuilder = {
    http("post scheduled document upload review page")
      .post(s"$baseUrl" + "${supportEvidencePageType}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/who-is-the-declarant": String))
  }


  def getScheduledMrnWhoIsDeclarantPage : HttpRequestBuilder = {
    http("get Scheduled MRN who is declarant page")
      .get(s"$baseUrl/$route/scheduled/who-is-the-declarant": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Who is making this claim?"))
  }

  def postScheduledMrnWhoIsDeclarantPage : HttpRequestBuilder = {
    http("post Scheduled MRN who is declarant page")
      .post(s"$baseUrl/$route/scheduled/who-is-the-declarant": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-who-is-making-the-claim", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/claimant-details": String))
  }

  def getClaimantDetailsPage : HttpRequestBuilder = {
    http("get scheduled MRN claimant details page")
      .get(s"$baseUrl/$route/scheduled/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your details as registered with CDS"))
  }

  def getDetailsContactPage : HttpRequestBuilder = {
    http("get scheduled MRN details contact page")
      .get(s"$baseUrl/$route/scheduled/claimant-details/details-contact": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Change contact details"))
  }

  def postDetailsContactPage : HttpRequestBuilder = {
    http("post scheduled MRN details contact page")
      .post(s"$baseUrl/$route/scheduled/claimant-details/details-contact": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/claimant-details": String))
  }

  def postChangeClaimantDetailsPage : HttpRequestBuilder = {
    http("post change claimant details page")
      .post(s"$baseUrl/$route/scheduled/change-claimant-details":String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claimant-details", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/claim-northern-ireland": String))
  }

  //  def getBulkScheduledMrnEnterYourDetailsAsRegisteredCdsPage : HttpRequestBuilder = {
//    http("get Scheduled MRN enter your details as registered with cds")
//      .get(s"$baseUrl/$route/single/claimant-details": String)
//      .check(saveCsrfToken())
//      .check(status.is(200))
//      .check(regex("Enter your details as registered with CDS"))
//  }
//
//  def postBulkScheduledMrnEnterYourDetailsAsRegisteredCdsPage : HttpRequestBuilder = {
//    http("post Scheduled MRN enter your details as registered with cds")
//      .post(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
//      .formParam("csrfToken", "${csrfToken}")
//      .formParam("enter-claimant-details-as-registered-with-cds.individual-full-name", "IT Solutions LTD")
//      .formParam("enter-claimant-details-as-registered-with-cds.individual-email", "someemail@mail.com")
//      .formParam("nonUkAddress-line1", "19 Bricks Road")
//      .formParam("nonUkAddress-line2", "")
//      .formParam("nonUkAddress-line3", "")
//      .formParam("nonUkAddress-line4", "Newcastle")
//      .formParam("postcode", "NE12 5BT")
//      .formParam("countryCode", "GB")
//      .formParam("enter-claimant-details-as-registered-with-cds.add-company-details", "true")
//      .check(status.is(303))
//      .check(header("Location").is(s"/$route/single/enter-your-contact-details": String))
//  }
//
//  def getBulkScheduledMrnEnterYourContactDetailsPage : HttpRequestBuilder = {
//    http("get Scheduled MRN enter your contact details page")
//      .get(s"$baseUrl/$route/single/enter-your-contact-details": String)
//      .check(saveCsrfToken())
//      .check(status.is(200))
//      .check(regex("Enter your contact details"))
//  }
//
//  def postBulkScheduledMrnEnterYourContactDetailsPage : HttpRequestBuilder = {
//    http("post Scheduled MRN enter your contact details page")
//      .post(s"$baseUrl/$route/single/enter-your-contact-details": String)
//      .formParam("csrfToken", "${csrfToken}")
//      .formParam("enter-your-contact-details.contact-name", "Online Sales LTD")
//      .formParam("enter-your-contact-details.contact-email", "someemail@mail.com")
//      .formParam("enter-your-contact-details.contact-phone-number", "+4420723934397")
//      .formParam("nonUkAddress-line1", "11 Mount Road")
//      .formParam("nonUkAddress-line2", "")
//      .formParam("nonUkAddress-line3", "")
//      .formParam("nonUkAddress-line4", "London")
//      .formParam("postcode", "E10 7PP")
//      .formParam("countryCode", "GB")
//      .check(status.is(303))
//      .check(header("Location").is(s"/$route/scheduled/claim-northern-ireland": String))
//  }

  def getScheduledMrnClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("get Scheduled claim northern ireland page")
      .get(s"$baseUrl/$route/scheduled/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your commodities (.*) imported or moved through Northern Ireland?"))
  }

  def postScheduledMrnClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("post Scheduled claim northern ireland page")
      .post(s"$baseUrl/$route/scheduled/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/choose-basis-for-claim": String))

  }

  def getScheduledMrnChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("get Scheduled MRN choose basis of claim page")
      .get(s"$baseUrl/$route/scheduled/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the basis for claim"))
  }

  def postScheduledMrnChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("post Scheduled MRN choose basis of claim page")
      .post(s"$baseUrl/$route/scheduled/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "3")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/enter-commodity-details": String))
  }

  def getScheduledMrnEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("get Scheduled MRN enter commodity details page")
      .get(s"$baseUrl/$route/scheduled/enter-commodity-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us the reason for this claim"))
  }

  def postScheduledMrnEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("post Scheduled MRN enter commodity details page")
      .post(s"$baseUrl/$route/single/enter-commodity-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-commodities-details", "No reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/select-duties": String))
  }

  def getScheduledMrnSelectDutiesPage : HttpRequestBuilder = {
    http("get Scheduled MRN select duties page")
      .get(s"$baseUrl/$route/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))
  }

  def postScheduledMrnSelectDutiesPage : HttpRequestBuilder = {
    http("post Scheduled MRN select duties page")
      .post(s"$baseUrl/$route/single/select-duties": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A85")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/start-claim": String))
  }

  def getScheduledMrnStartClaimPage : HttpRequestBuilder = {
    http("get Scheduled MRN start claim page")
      .get(s"$baseUrl/$route/single/start-claim": String)
      .check(status.is(303))
      .check(header("Location").saveAs("action3"))
  }

  def getScheduledMrnEnterClaimPage : HttpRequestBuilder = {
    http("get Scheduled MRN enter claim page")
      .get(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .check(status.is(200))
      .check(regex("Enter the claim amount for duty A85 - Provisional Anti-Dumping Duty"))
  }

  def postScheduledMrnEnterClaimPage : HttpRequestBuilder = {
    http("post Scheduled MRN enter claim page")
      .post(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim", "39")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-claim": String))
  }

  def getScheduledMrnCheckClaimPage : HttpRequestBuilder = {
    http("get Scheduled MRN check claim page")
      .get(s"$baseUrl/$route/single/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the reimbursement claim totals for all MRNs"))
  }

  def postScheduledMrnCheckClaimPage : HttpRequestBuilder = {
    http("post Scheduled MRN check claim page")
      .post(s"$baseUrl/$route/single/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/check-these-bank-details-are-correct": String))
  }

  def getScheduledMrnCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("get Scheduled MRN check these bank details are correct page")
      .get(s"$baseUrl/$route/single/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))
  }

  def getScheduledMRNBankAccountTypePage : HttpRequestBuilder = {
    http("get scheduled bank account type")
      .get(s"$baseUrl/$route/scheduled/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postScheduledMRNBankAccountTypePage : HttpRequestBuilder = {
    http("post scheduled bank account type")
      .post(s"$baseUrl/$route/scheduled/bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/enter-bank-account-details": String))
  }

  def getScheduledMRNEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("get scheduled enter bank account details page")
      .get(s"$baseUrl/$route/scheduled/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter bank account details"))
  }

  def postScheduledEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("post scheduled enter bank account details page")
      .post(s"$baseUrl/$route/scheduled/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-details.account-name", "Natwest")
      .formParam("enter-bank-details.sort-code", "456789")
      .formParam("enter-bank-details.account-number", "45678901")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/check-these-bank-details-are-correct": String))
  }

  def getScheduledUploadSupportEvidencePage : HttpRequestBuilder = {
    http("get upload support evidence page")
      .get(s"$baseUrl/$route/scheduled/supporting-evidence/upload-supporting-evidence": String)
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
      .check(regex("Add documents to support your claim"))
  }

  def postScheduledUploadSupportEvidencePage : HttpRequestBuilder = {
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

  def getScheduledScanProgressWaitPage : HttpRequestBuilder = {
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postScheduledScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postScheduledScanProgressWaitPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }

  def getScheduledSelectSupportingEvidencePage : HttpRequestBuilder = {
    http("get select supporting evidence page")
      .get(s"$baseUrl" + "${selectPage}")
      .check(status.is(200))
      //.check(regex("Select the description of the file you just uploaded"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))
  }

  def postScheduledSelectSupportingEvidencePage : HttpRequestBuilder = {
    http("post select supporting evidence page")
      .post(s"$baseUrl" + "${supportEvidencePageType}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.choose-document-type", "5")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/supporting-evidence/check-your-answers": String))
  }

  def getScheduledCheckYourAnswersPage : HttpRequestBuilder = {
    http("get check your answers page")
      .get(s"$baseUrl/$route/scheduled/supporting-evidence/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))
  }

  def postScheduledCheckYourAnswersPage : HttpRequestBuilder = {
    http("post check your answers page")
      .post(s"$baseUrl/$route/scheduled/supporting-evidence/check-your-answers": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/check-answers-accept-send": String))
  }

  def getScheduledCheckAnswersAcceptSendPage : HttpRequestBuilder = {
    http("get check answers and send page")
      .get(s"$baseUrl/$route/scheduled/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your application"))
  }

  def postScheduledCheckAnswersAcceptSendPage : HttpRequestBuilder = {
    http("post check answers and send page")
      .post(s"$baseUrl/$route/scheduled/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/scheduled/claim-submitted": String))
  }

  def getScheduledClaimSubmittedPage : HttpRequestBuilder = {
    http(("get submitted page"))
      .get(s"$baseUrl/$route/scheduled/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
  }

  def postBulkScheduledMrnCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("post Scheduled MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
      .check(status.is(200))
  }
}
