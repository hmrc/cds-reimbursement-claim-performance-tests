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
import uk.gov.hmrc.perftests.cdsrc.MultipleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.BulkScheduledMrnRequests.{getScheduledMrnSelectDutiesPage, _}
import uk.gov.hmrc.perftests.cdsrc.EntryNumberRequests._
import uk.gov.hmrc.perftests.cdsrc.SingleMrnRequests._

class CDSRSimulation extends PerformanceTestRunner {

  //  val entryNumberJourney:List[ActionBuilder] = List[ActionBuilder](
  //    getAuthLoginPage,
  //    loginWithAuthLoginStub(),
  //    getCdsrStartPage,
  //    getCheckEoriDetailsPage,
  //    postCheckEoriDetailsPage,
  //    getSelectNumberOfClaimsPage,
  //    postSelectNumberOfClaimsPage,
  //    getMRNPage,
  //    postMRNPage,
  //    getEnterDeclarationDetails,
  //    postEnterDeclarationDetails,
  //    getWhoIsDeclarantPage,
  //    postWhoIsDeclarantPage,
  //    getEnterYourDetailsAsRegisteredWithCdsPage,
  //    postEnterYourDetailsAsRegisteredWithCdsPage,
  //    getEnterYourContactDetailsPage,
  //    postEnterYourContactDetailsPage,
  //    getEnterReasonForClaimAndBasisPage,
  //    postEnterReasonForClaimAndBasisPage,
  //    getEnterCommodityDetailsPage,
  //    postEnterCommodityDetailsPage,
  //    getSelectDutiesPage,
  //    postSelectDutiesPage,
  //    getStartClaimPage,
  //    getEnterClaimPage,
  //    postEnterClaimPage,
  //    getCheckClaimPage,
  //    postCheckClaimPage,
  //    getEnterBankAccountDetailsPage,
  //    PostEnterBankAccountDetailsPage,
  //    getUploadSupportEvidencePage,
  //    postUploadSupportEvidencePage,
  //    getScanProgressWaitPage,
  //    postScanProgressWaitPage) ++
  //    postScanProgressWaitPage1 ++
  //    List[ActionBuilder](
  //           getSelectSupportingEvidencePage,
  //          postSelectSupportingEvidencePage,
  //          getCheckYourAnswersPage,
  //          postCheckYourAnswersPage,
  //          getCheckAnswersAcceptSendPage,
  //          postCheckAnswersAcceptSendPage,
  //          getClaimSubmittedPage
  //  )
  //
  //  setup("Entry-number-journey", "Entry number journey") withActions
  //    (entryNumberJourney:_*
  //    )

  val MRNJourney: List[ActionBuilder] = List[ActionBuilder](
    getMRNAuthLoginPage,
    loginWithAuthLoginStubMRN("GB000000000000001"),
    getMRNCdsrStartPage,
    getTheMRNCheckEoriDetailsPage,
    postTheMRNCheckEoriDetailsPage,
    getChooseHowManyMrnsPage,
    postChooseHowManyMrnsPage,
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
    getTheMrnClaimantDetailsCheckPage,
    getTheMrnChangeContactDetailsPage,
    postTheMrnChangeContactDetailsPage,
    getTheMrnClaimantDetailsCheckPage1,
    postTheMrnClaimantDetailsCheckPage,
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
    getSelectReimbursementMethodPage,
    postSelectReimbursementMethodPage,
    getTheMRNCheckTheseBankDetailsAreCorrectPage,
    getTheMRNBankAccountTypePage,
    postTheMRNBankAccountTypePage,
    getTheMRNEnterBankAccountDetailsPage,
    postTheMRNEnterBankAccountDetailsPage,
    getUploadSupportEvidencePage,
    postUploadSupportEvidencePage,
    getScanProgressWaitPage,
    postScanProgressWaitPage
  ) ++
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
    (MRNJourney: _*)

