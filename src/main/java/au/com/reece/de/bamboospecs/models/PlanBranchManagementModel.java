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
            this.delayCleanAfterDelete = 7;
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
