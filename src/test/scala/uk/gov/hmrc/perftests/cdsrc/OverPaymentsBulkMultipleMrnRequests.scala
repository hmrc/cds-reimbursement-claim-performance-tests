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
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.concurrent.duration.DurationInt


object OverPaymentsBulkMultipleMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-back-import-duty-vat"
  val route1: String = "claim-back-import-duty-vat/overpayments"
  val overPaymentsV2: String = baseUrlFor("cds-reimbursement-claim-frontend") + s"/claim-back-import-duty-vat/test-only"
  val baseUrlUploadCustomsDocuments: String = baseUrlFor("upload-customs-documents-frontend")

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def postOverpaymentsMultipleChooseHowManyMrnsPage: HttpRequestBuilder =
    http("post the choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("overpayments.choose-how-many-mrns", "Multiple")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-movement-reference-number": String))

  def getOverpaymentsMultipleMrnPage : HttpRequestBuilder =
    http("get overpayments multiple MRN page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the first Movement Reference Number (.*)"))

  def postOverpaymentsMultipleMrnPage : HttpRequestBuilder =
    http("post overpayments multiple MRN page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-movement-reference-number/1": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/check-declaration-details": String))

  def getOverpaymentsMultipleMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("get overpayments multiple MRN check declaration details page")
      .get(s"$baseUrl/$route1/v2/multiple/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postOverpaymentsMultipleMrnCheckDeclarationPage : HttpRequestBuilder =
    http("post overpayments multiple MRN check declaration details page")
      .post(s"$baseUrl/$route1/v2/multiple/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-movement-reference-number/2": String))

  def getOverpaymentsMultipleEnterSecondMRNPage : HttpRequestBuilder =
    http("get overpayments multiple second MRN page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-movement-reference-number/2": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the second Movement Reference Number (.*)"))

  def postOverpaymentsMultipleEnterSecondMRNPage : HttpRequestBuilder =
    http("post overpayments multiple second MRN page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-movement-reference-number/2": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "20AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/check-movement-reference-numbers": String))

  def getOverpaymentsMultipleCheckMRNPage : HttpRequestBuilder =
    http("get overpayments multiple check MRN page")
      .get(s"$baseUrl/$route1/v2/multiple/check-movement-reference-numbers": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Movement Reference Numbers (.*) added to your claim"))

  def postOverpaymentsMultipleCheckMRNPage : HttpRequestBuilder =
    http("post overpayments multiple check MRN page")
      .post(s"$baseUrl/$route1/v2/multiple/check-movement-reference-numbers": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-movement-reference-numbers", "false")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/claimant-details": String))

  def getOverpaymentsMultipleMrnClaimantDetailsPage : HttpRequestBuilder =
    http("get overpayments multiple MRN claimant details page")
      .get(s"$baseUrl/$route1/v2/multiple/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def getOverpaymentsMultipleChangeContactDetailsPage : HttpRequestBuilder =
    http("get overpayments multiple change contact details page")
      .get(s"$baseUrl/$route1/v2/multiple/claimant-details/change-contact-details": String)
      .check(status.is(200))
      .check(regex("Change contact details"))

  def postOverpaymentsMultipleChangeContactDetailsPage : HttpRequestBuilder =
    http("post overpayments multiple change contact details page")
      .post(s"$baseUrl/$route1/v2/multiple/claimant-details/change-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-contact-details.contact-phone-number", "+4420723934397")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/claimant-details": String))

  def getOverpaymentsMultipleClaimantDetailsCheckPage1 : HttpRequestBuilder =
    http("get overpayments multiple claimant details page from details contact page")
      .get(s"$baseUrl/$route1/v2/multiple/claimant-details": String)
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))

  def postOverpaymentsMultipleClaimantDetailsCheckPage : HttpRequestBuilder =
    http("post overpayments multiple claimant details check page")
      .post(s"$baseUrl/$route1/v2/multiple/claimant-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/claim-northern-ireland": String))

  def getOverpaymentsMultipleClaimMrnClaimNorthernIrelandPage : HttpRequestBuilder =
    http("get overpayments multiple claim northern ireland page")
      .get(s"$baseUrl/$route1/v2/multiple/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your goods moved through or imported to Northern Ireland?"))

  def postOverpaymentsMultipleClaimNorthernIrelandPage : HttpRequestBuilder =
    http("post overpayments multiple claim northern ireland page")
      .post(s"$baseUrl/$route1/v2/multiple/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/choose-basis-for-claim": String))

  def getOverpaymentsMultipleChooseBasisOfClaimPage : HttpRequestBuilder =
    http("get overpayments multiple MRN choose basis of claim page")
      .get(s"$baseUrl/$route1/v2/multiple/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose the reason for making this claim"))

  def postOverpaymentsMultipleChooseBasisOfClaimPage : HttpRequestBuilder =
    http("post overpayments multiple MRN choose basis of claim page")
      .post(s"$baseUrl/$route1/v2/multiple/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "PersonalEffects")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-additional-details": String))

  def getOverpaymentsMultipleEnterCommodityDetailsPage : HttpRequestBuilder =
    http("get overpayments multiple MRN enter commodity details page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-additional-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Provide additional details about this claim"))

  def postOverpaymentsMultipleEnterCommodityDetailsPage : HttpRequestBuilder =
    http("post overpayments multiple MRN enter commodity details page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-additional-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-additional-details", "No reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/select-duties": String))

  def getOverpaymentsMultipleSelectDutiesOnePage : HttpRequestBuilder =
    http("get overpayments multiple MRN select duties one page")
      .get(s"$baseUrl/$route1/v2/multiple/select-duties/1": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for under first MRN"))

  def postOverpaymentsMultipleSelectDutiesOnePage : HttpRequestBuilder =
    http("post overpayments multiple MRN select duties one page")
      .post(s"$baseUrl/$route1/v2/multiple/select-duties/1": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A95")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-claim/1/A95": String))

  def getOverpaymentsMultipleSelectDutiesOneDutyPage : HttpRequestBuilder =
    http("get overpayments multiple MRN select duties one duty page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-claim/1/A95": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A95 - Provisional Countervailing Duty under first MRN"))

  def postOverpaymentsMultipleSelectDutiesOneDutyPage : HttpRequestBuilder =
    http("post overpayments multiple MRN select duties one duty page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-claim/1/A95": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("multiple-enter-claim", "16.70")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/select-duties/2": String))

  def getOverpaymentsMultipleSelectDutiesSecondPage : HttpRequestBuilder =
    http("get overpayments multiple MRN select duties second page")
      .get(s"$baseUrl/$route1/v2/multiple/select-duties/2": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for under second MRN"))

  def postOverpaymentsMultipleSelectDutiesSecondPage : HttpRequestBuilder =
    http("post overpayments multiple MRN select duties second page")
      .post(s"$baseUrl/$route1/v2/multiple/select-duties/2": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A85")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-claim/2/A85": String))

  def getOverpaymentsMultipleSelectDutiesSecondDutyPage : HttpRequestBuilder =
    http("get overpayments multiple MRN select duties second duty page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-claim/2/A85": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Claim details for A85 - Provisional Anti-Dumping Duty under second MRN"))

  def postOverpaymentsMultipleSelectDutiesSecondDutyPage : HttpRequestBuilder =
    http("post overpayments multiple MRN select duties second duty page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-claim/2/A85": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("multiple-enter-claim", "14.70")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/check-claim": String))

  def getOverpaymentsMultipleCheckClaimPage : HttpRequestBuilder =
    http("get overpayments multiple MRN check claim page")
      .get(s"$baseUrl/$route1/v2/multiple/check-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check the repayment totals for this claim"))

  def postOverpaymentsMultipleCheckClaimPage : HttpRequestBuilder =
    http("post overpayments multiple MRN check claim page")
      .post(s"$baseUrl/$route1/v2/multiple/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("multiple-check-claim-summary", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/choose-payee-type": String))

  def getOverpaymentsMultipleCheckBankDetailsPage : HttpRequestBuilder =
    http("get overpayments multiple MRN check bank details page")
      .get(s"$baseUrl/$route1/v2/multiple/check-bank-details": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))

  def getOverpaymentsMultipleBankAccountTypePage : HttpRequestBuilder = {
    http("get overpayments multiple bank account type")
      .get(s"$baseUrl/$route1/v2/multiple/bank-account-type": String)
      .check(status.is(200))
      .check(regex("What type of account details are you providing?"))
  }

  def postOverpaymentsMultipleBankAccountTypePage : HttpRequestBuilder =
    http("post overpayments multiple bank account type")
      .post(s"$baseUrl/$route1/v2/multiple/bank-account-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-bank-account-type", "Business")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/enter-bank-account-details": String))

  def getOverpaymentsMultipleEnterBankAccountDetailsPage : HttpRequestBuilder =
    http("get overpayments multiple enter bank account details page")
      .get(s"$baseUrl/$route1/v2/multiple/enter-bank-account-details": String)
      .check(status.is(200))
      .check(regex("Enter your bank account details"))

  def postOverpaymentsMultipleEnterBankAccountDetailsPage : HttpRequestBuilder =
    http("post overpayments multiple enter bank account details page")
      .post(s"$baseUrl/$route1/v2/multiple/enter-bank-account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-bank-account-details.account-name", "Halifax")
      .formParam("enter-bank-account-details.sort-code", "123456")
      .formParam("enter-bank-account-details.account-number", "23456789")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/check-bank-details": String))

  def getOverpaymentsMultipleChooseFileTypePage: HttpRequestBuilder =
    http("get overpayments multiple choose file type page")
      .get(s"$baseUrl/$route1/v2/multiple/choose-file-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Add supporting documents to your claim"))

  def postOverpaymentsMultipleChooseFileTypesPage: HttpRequestBuilder =
    http("post overpayments multiple choose file type page")
      .post(s"$baseUrl/$route1/v2/multiple/choose-file-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-file-type", "ImportAndExportDeclaration")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/choose-files": String))

  def getOverpaymentsMultipleChooseFilesPage: HttpRequestBuilder =
    http("get overpayments multiple choose files page")
      .get(s"$baseUrl/$route1/v2/multiple/choose-files": String)
      .check(status.is(303))

  def getOverpaymentsMultipleCheckYourAnswersPage: HttpRequestBuilder =
    http("get overpayments multiple check your answers page")
      .get(s"$baseUrl/$route1/v2/multiple/check-your-answers": String)
      .check(saveCsrfToken())
      .check(status.is(303))
       .check(regex("Check your answers before sending your claim"))

  def postOverpaymentsMultipleCheckYourAnswersPage: HttpRequestBuilder =
    http("post overpayments multiple submit claim page")
      .post(s"$baseUrl/$route1/v2/multiple/submit-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/v2/multiple/claim-submitted": String))

  def getOverpaymentsMultipleClaimSubmittedPage: HttpRequestBuilder =
    http("get overpayments multiple claim submitted page")
      .get(s"$baseUrl/$route1/v2/multiple/claim-submitted": String)
      .check(status.is(200))
      .check(regex("Claim submitted"))
      .check(regex("Your claim reference number"))

}