  val MultipleMRNJourney: List[ActionBuilder] = List[ActionBuilder](
    getMRNAuthLoginPage,
    loginWithAuthLoginStubMRN("GB000000000000001"),
    getMRNCdsrStartPage,
    getTheMRNCheckEoriDetailsPage,
    postTheMRNCheckEoriDetailsPage,
    getChooseHowManyMrnsPage,
    postMultipleChooseHowManyMrnsPage,
    getMultipleMrnPage,
    postMultipleMrnPage,
    getMultipleMrnCheckDeclarationPage,
    postMultipleMrnCheckDeclarationPage,
    getMultipleEnterMRNPage2,
    postSubmitMRNPage,
    getMultipleMrnWhoIsDeclarantPage,
    postMultipleClaimMrnWhoIsDeclarantPage,
    getMultipleClaimantDetailsCheckPage,
    getMultipleChangeContactDetailsPage,
    postMultipleChangeContactDetailsPage,
    getMultipleClaimantDetailsCheckPage1,
    postMultipleClaimantDetailsCheckPage,
    getMultipleClaimMrnClaimNorthernIrelandPage,
    postMultipleClaimNorthernIrelandPage,
    getMultipleChooseBasisOfClaimPage,
    postMultipleChooseBasisOfClaimPage,
    getMultipleEnterCommodityDetailsPage,
    postMultipleEnterCommodityDetailsPage,
    getMultipleCheckTheseBankDetailsAreCorrectPage,
    getMultipleBankAccountTypePage,
    postMultipleBankAccountTypePage,
    getMultipleEnterBankAccountDetailsPage,
    postMultipleEnterBankAccountDetailsPage,
    getMultipleUploadSupportEvidencePage,
    postMultipleUploadSupportEvidencePage,
    getMultipleScanProgressWaitPage,
    postMultipleScanProgressWaitPage
  ) ++
    postMultipleScanProgressWaitPage1 ++
    List[ActionBuilder](
      getMultipleSelectSupportingEvidencePage,
      postMultipleSelectSupportingEvidencePage,
      getMultipleCheckYourAnswersPage,
      postMultipleCheckYourAnswersPage,
      getMultipleCheckAnswersAcceptSendPage,
      postMultipleCheckAnswersAcceptSendPage,
      getMultipleClaimSubmittedPage
    )
  setup("Multiple-MRN-journey", "Multiple claims MRN journey") withActions
    (MultipleMRNJourney: _*)

