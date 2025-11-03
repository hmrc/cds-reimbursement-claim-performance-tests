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

import io.gatling.core.action.builder.ActionBuilder
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.cdsrc.AwesomeStubRequests._
import uk.gov.hmrc.perftests.cdsrc.EntryNumberRequests._
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsBulkMultipleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsScheduledMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsSingleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsMultipleRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsScheduledRequests._
import uk.gov.hmrc.perftests.cdsrc.RejectedGoodsSingleRequests._
import uk.gov.hmrc.perftests.cdsrc.SecuritiesSingleRequests._
import uk.gov.hmrc.perftests.cdsrc.SingleMrnRequests._

class CDSRSimulation extends PerformanceTestRunner {

  val useAwesomeStubs: Boolean =
    readProperty("useAwesomeStubs", "false").toBoolean

  def LoginTheUser(userId: String, eoriValue: String): List[ActionBuilder] =
    if (useAwesomeStubs)
      List(
        getLoginPage,
        loginUser(eoriValue)
      )
    else
      List(getMRNAuthLoginPage, loginWithAuthLoginStubMRN(eoriValue))

  val RejectedGoodsSingleMRNJourney: List[ActionBuilder] =
   LoginTheUser("user1","GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getRejectedGoodsSelectClaimTypePage,
        postRejectedGoodsSelectClaimTypePage,
        getRejectedGoodsChooseHowManyMrnsPage,
        postRejectedGoodsChooseHowManyMrnsPage,
        getRejectedGoodsHaveYourDocumentsReady,
        getRejectedGoodsMRNPage,
        postRejectedGoodsMRNPage,
        getRejectedGoodsImporterEoriEntryPage,
        postRejectedGoodsImporterEoriEntryPage,
        getRejectedGoodsDeclarantEoriEntryPage,
        postRejectedGoodsDeclarantEoriEntryPage,
        getRejectedGoodsCheckDeclarationPage,
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
        getRejectedGoodsEnterClaimDutyPage,
        getRejectedGoodsCheckClaimPage,
        getRejectedGoodsInspectionDatePage,
        postRejectedGoodsInspectionDatePage,
        getRejectedGoodsInspectionAddressChoosePage,
        postRejectedGoodsInspectionAddressChoosePage,
        getRejectedGoodsChoosePayeeTypePage,
        getRejectedGoodsRepaymentMethodPage,
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
        postRejectedGoodsChangeContactDetailsPage,
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
        getRejectedGoodsMultipleHaveYourDocumentsReady,
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
        getRejectedGoodsMultipleInspectionDatePage,
        postRejectedGoodsMultipleInspectionDatePage,
        getRejectedGoodsMultipleInspectionAddressChoosePage,
        postRejectedGoodsMultipleInspectionAddressChoosePage,
        getRejectedGoodsMultipleChoosePayeeTypePage,
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
        getRejectedGoodsMultipleClaimantDetailsPage,
        postRejectedGoodsMultipleChangeContactDetailsPage,
        getRejectedGoodsMultipleCheckYourAnswersPage,
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
        getRejectedGoodsScheduledHaveYourDocumentsReady,
        getRejectedGoodsScheduledMRNPage,
        postRejectedGoodsScheduledMRNPage,
        getRejectedGoodsScheduledImporterEoriEntryPage,
        postRejectedGoodsScheduledImporterEoriEntryPage,
        getRejectedGoodsScheduledDeclarantEoriEntryPage,
        postRejectedGoodsScheduledDeclarantEoriEntryPage,
        getRejectedGoodsScheduledCheckDeclarationPage,
        postRejectedGoodsScheduledCheckDeclarationPage,
        getRejectedGoodsScheduledUploadMrnListPage,
      ) ++
      List[ActionBuilder](
        getRejectedGoodsScheduledChooseBasisForClaimPage,
        postRejectedGoodsScheduledChooseBasisForClaimPage,
        getRejectedGoodsScheduledSpecialCircumstancesPage,
        postRejectedGoodsScheduledSpecialCircumstancesPage,
        getRejectedGoodsScheduledChooseDisposalMethodPage,
        postRejectedGoodsScheduledChooseDisposalMethodPage,
        getRejectedGoodsScheduledEnterRejectedDetailsPage,
        postRejectedGoodsScheduledEnterRejectedDetailsPage,
        getRejectedGoodsScheduledMrnSelectDutiesPage,
        postRejectedGoodsScheduledSelectMrnSelectDutiesPage,
        getRejectedGoodsScheduledMrnSelectDutiesUkDutyPage,
        postRejectedGoodsMrnSelectDutiesUkDutyPage,
        getRejectedGoodsScheduledMrnUkDutyPage,
        postRejectedGoodsScheduledMrnUkDutyPage,
        getRejectedGoodsScheduledMrnSelectDutiesEuDutyPage,
        postRejectedGoodsScheduledMrnSelectDutiesEuDutyPage,
        getRejectedGoodsScheduledMrnEuDutyPage,
        postRejectedGoodsScheduledMrnEuDutyPage,
        getRejectedGoodsScheduledMrnSelectDutiesBeerPage,
        postRejectedGoodsScheduledMrnSelectDutiesBeerPage,
        getRejectedGoodsScheduledMrnBeerPage,
        postRejectedGoodsScheduledMrnBeerPage,
        getRejectedGoodsScheduledMrnSelectDutiesMadeWinePage,
        postRejectedGoodsScheduledMrnSelectDutiesMadeWinePage,
        getRejectedGoodsScheduledMrnMadeWinePage,
        postRejectedGoodsScheduledMrnMadeWinePage,
        getRejectedGoodsScheduledMrnSelectDutiesSpiritsPage,
        postRejectedGoodsScheduledMrnSelectDutiesSpiritsPage,
        getRejectedGoodsScheduledMrnSpiritsPage,
        postRejectedGoodsScheduledMrnSpiritsPage,
        getRejectedGoodsScheduledMrnSelectDutiesOtherFermentedProductsPage,
        postRejectedGoodsScheduledMrnSelectDutiesOtherFermentedProductsPage,
        getRejectedGoodsScheduledMrnOtherFermentedProductsPage,
        postRejectedGoodsScheduledMrnOtherFermentedProductsPage,
        getRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage,
        postRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage,
        getRejectedGoodsScheduledMrnBiofuelsPage,
        postRejectedGoodsScheduledMrnBiofuelsPage,
        getRejectedGoodsScheduledMrnSelectDutiesTobaccoPage,
        postRejectedGoodsScheduledMrnSelectDutiesTobaccoPage,
        getRejectedGoodsScheduledMrnTobaccoPage,
        postRejectedGoodsScheduledMrnTobaccoPage,
        getRejectedGoodsScheduledMrnCheckClaimPage,
        postRejectedGoodsScheduledCheckClaimPage,
        getRejectedGoodsScheduledInspectionDatePage,
        postRejectedGoodsScheduledInspectionDatePage,
        getRejectedGoodsScheduledInspectionAddressChoosePage,
        postRejectedGoodsScheduledInspectionAddressChoosePage,
        getRejectedGoodsScheduledChoosePayeeTypePage,
        getRejectedGoodsScheduledEnterBankDetailsPage,
        postRejectedGoodsScheduledEnterBankDetailsPage,
        getRejectedGoodsScheduledChooseFileTypePage,
        postRejectedGoodsScheduledChooseFileTypesPage,
        getRejectedGoodsScheduledChooseFilesPage,
        getRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        postRejectedGoodsUploadCustomsDocumentsChooseFilePage,
        getRejectedGoodsSingleScanProgressWaitPage,
        getRejectedGoodsScheduledContactDetailsPage,
        postRejectedGoodsScheduledChangeContactDetailsPage,
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getRejectedGoodsScheduledCheckYourAnswersPage,
        postRejectedGoodsScheduledCheckYourAnswersPage,
        getRejectedGoodsScheduledClaimSubmittedPage
      )

