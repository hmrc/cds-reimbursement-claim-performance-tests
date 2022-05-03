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
import io.gatling.core.check.CheckBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object RejectedGoodsSingleRequests extends ServicesConfiguration with RequestUtils {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val route: String = "claim-for-reimbursement-of-import-duties"
  val route1: String = "claim-for-reimbursement-of-import-duties/rejected-goods"

  val authUrl: String = baseUrlFor("auth-login-stub")
  val redirect = s"$baseUrl/$route/start/claim-for-reimbursement"
  val redirect1 = s"$baseUrl/$route/start"
  val CsrfPattern = """<input type="hidden" name="csrfToken" value="([^"]+)""""

  def saveCsrfToken(): CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def getRejectedGoodsSelectClaimTypePage : HttpRequestBuilder = {
    http("get select claim type page")
      .get(s"$baseUrl/$route/select-claim-type": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Choose type of claim"))
  }

  def postRejectedGoodsSelectClaimTypePage : HttpRequestBuilder = {
    http("post rejected goods select claim type page")
      .post(s"$baseUrl/$route/select-claim-type": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choose-claim-type",  "RejectedGoods")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/choose-how-many-mrns": String))
  }

  def getRejectedGoodsChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("get the rejected goods choose how many mrns page")
      .get(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .check(status.is(200))
      .check(saveCsrfToken())
      .check(regex("Choose how many MRNs you want to submit in this claim"))
  }

  def postRejectedGoodsChooseHowManyMrnsPage : HttpRequestBuilder = {
    http("post the rejected goods choose how many mrns page")
      .post(s"$baseUrl/$route1/choose-how-many-mrns": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("rejected-goods.choose-how-many-mrns", "Individual")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-movement-reference-number": String))
  }

  def getRejectedGoodsMRNPage : HttpRequestBuilder = {
    http("get The MRN page")
      .get(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the MRN"))
  }

  def postRejectedGoodsMRNPage : HttpRequestBuilder = {
    http("post The MRN page")
      .post(s"$baseUrl/$route1/single/enter-movement-reference-number": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-movement-reference-number", "10ABCDEFGHIJKLMNO0")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-importer-eori": String))
  }

  def getRejectedGoodsImporterEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN importer eori entry page")
      .get(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the importer’s EORI number"))
  }

  def postRejectedGoodsImporterEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN importer eori entry page")
      .post(s"$baseUrl/$route1/single/enter-importer-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-importer-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/enter-declarant-eori": String))
  }

  def getRejectedGoodsDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("get the MRN declarant eori entry page")
      .get(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Enter the declarant’s EORI number"))
  }

  def postRejectedGoodsDeclarantEoriEntryPage : HttpRequestBuilder = {
    http("post the MRN declarant eori entry page")
      .post(s"$baseUrl/$route1/single/enter-declarant-eori": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("enter-declarant-eori-number", "GB123456789012345")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/check-declaration-details": String))
  }

  def getRejectedGoodsCheckDeclarationPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/single/check-declaration-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("Check these declaration details are correct"))
  }

  def postRejectedGoodsCheckDeclarationPage : HttpRequestBuilder = {
    http("post the MRN check declaration details page")
      .post(s"$baseUrl/$route1/single/check-declaration-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("check-declaration-details", "true")
      .check(status.is(303))
      .check(header("Location").is(s"/$route1/single/claimant-details": String))
  }

  def getRejectedGoodsClaimantDetailsPage : HttpRequestBuilder = {
    http("get the MRN check declaration details page")
      .get(s"$baseUrl/$route1/single/claimant-details": String)
      .check(saveCsrfToken())
      .check(status.is(200))
      .check(regex("How we will contact you about this claim"))
  }
}
