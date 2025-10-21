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
import io.gatling.http.check.header.{HttpHeaderCheckType, HttpHeaderRegexCheckType}
import io.gatling.http.request.builder.HttpRequestBuilder
import jodd.lagarto.dom.NodeSelector
import uk.gov.hmrc.performance._
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object AwesomeStubRequests extends ServicesConfiguration {


 def saveCsrfToken: CheckBuilder[CssCheckType, NodeSelector] = css("input[name='csrfToken']", "value").optional.saveAs("csrfToken")

  val baseUrl: String  = baseUrlFor("cds-reimbursement-claim-frontend")
  val authUrl: String  = baseUrlFor("auth-login-stub")
  val route: String    = "claim-back-import-duty-vat"
  val redirect: String = s"$baseUrl/$route/start"


  def getLoginPage: HttpRequestBuilder =
    http("Get login stub page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in")
      .check(status.is(200))
      .check(saveCsrfToken)
      .check(regex("Authority Wizard").exists)


  def loginUser(eoriValue: String): HttpRequestBuilder =
    http("Login the user")
     .post(s"$authUrl/auth-login-stub/gg-sign-in" :String)
      .formParam("csrfToken","#{csrfToken}")
      .formParam("redirectionUrl", s"$baseUrl/claim-back-import-duty-vat/start")
     //.formParam("redirectionUrl", s"$baseUrl/claim-back-import-duty-vat/start")
      .formParam("authorityId", "12349")
      .formParam("excludeGnapToken","false")
      .formParam("credentialStrength", "strong")
     .formParam("confidenceLevel", "200")
      .formParam("affinityGroup", "Organisation")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[0].name", "HMRC-CUS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "EORINumber")
     .formParam("enrolment[0].taxIdentifier[0].value", s"$eoriValue")
      .check(status.is(303))

  private val userDetailsUrlPattern = s"""([^"]+)"""

  def saveBearerTokenHeader: CheckBuilder.Final[HttpHeaderRegexCheckType, Response] =
    headerRegex("Authorization", """Bearer\s([^"]+)""").saveAs("bearerToken")

   def saveSessionIdHeader: CheckBuilder[HttpHeaderCheckType, Response] =
    header("X-Session-ID").saveAs("sessionId")

   def savePlanetIdHeader: CheckBuilder[HttpHeaderCheckType, Response] =
    header("X-Planet-ID").saveAs("planetId")

   def saveUserIdHeader: CheckBuilder[HttpHeaderCheckType, Response] =
    header("X-User-ID").saveAs("userId")

   def saveUserDetailsUrl: CheckBuilder[HttpHeaderRegexCheckType, Response] =
    headerRegex("Location", userDetailsUrlPattern).saveAs("userDetailsUrl")
}
