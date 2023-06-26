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
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object BulkScheduledMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/overpayments"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def postBulkScheduledSelectNumberOfClaimsPage: HttpRequestBuilder =
    http("post the select number of claims page")
      .post(s"$baseUrl/$route/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-number-of-claims", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-movement-reference-number": String))

  def getBulkScheduledMrnPage: HttpRequestBuilder =
    http("get Scheduled MRN page")
      .get(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the first Movement Reference Number (.*)"))

  def postBulkScheduledMrnPage: HttpRequestBuilder =
    http("post Scheduled MRN page")
      .post(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "01AAAAAAAAAAAAAAA2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-declaration-details": String))

  def getBulkScheduledMrnCheckDeclarationPage: HttpRequestBuilder =
    http("get Scheduled check declaration details page")
      .get(s"$baseUrl/$route1/scheduled/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))

  def postBulkScheduledMrnCheckDeclarationPage: HttpRequestBuilder =
    http("post Scheduled check declaration details page")
      .post(s"$baseUrl/$route1/scheduled/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/scheduled-document-upload/choose-files": String))

  def getBulkScheduledDocumentUploadChooseFilesPage: HttpRequestBuilder =
    http("get Scheduled document upload choose files page")
      .get(s"$baseUrl/$route1/scheduled/scheduled-document-upload/choose-files": String)
      .check(status.is(303))

  def getScheduledUploadDocumentsChooseFilePage: HttpRequestBuilder =
    http("get scheduled upload documents choose file page")
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
      .check(regex("Add a document showing all MRNs in this claim"))

  def postScheduledUploadDocumentsChooseFilePagePage: HttpRequestBuilder =
    http("post scheduled upload documents choose file page")
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

  def getScheduledDocumentUploadProgressPage: HttpRequestBuilder =
    http("get upload progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.in(303, 200))
//      .check(saveCsrfToken())
//      .check(regex("We are checking your document"))
//      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))

  def postScheduledDocumentUploadProgressPage: HttpRequestBuilder =
    http("post upload progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))

  def postScheduledDocumentUploadProgressPage1: List[ActionBuilder] =
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(
        http(" post scan progressing wait page1")
          .get(s"$baseUrl" + "${scanPage}")
          .check(status.in(303, 200))
          .check(header("Location").optional.saveAs("selectPage"))
      )
    ).actionBuilders

  def getScheduledUploadDocumentsSummaryPage: HttpRequestBuilder =
    http("get scheduled upload documents summary page")
      .get(s"$baseUrl" + "${selectPage}")
      .check(status.is(200))
      .check(regex("You have successfully uploaded a document showing all the MRNs in this claim"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))

  def postScheduledUploadDocumentsSummaryPage: HttpRequestBuilder =
    http("post scheduled upload documents summary page")
      //.post(s"$baseUrl" + "${supportEvidencePageType}")
      .post(s"$baseUrl/$route1/upload-documents/summary": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("schedule-document.check-your-answers", "false")
      .check(status.is(303))
  //.check(header("Location").is(s"/$route/scheduled/scheduled-document-upload/continue": String))

  def getScheduledDocumentsUploadContinuePage: HttpRequestBuilder =
    http("get scheduled documents upload continue page")
      .get(s"$baseUrl/$route1/scheduled/scheduled-document-upload/continue": String)
      .check(status.is(303))

  def getScheduledMrnClaimantDetailsPage: HttpRequestBuilder =
    http("get Scheduled  claimant details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def getScheduledMrnChangeContactDetailsPage: HttpRequestBuilder =
    http("get scheduled change contact details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))

  def postScheduledMrnChangeContactDetailsPage: HttpRequestBuilder =
    http("post scheduled change contact details page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claimant-details": String))

  def getScheduledMrnClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get scheduled claimant details page from details contact page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def postScheduledMrnClaimantDetailsCheckPage: HttpRequestBuilder =
    http("post scheduled claimant details check page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claimant-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claim-northern-ireland": String))

  def getScheduledMrnClaimNorthernIrelandPage: HttpRequestBuilder =
    http("get Scheduled claim northern ireland page")
      .get(s"$baseUrl/$route1/scheduled/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your goods moved through or imported to Northern Ireland?"))

  def postScheduledMrnClaimNorthernIrelandPage: HttpRequestBuilder =
    http("post Scheduled claim northern ireland page")
      .post(s"$baseUrl/$route1/scheduled/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-basis-for-claim": String))

  def getScheduledMrnChooseBasisOfClaimPage: HttpRequestBuilder =
    http("get Scheduled choose basis of claim page")
      .get(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))

  def postScheduledMrnChooseBasisOfClaimPage: HttpRequestBuilder =
    http("post Scheduled choose basis of claim page")
      .post(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "DutySuspension")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-additional-details": String))

  def getScheduledMrnEnterCommodityDetailsPage: HttpRequestBuilder =
    http("get Scheduled enter commodity details page")
      .get(s"$baseUrl/$route1/scheduled/enter-additional-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide additional details about this claim"))

  def postScheduledMrnEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post Scheduled enter commodity details page")
      .post(s"$baseUrl/$route1/scheduled/enter-additional-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-additional-details", "No reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/select-duty-types": String))

  def getScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("get Scheduled select duty types page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duty types you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("post scheduled select duty types page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-types[]", "uk-duty")
      .formParam("select-duty-types[]", "eu-duty")
      .formParam("select-duty-types[]", "beer")
      .formParam("select-duty-types[]", "wine")
      .formParam("select-duty-types[]", "made-wine")
      .formParam("select-duty-types[]", "low-alcohol-beverages")
      .formParam("select-duty-types[]", "spirits")
      .formParam("select-duty-types[]", "cider-perry")
      .formParam("select-duty-types[]", "hydrocarbon-oils")
      .formParam("select-duty-types[]", "biofuels")
      .formParam("select-duty-types[]", "miscellaneous-road-fuels")
      .formParam("select-duty-types[]", "tobacco")
      .formParam("select-duty-types[]", "climate-change-levy")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/start": String))

  def getScheduledMrnStartClaimPage: HttpRequestBuilder =
    http("get Scheduled select duties start page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/start": String)
      .check(status.is(303))
      .check(header("Location").saveAs("action3"))

  def getScheduledMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("get select duties uk duty page")
      .get { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .check(status.is(200))
      .check(regex("Select the UK duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("post select duties uk duty page")
      .post { session =>
        val Location = session.attributes("action3")
        s"$baseUrl$Location"
      }
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/eu-duty": String))

  def getScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("get select duties eu duty page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .check(status.is(200))
      .check(regex("Select the EU duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("post select duties eu duty page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "A50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/beer": String))

  def getScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("get select duties beer page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/beer": String)
      .check(status.is(200))
      .check(regex("Select the beer duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("post select duties beer page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/beer": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "440")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/wine": String))

  def getScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("get select duties wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/wine": String)
      .check(status.is(200))
      .check(regex("Select the wine duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("post select duties wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/wine": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "413")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/made-wine": String))

  def getScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("get select duties made wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/made-wine": String)
      .check(status.is(200))
      .check(regex("Select the made-wine duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("post select duties made wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/made-wine": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "423")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/low-alcohol-beverages": String))

  def getScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("get select duties alcohol page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/low-alcohol-beverages": String)
      .check(status.is(200))
      .check(regex("Select the low alcohol beverages duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("post select duties alcohol page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/low-alcohol-beverages": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "435")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/spirits": String))

  def getScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("get select duties spirits page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/spirits": String)
      .check(status.is(200))
      .check(regex("Select the spirits duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("post select duties spirits page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/spirits": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "462")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/cider-perry": String))

  def getScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("get select duties cider page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/cider-perry": String)
      .check(status.is(200))
      .check(regex("Select the cider and perry duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("post select duties cider page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/cider-perry": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "483")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/hydrocarbon-oils": String))

  def getScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("get select duties hydrocarbon oils page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/hydrocarbon-oils": String)
      .check(status.is(200))
      .check(regex("Select the hydrocarbon oil duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("post select duties hydrocarbon oils page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/hydrocarbon-oils": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "551")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/biofuels": String))

  def getScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("get select duties biofuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/biofuels": String)
      .check(status.is(200))
      .check(regex("Select the biofuels duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("post select duties biofuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/biofuels": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "589")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/miscellaneous-road-fuels": String))

  def getScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("get select duties road fuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/miscellaneous-road-fuels": String)
      .check(status.is(200))
      .check(
        regex("Select the miscellaneous road fuels duties you want to claim for all MRNs in the file you uploaded")
      )

  def postScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("post select duties road fuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/miscellaneous-road-fuels": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "592")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/tobacco": String))

  def getScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("get select duties tobacco page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/tobacco": String)
      .check(status.is(200))
      .check(regex("Select the tobacco products duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("post select duties tobacco page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/tobacco": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "611")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/climate-change-levy": String))

  def getScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("get select duties climate change levy page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/climate-change-levy": String)
      .check(status.is(200))
      .check(regex("Select the Climate Change Levy duties you want to claim for all MRNs in the file you uploaded"))

  def postScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("post select duties climate change levy page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/climate-change-levy": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duty-codes[]", "99A")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/reimbursement-claim/start": String))

  def getScheduledMrnStartPage: HttpRequestBuilder =
    http("get Scheduled MRN claim start page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/reimbursement-claim/start": String)
      .check(status.is(303))

  def getScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("get select duties uk duty tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/uk-duty/A00": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under UK Duty A00"))

  def postScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("post select duties uk duty tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/uk-duty/A00": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "3.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/eu-duty/A50": String))

  def getScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("get select duties eu duty tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/eu-duty/A50": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under EU Duty A50"))

  def postScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("post select duties eu duty tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/eu-duty/A50": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "4.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/beer/440": String))

  def getScheduledMrnBeerPage: HttpRequestBuilder =
    http("get select duties beer tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/beer/440": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Beer 440"))

  def postScheduledMrnBeerPage: HttpRequestBuilder =
    http("post select duties beer tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/beer/440": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/wine/413": String))

  def getScheduledMrnWinePage: HttpRequestBuilder =
    http("get select duties wine tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/wine/413": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Wine 413"))

  def postScheduledMrnWinePage: HttpRequestBuilder =
    http("post select duties wine tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/wine/413": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/made-wine/423": String))

  def getScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("get select duties made wine tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/made-wine/423": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Made-wine 423"))

  def postScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("post select duties made wine tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/made-wine/423": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/low-alcohol-beverages/435": String))

  def getScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("get select duties alcohol tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/low-alcohol-beverages/435": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Low alcohol beverages 435"))

  def postScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("post select duties alcohol tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/low-alcohol-beverages/435": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/spirits/462": String))

  def getScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("get select duties spirits tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/spirits/462": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Spirits 462"))

  def postScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("post select duties spirits tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/spirits/462": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/cider-perry/483": String))

  def getScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("get select duties cider tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/cider-perry/483": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Cider and perry 483"))

  def postScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("post select duties cider tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/cider-perry/483": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/hydrocarbon-oils/551": String))

  def getScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("get select duties hydrocarbon oils tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/hydrocarbon-oils/551": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Hydrocarbon oil 551"))

  def postScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("post select duties hydrocarbon oils tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/hydrocarbon-oils/551": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/biofuels/589": String))

  def getScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("get select duties biofuels tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/biofuels/589": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Biofuels 589"))

  def postScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("post select duties biofuels tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/biofuels/589": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/miscellaneous-road-fuels/592": String))

  def getScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("get select duties miscellaneous road tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/miscellaneous-road-fuels/592": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Miscellaneous road fuels 592"))

  def postScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("post select duties miscellaneous road tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/miscellaneous-road-fuels/592": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/tobacco/611": String))

  def getScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("get select duties tobacco tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/tobacco/611": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Tobacco products 611"))

  def postScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("post select duties tobacco tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/tobacco/611": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/climate-change-levy/99A": String))

  def getScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("get select duties climate change tax page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/climate-change-levy/99A": String)
      .check(status.is(200))
      .check(regex("Claim details for all MRNs under Climate Change Levy 99A"))

  def postScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("post select duties climate change tax page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/climate-change-levy/99A": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-scheduled-claim.paid-amount", "10")
      .formParam("enter-scheduled-claim.actual-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-claim": String))

  def getScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("get Scheduled MRN check claim page")
      .get(s"$baseUrl/$route1/scheduled/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the repayment claim totals for all MRNs"))

  def postScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("post Scheduled MRN check claim page")
      .post(s"$baseUrl/$route1/scheduled/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-these-bank-details-are-correct": String))

  def getScheduledMrnCheckTheseBankDetailsAreCorrectPage: HttpRequestBuilder =
    http("get Scheduled MRN check these bank details are correct page")
      .get(s"$baseUrl/$route1/scheduled/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))

  def getScheduledMRNBankAccountTypePage: HttpRequestBuilder =
    http("get scheduled bank account type")
      .get(s"$baseUrl/$route1/scheduled/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))

  def postScheduledMRNBankAccountTypePage: HttpRequestBuilder =
    http("post scheduled bank account type")
      .post(s"$baseUrl/$route1/scheduled/bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Personal")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-bank-account-details": String))

  def getScheduledMRNEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get scheduled enter bank account details page")
      .get(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter your bank account details"))

  def postScheduledEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post scheduled enter bank account details page")
      .post(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Natwest")
      .formParam("enter-bank-account-details.sort-code", "456789")
      .formParam("enter-bank-account-details.account-number", "45678901")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-these-bank-details-are-correct": String))

  def getScheduledUploadSupportEvidencePage: HttpRequestBuilder =
    http("get upload support evidence page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
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

  def postScheduledUploadSupportEvidencePage: HttpRequestBuilder =
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

  def getScheduledScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("${UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))

  def postScheduledScanProgressWaitPage: HttpRequestBuilder   =
    http("post scan progress wait page")
      .post(s"$baseUrl" + "${actionlll}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))
  //To store the session //how to unset the selectpage key?
  def postScheduledScanProgressWaitPage1: List[ActionBuilder] =
    asLongAs(session => session("scheduledScanSelectPage").asOption[String].isEmpty)(
      pause(2.second).exec(
        http(" post scan progressing wait page1")
          .get(s"$baseUrl" + "${scanPage}")
          .check(status.in(303, 200))
          .check(header("Location").optional.saveAs("scheduledScanSelectPage"))
      )
    ).actionBuilders

  def getScheduledSelectSupportingEvidenceTypePage: HttpRequestBuilder =
    http("get select supporting evidence type page")
      //.get(s"$baseUrl" + "${scheduledScanSelectPage}")
      .get(s"$baseUrl/$route1/scheduled/supporting-evidence/select-supporting-evidence-type": String)
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))

  def postScheduledSelectSupportingEvidenceTypePage: HttpRequestBuilder =
    http("post select supporting evidence type page")
      //.post(s"$baseUrl" + "${supportEvidencePageType}")
      .post(s"$baseUrl/$route1/scheduled/supporting-evidence/select-supporting-evidence-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "AirWayBill")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/supporting-evidence/choose-files": String))

  def getScheduledSupportingChooseFilesPage: HttpRequestBuilder =
    http("get select supporting evidence choose files page")
      //.get(s"$baseUrl" + "${scheduledScanSelectPage}")
      .get(s"$baseUrl/$route1/scheduled/supporting-evidence/choose-files": String)
      .check(status.is(303))

  def getScheduledUploadDocumentsSummaryPage1: HttpRequestBuilder =
    http("get upload documents summary page")
      .get(s"$baseUrl/$route1/upload-documents/summary": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))

  def postScheduledUploadDocumentsSummaryPagePage: HttpRequestBuilder =
    http("post upload documents summary page")
      .post(s"$baseUrl/$route1/upload-documents/summary": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))
  //.check(header("Location").is(s"/$route/scheduled/check-answers-accept-send": String))

  def getScheduledCheckAnswersAcceptSendPage: HttpRequestBuilder =
    http("get check answers and send page")
      .get(s"$baseUrl/$route1/scheduled/check-answers-accept-send": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check your answers before sending your claim"))

  def postScheduledCheckAnswersAcceptSendPage: HttpRequestBuilder =
    http("post check answers and send page")
      .post(s"$baseUrl/$route1/scheduled/check-answers-accept-send": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claim-submitted": String))

  def getScheduledClaimSubmittedPage: HttpRequestBuilder =
    http("get submitted page")
      .get(s"$baseUrl/$route1/scheduled/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))

  def postBulkScheduledMrnCheckTheseBankDetailsAreCorrectPage: HttpRequestBuilder =
    http("post Scheduled MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
      .check(status.is(200))
}
