/*
 * Copyright 2019 Reece Pty Ltd
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

package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.enums.PlanBranchCreateStrategy;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;

public class PlanBranchManagementModel {

    public PlanBranchCreateStrategy createStrategy = PlanBranchCreateStrategy.MANUALLY;

    public String branchPattern;

    public boolean issueLinkingEnabled = true;

    public Integer delayCleanAfterDelete;
    public Integer delayCleanAfterInactivity;

    public PlanBranchManagement asPlanBranchManagement() {
        // plan branch management - cleanup
        if (this.delayCleanAfterDelete == null) {
            this.delayCleanAfterDelete = 0;
        }
        BranchCleanup removedBranchCleanup = new BranchCleanup()
            .whenRemovedFromRepositoryAfterDays(this.delayCleanAfterDelete);
        if (this.delayCleanAfterInactivity != null) {
            removedBranchCleanup.whenInactiveInRepositoryAfterDays(this.delayCleanAfterInactivity);
        }

        PlanBranchManagement pbm = new PlanBranchManagement()
            .issueLinkingEnabled(this.issueLinkingEnabled)
            .delete(removedBranchCleanup);

        switch (this.createStrategy) {
            case MANUALLY:
                pbm.createManually();
                break;
            case ON_PULL_REQUEST:
                pbm.createForPullRequest();
                break;
            case ON_NEW_BRANCH:
                pbm.createForVcsBranch();
                break;
            case ON_BRANCH_PATTERN:
                if (this.branchPattern == null) {
                    throw new RuntimeException("branchPattern is required for ON_BRANCH_PATTERN");
                }
                pbm.createForVcsBranchMatching(this.branchPattern);
                break;
        }

        pbm.triggerBuildsLikeParentPlan()
            .notificationLikeParentPlan();

        return pbm;
    }}
