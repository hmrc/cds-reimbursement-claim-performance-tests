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
import uk.gov.hmrc.perftests.cdsrc.MultipleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.BulkScheduledMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.EntryNumberRequests._
import uk.gov.hmrc.perftests.cdsrc.FeatureSwitch._
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsScheduledMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsBulkMultipleMrnRequests._
import uk.gov.hmrc.perftests.cdsrc.OverPaymentsSingleMrnRequests._
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
//        loginUser(userId),
        updateUserRole(eoriValue),
//        postSuccessfulLogin
      )
    else
      List(getMRNAuthLoginPage, loginWithAuthLoginStubMRN(eoriValue))

  val RejectedGoodsSingleMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
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
        getRejectedGoodsEnterClaimDutyPage,
        postRejectedGoodsEnterClaimDutyPage,
        getRejectedGoodsCheckClaimPage,
        postRejectedGoodsCheckClaimPage,
        getRejectedGoodsInspectionDatePage,
        postRejectedGoodsInspectionDatePage,
        getRejectedGoodsInspectionAddressChoosePage,
        postRejectedGoodsInspectionAddressChoosePage,
//        getRejectedGoodsRepaymentMethodPage,
//        postRejectedGoodsRepaymentMethodPage,
        getRejectedGoodsChoosePayeeTypePage,
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
        cdsOverPaymentsV2Disable,
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
        getRejectedGoodsMultipleChoosePayeeTypePage,
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
        cdsOverPaymentsV2Disable,
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
        getRejectedGoodsScheduledMrnSelectDutiesWinePage,
        postRejectedGoodsScheduledMrnSelectDutiesWinePage,
        getRejectedGoodsScheduledMrnWinePage,
        postRejectedGoodsScheduledMrnWinePage,
        getRejectedGoodsScheduledMrnSelectDutiesMadeWinePage,
        postRejectedGoodsScheduledMrnSelectDutiesMadeWinePage,
        getRejectedGoodsScheduledMrnMadeWinePage,
        postRejectedGoodsScheduledMrnMadeWinePage,
        getRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
        postRejectedGoodsScheduledMrnSelectDutiesLowAlcoholBeveragesPage,
        getRejectedGoodsScheduledMrnLowAlcoholPage,
        postRejectedGoodsScheduledMrnLowAlcoholPage,
        getRejectedGoodsScheduledMrnSelectDutiesSpiritsPage,
        postRejectedGoodsScheduledMrnSelectDutiesSpiritsPage,
        getRejectedGoodsScheduledMrnSpiritsPage,
        postRejectedGoodsScheduledMrnSpiritsPage,
        getRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage,
        postRejectedGoodsScheduledMrnSelectDutiesCiderPerryPage,
        getRejectedGoodsScheduledMrnCiderPerryPage,
        postRejectedGoodsScheduledMrnCiderPerryPage,
        getRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage,
        postRejectedGoodsScheduledMrnSelectDutiesHydrocarbonOilsPage,
        getRejectedGoodsScheduledMrnHydroOilsPage,
        postRejectedGoodsScheduledMrnHydroOilsPage,
        getRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage,
        postRejectedGoodsScheduledMrnSelectDutiesBiofuelsPage,
        getRejectedGoodsScheduledMrnBiofuelsPage,
        postRejectedGoodsScheduledMrnBiofuelsPage,
        getRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage,
        postRejectedGoodsScheduledMrnSelectDutiesMiscellaneousPage,
        getRejectedGoodsScheduledMrnRoadFuelsPage,
        postRejectedGoodsScheduledMrnRoadFuelsPage,
        getRejectedGoodsScheduledMrnSelectDutiesTobaccoPage,
        postRejectedGoodsScheduledMrnSelectDutiesTobaccoPage,
        getRejectedGoodsScheduledMrnTobaccoPage,
        postRejectedGoodsScheduledMrnTobaccoPage,
        getRejectedGoodsScheduledMrnSelectDutiesClimatePage,
        postRejectedGoodsScheduledMrnSelectDutiesClimatePage,
        getRejectedGoodsScheduledMrnClimateLevyPage,
        postRejectedGoodsScheduledMrnClimateLevyPage,
        getRejectedGoodsScheduledCheckClaimPage,
        postRejectedGoodsScheduledCheckClaimPage,
        getRejectedGoodsScheduledInspectionDatePage,
        postRejectedGoodsScheduledInspectionDatePage,
        getRejectedGoodsScheduledInspectionAddressChoosePage,
        postRejectedGoodsScheduledInspectionAddressChoosePage,
        getRejectedGoodsScheduledChoosePayeeTypePage,
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

  val SecuritiesSingleBod4Journey : List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage(MRN = "01AAAAAAAAAAAAAAA1"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage(trueOrFalse = true, reasonForSecurity = "End Use Relief"),
        getSecuritiesTotalImportDischargedPage,
        postSecuritiesTotalImportDischargedForBod4Page,
        getSecuritiesBod4MandatoryCheckPage,
        postSecuritiesBod4MandatoryCheckPage,
        getSecuritiesSelectSecuritiesPage,
        getSecuritiesSelectSecurities1Page,
        postSecuritiesSelectSecurities1Page,
        getSecuritiesSelectSecurities2Page,
        postSecuritiesSelectSecurities2Page,
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesCheckDeclarationDetailsPage(trueOrFalse = true),
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
  setup("Securities-Single-MRN-BOD4-journey", "Securities Single MRN with BOD4 journey") withActions
    (SecuritiesSingleBod4Journey: _*)

  val SecuritiesSingleBOD3Journey : List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage(MRN = "01AAAAAAAAAAAAAAA1"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage(trueOrFalse = true, reasonForSecurity = "Inward Processing Relief"),
        getSecuritiesTotalImportDischargedPage,
        postSecuritiesTotalImportDischargedForBod3Page,
        getSecuritiesBod3MandatoryCheckPage,
        postSecuritiesBod3MandatoryCheckPage,
        getSecuritiesSelectSecuritiesPage,
        getSecuritiesSelectSecurities1Page,
        postSecuritiesSelectSecurities1Page,
        getSecuritiesSelectSecurities2Page,
        postSecuritiesSelectSecurities2Page,
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesCheckDeclarationDetailsPage(trueOrFalse = true),
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
  setup("Securities-Single-MRN-BOD3-journey", "Securities Single MRN with BOD3 journey") withActions
    (SecuritiesSingleBOD3Journey: _*)

  val SecuritiesSingleTemporaryAdmissionsJourney : List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
          postSecuritiesEnterMovementReferenceNumberPage(MRN = "01AAAAAAAAAAAAAAA1"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityPage(trueOrFalse = false, reasonForSecurity = "Temporary Admissions (2 months Expiration)"),
        getSecuritiesSelectSecuritiesPage,
        getSecuritiesSelectSecurities1Page,
        postSecuritiesSelectSecurities1Page,
        getSecuritiesSelectSecurities2Page,
        postSecuritiesSelectSecurities2Page,
        getSecuritiesCheckDeclarationDetailsPage,
        postSecuritiesCheckDeclarationDetailsPage(trueOrFalse = false),
        getSecuritiesExportMethodPage,
        postSecuritiesExportMethodPage,
        getSecuritiesExportMRNPage,
        postSecuritiesExportMRNPage,
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
  setup("Securities-Single-MRN-TA-journey", "Securities Single MRN with Temporary Admissions journey") withActions
    (SecuritiesSingleTemporaryAdmissionsJourney: _*)

  val SecuritiesSingleAcc14Error086Journey : List[ActionBuilder] =
    LoginTheUser("user1", "AA12345678901234Z") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage("41ABCDEFGHIJKLMNO1"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityForErrorPage(trueOrFalse = true, reasonForSecurity = "Account Sales"),
        getSecuritiesAcc14Error086Page
      )
        setup("Securities-Single-MRN-ACC14-086-journey", "Securities Single MRN with ACC14 086 journey") withActions
          (SecuritiesSingleAcc14Error086Journey: _*)

  val SecuritiesSingleAcc14Error072Journey : List[ActionBuilder] =
    LoginTheUser("user1", "AA12345678901234Z") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage("41ABCDEFGHIJKLMNO2"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityForErrorPage(trueOrFalse = false, reasonForSecurity = "Account Sales"),
        getSecuritiesAcc14Error072Page
      )
  setup("Securities-Single-MRN-ACC14-072-journey", "Securities Single MRN with ACC14 072 journey") withActions
    (SecuritiesSingleAcc14Error072Journey: _*)

  val SecuritiesSingleTPI04ErrorJourney : List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Disable,
        getMRNCdsrStartPage,
        getTheMRNCheckEoriDetailsPage,
        postTheMRNCheckEoriDetailsPage,
        getSecuritiesSelectClaimTypePage,
        postSecuritiesSelectClaimTypePage,
        getSecuritiesEnterMovementReferenceNumberPage,
        postSecuritiesEnterMovementReferenceNumberPage("30ABCDEFGHIJKLMNO1"),
        getSecuritiesReasonForSecurityPage,
        postSecuritiesReasonForSecurityForTP104ErrorPage(trueOrFalse = true, reasonForSecurity = "Manual override of duty amount"),
        getSecuritiesTPI04ErrorPage
      )
  setup("Securities-Single-MRN-TPI04-journey", "Securities Single MRN with TPI04 journey") withActions
    (SecuritiesSingleTPI04ErrorJourney: _*)

  val OverPaymentsSingleMRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Enable,
        getOverPaymentsMRNCdsrStartPage,
        getOverPaymentsMRNCheckEoriDetailsPage,
        postOverPaymentsMRNCheckEoriDetailsPage,
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
        getOverpaymentsMRNClaimantDetailsPage,
        getOverpaymentsMrnChangeContactDetailsPage,
        postOverpaymentsMrnChangeContactDetailsPage,
        getOverpaymentsMrnClaimantDetailsCheckPage1,
        postOverpaymentsMrnClaimantDetailsCheckPage,
        getOverpaymentsMRNClaimNorthernIrelandPage,
        postOverpaymentsMRNClaimNorthernIrelandPage,
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
        getOverpaymentsMRNStartClaimPage,
        getOverpaymentsMRNEnterClaimPage,
        postOverpaymentsMRNEnterClaimPage,
        getOverpaymentsMRNCheckClaimPage,
        postOverpaymentsMRNCheckClaimPage,
        getOverpaymentsSelectReimbursementMethodPage,
        postOverpaymentsSelectReimbursementMethodPage,
//        getOverpaymentsCheckBankDetailsPage,
//        postOverpaymentsCheckBankDetailsPage,
//        getOverpaymentsBankAccountTypePage,
//        postOverpaymentsBankAccountTypePage,
//        getOverpaymentsEnterBankDetailsPage,
//        postOverpaymentsEnterBankDetailsPage,
        getOverpaymentsChooseFileTypePage,
        postOverpaymentsChooseFileTypesPage,
        getOverpaymentsChooseFilesPage,
        getOverpaymentsUploadCustomsDocumentsChooseFilePage,
        postOverpaymentsUploadCustomsDocumentsChooseFilePage,
        getOverpaymentsScanProgressWaitPage,
        ) ++
        getOverpaymentsFileVerificationStatusPage ++
        List[ActionBuilder](
          getOverpaymentsContinueToHostPage,
            getOverpaymentsCheckYourAnswersPage,
        postOverpaymentsCheckYourAnswersPage,
        getOverpaymentsClaimSubmittedPage
      )

  setup("OverPayments-Single-MRN-journey", "Overpayments Single Movement reference number journey") withActions
    (OverPaymentsSingleMRNJourney: _*)

  val OverPaymentsBulkMultipleV2MRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000001") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Enable,
        getOverPaymentsMRNCdsrStartPage,
        getOverPaymentsMRNCheckEoriDetailsPage,
        postOverPaymentsMRNCheckEoriDetailsPage,
        getOverPaymentsSelectClaimTypePage,
        postOverPaymentsSelectClaimTypePage,
        getOverpaymentsChooseHowManyMrnsPage,
        postOverpaymentsMultipleChooseHowManyMrnsPage,
        getOverpaymentsMultipleMrnPage,
        postOverpaymentsMultipleMrnPage,
        getOverpaymentsMultipleMrnCheckDeclarationPage,
        postOverpaymentsMultipleMrnCheckDeclarationPage,
        getOverpaymentsMultipleEnterSecondMRNPage,
        postOverpaymentsMultipleEnterSecondMRNPage,
        getOverpaymentsMultipleCheckMRNPage,
        postOverpaymentsMultipleCheckMRNPage,
        getOverpaymentsMultipleMrnClaimantDetailsPage,
        getOverpaymentsMultipleChangeContactDetailsPage,
        postOverpaymentsMultipleChangeContactDetailsPage,
        getOverpaymentsMultipleClaimantDetailsCheckPage1,
        postOverpaymentsMultipleClaimantDetailsCheckPage,
        getOverpaymentsMultipleClaimMrnClaimNorthernIrelandPage,
        postOverpaymentsMultipleClaimNorthernIrelandPage,
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
        postOverpaymentsMultipleCheckClaimPage,
        getOverpaymentsMultipleCheckBankDetailsPage,
        getOverpaymentsMultipleBankAccountTypePage,
        postOverpaymentsMultipleBankAccountTypePage,
        getOverpaymentsMultipleEnterBankAccountDetailsPage,
        postOverpaymentsMultipleEnterBankAccountDetailsPage,
        getOverpaymentsMultipleChooseFileTypePage,
        postOverpaymentsMultipleChooseFileTypesPage,
        getOverpaymentsMultipleChooseFilesPage,
        getOverpaymentsUploadCustomsDocumentsChooseFilePage,
        postOverpaymentsUploadCustomsDocumentsChooseFilePage,
        getOverpaymentsScanProgressWaitPage,
        ) ++
        getOverpaymentsFileVerificationStatusPage ++
      List[ActionBuilder](
        getOverpaymentsMultipleCheckYourAnswersPage,
        postOverpaymentsMultipleCheckYourAnswersPage,
        getOverpaymentsMultipleClaimSubmittedPage
        )

  setup("OverPayments-Bulk-Multiple-V2-MRN-journey", "Overpayments Bulk Multiple V2 Movement reference number journey") withActions
    (OverPaymentsBulkMultipleV2MRNJourney: _*)


  val OverPaymentsBulkScheduledV2MRNJourney: List[ActionBuilder] =
    LoginTheUser("user1", "GB000000000000002") ++
      List[ActionBuilder](
        cdsOverPaymentsV2Enable,
        getOverPaymentsMRNCdsrStartPage,
        getOverPaymentsMRNCheckEoriDetailsPage,
        postOverPaymentsMRNCheckEoriDetailsPage,
        getOverPaymentsSelectClaimTypePage,
        postOverPaymentsSelectClaimTypePage,
        getOverpaymentsChooseHowManyMrnsPage,
        postOverpaymentsScheduledChooseHowManyMrnsPage,
        getOverpaymentsScheduledMRNPage,
        postOverpaymentsScheduledMrnPage,
        getOverpaymentsScheduledImporterEoriEntryPage,
        postOverpaymentsScheduledImporterEoriEntryPage,
        getOverpaymentsScheduledDeclarantEoriEntryPage,
        postOverpaymentsScheduledDeclarantEoriEntryPage,
        getOverpaymentsScheduledMrnCheckDeclarationPage,
        postOverpaymentsScheduledMrnCheckDeclarationPage,
        getOverpaymentsScheduledUploadMrnListPage,
        getScheduledUploadDocumentsChooseFilePage,
        postScheduledUploadDocumentsChooseFilePagePage,
        getScheduledDocumentUploadProgressPage,
      ) ++
      getFileVerificationStatusPage ++
      List[ActionBuilder](
        getOverpaymentsScheduledClaimantDetailsPage,
        getOverpaymentsScheduledContactDetailsPage,
        postOverpaymentsScheduledChangeContactDetailsPage,
        getOverpaymentsScheduledMrnClaimantDetailsCheckPage1,
        postOverpaymentsScheduledMrnClaimantDetailsCheckPage,
        getOverpaymentsScheduledMrnClaimNorthernIrelandPage,
        postOverpaymentsScheduledMrnClaimNorthernIrelandPage,
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
        getOverpaymentsScheduledMrnCheckBankDetailsPage,
        getOverpaymentsScheduledMRNBankAccountTypePage,
        postOverpaymentsScheduledMRNBankAccountTypePage,
        getOverpaymentsScheduledMRNEnterBankAccountDetailsPage,
        postOverpaymentsScheduledEnterBankAccountDetailsPage,
        getOverpaymentsScheduledChooseFileTypePage,
        postOverpaymentsScheduledChooseFileTypesPage,
        getOverpaymentsScheduledChooseFilesPage,
        getOverpaymentsUploadCustomsDocumentsChooseFilePage,
        postOverpaymentsUploadCustomsDocumentsChooseFilePage,
        getOverpaymentsScanProgressWaitPage,
      ) ++
      getOverpaymentsFileVerificationStatusPage ++
      List[ActionBuilder](
        getOverpaymentsScheduledCheckYourAnswersPage,
        postOverpaymentsScheduledCheckYourAnswersPage,
        getOverpaymentsScheduledClaimSubmittedPage
      )

  setup("OverPayments-Bulk-Scheduled-V2-MRN-journey", "Overpayments Bulk Scheduled V2 Movement reference number journey") withActions
    (OverPaymentsBulkScheduledV2MRNJourney: _*)
    runSimulation()
}
