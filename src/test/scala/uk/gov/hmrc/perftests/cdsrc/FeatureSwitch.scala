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
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsSingleMrnRequests._


object FeatureSwitch extends ServicesConfiguration {

  def cdsOverPaymentsV2Enable: HttpRequestBuilder = {
    //val url = if (trueOrFalse) s"/$overPaymentsV2/feature/overpayments_v2/enable":String else s"$overPaymentsV2/feature/overpayments_v2/disable" :String
    http(requestName = "Set Overpayments v2")
      .get(s"$overPaymentsV2/feature/overpayments_v2/enable" :String)
     // .body(StringBody(JsBoolean(true).toString())).asJson
      .check(status.is(expected = 200))
  }

  def cdsOverPaymentsV2Disable: HttpRequestBuilder = {
    http(requestName = "Set Overpayments v2")
      .get(s"$overPaymentsV2/feature/overpayments_v2/disable" :String)
      .check(status.is(expected = 200))
  }



}
