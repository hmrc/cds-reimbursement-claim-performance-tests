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
import uk.gov.hmrc.perftests.cdsrc.AwesomeStubRequests._
import uk.gov.hmrc.perftests.cdsrc.MultipleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.BulkScheduledMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.EntryNumberRequests._
import uk.gov.hmrc.perftests.cdsrc.SingleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsSingleRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsMultipleRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsScheduledRequests._
import uk.gov.hmrc.perftests.cdsrc.SecuritiesSingleRequests._

class CDSRSimulation extends PerformanceTestRunner {

  val useAwesomeStubs: Boolean =
    readProperty("useAwesomeStubs", "false").toBoolean

  def LoginTheUser(userId: String, eoriValue: String): List[ActionBuilder] =
    if (useAwesomeStubs)
      List(
        getLoginPage,
        loginUser(userId),
        updateUserRole(eoriValue),
        postSuccessfulLogin
      )
    else
      List(getMRNAuthLoginPage, loginWithAuthLoginStubMRN(eoriValue))

  val MRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
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
        getScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getCheckAnswersAcceptSendPage,
        postCheckAnswersAcceptSendPage,
        getClaimSubmittedPage
      )

  setup("MRN-journey", "Movement reference number journey") withActions
    (MRNJourney: _*)

  val MultipleMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
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
        getScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getMultipleCheckAnswersAcceptSendPage,
        postMultipleCheckAnswersAcceptSendPage,
        getMultipleClaimSubmittedPage
      )
  setup("Multiple-MRN-journey", "Multiple claims MRN journey") withActions
    (MultipleMRNJourney: _*)

  val MultipleClaimsScheduledMRNJourney: List[ActionBuilder] =
    LoginTheUser("user2", "GB000000000000002") ++
      List[ActionBuilder](
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
        getScheduledDocumentUploadProgressPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
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
        getUploadDocumentsChooseFilePage,
        postUploadDocumentsChoosefilesPage,
        getScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage1 ++
      List[ActionBuilder](
        getScheduledCheckAnswersAcceptSendPage,
        postScheduledCheckAnswersAcceptSendPage,
        getScheduledClaimSubmittedPage
      )
  setup("Multiple-Claims-Scheduled-MRN-journey", "Multiple claims Scheduled document MRN journey") withActions
    (MultipleClaimsScheduledMRNJourney: _*)

  val RejectedGoodsSingleMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
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
        getRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        postRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        getRejectedGoodsSingleScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getRejectedGoodsCheckYourAnswersPage,
        postRejectedGoodsCheckYourAnswersPage,
        getRejectedGoodsClaimSubmittedPage
      )

  setup("Rejected-Goods-Single-MRN-journey", "Rejected Goods Single MRN journey") withActions
    (RejectedGoodsSingleMRNJourney: _*)

  val RejectedGoodsMultipleMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
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
        getRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        postRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        getRejectedGoodsSingleScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getRejectedGoodsMultipleCheckYourAnswersPage,
        postRejectedGoodsMultipleCheckYourAnswersPage,
        getRejectedGoodsMultipleClaimSubmittedPage
      )

  setup("Rejected-Goods-Multiple-MRN-journey", "Rejected Goods Multiple MRN journey") withActions
    (RejectedGoodsMultipleMRNJourney: _*)

  val RejectedGoodsScheduledMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getRejectedGoodsScheduledSelectClaimTypePage,
        postRejectedGoodsScheduledSelectClaimTypePage,
        getRejectedGoodsScheduledChooseHowManyMrnsPage,
        postRejectedGoodsScheduledChooseHowManyMrnsPage,
        getRejectedGoodsScheduledMRNPage,
        postRejectedGoodsScheduledMRNPage,
        getRejectedGoodsScheduledImporterEoriEntryPage,
        postRejectedGoodsScheduledImporterEoriEntryPage,
        getRejectedGoodsScheduledDeclarantEoriEntryPage,
        postRejectedGoodsScheduledDeclarantEoriEntryPage,
        getRejectedGoodsScheduledCheckDeclarationPage,
        postRejectedGoodsScheduledCheckDeclarationPage,
        getRejectedGoodsScheduledUploadMrnListPage,
        //getRejectedGoodsScheduledDocumentUploadChooseFilesPage,
        getScheduledUploadDocumentsChooseFilePage,
        postScheduledUploadDocumentsChooseFilePagePage,
        getScheduledDocumentUploadProgressPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getRejectedGoodsScheduledClaimantDetailsPage,
        getRejectedGoodsScheduledContactDetailsPage,
        postRejectedGoodsScheduledChangeContactDetailsPage,
        postRejectedGoodsScheduledClaimDetailsPage,
        getRejectedGoodsScheduledChooseBasisForClaimPage,
        postRejectedGoodsScheduledChooseBasisForClaimPage,
        getRejectedGoodsScheduledSpecialCircumstancesPage,
        postRejectedGoodsScheduledSpecialCircumstancesPage,
        getRejectedGoodsScheduledChooseDisposalMethodPage,
        postRejectedGoodsScheduledChooseDisposalMethodPage,
        getRejectedGoodsScheduledEnterRejectedDetailsPage,
        postRejectedGoodsScheduledEnterRejectedDetailsPage,
        getRejectedGoodsScheduledMrnSelectDutiesPage,
        postRejectedGoodsScheduledMrnSelectDutiesPage,
        getRejectedGoodsScheduledMrnSelectDutiesUkDutyPage,
        postRejectedGoodsScheduledMrnSelectDutiesUkDutyPage,
        getRejectedGoodsScheduledMrnSelectDutiesEuDutyPage,
        postRejectedGoodsScheduledMrnSelectDutiesEuDutyPage,
        getRejectedGoodsScheduledMrnSelectDutiesBeerPage,
        postRejectedGoodsScheduledMrnSelectDutiesBeerPage,
        getRejectedGoodsScheduledMrnSelectDutiesWinePage,
        postRejectedGoodsScheduledMrnSelectDutiesWinePage,
        getRejectedGoodsScheduledMrnSelectDutiesMadeWinePage,
        postRejectedGoodsScheduledMrnSelectDutiesMadeWinePage,
        getRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
        postRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
        getRejectedGoodsScheduledMrnSelectDutiesSpiritsPage,
        postRejectedGoodsScheduledMrnSelectDutiesSpiritsPage,
        getRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage,
        postRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage,
        getRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage,
        postRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage,
        getRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage,
        postRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage,
        getRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage,
        postRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage,
        getRejectedGoodsScheduledMrnSelectDutiesTobaccoPage,
        postRejectedGoodsScheduledMrnSelectDutiesTobaccoPage,
        getRejectedGoodsScheduledMrnSelectDutiesClimatePage,
        postRejectedGoodsScheduledMrnSelectDutiesClimatePage,
        getRejectedGoodsScheduledMrnStartPage,
        getRejectedGoodsScheduledMrnUkDutyPage,
        postRejectedGoodsScheduledMrnUkDutyPage,
        getRejectedGoodsScheduledMrnEuDutyPage,
        postRejectedGoodsScheduledMrnEuDutyPage,
        getRejectedGoodsScheduledMrnBeerPage,
        postRejectedGoodsScheduledMrnBeerPage,
        getRejectedGoodsScheduledMrnWinePage,
        postRejectedGoodsScheduledMrnWinePage,
        getRejectedGoodsScheduledMrnMadeWinePage,
        postRejectedGoodsScheduledMrnMadeWinePage,
        getRejectedGoodsScheduledMrnLowAlcoholPage,
        postRejectedGoodsScheduledMrnLowAlcoholPage,
        getRejectedGoodsScheduledMrnSpiritsPage,
        postRejectedGoodsScheduledMrnSpiritsPage,
        getRejectedGoodsScheduledMrnCiderPerryPage,
        postRejectedGoodsScheduledMrnCiderPerryPage,
        getRejectedGoodsScheduledMrnHydroOilsPage,
        postRejectedGoodsScheduledMrnHydroOilsPage,
        getRejectedGoodsScheduledMrnBiofuelsPage,
        postRejectedGoodsScheduledMrnBiofuelsPage,
        getRejectedGoodsScheduledMrnRoadFuelsPage,
        postRejectedGoodsScheduledMrnRoadFuelsPage,
        getRejectedGoodsScheduledMrnTobaccoPage,
        postRejectedGoodsScheduledMrnTobaccoPage,
        getRejectedGoodsScheduledMrnClimateLevyPage,
        postRejectedGoodsScheduledMrnClimateLevyPage,
        getRejectedGoodsScheduledCheckClaimPage,
        postRejectedGoodsScheduledCheckClaimPage,
        getRejectedGoodsScheduledInspectionDatePage,
        postRejectedGoodsScheduledInspectionDatePage,
        getRejectedGoodsScheduledInspectionAddressChoosePage,
        postRejectedGoodsScheduledInspectionAddressChoosePage,
        getRejectedGoodsScheduledCheckBankDetailsPage,
        getRejectedGoodsScheduledBankAccountTypePage,
        postRejectedGoodsScheduledBankAccountTypePage,
        getRejectedGoodsScheduledEnterBankDetailsPage,
        postRejectedGoodsScheduledEnterBankDetailsPage,
        getRejectedGoodsScheduledChooseFileTypePage,
        postRejectedGoodsScheduledChooseFileTypesPage,
        getRejectedGoodsScheduledChooseFilesPage,
        getRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        postRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        getRejectedGoodsSingleScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getRejectedGoodsScheduledCheckYourAnswersPage,
        postRejectedGoodsScheduledCheckYourAnswersPage,
        getRejectedGoodsScheduledClaimSubmittedPage
      )

  setup("Rejected-Goods-Scheduled-MRN-journey", "Rejected Goods Scheduled MRN journey") withActions
    (RejectedGoodsScheduledMRNJourney: _*)

  val SecuritiesSingleJourney : List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage,
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage,
        getSecuritiesTotalImportDischargedPage,
        postSecuritiesTotalImportDischargedPage,
        getSecuritiesBod4MandatoryCheckPage,
        postSecuritiesBod4MandatoryCheckPage,
        getSecuritiesSelectSecuritiesPage,
        getSecuritiesSelectSecurities1Page,
        postSecuritiesSelectSecurities1Page,
        getSecuritiesSelectSecurities2Page,
        postSecuritiesSelectSecurities2Page,
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesCheckDeclarationDetailsPage,
        getSecuritiesClaimantDetailsPage,
        postSecuritiesClaimantDetailsPage,
        getSecuritiesConfirmFullRepaymentPage,
        getSecuritiesConfirmFullRepayment1of2Page,
        postSecuritiesConfirmFullRepayment1of2Page,
        getSecuritiesConfirmFullRepayment2of2Page,
        postSecuritiesConfirmFullRepayment2of2Page,
        getSecuritiesSelectDutiesPage,
        postSecuritiesSelectDutiesPage,
        getSecuritiesEnterClaimPage,
        getSecuritiesEnterClaimTaxCodePage,
        postSecuritiesEnterClaimTaxCodePage,
        getSecuritiesCheckClaimPage,
        postSecuritiesCheckClaimPage,
        getSecuritiesCheckBankDetailsPage,
        getSecuritiesLetterOfAuthorityPage,
        postSecuritiesLetterOfAuthorityPage,
        getSecuritiesChooseBankAccountTypePage,
        postSecuritiesChooseBankAccountTypePage,
        getSecuritiesEnterBankAccountDetailsPage,
        postSecuritiesEnterBankAccountDetailsPage,
        getSecuritiesChooseFileTypePage,
        postSecuritiesChooseFileTypePage,
        getSecuritiesChooseFilesPage,
        getSecuritiesCustomsDocumentsChooseFilePage,
        postSecuritiesUploadCustomsDocumentsChooseFilePage,
        getSecuritiesSingleScanProgressWaitPage
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getSecuritiesCheckYourAnswersPage,
        postSecuritiesCheckYourAnswersPage,
        getSecuritiesClaimSubmittedPage
      )
  setup("Securities-Single-MRN-journey", "Securities Single MRN journey") withActions
    (SecuritiesSingleJourney: _*)
  runSimulation()
}
