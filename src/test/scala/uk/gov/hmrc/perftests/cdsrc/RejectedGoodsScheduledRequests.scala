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
import io.gatling.core.check.css.CssCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import jodd.lagarto.dom.NodeSelector
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt

object RejectedGoodsScheduledRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/rejected-goods"
  val RejectedGoodsV2: String                = baseUrlFor("cds-reimbursement-claim-frontend") + s"/claim-back-import-duty-vat/test-only"
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken: CheckBuilder[CssCheckType, NodeSelector] = css("input[name='csrfToken']", "value").optional.saveAs("csrfToken")

  def getRejectedGoodsScheduledSelectClaimTypePage: HttpRequestBuilder =
    http("get select claim type page")
      .get(s"$baseUrl/$route/choose-claim-type": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))
      .check(regex("Start a new claim"))


  def postRejectedGoodsScheduledSelectClaimTypePage: HttpRequestBuilder =
    http("post rejected goods scheduled select claim type page")
      .post(s"$baseUrl/$route/choose-claim-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-claim-type", "RejectedGoods")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))

  def getRejectedGoodsScheduledChooseHowManyMrnsPage: HttpRequestBuilder =
    http("get the rejected goods scheduled choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(bodyString.transform(_.contains("Single or multiple Movement Reference Numbers (MRNs)")).is(true))

  def postRejectedGoodsScheduledChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post the rejected scheduled goods choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("rejected-goods.choose-how-many-mrns", "Scheduled")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/have-your-documents-ready": String))

  def getRejectedGoodsScheduledHaveYourDocumentsReady: HttpRequestBuilder =
    http("get the supporting documents ready page")
      .get(s"$baseUrl/$route1/scheduled/have-your-documents-ready": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Files you need for this claim")).is(true))

  def getRejectedGoodsScheduledMRNPage: HttpRequestBuilder =
    http("get rejected goods scheduled the MRN page")
      .get(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("First Movement Reference Number (MRN)")).is(true))

  def postRejectedGoodsScheduledMRNPage: HttpRequestBuilder =
    http("post rejected goods scheduled the MRN page")
      .post(s"$baseUrl/$route1/scheduled/enter-movement-reference-number": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-importer-eori": String))

  def getRejectedGoodsScheduledImporterEoriEntryPage: HttpRequestBuilder =
    http("get the rejected goods scheduled MRN importer eori entry page")
      .get(s"$baseUrl/$route1/scheduled/enter-importer-eori": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What is the importer’s EORI number?")).is(true))

  def postRejectedGoodsScheduledImporterEoriEntryPage: HttpRequestBuilder =
    http("post the rejected goods scheduled MRN importer eori entry page")
      .post(s"$baseUrl/$route1/scheduled/enter-importer-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-declarant-eori": String))

  def getRejectedGoodsScheduledDeclarantEoriEntryPage: HttpRequestBuilder =
    http("get the rejected goods scheduled MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/scheduled/enter-declarant-eori": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Enter the declarant’s EORI number")).is(true))

  def postRejectedGoodsScheduledDeclarantEoriEntryPage: HttpRequestBuilder =
    http("post the rejected goods scheduled MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/scheduled/enter-declarant-eori": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-mrn": String))

  def getRejectedGoodsScheduledCheckDeclarationPage: HttpRequestBuilder =
    http("get the rejected goods scheduled MRN check declaration details page")
      .get(s"$baseUrl/$route1/scheduled/check-mrn": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))

  def postRejectedGoodsScheduledCheckDeclarationPage: HttpRequestBuilder =
    http("post rejected goods scheduled check declaration details page")
      .post(s"$baseUrl/$route1/scheduled/check-mrn": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/upload-mrn-list": String))

  def getRejectedGoodsScheduledUploadMrnListPage: HttpRequestBuilder =
    http("get rejected goods scheduled upload mrn list page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-files": String)
      .check(saveCsrfToken)
      .check(status.is(303))

  def getRejectedGoodsUploadDocumentsChooseFilePage: HttpRequestBuilder =
    http("get rejected goods scheduled upload documents choose file page")
      .get(s"$baseUrlUploadCustomsDocuments/upload-customs-documents/choose-files": String)
      .check(saveFileUploadUrl)
      .check(saveCallBack)
      .check(saveAmazonDate)
      .check(saveSuccessRedirect)
      .check(saveAmazonCredential)
      .check(saveUpscanInitiateResponse)
      .check(saveUpscanInitiateReceived)
      .check(saveRequestId)
      .check(saveAmazonAlgorithm)
      .check(saveKey)
      .check(saveAmazonSignature)
      .check(saveErrorRedirect)
      .check(saveSessionId)
      .check(savePolicy)
      .check(status.is(200))
      .check(regex("""data-file-upload-check-status-url="(.*)"""").saveAs("fileVerificationUrl"))
      .check(bodyString.transform(_.contains("Add your claim details")).is(true))

  def postRejectedGoodsScheduledUploadDocumentsChooseFilePagePage: HttpRequestBuilder =
    http("post rejected goods scheduled upload documents choose file page")
      .post("#{fileUploadAmazonUrl}")
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryjoqtomO5urVl5B6N")
      .asMultipartForm
      .bodyPart(StringBodyPart("x-amz-meta-callback-url", "#{callBack}"))
      .bodyPart(StringBodyPart("x-amz-date", "#{amazonDate}"))
      .bodyPart(StringBodyPart("success_action_redirect", "#{successRedirect}"))
      .bodyPart(StringBodyPart("x-amz-credential", "#{amazonCredential}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-response", "#{upscanInitiateResponse}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-received", "#{upscanInitiateReceived}"))
      .bodyPart(StringBodyPart("x-amz-meta-request-id", "#{requestId}"))
      .bodyPart(StringBodyPart("x-amz-algorithm", "#{amazonAlgorithm}"))
      .bodyPart(StringBodyPart("key", "#{key}"))
      .bodyPart(StringBodyPart("x-amz-signature", "#{amazonSignature}"))
      .bodyPart(StringBodyPart("error_action_redirect", "#{errorRedirect}"))
      .bodyPart(StringBodyPart("x-amz-meta-original-filename", "validFile.png"))
      .bodyPart(StringBodyPart("acl", "private"))
      .bodyPart(StringBodyPart("x-amz-meta-session-id", "#{sessionId}"))
      .bodyPart(StringBodyPart("x-amz-meta-consuming-service", "cds-reimbursement-claim-frontend"))
      .bodyPart(StringBodyPart("policy", "#{policy}"))
      .bodyPart(RawFileBodyPart("file", "data/validFile.png"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  def getRejectedGoodsScheduledDocumentUploadProgressPage: HttpRequestBuilder =
    http("get upload progress wait page")
      .get("#{UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))

  def postRejectedGoodsScheduledDocumentUploadProgressPage: HttpRequestBuilder =
    http("post upload progress wait page")
      .post(s"$baseUrl" + "#{actionlll}")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))

  def postRejectedGoodsScheduledDocumentUploadProgressPage1: List[ActionBuilder] =
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(
        http(" post scan progressing wait page1")
          .get(s"$baseUrl" + "#{scanPage}")
          .check(status.in(303, 200))
          .check(header("Location").optional.saveAs("selectPage"))
      )
    ).actionBuilders

  def getRejectedGoodsScheduledUploadDocumentsSummaryPage: HttpRequestBuilder =
    http("get rejected goods scheduled upload documents summary page")
      .get(s"$baseUrl" + "#{selectPage}")
      .check(status.is(200))
      .check(regex("You have successfully uploaded a document showing all the MRNs in this claim"))
      .check(css("#main-content > div > div > form", "action").saveAs("supportEvidencePageType"))


  def getRejectedGoodsScheduledClaimantDetailsPage: HttpRequestBuilder =
    http("get rejected goods scheduled MRN claimant details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("How we will contact you about this claim")).is(true))

  def getRejectedGoodsScheduledContactDetailsPage: HttpRequestBuilder =
    http("get rejected goods scheduled change contact details page")
      .get(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Who should we contact about this claim?")).is(true))

  def postRejectedGoodsScheduledChangeContactDetailsPage: HttpRequestBuilder =
    http("post rejected goods scheduled change contact details page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/claimant-details": String))

  def postRejectedGoodsScheduledClaimDetailsPage: HttpRequestBuilder =
    http("post rejected goods scheduled claim details page")
      .post(s"$baseUrl/$route1/scheduled/claimant-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-your-answers": String))

  def getRejectedGoodsScheduledChooseBasisForClaimPage: HttpRequestBuilder =
    http("get rejected goods scheduled choose basis for claim page")
      .get(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Why are you making this claim?")).is(true))

  def postRejectedGoodsScheduledChooseBasisForClaimPage: HttpRequestBuilder =
    http("post rejected goods scheduled choose basis for claim page")
      .post(s"$baseUrl/$route1/scheduled/choose-basis-for-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-basis-for-claim.rejected-goods", "SpecialCircumstances")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-special-circumstances": String))

  def getRejectedGoodsScheduledSpecialCircumstancesPage: HttpRequestBuilder =
    http("get rejected goods scheduled special circumstances page")
      .get(s"$baseUrl/$route1/scheduled/enter-special-circumstances": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Enter any special circumstances")).is(true))

  def postRejectedGoodsScheduledSpecialCircumstancesPage: HttpRequestBuilder =
    http("post rejected goods scheduled special circumstances page")
      .post(s"$baseUrl/$route1/scheduled/enter-special-circumstances": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-special-circumstances.rejected-goods", "reason")
      .check(status.is(303))

  def getRejectedGoodsScheduledChooseDisposalMethodPage: HttpRequestBuilder =
    http("get rejected goods scheduled choose disposal method page")
      .get(s"$baseUrl/$route1/scheduled/choose-disposal-method": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("How will you dispose of the goods?")).is(true))

  def postRejectedGoodsScheduledChooseDisposalMethodPage: HttpRequestBuilder =
    http("post rejected goods scheduled choose disposal method page")
      .post(s"$baseUrl/$route1/scheduled/choose-disposal-method": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-method-of-disposal.rejected-goods", "PlacedInCustomsWarehouse")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-rejected-goods-details": String))

  def getRejectedGoodsScheduledEnterRejectedDetailsPage: HttpRequestBuilder =
    http("get rejected goods scheduled enter rejected goods details page")
      .get(s"$baseUrl/$route1/scheduled/enter-rejected-goods-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Tell us more about your claim")).is(true))

  def postRejectedGoodsScheduledEnterRejectedDetailsPage: HttpRequestBuilder =
    http("post rejected goods scheduled enter rejected goods details page")
      .post(s"$baseUrl/$route1/scheduled/enter-rejected-goods-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-rejected-goods-details.rejected-goods", "Any")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/select-duty-types": String))

  def getRejectedGoodsScheduledMrnSelectDutiesPage: HttpRequestBuilder =
    http("get rejected goods scheduled select duty types page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Import tax to claim")).is(true))

  def postRejectedGoodsScheduledSelectMrnSelectDutiesPage: HttpRequestBuilder =
    http("post Rejected Goods scheduled select duty types page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/select-duty-types": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-types[]", "uk-duty")
      .formParam("select-duty-types[]", "eu-duty")
      .formParam("select-duty-types[]", "excise-duty")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/uk-duty": String))

  def getRejectedGoodsScheduledMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("get Rejected Goods select duties uk duty page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/uk-duty": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which UK duties do you want to claim for?")).is(true))

  def postRejectedGoodsMrnSelectDutiesUkDutyPage: HttpRequestBuilder =
    http("post Rejected Goods select duties uk duty page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/uk-duty": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "A00")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/uk-duty/A00": String))

  def getRejectedGoodsScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("get Rejected Goods select duties uk duty tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/uk-duty/A00": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("A00 - Customs Duty")).is(true))

  def postRejectedGoodsScheduledMrnUkDutyPage: HttpRequestBuilder =
    http("post Rejected Goods select duties uk duty tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/uk-duty/A00": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "3.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/eu-duty": String))

  def getRejectedGoodsScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("get Rejected Goods select duties eu duty page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which EU duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesEuDutyPage: HttpRequestBuilder =
    http("post Rejected Goods select duties eu duty page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/eu-duty": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "A50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/eu-duty/A50": String))

  def getRejectedGoodsScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("get Rejected Goods select duties eu duty tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/eu-duty/A50": String)
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(bodyString.transform(_.contains("A50 - Customs Duty")).is(true))

  def postRejectedGoodsScheduledMrnEuDutyPage: HttpRequestBuilder =
    http("post Rejected Goods select duties eu duty tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/eu-duty/A50": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "4.50")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty": String))

  def getRejectedGoodsMrnExciseSelectDutiesPage: HttpRequestBuilder =
    http("get Rejected Goods scheduled select duty types page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise duties")).is(true))

  def postRejectedGoodsScheduledMrnExciseSelectDutiesPage: HttpRequestBuilder =
    http("post Rejected Goods scheduled select excise duty types page")
      .post(s"$baseUrl/$route1/scheduled/select-excise-categories")
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-excise-categories[]", "beer")
      .formParam("select-excise-categories[]", "made-wine")
      .formParam("select-excise-categories[]", "spirits")
      .formParam("select-excise-categories[]", "other-fermented-products")
      .formParam("select-excise-categories[]", "biofuels")
      .formParam("select-excise-categories[]", "tobacco")
      .check(status.in(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/beer": String))


  def getRejectedGoodsScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("get RejectedGoods select duties beer page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/beer": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which beer duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesBeerPage: HttpRequestBuilder =
    http("post RejectedGoods select duties beer page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/beer": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "440")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/440": String))

  def getRejectedGoodsScheduledMrnBeerPage: HttpRequestBuilder =
    http("get RejectedGoods select duties beer tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/440": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 440 Beer")).is(true))


  def postRejectedGoodsScheduledMrnBeerPage: HttpRequestBuilder =
    http("post RejectedGoods select duties beer tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/440": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))

  def getRejectedGoodsScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("get RejectedGoods select duties wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/wine": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which wine duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesWinePage: HttpRequestBuilder =
    http("post RejectedGoods select duties wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/wine": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "413")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/413": String))

  def getRejectedGoodsScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("get RejectedGoods select duties made wine page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/made-wine": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which made-wine duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesMadeWinePage: HttpRequestBuilder =
    http("post RejectedGoods select duties made wine page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/made-wine": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "423")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/423": String))

  def getRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("get RejectedGoods select duties alcohol page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/low-alcohol-beverages": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which low alcohol beverages duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage: HttpRequestBuilder =
    http("post RejectedGoods select duties alcohol page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/low-alcohol-beverages": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "435")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/435": String))

  def getRejectedGoodsScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties spirits page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/spirits": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which spirits duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesSpiritsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties spirits page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/spirits": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "462")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/462": String))

  def getRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("get RejectedGoods select duties cider page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/cider-perry": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which cider and perry duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage: HttpRequestBuilder =
    http("post RejectedGoods select duties cider page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/cider-perry": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "483")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/483": String))

  def getRejectedGoodsScheduledMrnSelectDutiesOtherFermentedProductsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties other fermented products page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/other-fermented-products": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which other fermented products duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesOtherFermentedProductsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties other fermented products page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/other-fermented-products": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "334")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/334": String))

  def getRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties hydrocarbon oils page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/hydrocarbon-oils": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which hydrocarbon oil duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties hydrocarbon oils page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/hydrocarbon-oils": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "551")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/551": String))

  def getRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties biofuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/biofuels": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which biofuels duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties biofuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/biofuels": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "589")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/589": String))

  def getRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("get RejectedGoods select duties road fuels page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/miscellaneous-road-fuels": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which miscellaneous road fuels duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage: HttpRequestBuilder =
    http("post RejectedGoods select duties road fuels page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/miscellaneous-road-fuels": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "592")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/592": String))

  def getRejectedGoodsScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("get RejectedGoods select duties tobacco page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/tobacco": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which tobacco products duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesTobaccoPage: HttpRequestBuilder =
    http("post RejectedGoods select duties tobacco page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/tobacco": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "611")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/611": String))

  def getRejectedGoodsScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("get RejectedGoods select duties climate change levy page")
      .get(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/climate-change-levy": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Which Climate Change Levy duties do you want to claim for?")).is(true))

  def postRejectedGoodsScheduledMrnSelectDutiesClimatePage: HttpRequestBuilder =
    http("post RejectedGoods select duties climate change levy page")
      .post(s"$baseUrl/$route1/scheduled/select-duties/excise-duty/climate-change-levy": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duty-codes[]", "99A")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-claim/excise-duty/99A": String))

  def getRejectedGoodsScheduledMrnEnterClaimPage: HttpRequestBuilder =
    http("get RejectedGoods scheduled enter claim page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim": String)
      .check(status.is(303))


  def getRejectedGoodsScheduledMrnWinePage: HttpRequestBuilder =
    http("get RejectedGoods select duties wine tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/413": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 413 Wine")).is(true))

  def postRejectedGoodsScheduledMrnWinePage: HttpRequestBuilder =
    http("post RejectedGoods select duties wine tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/413": String)
      .formParam("csrfToken","#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/made-wine": String))

  def getRejectedGoodsScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("get RejectedGoods select duties made wine tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/423": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 423 Made-wine")).is(true))

  def postRejectedGoodsScheduledMrnMadeWinePage: HttpRequestBuilder =
    http("post RejectedGoods select duties made wine tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/423": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))

  def getRejectedGoodsScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("get RejectedGoods select duties alcohol tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/435": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 435 Low alcohol beverages")).is(true))

  def postRejectedGoodsScheduledMrnLowAlcoholPage: HttpRequestBuilder =
    http("post RejectedGoods select duties alcohol tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/435": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/spirits": String))

  def getRejectedGoodsScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties spirits tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/462": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 462 Spirits")).is(true))

  def postRejectedGoodsScheduledMrnSpiritsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties spirits tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/462": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))

  def getRejectedGoodsScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("get RejectedGoods select duties cider tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/483": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 483 Cider and perry")).is(true))

  def postRejectedGoodsScheduledMrnCiderPerryPage: HttpRequestBuilder =
    http("post RejectedGoods select duties cider tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/483": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/other-fermented-products": String))

  def getRejectedGoodsScheduledMrnOtherFermentedProductsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties other fermented products tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/334": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 334 Other fermented products")).is(true))

  def postRejectedGoodsScheduledMrnOtherFermentedProductsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties other fermented products tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/334": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))

  def getRejectedGoodsScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties hydrocarbon oils tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/551": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 551 Hydrocarbon oil")).is(true))

  def postRejectedGoodsScheduledMrnHydroOilsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties hydrocarbon oils tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/551": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/biofuels": String))

  def getRejectedGoodsScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties biofuels tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/589": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 589 Biofuels")).is(true))

  def postRejectedGoodsScheduledMrnBiofuelsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties biofuels tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/589": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))

  def getRejectedGoodsScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("get RejectedGoods select duties miscellaneous road tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/592": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 592 Miscellaneous road fuels")).is(true))

  def postRejectedGoodsScheduledMrnRoadFuelsPage: HttpRequestBuilder =
    http("post RejectedGoods select duties miscellaneous road tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/592": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/select-duties/excise-duty/tobacco": String))

  def getRejectedGoodsScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("get RejectedGoods select duties tobacco tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/611": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 611 Tobacco products")).is(true))

  def postRejectedGoodsScheduledMrnTobaccoPage: HttpRequestBuilder =
    http("post RejectedGoods select duties tobacco tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/611": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-claim": String))

  def getRejectedGoodsScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("get select duties climate change tax page")
      .get(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/99A": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Excise Duty - 99A Climate Change Levy")).is(true))

  def postRejectedGoodsScheduledMrnClimateLevyPage: HttpRequestBuilder =
    http("post RejectedGoods select duties climate change tax page")
      .post(s"$baseUrl/$route1/scheduled/enter-claim/excise-duty/99A": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim.scheduled.paid-amount", "10")
      .formParam("enter-claim.scheduled.claim-amount", "2")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/check-claim": String))

  def getRejectedGoodsScheduledMrnCheckClaimPage: HttpRequestBuilder =
    http("get RejectedGoods scheduled MRN check claim page")
      .get(s"$baseUrl/$route1/scheduled/check-claim": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))
      //.check(bodyString.transform(_.contains("Check the repayment totals for this claim")).is(true))

  def postRejectedGoodsScheduledCheckClaimPage: HttpRequestBuilder =
    http("post rejected goods scheduled check claim page")
      .post(s"$baseUrl/$route1/scheduled/check-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/enter-inspection-date": String))

  def getRejectedGoodsScheduledInspectionDatePage: HttpRequestBuilder =
    http("get rejected goods scheduled inspection date page")
      .get(s"$baseUrl/$route1/scheduled/enter-inspection-date": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Making your goods available for inspection")).is(true))

  def postRejectedGoodsScheduledInspectionDatePage: HttpRequestBuilder =
    http("post rejected goods scheduled inspection date page")
      .post(s"$baseUrl/$route1/scheduled/enter-inspection-date": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-inspection-date.rejected-goods.day", "19")
      .formParam("enter-inspection-date.rejected-goods.month", "10")
      .formParam("enter-inspection-date.rejected-goods.year", "2000")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/inspection-address/choose-type": String))

  def getRejectedGoodsScheduledInspectionAddressChoosePage: HttpRequestBuilder =
    http("get rejected goods scheduled inspection address choose type page")
      .get(s"$baseUrl/$route1/scheduled/inspection-address/choose-type": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Choose an address for the inspection")).is(true))

  def postRejectedGoodsScheduledInspectionAddressChoosePage: HttpRequestBuilder =
    http("post rejected goods scheduled inspection address choose type page")
      .post(s"$baseUrl/$route1/scheduled/inspection-address/choose-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("inspection-address.type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-payee-type": String))

  def getRejectedGoodsScheduledChoosePayeeTypePage: HttpRequestBuilder =
    http("get the rejected goods choose payee type page")
      .get(s"$baseUrl/$route1/scheduled/choose-payee-type": String)
      .check(status.in(200,303))


  def getRejectedGoodsScheduledEnterBankDetailsPage: HttpRequestBuilder =
    http("get rejected goods scheduled enter bank details page")
      .get(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Enter the UK-based bank account details")).is(true))

  def postRejectedGoodsScheduledEnterBankDetailsPage: HttpRequestBuilder =
    http("post rejected goods scheduled enter bank details page")
      .post(s"$baseUrl/$route1/scheduled/enter-bank-account-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Mybank")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "26152639")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-file-type": String))

  def getRejectedGoodsScheduledChooseFileTypePage: HttpRequestBuilder =
    http("get rejected goods scheduled choose file type page")
      .get(s"$baseUrl/$route1/scheduled/choose-file-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Add supporting documents to your claim")).is(true))


  def postRejectedGoodsScheduledChooseFileTypesPage: HttpRequestBuilder =
    http("post rejected goods scheduled choose file type page")
      .post(s"$baseUrl/$route1/scheduled/choose-file-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/scheduled/choose-files": String))

  def getRejectedGoodsScheduledChooseFilesPage: HttpRequestBuilder =
    http("get rejected goods scheduled choose files page")
      .get(s"$baseUrl/$route1/scheduled/choose-files": String)
      .check(status.is(303))

  def getRejectedGoodsScheduledChooseFilePage: HttpRequestBuilder =
    http("get rejected goods scheduled choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(status.is(200))
      .check(regex("Upload (.*)"))

  def getRejectedGoodsScheduledUploadDocumentsChooseFilePage: HttpRequestBuilder =
    http("get rejected goods scheduled upload documents choose file page")
      .get(s"$baseUrl/$route/upload-documents/choose-file": String)
      .check(saveFileUploadUrl)
      .check(saveCallBack)
      .check(saveAmazonDate)
      .check(saveSuccessRedirect)
      .check(saveAmazonCredential)
      .check(saveUpscanInitiateResponse)
      .check(saveUpscanInitiateReceived)
      .check(saveRequestId)
      .check(saveAmazonAlgorithm)
      .check(saveKey)
      .check(saveAmazonSignature)
      .check(saveErrorRedirect)
      .check(saveSessionId)
      .check(savePolicy)
      .check(status.is(200))
      .check(regex("Upload (.*)"))

  def postRejectedGoodsScheduledUploadDocumentsChooseFilePage: HttpRequestBuilder =
    http("post rejected goods scheduled upload support evidence page")
      .post("#{fileUploadAmazonUrl}")
      .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryjoqtomO5urVl5B6N")
      .asMultipartForm
      .bodyPart(StringBodyPart("x-amz-meta-callback-url", "#{callBack}"))
      .bodyPart(StringBodyPart("x-amz-date", "#{amazonDate}"))
      .bodyPart(StringBodyPart("success_action_redirect", "#{successRedirect}"))
      .bodyPart(StringBodyPart("x-amz-credential", "#{amazonCredential}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-response", "#{upscanInitiateResponse}"))
      .bodyPart(StringBodyPart("x-amz-meta-upscan-initiate-received", "#{upscanInitiateReceived}"))
      .bodyPart(StringBodyPart("x-amz-meta-request-id", "#{requestId}"))
      .bodyPart(StringBodyPart("x-amz-algorithm", "#{amazonAlgorithm}"))
      .bodyPart(StringBodyPart("key", "#{key}"))
      .bodyPart(StringBodyPart("x-amz-signature", "#{amazonSignature}"))
      .bodyPart(StringBodyPart("error_action_redirect", "#{errorRedirect}"))
      .bodyPart(StringBodyPart("x-amz-meta-original-filename", "validFile.png"))
      .bodyPart(StringBodyPart("acl", "private"))
      .bodyPart(StringBodyPart("x-amz-meta-session-id", "#{sessionId}"))
      .bodyPart(StringBodyPart("x-amz-meta-consuming-service", "cds-reimbursement-claim-frontend"))
      .bodyPart(StringBodyPart("policy", "#{policy}"))
      .bodyPart(RawFileBodyPart("file", "data/validFile.png"))
      .check(status.is(303))
      .check(header("Location").saveAs("UpscanResponseSuccess"))

  def getRejectedGoodsScheduledScanProgressWaitPage: HttpRequestBuilder =
    http("get scan progress wait page")
      .get("#{UpscanResponseSuccess}")
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(regex("We are checking your document"))
      .check(css("#main-content > div > div > form", "action").saveAs("actionlll"))

  def postRejectedGoodsScheduledScanProgressWaitPage: HttpRequestBuilder =
    http("post scan progress wait page")
      .post(s"$baseUrl" + "#{actionlll}")
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("scanPage"))

  def postRejectedGoodsScheduledScanProgressWaitPage1: List[ActionBuilder] =
    asLongAs(session => session("selectPage").asOption[String].isEmpty)(
      pause(2.second).exec(
        http(" post scan progressing wait page1")
          .get(s"$baseUrl" + "#{scanPage}")
          .check(status.in(303, 200))
          .check(header("Location").is(s"/$route/upload-documents/summary": String))
          .check(header("Location").optional.saveAs("selectPage1"))
      )
    ).actionBuilders

  def getRejectedGoodsScheduledDocumentsSummaryPage1: HttpRequestBuilder =
    http("get rejected goods scheduled upload documents summary page1")
      .get(s"$baseUrl/$route/upload-documents/summary": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(regex("You have added 1 document to your claim"))

  def postRejectedGoodsScheduledDocumentsSummaryPage: HttpRequestBuilder =
    http("post rejected goods scheduled upload documents page")
      .post(s"$baseUrl/$route/upload-documents/summary": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("supporting-evidence.check-your-answers", "false")
      .check(status.is(303))

  def getRejectedGoodsScheduledCheckYourAnswersPage: HttpRequestBuilder =
    http("get rejected goods scheduled check your answers page")
      .get(s"$baseUrl/$route1/scheduled/check-your-answers": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))


  def postRejectedGoodsScheduledCheckYourAnswersPage: HttpRequestBuilder =
    http("post rejected goods scheduled submit claim page")
      .post(s"$baseUrl/$route1/scheduled/submit-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))

  def getRejectedGoodsScheduledClaimSubmittedPage: HttpRequestBuilder =
    http("get rejected goods scheduled claim submitted page")
      .get(s"$baseUrl/$route1/scheduled/claim-submitted": String)
      .check(status.in(200,303))

}