  val MultipleClaimsScheduledMRNJourney: List[ActionBuilder] = List[ActionBuilder](
    getMRNAuthLoginPage,
    loginWithAuthLoginStubMRN("GB000000000000001"),
    getMRNCdsrStartPage,
    getTheMRNCheckEoriDetailsPage,
    postTheMRNCheckEoriDetailsPage,
    getChooseHowManyMrnsPage,
    postBulkScheduledSelectNumberOfClaimsPage,
    getBulkScheduledMrnPage,
    postBulkScheduledMrnPage,
    getBulkScheduledMrnCheckDeclarationPage,
    postBulkScheduledMrnCheckDeclarationPage,
    getScheduledDocumentUploadPage,
    postScheduledDocumentUploadPage,
    getScheduledDocumentUploadProgressPage,
    postScheduledDocumentUploadProgressPage
  ) ++
    postScheduledDocumentUploadProgressPage1 ++
    List[ActionBuilder](
      getScheduledDocumentUploadReviewPage,
      postScheduledDocumentUploadReviewPage,
      getScheduledMrnWhoIsDeclarantPage,
      postScheduledMrnWhoIsDeclarantPage,
      getScheduledClaimantDetailsPage,
      getScheduledMrnChangeContactDetailsPage,
      postScheduledMrnChangeContactDetailsPage,
      getScheduledMrnClaimantDetailsCheckPage1,
      postScheduledMrnClaimantDetailsCheckPage,
      getScheduledMrnClaimNorthernIrelandPage,
      postScheduledMrnClaimNorthernIrelandPage,
      getScheduledMrnChooseBasisOfClaimPage,
      postScheduledMrnChooseBasisOfClaimPage,
      getScheduledMrnEnterCommodityDetailsPage,
      postScheduledMrnEnterCommodityDetailsPage,
      getScheduledMrnSelectDutiesPage,
      postScheduledMrnSelectDutiesPage,
      getScheduledMrnStartClaimPage,
      getScheduledMrnSelectDutiesUkDutyPage,
      postScheduledMrnSelectDutiesUkDutyPage,
      getScheduledMrnSelectDutiesEuDutyPage,
      postScheduledMrnSelectDutiesEuDutyPage,
      getScheduledMrnSelectDutiesBeerPage,
      postScheduledMrnSelectDutiesBeerPage,
      getScheduledMrnSelectDutiesWinePage,
      postScheduledMrnSelectDutiesWinePage,
      getScheduledMrnSelectDutiesMadeWinePage,
      postScheduledMrnSelectDutiesMadeWinePage,
      getScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
      postScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
      getScheduledMrnSelectDutiesSpiritsPage,
      postScheduledMrnSelectDutiesSpiritsPage,
      getScheduledMrnSelectDutiesCiderPerryPage,
      postScheduledMrnSelectDutiesCiderPerryPage,
      getScheduledMrnSelectDutiesHydrocarbonOilsPage,
      postScheduledMrnSelectDutiesHydrocarbonOilsPage,
      getScheduledMrnSelectDutiesBiofuelsPage,
      postScheduledMrnSelectDutiesBiofuelsPage,
      getScheduledMrnSelectDutiesMiscellaneousPage,
      postScheduledMrnSelectDutiesMiscellaneousPage,
      getScheduledMrnSelectDutiesTobaccoPage,
      postScheduledMrnSelectDutiesTobaccoPage,
      getScheduledMrnSelectDutiesClimatePage,
      postScheduledMrnSelectDutiesClimatePage,
      getScheduledMrnStartPage,
      getScheduledMrnUkDutyPage,
      postScheduledMrnUkDutyPage,
      getScheduledMrnEuDutyPage,
      postScheduledMrnEuDutyPage,
      getScheduledMrnBeerPage,
      postScheduledMrnBeerPage,
      getScheduledMrnWinePage,
      postScheduledMrnWinePage,
      getScheduledMrnMadeWinePage,
      postScheduledMrnMadeWinePage,
      getScheduledMrnLowAlcoholPage,
      postScheduledMrnLowAlcoholPage,
      getScheduledMrnSpiritsPage,
      postScheduledMrnSpiritsPage,
      getScheduledMrnCiderPerryPage,
      postScheduledMrnCiderPerryPage,
      getScheduledMrnHydroOilsPage,
      postScheduledMrnHydroOilsPage,
      getScheduledMrnBiofuelsPage,
      postScheduledMrnBiofuelsPage,
      getScheduledMrnRoadFuelsPage,
      postScheduledMrnRoadFuelsPage,
      getScheduledMrnTobaccoPage,
      postScheduledMrnTobaccoPage,
      getScheduledMrnClimateLevyPage,
      postScheduledMrnClimateLevyPage,
      getScheduledMrnCheckClaimPage,
      postScheduledMrnCheckClaimPage,
      getScheduledMrnCheckTheseBankDetailsAreCorrectPage,
      getScheduledMRNBankAccountTypePage,
      postScheduledMRNBankAccountTypePage,
      getScheduledMRNEnterBankAccountDetailsPage,
      postScheduledEnterBankAccountDetailsPage,
      getScheduledUploadSupportEvidencePage,
      postScheduledUploadSupportEvidencePage,
      getScheduledScanProgressWaitPage,
      postScheduledScanProgressWaitPage
    ) ++
    postScheduledScanProgressWaitPage1 ++
    List[ActionBuilder](
      getScheduledSelectSupportingEvidencePage,
      postScheduledSelectSupportingEvidencePage,
      getScheduledCheckYourAnswersPage,
      postScheduledCheckYourAnswersPage,
      getScheduledCheckAnswersAcceptSendPage,
      postScheduledCheckAnswersAcceptSendPage,
      getScheduledClaimSubmittedPage
    )
  setup("Multiple-Claims-Scheduled-MRN-journey", "Multiple claims Scheduled document MRN journey") withActions
    (MultipleClaimsScheduledMRNJourney: _*)
  runSimulation()
}
