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

import io.gatling.core.action.builder.ActionBuilder
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.cdsrc.MultipleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.BulkScheduledMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.EntryNumberRequests._
import uk.gov.hmrc.perftests.cdsrc.SingleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsSingleRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsMultipleRequests._

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
    getSelectClaimTypePage,
    postSelectClaimTypePage,
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
    getTheMRNClaimantDetailsPage,
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
    getSelectSelectSupportingEvidenceTypePage,
    postSelectSupportingEvidenceTypePage,
    getSupportingEvidenceChooseFilesPage,
    getUploadDocumentsChooseFilePage,
    postUploadDocumentsChoosefilesPage,
    getScanProgressWaitPage,
    postScanProgressWaitPage
  ) ++
    postScanProgressWaitPage1 ++
    List[ActionBuilder](
      getUploadDocumentsSummaryPage,
      postUploadDocumentsSummaryPage,
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
    getSelectClaimTypePage,
    postSelectClaimTypePage,
    getChooseHowManyMrnsPage,
    postMultipleChooseHowManyMrnsPage,
    getMultipleMrnPage,
    postMultipleMrnPage,
    getMultipleMrnCheckDeclarationPage,
    postMultipleMrnCheckDeclarationPage,
    getMultipleEnterSecondMRNPage,
    postMultipleEnterSecondMRNPage,
    getMultipleCheckMRNPage,
    postMultipleCheckMRNPage,
    getMultipleMrnClaimantDetailsPage,
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
    getMultipleSelectDutiesOnePage,
    postMultipleSelectDutiesOnePage,
    getMultipleSelectDutiesOneDutyPage,
    postMultipleSelectDutiesOneDutyPage,
    getMultipleSelectDutiesSecondPage,
    postMultipleSelectDutiesSecondPage,
    getMultipleCheckClaimPage,
    postMultipleCheckClaimPage,
    getMultipleCheckTheseBankDetailsAreCorrectPage,
    getMultipleBankAccountTypePage,
    postMultipleBankAccountTypePage,
    getMultipleEnterBankAccountDetailsPage,
    postMultipleEnterBankAccountDetailsPage,
    getMultipleSelectSupportingEvidenceTypePage,
    postMultipleSelectSupportingEvidenceTypePage,
    getMultipleSupportingEvidenceChooseFilesPage,
    getUploadDocumentsChooseFilePage,
    postUploadDocumentsChoosefilesPage,
    getMultipleScanProgressWaitPage,
    postMultipleScanProgressWaitPage
  ) ++
    postMultipleScanProgressWaitPage1 ++
    List[ActionBuilder](
      getUploadDocumentsSummaryPage,
      postUploadDocumentsSummaryPage,
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
    getSelectClaimTypePage,
    postSelectClaimTypePage,
    getChooseHowManyMrnsPage,
    postBulkScheduledSelectNumberOfClaimsPage,
    getBulkScheduledMrnPage,
    postBulkScheduledMrnPage,
    getBulkScheduledMrnCheckDeclarationPage,
    postBulkScheduledMrnCheckDeclarationPage,
    getBulkScheduledDocumentUploadChooseFilesPage,
    getScheduledUploadDocumentsChooseFilePage,
    postScheduledUploadDocumentsChooseFilePagePage,
    getScheduledDocumentUploadProgressPage,
    postScheduledDocumentUploadProgressPage
  ) ++
    postScheduledDocumentUploadProgressPage1 ++
    List[ActionBuilder](
      getScheduledUploadDocumentsSummaryPage,
      postScheduledUploadDocumentsSummaryPage,
      getScheduledDocumentsUploadContinuePage,
      getScheduledMrnClaimantDetailsPage,
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
      getScheduledSelectSupportingEvidenceTypePage,
      postScheduledSelectSupportingEvidenceTypePage,
      getScheduledSupportingChooseFilesPage,
      getScheduledUploadSupportEvidencePage,
      postScheduledUploadSupportEvidencePage,
      getScheduledScanProgressWaitPage,
      postScheduledScanProgressWaitPage
    ) ++
    postScheduledScanProgressWaitPage1 ++
    List[ActionBuilder](
      getScheduledUploadDocumentsSummaryPage1,
      postScheduledUploadDocumentsSummaryPagePage,
      getScheduledCheckAnswersAcceptSendPage,
      postScheduledCheckAnswersAcceptSendPage,
      getScheduledClaimSubmittedPage
    )
  setup("Multiple-Claims-Scheduled-MRN-journey", "Multiple claims Scheduled document MRN journey") withActions
    (MultipleClaimsScheduledMRNJourney: _*)

  val RejectedGoodsSingleMRNJourney: List[ActionBuilder] = List[ActionBuilder](
    getMRNAuthLoginPage,
    loginWithAuthLoginStubMRN("GB000000000000001"),
    getMRNCdsrStartPage,
    getTheMRNCheckEoriDetailsPage,
    postTheMRNCheckEoriDetailsPage,
    getRejectedGoodsSelectClaimTypePage,
    postRejectedGoodsSelectClaimTypePage,
    getRejectedGoodsChooseHowManyMrnsPage,
    postRejectedGoodsChooseHowManyMrnsPage,
    getRejectedGoodsMRNPage,
    postRejectedGoodsMRNPage,
    getRejectedGoodsImporterEoriEntryPage,
    postRejectedGoodsImporterEoriEntryPage,
    getRejectedGoodsDeclarantEoriEntryPage,
    postRejectedGoodsDeclarantEoriEntryPage,
    getRejectedGoodsCheckDeclarationPage,
    postRejectedGoodsCheckDeclarationPage,
    getRejectedGoodsClaimantDetailsPage,
    getRejectedGoodsContactDetailsPage,
    postRejectedGoodsChangeContactDetailsPage,
    getRejectedGoodsClaimantDetailsPage1,
    postRejectedGoodsClaimDetailsPage,
    getRejectedGoodsChooseBasisForClaimPage,
    postRejectedGoodsChooseBasisForClaimPage,
    getRejectedGoodsSpecialCircumstancesPage,
    postRejectedGoodsSpecialCircumstancesPage,
    getRejectedGoodsChooseDisposalMethodPage,
    postRejectedGoodsChooseDisposalMethodPage,
    getRejectedGoodsEnterRejectedDetailsPage,
    postRejectedGoodsEnterRejectedDetailsPage,
    getRejectedGoodsSelectDutiesPage,
    postRejectedGoodsSelectDutiesPage,
    getRejectedGoodsEnterClaimPage,
    postRejectedGoodsEnterClaimPage,
    getRejectedGoodsCheckClaimPage,
    postRejectedGoodsCheckClaimPage,
    getRejectedGoodsInspectionDatePage,
    postRejectedGoodsInspectionDatePage,
    getRejectedGoodsInspectionAddressChoosePage,
    postRejectedGoodsInspectionAddressChoosePage,
    getRejectedGoodsRepaymentMethodPage,
    postRejectedGoodsRepaymentMethodPage,
    getRejectedGoodsCheckBankDetailsPage,
    getRejectedGoodsBankAccountTypePage,
    postRejectedGoodsBankAccountTypePage,
    getRejectedGoodsEnterBankDetailsPage,
    postRejectedGoodsEnterBankDetailsPage,
    getRejectedGoodsChooseFileTypePage,
    postRejectedGoodsChooseFileTypesPage,
    getRejectedGoodsChooseFilesPage,
    getRejectedGoodsChooseFilePage,
    getRejectedGoodsSingleUploadDocumentsChooseFilePage,
    postRejectedGoodsSingleUploadDocumentsChooseFilePage,
    getRejectedGoodsSingleScanProgressWaitPage,
    postRejectedGoodsSingleScanProgressWaitPage
  )++
    postRejectedGoodsSingleScanProgressWaitPage1 ++
    List[ActionBuilder](
      getRejectedGoodsDocumentsSummaryPage,
      postRejectedGoodsDocumentsSummaryPage,
      getRejectedGoodsCheckYourAnswersPage,
      postRejectedGoodsCheckYourAnswersPage,
      getRejectedGoodsClaimSubmittedPage
    )

  setup("Rejected-Goods-Single-MRN-journey", "Rejected Goods Single MRN journey") withActions
    (RejectedGoodsSingleMRNJourney: _*)

  val RejectedGoodsMultipleMRNJourney: List[ActionBuilder] = List[ActionBuilder](
    getMRNAuthLoginPage,
    loginWithAuthLoginStubMRN("GB000000000000001"),
    getMRNCdsrStartPage,
    getTheMRNCheckEoriDetailsPage,
    postTheMRNCheckEoriDetailsPage,
    getRejectedGoodsMultipleSelectClaimTypePage,
    postRejectedGoodsMultipleSelectClaimTypePage,
    getRejectedGoodsMultipleChooseHowManyMrnsPage,
    postRejectedGoodsMultipleChooseHowManyMrnsPage,
    getRejectedGoodsMultipleMRNPage,
    postRejectedGoodsMultipleMRNPage,
    getRejectedGoodsMultipleImporterEoriEntryPage,
    postRejectedGoodsMultipleImporterEoriEntryPage,
    getRejectedGoodsMultipleDeclarantEoriEntryPage,
    postRejectedGoodsMultipleDeclarantEoriEntryPage,
    getRejectedGoodsMultipleCheckDeclarationPage,
    postRejectedGoodsMultipleCheckDeclarationPage,
    getRejectedGoodsMultipleMRN2Page,
    postRejectedGoodsMultipleMRN2Page,
    getRejectedGoodsCheckMRNsPage,
    postRejectedGoodsCheckMRNsPage,
    getRejectedGoodsMultipleClaimantDetailsPage,
    getRejectedGoodsMultipleContactDetailsPage,
    postRejectedGoodsMultipleChangeContactDetailsPage,
    getRejectedGoodsMultipleClaimantDetailsPage1,
    postRejectedGoodsMultipleClaimDetailsPage,
    getRejectedGoodsMultipleChooseBasisForClaimPage,
    postRejectedGoodsMultipleChooseBasisForClaimPage,
    getRejectedGoodsMultipleSpecialCircumstancesPage,
    postRejectedGoodsMultipleSpecialCircumstancesPage,
    getRejectedGoodsMultipleChooseDisposalMethodPage,
    postRejectedGoodsMultipleChooseDisposalMethodPage,
    getRejectedGoodsMultipleEnterRejectedDetailsPage,
    postRejectedGoodsMultipleEnterRejectedDetailsPage,
    getRejectedGoodsMultipleSelectDutiesPage,
    postRejectedGoodsMultipleSelectDutiesOnePage,
    getRejectedGoodsMultipleEnterClaimDutyOnePage,
    postRejectedGoodsMultipleEnterClaimDutyOnePage,
    getRejectedGoodsMultipleSelectDutiesTwoPage,
    postRejectedGoodsMultipleSelectDutiesTwoPage,
    getRejectedGoodsMultipleEnterClaimDutyTwoPage,
    postRejectedGoodsMultipleEnterClaimDutyTwoPage,
    getRejectedGoodsMultipleCheckClaimPage,
    postRejectedGoodsMultipleCheckClaimPage,
    getRejectedGoodsMultipleInspectionDatePage,
    postRejectedGoodsMultipleInspectionDatePage,
    getRejectedGoodsMultipleInspectionAddressChoosePage,
    postRejectedGoodsMultipleInspectionAddressChoosePage,
    getRejectedGoodsMultipleCheckBankDetailsPage,
    getRejectedGoodsMultipleBankAccountTypePage,
    postRejectedGoodsMultipleBankAccountTypePage,
    getRejectedGoodsMultipleEnterBankDetailsPage,
    postRejectedGoodsMultipleEnterBankDetailsPage,
    getRejectedGoodsMultipleChooseFileTypePage,
    postRejectedGoodsMultipleChooseFileTypesPage,
    getRejectedGoodsMultipleChooseFilesPage,
    getRejectedGoodsMultipleChooseFilePage,
    getRejectedGoodsMultipleUploadDocumentsChooseFilePage,
    postRejectedGoodsMultipleUploadDocumentsChooseFilePage,
    getRejectedGoodsMultipleScanProgressWaitPage,
    postRejectedGoodsMultipleScanProgressWaitPage
  )++
    postRejectedGoodsMultipleScanProgressWaitPage1 ++
    List[ActionBuilder](
      getRejectedGoodsMultipleDocumentsSummaryPage,
      postRejectedGoodsMultipleDocumentsSummaryPage,
      getRejectedGoodsMultipleCheckYourAnswersPage,
      postRejectedGoodsMultipleCheckYourAnswersPage,
      getRejectedGoodsMultipleClaimSubmittedPage
    )

      setup("Rejected-Goods-Multiple-MRN-journey", "Rejected Goods Multiple MRN journey") withActions
      (RejectedGoodsMultipleMRNJourney: _*)
  runSimulation()
}
