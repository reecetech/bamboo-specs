package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String bambooServer;

    @NotNull
    @NotEmpty
    public String projectKey;

    @NotNull
    @NotEmpty
    public String projectName;

    @NotNull
    @NotEmpty
    public String planKey;

    @NotNull
    @NotEmpty
    public String planName;

    @NotNull
    @NotEmpty
    public String description;

    @NotNull
    public RepositoryModel repository;

    @NotNull
    public Boolean repositoryPolling;

    @NotNull
    public List<@Valid NotificationModel> notifications;

    @NotNull
    public List<@Valid StageModel> stages;

    public DependencyModel dependencies;

    public Plan getPlan(boolean complete) {
        Project project = new Project().key(this.projectKey);
        Plan plan = new Plan(project, this.planName, this.planKey);
        plan.description(this.description);
        plan.notifications(this.notifications.stream().map(NotificationModel::forPlan).collect(Collectors.toList()).toArray(new Notification[]{}));

        if (!complete) {
            //return plan;
        }

        this.addPluginConfiguration(plan);

        if (this.repository != null) {
            this.repository.addToPlan(plan);
        }

        if (this.repositoryPolling) {
            plan.triggers(new RepositoryPollingTrigger().description("Timed polling"));
        }


        ArrayList<Stage> stages = new ArrayList<>();
        for (StageModel stage: this.stages) {
            stages.add(stage.asStage(plan));
        }
        plan.stages(stages.toArray(new Stage[stages.size()]));

        this.addPlanBranchManagement(plan);

        this.dependencies.addToPlan(plan);

        return plan;
    }

    private void addPlanBranchManagement(Plan plan) {
        // plan branch management - cleanup
        plan.planBranchManagement(new PlanBranchManagement()
                .createForVcsBranch()
                .delete(new BranchCleanup()
                        .whenRemovedFromRepositoryAfterDays(7)
                        .whenInactiveInRepositoryAfterDays(30))
                .notificationLikeParentPlan());
    }

    private void addPluginConfiguration(Plan plan) {
        // this is the basic configuration needed
        plan.pluginConfigurations(
                new ConcurrentBuilds().useSystemWideDefault(false),
                new AllOtherPluginsConfiguration()
        );
    }
}
