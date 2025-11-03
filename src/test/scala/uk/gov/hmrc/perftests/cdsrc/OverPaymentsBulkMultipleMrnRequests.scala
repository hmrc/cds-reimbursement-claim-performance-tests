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
import io.gatling.core.check.css.CssCheckType
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import jodd.lagarto.dom.NodeSelector
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object OverPaymentsBulkMultipleMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String                       = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String                         = "claim-back-import-duty-vat"
  val route1: String                        = "claim-back-import-duty-vat/overpayments"
  val overPaymentsV2: String                = baseUrlFor("cds-reimbursement-claim-frontend") + s"/claim-back-import-duty-vat/test-only"
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect        = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1       = s"$baseUrl/$route/start"
  val CsrfPattern     = """<input type="hidden" name="csrfToken" value="([^"]+)""""


  def saveCsrfToken: CheckBuilder[CssCheckType, NodeSelector] = css("input[name='csrfToken']", "value").optional.saveAs("csrfToken")

  def postOverpaymentsMultipleChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post the choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("overpayments.choose-how-many-mrns", "Multiple")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/have-your-documents-ready": String))

  def getOverpaymentsMultipleHaveYourDocumentsReady: HttpRequestBuilder =
    http("get the supporting documents ready page")
      .get(s"$baseUrl/$route1/multiple/have-your-documents-ready": String)
      .check(status.in(200,303))
      .check(bodyString.transform(_.contains("Have your supporting documents ready")).is(true))

  def getOverpaymentsMultipleMrnPage: HttpRequestBuilder =
    http("get overpayments multiple MRN page")
      .get(s"$baseUrl/$route1/multiple/enter-movement-reference-number": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("First Movement Reference Number (MRN)")).is(true))

  def postOverpaymentsMultipleMrnPage: HttpRequestBuilder =
    http("post overpayments multiple MRN page")
      .post(s"$baseUrl/$route1/multiple/enter-movement-reference-number/1": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", "10AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-mrn": String))

  def getOverpaymentsMultipleMrnCheckDeclarationPage: HttpRequestBuilder =
    http("get overpayments multiple MRN check declaration details page")
      .get(s"$baseUrl/$route1/multiple/check-mrn": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the Movement Reference Number (MRN) you entered")).is(true))

  def postOverpaymentsMultipleMrnCheckDeclarationPage: HttpRequestBuilder =
    http("post overpayments multiple MRN check declaration details page")
      .post(s"$baseUrl/$route1/multiple/check-mrn": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-movement-reference-number/2": String))

  def getOverpaymentsMultipleEnterSecondMRNPage: HttpRequestBuilder =
    http("get overpayments multiple second MRN page")
      .get(s"$baseUrl/$route1/multiple/enter-movement-reference-number/2": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Second Movement Reference Number (MRN)")).is(true))

  def postOverpaymentsMultipleEnterSecondMRNPage: HttpRequestBuilder =
    http("post overpayments multiple second MRN page")
      .post(s"$baseUrl/$route1/multiple/enter-movement-reference-number/2": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-movement-reference-number", "20AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-movement-reference-numbers": String))

  def getOverpaymentsMultipleCheckMRNPage: HttpRequestBuilder =
    http("get overpayments multiple check MRN page")
      .get(s"$baseUrl/$route1/multiple/check-movement-reference-numbers": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Movement Reference Numbers (MRNs) added to your claim")).is(true))

  def postOverpaymentsMultipleCheckMRNPage: HttpRequestBuilder =
    http("post overpayments multiple check MRN page")
      .post(s"$baseUrl/$route1/multiple/check-movement-reference-numbers": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("check-movement-reference-numbers", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-basis-for-claim": String))

  def getOverpaymentsMultipleChooseBasisOfClaimPage: HttpRequestBuilder =
    http("get overpayments multiple MRN choose basis of claim page")
      .get(s"$baseUrl/$route1/multiple/choose-basis-for-claim": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Why are you making this claim?")).is(true))

  def postOverpaymentsMultipleChooseBasisOfClaimPage: HttpRequestBuilder =
    http("post overpayments multiple MRN choose basis of claim page")
      .post(s"$baseUrl/$route1/multiple/choose-basis-for-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-basis-for-claim", "PersonalEffects")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-additional-details": String))

  def getOverpaymentsMultipleEnterCommodityDetailsPage: HttpRequestBuilder =
    http("get overpayments multiple MRN enter commodity details page")
      .get(s"$baseUrl/$route1/multiple/enter-additional-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Tell us more about your claim")).is(true))

  def postOverpaymentsMultipleEnterCommodityDetailsPage: HttpRequestBuilder =
    http("post overpayments multiple MRN enter commodity details page")
      .post(s"$baseUrl/$route1/multiple/enter-additional-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-additional-details", "No reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties": String))

  def getOverpaymentsMultipleSelectDutiesOnePage: HttpRequestBuilder =
    http("get overpayments multiple MRN select duties one page")
      .get(s"$baseUrl/$route1/multiple/select-duties/1": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What do you want to claim?")).is(true))

  def postOverpaymentsMultipleSelectDutiesOnePage: HttpRequestBuilder =
    http("post overpayments multiple MRN select duties one page")
      .post(s"$baseUrl/$route1/multiple/select-duties/1": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duties[]", "A95")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-claim/1/A95": String))

  def getOverpaymentsMultipleSelectDutiesOneDutyPage: HttpRequestBuilder =
    http("get overpayments multiple MRN select duties one duty page")
      .get(s"$baseUrl/$route1/multiple/enter-claim/1/A95": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("A95 - Provisional Countervailing Duty")).is(true))

  def postOverpaymentsMultipleSelectDutiesOneDutyPage: HttpRequestBuilder =
    http("post overpayments multiple MRN select duties one duty page")
      .post(s"$baseUrl/$route1/multiple/enter-claim/1/A95": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim-amount", "16.70")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/select-duties/2": String))

  def getOverpaymentsMultipleSelectDutiesSecondPage: HttpRequestBuilder =
    http("get overpayments multiple MRN select duties second page")
      .get(s"$baseUrl/$route1/multiple/select-duties/2": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("What do you want to claim?")).is(true))

  def postOverpaymentsMultipleSelectDutiesSecondPage: HttpRequestBuilder =
    http("post overpayments multiple MRN select duties second page")
      .post(s"$baseUrl/$route1/multiple/select-duties/2": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("select-duties[]", "A85")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-claim/2/A85": String))

  def getOverpaymentsMultipleSelectDutiesSecondDutyPage: HttpRequestBuilder =
    http("get overpayments multiple MRN select duties second duty page")
      .get(s"$baseUrl/$route1/multiple/enter-claim/2/A85": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("A85 - Provisional Anti-Dumping Duty")).is(true))

  def postOverpaymentsMultipleSelectDutiesSecondDutyPage: HttpRequestBuilder =
    http("post overpayments multiple MRN select duties second duty page")
      .post(s"$baseUrl/$route1/multiple/enter-claim/2/A85": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-claim-amount", "14.70")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/check-claim": String))

  def getOverpaymentsMultipleCheckClaimPage: HttpRequestBuilder =
    http("get overpayments multiple MRN check claim page")
      .get(s"$baseUrl/$route1/multiple/check-claim": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Check the repayment totals for this claim")).is(true))

  def postOverpaymentsMultipleCheckClaimPage: HttpRequestBuilder =
    http("post overpayments multiple MRN check claim page")
      .post(s"$baseUrl/$route1/multiple/check-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-payee-type": String))

  def getOverpaymentsMultipleChoosePayeeTypePage: HttpRequestBuilder =
    http("get the Overpayments choose payee type page")
      .get(s"$baseUrl/$route1/multiple/choose-payee-type": String)
      .check(saveCsrfToken)
      .check(status.in(303,200))
      .check(bodyString.transform(_.contains("Who will the repayment be made to?")).is(true))

  def postOverpaymentsMultipleChoosePayeeTypePage: HttpRequestBuilder =
    http("post the Overpayments choose payee type page")
      .post(s"$baseUrl/$route1/multiple/choose-payee-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-payee-type", "Declarant")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/enter-bank-account-details": String))


  def getOverpaymentsMultipleEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("get overpayments multiple enter bank account details page")
      .get(s"$baseUrl/$route1/multiple/enter-bank-account-details": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Enter the UK-based bank account details")).is(true))

  def postOverpaymentsMultipleEnterBankAccountDetailsPage: HttpRequestBuilder =
    http("post overpayments multiple enter bank account details page")
      .post(s"$baseUrl/$route1/multiple/enter-bank-account-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Halifax")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "23456789")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-file-type": String))

  def getOverpaymentsMultipleChooseFileTypePage: HttpRequestBuilder =
    http("get overpayments multiple choose file type page")
      .get(s"$baseUrl/$route1/multiple/choose-file-type": String)
      .check(saveCsrfToken)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Add supporting documents to your claim")).is(true))

  def postOverpaymentsMultipleChooseFileTypesPage: HttpRequestBuilder =
    http("post overpayments multiple choose file type page")
      .post(s"$baseUrl/$route1/multiple/choose-file-type": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/choose-files": String))

  def getOverpaymentsMultipleChooseFilesPage: HttpRequestBuilder =
    http("get overpayments multiple choose files page")
      .get(s"$baseUrl/$route1/multiple/choose-files": String)
      .check(status.is(303))


  def getOverpaymentsMultipleContinueToHostPage: HttpRequestBuilder =
    http("get overpayments continue to host page")
      .get(s"$baseUrl/upload-customs-documents/continue-to-host": String)
      .check(status.is(303))
      .check(header("Location").is(s"$baseUrl/$route1/multiple/check-your-answers": String))

  def getOverpaymentsMultipleMrnClaimantDetailsPage: HttpRequestBuilder =
    http("get overpayments multiple MRN claimant details page")
      .get(s"$baseUrl/$route1/multiple/claimant-details": String)
      .check(saveCsrfToken)
      .check(status.is(200))


  def getOverpaymentsMultipleChangeContactDetailsPage: HttpRequestBuilder =
    http("get overpayments multiple change contact details page")
      .get(s"$baseUrl/$route1/multiple/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(bodyString.transform(_.contains("Who should we contact about this claim?")).is(true))

  def postOverpaymentsMultipleChangeContactDetailsPage: HttpRequestBuilder =
    http("post overpayments multiple change contact details page")
      .post(s"$baseUrl/$route1/multiple/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/multiple/claimant-details/lookup-address": String))

  def getOverpaymentsMultipleClaimantDetailsCheckPage1: HttpRequestBuilder =
    http("get overpayments multiple claimant details page from details contact page")
      .get(s"$baseUrl/$route1/multiple/claimant-details": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))
      .check(header("Location").is(s"/$route1/multiple/claimant-details/lookup-address": String))


  def getOverpaymentsMultipleCheckYourAnswersPage: HttpRequestBuilder =
    http("get overpayments multiple check your answers page")
      .get(s"$baseUrl/$route1/multiple/check-your-answers": String)
      .check(saveCsrfToken)
      .check(status.in(200,303))

  def postOverpaymentsMultipleCheckYourAnswersPage: HttpRequestBuilder =
    http("post overpayments multiple submit claim page")
      .post(s"$baseUrl/$route1/multiple/submit-claim": String)
      .formParam("csrfToken", "#{csrfToken}")
      .check(status.is(303))


  def getOverpaymentsMultipleClaimSubmittedPage: HttpRequestBuilder =
    http("get overpayments multiple claim submitted page")
      .get(s"$baseUrl/$route1/multiple/claim-submitted": String)
      .check(status.in(200,303))


}