  setup("Rejected-Goods-Scheduled-MRN-journey", "Rejected Goods Scheduled MRN journey") withActions
    (RejectedGoodsScheduledMRNJourney: _*)


  val OverPaymentsSingleMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getOverPaymentsMRNCdsrStartPage,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getOverPaymentsSelectClaimTypePage,
        postOverPaymentsSelectClaimTypePage,
        getOverpaymentsChooseHowManyMrnsPage,
        postOverpaymentsChooseHowManyMrnsPage,
        getOverpaymentsMRNPage,
        postOverpaymentsMRNPage,
        getOverpaymentsMRNImporterEoriEntryPage,
        postOverpaymentsMRNImporterEoriEntryPage,
        getOverpaymentsMRNDeclarantEoriEntryPage,
        postOverpaymentsMRNDeclarantEoriEntryPage,
        getOverpaymentsMRNCheckDeclarationPage,
        postOverpaymentsMRNCheckDeclarationPage,
        getOverpaymentsMRNChooseBasisOfClaimPage,
        postOverpaymentsMRNChooseBasisOfClaimPage,
        getOverpaymentsDuplicateMRNPage,
        postOverpaymentsDuplicateMRNPage,
        getOverpaymentsMRNCheckDuplicateDeclarationPage,
        postOverpaymentsMRNCheckDuplicateDeclarationPage,
        getOverpaymentsMRNEnterCommodityDetailsPage,
        postOverpaymentsMRNEnterCommodityDetailsPage,
        getOverpaymentsMRNSelectDutiesPage,
        postOverpaymentsMRNSelectDutiesPage,
        getOverpaymentsMRNEnterClaimPage,
        getOverpaymentsMRNStartClaimPage,
        getOverpaymentsMRNCheckClaimPage,
        getOverpaymentsSelectReimbursementMethodPage,
        postOverpaymentsSelectReimbursementMethodPage,
        getOverpaymentsRepaymentMethodPage,
        getOverpaymentsEnterBankDetailsPage,
        postOverpaymentsEnterBankDetailsPage,
        getOverpaymentsChooseFileTypePage,
        postOverpaymentsChooseFileTypesPage,
        getOverpaymentsChooseFilesPage,
        getOverpaymentsUploadCustomsDocumentsChooseFilePage,
        postOverpaymentsUploadCustomsDocumentsChooseFilePage,
        getOverpaymentsScanProgressWaitPage
      ) ++
      getOverpaymentsFileVerificationStatusPage ++
      List[ActionBuilder](
        postOverpaymentsMrnChangeContactDetailsPage,
        getOverpaymentsCheckYourAnswersPage,
        getOverpaymentsClaimSubmittedPage
      )

  setup("OverPayments-Single-MRN-journey", "Overpayments Single Movement reference number journey") withActions
    (OverPaymentsSingleMRNJourney: _*)

  val OverPaymentsBulkMultipleV2MRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getOverPaymentsSelectClaimTypePage,
        postOverPaymentsSelectClaimTypePage,
        getOverpaymentsChooseHowManyMrnsPage,
        postOverpaymentsMultipleChooseHowManyMrnsPage,
        getOverpaymentsMultipleHaveYourDocumentsReady,
        getOverpaymentsMultipleMrnPage,
        postOverpaymentsMultipleMrnPage,
        getOverpaymentsMultipleMrnCheckDeclarationPage,
        postOverpaymentsMultipleMrnCheckDeclarationPage,
        getOverpaymentsMultipleEnterSecondMRNPage,
        postOverpaymentsMultipleEnterSecondMRNPage,
        getOverpaymentsMultipleCheckMRNPage,
        postOverpaymentsMultipleCheckMRNPage,
        getOverpaymentsMultipleChooseBasisOfClaimPage,
        postOverpaymentsMultipleChooseBasisOfClaimPage,
        getOverpaymentsMultipleEnterCommodityDetailsPage,
        postOverpaymentsMultipleEnterCommodityDetailsPage,
        getOverpaymentsMultipleSelectDutiesOnePage,
        postOverpaymentsMultipleSelectDutiesOnePage,
        getOverpaymentsMultipleSelectDutiesOneDutyPage,
        postOverpaymentsMultipleSelectDutiesOneDutyPage,
        getOverpaymentsMultipleSelectDutiesSecondPage,
        postOverpaymentsMultipleSelectDutiesSecondPage,
        getOverpaymentsMultipleSelectDutiesSecondDutyPage,
        postOverpaymentsMultipleSelectDutiesSecondDutyPage,
        getOverpaymentsMultipleCheckClaimPage,
        getOverpaymentsMultipleChoosePayeeTypePage,
        postOverpaymentsMultipleChoosePayeeTypePage,
        getOverpaymentsMultipleEnterBankAccountDetailsPage,
        postOverpaymentsMultipleEnterBankAccountDetailsPage,
        getOverpaymentsMultipleChooseFileTypePage,
        postOverpaymentsMultipleChooseFileTypesPage,
        getOverpaymentsMultipleChooseFilesPage,
        getOverpaymentsUploadCustomsDocumentsChooseFilePage,
        postOverpaymentsUploadCustomsDocumentsChooseFilePage,
        getOverpaymentsScanProgressWaitPage,
        getOverpaymentsMultipleChangeContactDetailsPage,
        postOverpaymentsMultipleChangeContactDetailsPage,
      ) ++
      getOverpaymentsFileVerificationStatusPage ++
      List[ActionBuilder](
        getOverpaymentsMultipleCheckYourAnswersPage,
        postOverpaymentsMultipleCheckYourAnswersPage,
        getOverpaymentsMultipleClaimSubmittedPage
      )

  setup(
   "OverPayments-Bulk-Multiple-V2-MRN-journey",
    "Overpayments Bulk Multiple journey"
  ) withActions
    (OverPaymentsBulkMultipleV2MRNJourney: _*)

  val OverPaymentsBulkScheduledV2MRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000002") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getOverPaymentsSelectClaimTypePage,
        postOverPaymentsSelectClaimTypePage,
        getOverpaymentsChooseHowManyMrnsPage,
        postOverpaymentsScheduledChooseHowManyMrnsPage,
        getOverpaymentsScheduledHaveYourDocumentsReady,
        getOverpaymentsScheduledMRNPage,
        postOverpaymentsScheduledMrnPage,
        getOverpaymentsScheduledImporterEoriEntryPage,
        postOverpaymentsScheduledImporterEoriEntryPage,
        getOverpaymentsScheduledDeclarantEoriEntryPage,
        postOverpaymentsScheduledDeclarantEoriEntryPage,
        getOverpaymentsScheduledMrnCheckDeclarationPage,
        postOverpaymentsScheduledMrnCheckDeclarationPage,
        getOverpaymentsScheduledUploadMrnListPage,
      ) ++
      List[ActionBuilder](
        getOverpaymentsScheduledChooseBasisForClaimPage,
        postOverpaymentsScheduledChooseBasisForClaimPage,
        getOverpaymentsScheduledEnterAdditionalDetailsPage,
        postOverpaymentsScheduledMrnEnterCommodityDetailsPage,
        getOverpaymentsScheduledMrnSelectDutiesPage,
        postOverpaymentsScheduledMrnSelectDutiesPage,
        getOverpaymentsScheduledMrnSelectDutiesUkDutyPage,
        postOverpaymentsMrnSelectDutiesUkDutyPage,
        getOverpaymentsScheduledMrnUkDutyPage,
        postOverpaymentsScheduledMrnUkDutyPage,
        getOverpaymentsScheduledMrnSelectDutiesEuDutyPage,
        postOverpaymentsScheduledMrnSelectDutiesEuDutyPage,
        getOverpaymentsScheduledMrnEuDutyPage,
        postOverpaymentsScheduledMrnEuDutyPage,
        getScheduledMrnExciseSelectDutiesPage,
        postScheduledMrnExciseSelectDutiesPage,
        getOverpaymentsScheduledMrnSelectDutiesBeerPage,
        postOverpaymentsScheduledMrnSelectDutiesBeerPage,
        getOverpaymentsScheduledMrnBeerPage,
        postOverpaymentsScheduledMrnBeerPage,
        getOverpaymentsScheduledMrnSelectDutiesWinePage,
        postOverpaymentsScheduledMrnSelectDutiesWinePage,
        getOverpaymentsScheduledMrnWinePage,
        postOverpaymentsScheduledMrnWinePage,
        getOverpaymentsScheduledMrnSelectDutiesMadeWinePage,
        postOverpaymentsScheduledMrnSelectDutiesMadeWinePage,
        getOverpaymentsScheduledMrnMadeWinePage,
        postOverpaymentsScheduledMrnMadeWinePage,
        getOverpaymentsScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
        postOverpaymentsScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
        getOverpaymentsScheduledMrnLowAlcoholPage,
        postOverpaymentsScheduledMrnLowAlcoholPage,
        getOverpaymentsScheduledMrnSelectDutiesSpiritsPage,
        postOverpaymentsScheduledMrnSelectDutiesSpiritsPage,
        getOverpaymentsScheduledMrnSpiritsPage,
        postOverpaymentsScheduledMrnSpiritsPage,
        getOverpaymentsScheduledMrnSelectDutiesCiderPerryPage,
        postOverpaymentsScheduledMrnSelectDutiesCiderPerryPage,
        getOverpaymentsScheduledMrnCiderPerryPage,
        postOverpaymentsScheduledMrnCiderPerryPage,
        getOverpaymentsScheduledMrnSelectDutiesOtherFermentedProductsPage,
        postOverpaymentsScheduledMrnSelectDutiesOtherFermentedProductsPage,
        getOverpaymentsScheduledMrnOtherFermentedProductsPage,
        postOverpaymentsScheduledMrnOtherFermentedProductsPage,
        getOverpaymentsScheduledMrnSelectDutiesHydrocarbonOilsPage,
        postOverpaymentsScheduledMrnSelectDutiesHydrocarbonOilsPage,
        getOverpaymentsScheduledMrnHydroOilsPage,
        postOverpaymentsScheduledMrnHydroOilsPage,
        getOverpaymentsScheduledMrnSelectDutiesBiofuelsPage,
        postOverpaymentsScheduledMrnSelectDutiesBiofuelsPage,
        getOverpaymentsScheduledMrnBiofuelsPage,
        postOverpaymentsScheduledMrnBiofuelsPage,
        getOverpaymentsScheduledMrnSelectDutiesMiscellaneousPage,
        postOverpaymentsScheduledMrnSelectDutiesMiscellaneousPage,
        getOverpaymentsScheduledMrnRoadFuelsPage,
        postOverpaymentsScheduledMrnRoadFuelsPage,
        getOverpaymentsScheduledMrnSelectDutiesTobaccoPage,
        postOverpaymentsScheduledMrnSelectDutiesTobaccoPage,
        getOverpaymentsScheduledMrnTobaccoPage,
        postOverpaymentsScheduledMrnTobaccoPage,
        getOverpaymentsScheduledMrnSelectDutiesClimatePage,
        postOverpaymentsScheduledMrnSelectDutiesClimatePage,
        getOverpaymentsScheduledMrnClimateLevyPage,
        postOverpaymentsScheduledMrnClimateLevyPage,
        getOverpaymentsScheduledMrnCheckClaimPage,
        postOverpaymentsScheduledMrnCheckClaimPage,
        getOverpaymentScheduledChoosePayeeTypePage,
        postOverpaymentsScheduledMRNChoosePayeeTypePage,
        getOverpaymentsScheduledMRNEnterBankAccountDetailsPage,
        postOverpaymentsScheduledEnterBankAccountDetailsPage,
        getOverpaymentsScheduledChooseFileTypePage,
        postOverpaymentsScheduledChooseFileTypesPage,
        getOverpaymentsScheduledChooseFilesPage,
        getOverpaymentsUploadCustomsDocumentsChooseFilePage,
        postOverpaymentsUploadCustomsDocumentsChooseFilePage,
        getOverpaymentsScanProgressWaitPage,
        getOverpaymentsScheduledContactDetailsPage,
        postOverpaymentsScheduledChangeContactDetailsPage,
      ) ++
      getOverpaymentsFileVerificationStatusPage ++
      List[ActionBuilder](
        getOverpaymentsScheduledCheckYourAnswersPage,
        postOverpaymentsScheduledCheckYourAnswersPage,
        getOverpaymentsScheduledClaimSubmittedPage
      )

  setup(
    "OverPayments-Bulk-Scheduled-V2-MRN-journey",
    "Overpayments Bulk Scheduled journey"
  ) withActions
    (OverPaymentsBulkScheduledV2MRNJourney: _*)


  val SecuritiesSingleBod4Journey: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage(MRN = "01AAAAAAAAAAAAA111"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage(
          reasonForSecurity = "Authorised-use (Great Britain) or end-use (Northern Ireland) relief"
        ),
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesCheckDeclarationDetailsPage,
        getSecuritiesTotalImportDischargedPage,
        postSecuritiesTotalImportDischargedForBod4Page,
        getAddOtherDocuments,
        postAddOtherDocuments,
        getSecuritiesChoosePayeeTypePage,
        getSecuritiesEnterBankAccountDetailsPage,
        postSecuritiesEnterBankAccountDetailsPage,
        getSecuritiesClaimantDetailsPage,
      ) ++
      List[ActionBuilder](
        getSecuritiesCheckYourAnswersPage,
        postSecuritiesCheckYourAnswersPage,
        getSecuritiesClaimSubmittedPage
      )
  setup("Securities-Single-MRN-BOD4-journey", "Securities Single MRN with BOD4 journey") withActions
    (SecuritiesSingleBod4Journey: _*)

  val SecuritiesSingleBOD3Journey: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage(MRN = "01AAAAAAAAAAAAA111"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage(reasonForSecurity = "Inward-processing relief (IPR)"),
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesCheckDeclarationDetailsPage,
        getSecuritiesTotalImportDischargedPage,
        postSecuritiesTotalImportDischargedForBod3Page,
        getAddOtherDocuments,
        postAddOtherDocuments,
        getSecuritiesChoosePayeeTypePage,
        getSecuritiesEnterBankAccountDetailsPage,
        postSecuritiesEnterBankAccountDetailsPage,
        getSecuritiesClaimantDetailsPage,
      ) ++
      List[ActionBuilder](
        getSecuritiesCheckYourAnswersPage,
        postSecuritiesCheckYourAnswersPage,
        getSecuritiesClaimSubmittedPage
      )
  setup("Securities-Single-MRN-BOD3-journey", "Securities Single MRN with BOD3 journey") withActions
    (SecuritiesSingleBOD3Journey: _*)

  val SecuritiesSingleTemporaryAdmissionsJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage(MRN = "01AAAAAAAAAAAAA111"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage(reasonForSecurity = "Temporary Admission (2 months)"),
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesTACheckDeclarationDetailsPage,
        getSecuritiesHaveYourDocumentsReady,
        getSecuritiesConfirmPaymentExportMethodPage,
        getSecuritiesPartialClaimPage,
        postSecuritiesPartialClaimPage,
        getSecuritiesSelectDutyPage,
        getSecuritiesConfirmDutyRepaymentPage,
        postSecuritiesEnterClaimRedTaxCodePage,
        postSecuritiesEnterClaimTaxCodePage,
        getSecuritiesCheckClaimPage,
        postSecuritiesCheckClaimPage,
        getSecuritiesExportMethodPage,
        getSecuritiesExportMRNPage,
        postSecuritiesExportMRNPage,
        getSecuritiesChoosePayeeTypePage,
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
        getSecuritiesClaimantDetailsPage,
        postSecuritiesClaimantDetailsPage,
        getSecuritiesCheckYourAnswersPage,
        postSecuritiesCheckYourAnswersPage,
        getSecuritiesClaimSubmittedPage
      )
  setup("Securities-Single-MRN-TA-journey", "Securities Single MRN with Temporary Admissions journey") withActions
    (SecuritiesSingleTemporaryAdmissionsJourney: _*)

  runSimulation()
}
