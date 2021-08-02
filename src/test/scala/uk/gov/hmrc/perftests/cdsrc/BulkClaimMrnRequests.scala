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

object BulkClaimMrnRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-for-reimbursement-of-import-duties"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[HttpCheck, Response, CharSequence, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def postBulkClaimSelectNumberOfClaimsPage : HttpRequestBuilder = {
    http("post the select number of claims page")
      .post(s"$baseUrl/$route/select-number-of-claims": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-number-of-claims", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/bulk/enter-movement-reference-number": String))
  }

  def getBulkClaimMrnPage : HttpRequestBuilder = {
    http("get Claim MRN page")
      .get(s"$baseUrl/$route/bulk/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the lead Movement Reference Number (MRN)"))
  }

  def postBulkClaimMrnPage : HttpRequestBuilder = {
    http("post Claim MRN page")
      .post(s"$baseUrl/$route/bulk/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10AAAAAAAAAAAAAAA1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/bulk/check-declaration-details": String))
  }

  def getBulkClaimMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("get Claim MRN check declaration details page")
      .get(s"$baseUrl/$route/bulk/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these details are correct"))
  }

  def postBulkClaimMrnCheckDeclarationPage : HttpRequestBuilder = {
    http("post Claim MRN check declaration details page")
      .post(s"$baseUrl/$route/bulk/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/bulk/who-is-the-declarant": String))
  }

  def getBulkClaimMrnWhoIsDeclarantPage : HttpRequestBuilder = {
    http("get Claim MRN who is declarant page")
      .get(s"$baseUrl/$route/bulk/who-is-the-declarant": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Who is making this claim?"))
  }

  def postBulkClaimMrnWhoIsDeclarantPage : HttpRequestBuilder = {
    http("post Claim MRN who is declarant page")
      .post(s"$baseUrl/$route/scheduled/who-is-the-declarant": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-who-is-making-the-claim", "1")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-your-details-as-registered-with-cds": String))
  }

  def getBulkClaimMrnEnterYourDetailsAsRegisteredCdsPage : HttpRequestBuilder = {
    http("get Claim MRN enter your details as registered with cds")
      .get(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your details as registered with CDS"))
  }

  def postBulkClaimMrnEnterYourDetailsAsRegisteredCdsPage : HttpRequestBuilder = {
    http("post Claim MRN enter your details as registered with cds")
      .post(s"$baseUrl/$route/single/enter-your-details-as-registered-with-cds": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claimant-details-as-registered-with-cds.individual-full-name", "IT Solutions LTD")
      .formParam("enter-claimant-details-as-registered-with-cds.individual-email", "someemail@mail.com")
      .formParam("nonUkAddress-line1", "19 Bricks Road")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "Newcastle")
      .formParam("postcode", "NE12 5BT")
      .formParam("countryCode", "GB")
      .formParam("enter-claimant-details-as-registered-with-cds.add-company-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/enter-your-contact-details": String))
  }

  def getBulkClaimMrnEnterYourContactDetailsPage : HttpRequestBuilder = {
    http("get Claim MRN enter your contact details page")
      .get(s"$baseUrl/$route/single/enter-your-contact-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter your contact details"))
  }

  def postBulkClaimMrnEnterYourContactDetailsPage : HttpRequestBuilder = {
    http("post Claim MRN enter your contact details page")
      .post(s"$baseUrl/$route/single/enter-your-contact-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-your-contact-details.contact-name", "Online Sales LTD")
      .formParam("enter-your-contact-details.contact-email", "someemail@mail.com")
      .formParam("enter-your-contact-details.contact-phone-number", "+4420723934397")
      .formParam("nonUkAddress-line1", "11 Mount Road")
      .formParam("nonUkAddress-line2", "")
      .formParam("nonUkAddress-line3", "")
      .formParam("nonUkAddress-line4", "London")
      .formParam("postcode", "E10 7PP")
      .formParam("countryCode", "GB")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/bulk/claim-northern-ireland": String))
  }

  def getBulkClaimMrnClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("get Claim claim northern ireland page")
      .get(s"$baseUrl/$route/bulk/claim-northern-ireland": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Were your commodities (.*) imported or moved through Northern Ireland?"))
  }

  def postBulkClaimMrnClaimNorthernIrelandPage : HttpRequestBuilder = {
    http("post Claim claim northern ireland page")
      .post(s"$baseUrl/$route/scheduled/claim-northern-ireland": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("claim-northern-ireland", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/bulk/choose-basis-for-claim": String))

  }

  def getBulkClaimMrnChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("get Claim MRN choose basis of claim page")
      .get(s"$baseUrl/$route/bulk/choose-basis-for-claim": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the basis for claim"))
  }

  def postBulkClaimMrnChooseBasisOfClaimPage : HttpRequestBuilder = {
    http("post Claim MRN choose basis of claim page")
      .post(s"$baseUrl/$route/bulk/choose-basis-for-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-basis-for-claim", "3")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/bulk/enter-commodity-details": String))
  }

  def getBulkClaimMrnEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("get Claim MRN enter commodity details page")
      .get(s"$baseUrl/$route/bulk/enter-commodity-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Tell us the reason for this claim"))
  }

  def postBulkClaimMrnEnterCommodityDetailsPage : HttpRequestBuilder = {
    http("post Claim MRN enter commodity details page")
      .post(s"$baseUrl/$route/single/enter-commodity-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-commodities-details", "No reason")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/select-duties": String))
  }

  def getBulkClaimMrnSelectDutiesPage : HttpRequestBuilder = {
    http("get Claim MRN select duties page")
      .get(s"$baseUrl/$route/single/select-duties": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Select the duties you want to claim for"))
  }

  def postBulkClaimMrnSelectDutiesPage : HttpRequestBuilder = {
    http("post Claim MRN select duties page")
      .post(s"$baseUrl/$route/single/select-duties": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("select-duties[]", "A85")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/start-claim": String))
  }

  def getBulkClaimMrnStartClaimPage : HttpRequestBuilder = {
    http("get Claim MRN start claim page")
      .get(s"$baseUrl/$route/single/start-claim": String)
      .check(status.is(303))
      .check(header("Location").saveAs("action3"))
  }

  def getBulkClaimMrnEnterClaimPage : HttpRequestBuilder = {
    http("get Claim MRN enter claim page")
      .get(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .check(status.is(200))
      .check(regex("Enter the claim amount for duty A85 - Provisional Anti-Dumping Duty"))
  }

  def postBulkClaimMrnEnterClaimPage : HttpRequestBuilder = {
    http("post Claim MRN enter claim page")
      .post(session => {
        val Location = session.get.attributes("action3")
        s"$baseUrl$Location"
      })
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-claim", "39")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-claim": String))
  }

  def getBulkClaimMrnCheckClaimPage : HttpRequestBuilder = {
    http("get Scheduled MRN check claim page")
      .get(s"$baseUrl/$route/single/check-claim": String)
      .check(status.is(200))
      .check(regex("Check the reimbursement claim totals for all MRNs"))
  }

  def postBulkClaimMrnCheckClaimPage : HttpRequestBuilder = {
    http("post Claim MRN check claim page")
      .post(s"$baseUrl/$route/single/check-claim": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-claim-summary", "0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route/single/check-these-bank-details-are-correct": String))
  }

  def getBulkClaimMrnCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("get Claim MRN check these bank details are correct page")
      .get(s"$baseUrl/$route/single/check-these-bank-details-are-correct": String)
      .check(status.is(200))
      .check(regex("Check these bank details are correct"))
      .check(css(".govuk-button", "href").saveAs("uploadSupportingEvidencePage"))
  }

  def postBulkClaimMrnCheckTheseBankDetailsAreCorrectPage : HttpRequestBuilder = {
    http("post Claim MRN check these bank details are correct page")
      .get(s"$baseUrl" + "${uploadSupportingEvidencePage}")
      .check(status.is(200))
  }
}