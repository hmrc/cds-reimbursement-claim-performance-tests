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
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.check.regex.RegexCheckType
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object MultipleMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/overpayments"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def postMultipleChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("post the choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("overpayments.choose-how-many-mrns", "Multiple")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-movement-reference-number": String))
  }

  def getMultipleMrnPage : HttpRequestBuilder = {
    http("get multiple MRN page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the first Movement Reference Number (.*)"))
  }

  def postMultipleMrnPage : HttpRequestBuilder = {
    http("post multiple MRN page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-declaration-details": String))
  }

  def getMultipleMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("get multiple MRN check declaration details page")
      .get(s"$baseUrl/$route1/multiple/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postMultipleMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("post multiple MRN check declaration details page")
      .post(s"$baseUrl/$route1/multiple/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-movement-reference-number/2": String))
  }

  def getMultipleEnterSecondMRNPage : HttpRequestBuilder = {
    http("get multiple second MRN page")
      .get(s"$baseUrl/$route1/multiple/enter-movement-reference-number/2": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the second Movement Reference Number (.*)"))
  }

  def postMultipleEnterSecondMRNPage : HttpRequestBuilder = {
    http("post multiple second MRN page")
      .post(s"$baseUrl/$route1/multiple/enter-movement-reference-number/2": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-associated-mrn", "20AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-movement-reference-numbers": String))
  }

  def getMultipleCheckMRNPage : HttpRequestBuilder = {
    http("get multiple check MRN page")
      .get(s"$baseUrl/$route1/multiple/check-movement-reference-numbers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Movement Reference Numbers (.*) added to your claim"))
  }

  def postMultipleCheckMRNPage : HttpRequestBuilder = {
    http("post multiple check MRN page")
      .post(s"$baseUrl/$route1/multiple/check-movement-reference-numbers": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-movement-reference-numbers", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claimant-details": String))
  }

  def getMultipleMrnClaimantDetailsPage : HttpRequestBuilder = {
    http("get multiple MRN claimant details page")
      .get(s"$baseUrl/$route1/multiple/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def getMultipleChangeContactDetailsPage : HttpRequestBuilder = {
    http("get multiple change contact details page")
      .get(s"$baseUrl/$route1/multiple/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))
  }

  def postMultipleChangeContactDetailsPage : HttpRequestBuilder = {
    http("post multiple change contact details page")
      .post(s"$baseUrl/$route1/multiple/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claimant-details": String))
  }

  def getMultipleClaimantDetailsCheckPage1 : HttpRequestBuilder = {
    http("get multiple claimant details page from details contact page")
      .get(s"$baseUrl/$route1/multiple/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }

  def postMultipleClaimantDetailsCheckPage : HttpRequestBuilder = {
    http("post multiple claimant details check page")
      .post(s"$baseUrl/$route1/multiple/claimant-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claimant-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claim-northern-ireland": String))
  }


  def getMultipleClaimMrnClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("get multiple claim northern ireland page")
      .get(s"$baseUrl/$route1/multiple/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your goods moved through or imported to Northern Ireland?"))
  }

  def postMultipleClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("post multiple claim northern ireland page")
      .post(s"$baseUrl/$route1/multiple/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-basis-for-claim": String))

  }

  def getMultipleChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("get multiple MRN choose basis of claim page")
      .get(s"$baseUrl/$route1/multiple/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))
  }

  def postMultipleChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("post multiple MRN choose basis of claim page")
      .post(s"$baseUrl/$route1/multiple/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "InwardProcessingReliefFromCustomsDuty")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-additional-details": String))
  }

  def getMultipleEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("get multiple MRN enter commodity details page")
      .get(s"$baseUrl/$route1/multiple/enter-additional-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide additional details about this claim"))
  }

  def postMultipleEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("post multiple MRN enter commodity details page")
      .post(s"$baseUrl/$route1/multiple/enter-additional-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-additional-details", "No reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties/1": String))
  }

  //Single journey select duties
  def getMultipleSelectDutiesOnePage : HttpRequestBuilder = {
    http("get multiple MRN select duties one page")
      .get(s"$baseUrl/$route1/multiple/select-duties/1": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for under first MRN"))
  }

  def postMultipleSelectDutiesOnePage : HttpRequestBuilder = {
    http("post multiple MRN select duties one page")
      .post(s"$baseUrl/$route1/multiple/select-duties/1": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A95")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties/1/A95": String))
  }

  def getMultipleSelectDutiesOneDutyPage : HttpRequestBuilder = {
    http("get multiple MRN select duties one duty page")
      .get(s"$baseUrl/$route1/multiple/select-duties/1/A95": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A95 - Provisional Countervailing Duty under first MRN"))
  }

  def postMultipleSelectDutiesOneDutyPage : HttpRequestBuilder = {
    http("post multiple MRN select duties one duty page")
      .post(s"$baseUrl/$route1/multiple/select-duties/1/A95": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("multiple-enter-claim", "16.70")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties/2": String))
  }

  def getMultipleSelectDutiesSecondPage : HttpRequestBuilder = {
    http("get multiple MRN select duties second page")
      .get(s"$baseUrl/$route1/multiple/select-duties/2": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for under second MRN"))
  }

  def postMultipleSelectDutiesSecondPage : HttpRequestBuilder = {
    http("post multiple MRN select duties second page")
      .post(s"$baseUrl/$route1/multiple/select-duties/2": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A85")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties/2/A85": String))
  }

  def getMultipleSelectDutiesSecondDutyPage : HttpRequestBuilder = {
    http("get multiple MRN select duties second duty page")
      .get(s"$baseUrl/$route1/multiple/select-duties/2/A95": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A85 - Provisional Anti-Dumping Duty under second MRN"))
  }

  def postMultipleSelectDutiesSecondDutyPage : HttpRequestBuilder = {
    http("post multiple MRN select duties second duty page")
      .post(s"$baseUrl/$route1/multiple/select-duties/1/A95": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("multiple-enter-claim", "14.70")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-claim": String))
  }

  def getMultipleCheckClaimPage : HttpRequestBuilder = {
    http("get multiple MRN check claim page")
      .get(s"$baseUrl/$route1/multiple/check-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the repayment totals for this claim"))
  }

  def postMultipleCheckClaimPage : HttpRequestBuilder = {
    http("post multiple MRN check claim page")
      .post(s"$baseUrl/$route1/multiple/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("multiple-check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-these-bank-details-are-correct": String))
  }

  def getMultipleCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("get Claim MRN check these bank details are correct page")
      .get(s"$baseUrl/$route1/multiple/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))
  }

  def getMultipleBankAccountTypePage : HttpRequestBuilder = {
    http("get multiple bank account type")
      .get(s"$baseUrl/$route1/multiple/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postMultipleBankAccountTypePage : HttpRequestBuilder = {
    http("post multiple bank account type")
      .post(s"$baseUrl/$route1/multiple/bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Business")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-bank-account-details": String))
  }

  def getMultipleEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("get multiple enter bank account details page")
      .get(s"$baseUrl/$route1/multiple/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter your bank account details"))
  }

  def postMultipleEnterBankAccountDetailsPage : HttpRequestBuilder = {
    http("post multiple enter bank account details page")
      .post(s"$baseUrl/$route1/multiple/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Halifax")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "23456789")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-these-bank-details-are-correct": String))
  }

  def getMultipleUploadSupportEvidencePage : HttpRequestBuilder = {
    http("get upload support evidence page")
      .get(s"$baseUrl/$route1/multiple/supporting-evidence/upload-supporting-evidence": String)
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
      .check(regex("Add documents to support your claim"))
  }

  def postMultipleUploadSupportEvidencePage : HttpRequestBuilder = {
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

  def getMultipleScanProgressWaitPage : HttpRequestBuilder = {
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))
  }

  def postMultipleScanProgressWaitPage : HttpRequestBuilder = {
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  }

  def postMultipleScanProgressWaitPage1 : List[ActionBuilder] = {
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(http(" post scan progressing wait page1")
        .get(s"$baseUrl" + "${scanPage}")
        .check(status.in(303, 200))
        .check(header("Location").optional.saveAs("selectPage")))
    ).actionBuilders
  }


  def getMultipleSelectSupportingEvidenceTypePage : HttpRequestBuilder = {
    http("get select supporting evidence type page")
      //.get(s"$baseUrl" + "${selectPage}")
      .get(s"$baseUrl/$route1/multiple/supporting-evidence/select-supporting-evidence-type": String)
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))
  }

  def postMultipleSelectSupportingEvidenceTypePage : HttpRequestBuilder = {
    http("post select supporting evidence type page")
      //.post(s"$baseUrl" + "${supportEvidencePageType}")
      .post(s"$baseUrl/$route1/multiple/supporting-evidence/select-supporting-evidence-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "AirWayBill")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/supporting-evidence/choose-files": String))
  }

  def getMultipleSupportingEvidenceChooseFilesPage : HttpRequestBuilder = {
    http("get supporting evidence choose files page")
      .get(s"$baseUrl/$route1/multiple/supporting-evidence/choose-files": String)
      .check(status.is(303))
  }

  def getMultipleCheckYourAnswersPage : HttpRequestBuilder = {
    http("get check your answers page")
      .get(s"$baseUrl/$route1/multiple/supporting-evidence/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))
  }

  def postMultipleCheckYourAnswersPage : HttpRequestBuilder = {
    http("post check your answers page")
      .post(s"$baseUrl/$route1/multiple/supporting-evidence/check-your-answers": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/multiple/check-answers-accept-send": String))
  }

  def getMultipleCheckAnswersAcceptSendPage : HttpRequestBuilder = {
    http("get check answers and send page")
      .get(s"$baseUrl/$route1/multiple/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))
  }

  def postMultipleCheckAnswersAcceptSendPage : HttpRequestBuilder = {
    http("post check answers and send page")
      .post(s"$baseUrl/$route1/multiple/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claim-submitted": String))
  }

  def getMultipleClaimSubmittedPage : HttpRequestBuilder = {
    http(("get submitted page"))
      .get(s"$baseUrl/$route1/multiple/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))
  }
}
