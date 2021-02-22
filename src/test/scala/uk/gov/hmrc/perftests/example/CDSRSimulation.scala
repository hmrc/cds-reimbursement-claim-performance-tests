/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.example

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.example.EntryNumberRequests._

class CDSRSimulation extends PerformanceTestRunner {

  setup("Entry-number-journey", "Entry number journey") withRequests
    (
      getAuthLoginPage,
      loginWithAuthLoginStub(),
      getCdsrStartPage,
      getMRNPage,
      postMRNPage,
      getEnterDeclarationDetails,
      postEnterDeclarationDetails,
      getWhoIsDeclarantPage,
      postWhoIsDeclarantPage,
      getEnterClaimantDetailsAsIndividualPage,
      postEnterClaimantDetailsAsIndividualPage,
      getEnterClaimantDetailsAsCompanyPage,
      postEnterClaimantDetailsAsCompanyPage,
      getEnterReasonForClaimAndBasisPage,
      postEnterReasonForClaimAndBasisPage,
      getEnterCommodityDetailsPage,
      postEnterCommodityDetailsPage,
      getEnterUkDutyAmountsPage,
      postEnterUkDutyAmountsPage,
      getEnterEuDutyAmountsPage,
      postEnterEuDutyAmountsPage,
      getCheckReimbursementClaimTotalPage,
      postCheckReimbursementClaimTotalPage,
      getEnterBankAccountDetailsPage,
      PostEnterBankAccountDetailsPage,
      getUploadSupportEvidencePage,
      postUploadSupportEvidencePage(fileType = "validFile.png"),
      getScanProgressWaitPage,
      postScanProgressWaitPage,
      postScanProgressWaitPage1,
      getSelectSupportingEvidencePage,
      postSelectSupportingEvidencePage,
      getCheckYourAnswersPage,
      postCheckYourAnswersPage,
      getReviewYourClaimPage,
      postReviewYourClaimPage
    )

  runSimulation()
}
