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
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef._
import io.gatling.http.check.header.HttpHeaderCheckType
import io.gatling.http.check.header.HttpHeaderRegexCheckType
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object AwesomeStubRequests extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("cds-reimbursement-claim-frontend")
  val authUrl: String     = baseUrlFor("auth-login-stub")
  val route: String  = "claim-back-import-duty-vat"
  val redirect: String    = s"$baseUrl/$route/start"

  def getLoginPage: HttpRequestBuilder =
    http("Get login stub page")
      .get(s"$authUrl/auth-login-stub/gg-sign-in")
      .check(status.is(200))
      .check(regex("Authority Wizard").exists)

  def loginUser(eoriValue: String): HttpRequestBuilder =
    http("Login the user")
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .formParam("redirectionUrl", s"/$baseUrl/claim-back-import-duty-vat/start")
      .formParam("authorityId", "12345")
      .formParam("credentialStrength", "weak")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[0].name", "HMRC-CUS-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "EORINumber")
      .formParam("enrolment[0].taxIdentifier[0].value", s"${eoriValue}")
      .check(status.is(303))

  private val csrfPattern           = """<input type="hidden" name="csrfToken" value="([^"]+)"""
  private val userDetailsUrlPattern = s"""([^"]+)"""

  private def saveCsrfToken: CheckBuilder[RegexCheckType, String, String] =
    regex(_ => csrfPattern).saveAs("csrfToken")

  private def saveBearerTokenHeader: CheckBuilder[HttpHeaderRegexCheckType, Response, String] =
    headerRegex("Authorization", """Bearer\s([^"]+)""").saveAs("bearerToken")

  private def saveSessionIdHeader: CheckBuilder[HttpHeaderCheckType, Response, String] =
    header("X-Session-ID").saveAs("sessionId")

  private def savePlanetIdHeader: CheckBuilder[HttpHeaderCheckType, Response, String] =
    header("X-Planet-ID").saveAs("planetId")

  private def saveUserIdHeader: CheckBuilder[HttpHeaderCheckType, Response, String] =
    header("X-User-ID").saveAs("userId")

  private def saveUserDetailsUrl: CheckBuilder[HttpHeaderRegexCheckType, Response, String] =
    headerRegex("Location", userDetailsUrlPattern).saveAs("userDetailsUrl")
}
