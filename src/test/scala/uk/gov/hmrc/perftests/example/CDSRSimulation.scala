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
