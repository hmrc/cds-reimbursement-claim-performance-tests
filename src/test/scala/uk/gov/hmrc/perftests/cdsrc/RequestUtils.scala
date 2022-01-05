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
import io.gatling.core.check.regex.{RegexCheckType, RegexOfType}
import io.gatling.core.check.{CheckBuilder, MultipleFindCheckBuilder}

import java.io.InputStream


trait RequestUtils {

  private val amazonUrlPattern = """action="(.*?)""""
  private val callBackUrPattern = """name="x-amz-meta-callback-url" value="(.*?)""""
  private val amzDatePattern = """name="x-amz-date" value="(.*?)""""
  private val successRedirectPattern = """name="success_action_redirect" value="(.*?)""""
  private val credentialPattern = """name="x-amz-credential" value="(.*?)""""
  private val initiateResponsePattern = """name="x-amz-meta-upscan-initiate-response" value="(.*?)""""
  private val initiateReceivedPattern = """name="x-amz-meta-upscan-initiate-received" value="(.*?)""""
  private val requestIdPattern = """name="x-amz-meta-request-id" value="(.*?)""""
  private val algorithmPattern ="""name="x-amz-algorithm" value="(.*?)""""
  private val keyPattern = """name="key" value="(.*?)""""
  private val signaturePattern = """name="x-amz-signature" value="(.*?)""""
  private val errorRedirectPattern = """name="error_action_redirect" value="(.*?)""""
  private val sessionIdPattern ="""name="x-amz-meta-session-id" value="(.*?)""""
  private val policyPattern ="""name="policy" value="(.*?)""""
  private val metaOriginalFilename = """name="x-amz-meta-original-filename" value="(.*?)""""


  def bodyCheck(body: String): MultipleFindCheckBuilder[RegexCheckType, String, String] with RegexOfType = regex(body)

  def fileBytes(filename: String): Array[Byte] = {
    val resource: InputStream = getClass.getResourceAsStream(filename)
    Iterator.continually(resource.read).takeWhile(_ != -1).map(_.toByte).toArray
  }

  def saveFileUploadUrl: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(amazonUrlPattern).saveAs("fileUploadAmazonUrl")
  }

  def saveCallBack: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(callBackUrPattern).saveAs("callBack")
  }

  def saveAmazonDate: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(amzDatePattern).saveAs("amazonDate")
  }

  def saveSuccessRedirect: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(successRedirectPattern).saveAs("successRedirect")
  }

  def saveAmazonCredential: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(credentialPattern).saveAs("amazonCredential")
  }

  def saveUpscanIniateResponse: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(initiateResponsePattern).saveAs("upscanInitiateResponse")
  }

  def saveUpscanInitiateRecieved: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(initiateReceivedPattern).saveAs("upscanInitiateReceived")
  }

  def saveRequestId: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(requestIdPattern).saveAs("requestId")
  }

  def saveAmazonMetaOriginalFileName: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(metaOriginalFilename).saveAs("amazonMetaOriginalFileName")
  }

  def saveAmazonAlgorithm: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(algorithmPattern).saveAs("amazonAlgorithm")
  }

  def saveKey: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(keyPattern).saveAs("key")
  }

  def saveAmazonSignature: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(signaturePattern).saveAs("amazonSignature")
  }

  def saveErrorRedirect: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(errorRedirectPattern).saveAs("errorRedirect")
  }

  def saveSessionId: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(sessionIdPattern).saveAs("sessionId")
  }

  def savePolicy: CheckBuilder[RegexCheckType, String, String] = {
    bodyCheck(policyPattern).saveAs("policy")
  }

}
