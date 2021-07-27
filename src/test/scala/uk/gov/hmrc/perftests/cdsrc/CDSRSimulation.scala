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

import io.gatling.core.action.builder.ActionBuilder
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.cdsrc.EntryNumberRequests._
import uk.gov.hmrc.perftests.cdsrc.SingleMrnRequests._

class CDSRSimulation extends PerformanceTestRunner {

  val entryNumberJourney:List[ActionBuilder] = List[ActionBuilder](
    getAuthLoginPage,
    loginWithAuthLoginStub(),
    getCdsrStartPage,
    getCheckEoriDetailsPage,
    postCheckEoriDetailsPage,
    getSelectNumberOfClaimsPage,
    postSelectNumberOfClaimsPage,
    getMRNPage,
    postMRNPage,
    getEnterDeclarationDetails,
    postEnterDeclarationDetails,
    getWhoIsDeclarantPage,
    postWhoIsDeclarantPage,
    getEnterYourDetailsAsRegisteredWithCdsPage,
    postEnterYourDetailsAsRegisteredWithCdsPage,
    getEnterYourContactDetailsPage,
    postEnterYourContactDetailsPage,
    getEnterReasonForClaimAndBasisPage,
    postEnterReasonForClaimAndBasisPage,
    getEnterCommodityDetailsPage,
    postEnterCommodityDetailsPage,
    getSelectDutiesPage,
    postSelectDutiesPage,
    getStartClaimPage,
    getEnterClaimPage,
    postEnterClaimPage,
    getCheckClaimPage,
    postCheckClaimPage,
    getEnterBankAccountDetailsPage,
    PostEnterBankAccountDetailsPage,
    getUploadSupportEvidencePage,
    postUploadSupportEvidencePage,
    getScanProgressWaitPage,
    postScanProgressWaitPage) ++
    postScanProgressWaitPage1 ++
    List[ActionBuilder](
           getSelectSupportingEvidencePage,
          postSelectSupportingEvidencePage,
          getCheckYourAnswersPage,
          postCheckYourAnswersPage,
          getCheckAnswersAcceptSendPage,
          postCheckAnswersAcceptSendPage,
          getClaimSubmittedPage
  )

  setup("Entry-number-journey", "Entry number journey") withActions
    (entryNumberJourney:_*
    )

  val MRNJourney:List[ActionBuilder] = List[ActionBuilder](
      getMRNAuthLoginPage,
      loginWithAuthLoginStubMRN("GB000000000000001"),
      getMRNCdsrStartPage,
      getTheMRNCheckEoriDetailsPage,
      postTheMRNCheckEoriDetailsPage,
      getTheSelectNumberOfClaimsPage,
      postTheSelectNumberOfClaimsPage,
      getTheMRNPage,
      postTheMRNPage,
      getTheMRNImporterEoriEntryPage,
      postTheMRNImporterEoriEntryPage,
      getTheMRNDeclarantEoriEntryPage,
      postTheMRNDeclarantEoriEntryPage,
      getTheMRNCheckDeclarationPage,
      postTheMRNCheckDeclarationPage,
      getTheMRNWhoIsDeclarantPage,
      postTheMRNWhoIsDeclarantPage,
      getTheMRNEnterYourDetailsAsRegisteredCdsPage,
      postTheMRNEnterYourDetailsAsRegisteredCdsPage,
      getTheMRNEnterYourContactDetailsPage,
      postTheMRNEnterYourContactDetailsPage,
      getTheMRNClaimNorthernIrelandPage,
      postTheMRNClaimNorthernIrelandPage,
      getTheMRNChooseBasisOfClaimPage,
      postTheMRNChooseBasisOfClaimPage,
      getTheDuplicateMRNPage,
      postTheDuplicateMRNPage,
      getTheMRNCheckDuplicateDeclarationPage,
      postTheMRNCheckDuplicateDeclarationPage,
      getTheMRNEnterCommodityDetailsPage,
      postTheMRNEnterCommodityDetailsPage,
      getTheMRNSelectDutiesPage,
      postTheMRNSelectDutiesPage,
      getTheMRNStartClaimPage,
      getTheMRNEnterClaimPage,
      postTheMRNEnterClaimPage,
      getTheMRNCheckClaimPage,
      postTheMRNCheckClaimPage,
      getTheMRNCheckTheseBankDetailsAreCorrectPage,
//      postTheMRNCheckTheseBankDetailsAreCorrectPage,
      getUploadSupportEvidencePage,
      postUploadSupportEvidencePage,
      getScanProgressWaitPage,
      postScanProgressWaitPage) ++
      postScanProgressWaitPage1 ++
    List[ActionBuilder](
      getSelectSupportingEvidencePage,
      postSelectSupportingEvidencePage,
      getCheckYourAnswersPage,
      postCheckYourAnswersPage,
      getCheckAnswersAcceptSendPage,
      postCheckAnswersAcceptSendPage,
      getClaimSubmittedPage
    )
  setup("MRN-journey", "Movement reference number journey") withActions
    (MRNJourney:_*
      )

  runSimulation()
}
