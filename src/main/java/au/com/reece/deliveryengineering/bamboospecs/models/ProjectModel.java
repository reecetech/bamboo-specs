package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.Variable;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    public RepositoryModel repository;
    public String[] linkedRepositories;

    public Map<String, String> variables;

    public boolean repositoryPolling=false;

    public PlanBranchManagementModel branchManagement;

    @NotNull
    public List<@Valid NotificationModel> notifications;

    @NotNull
    public List<@Valid StageModel> stages;

    public DependencyModel dependencies;

    public Plan getPlan() {
        Project project = new Project().key(this.projectKey);
        Plan plan = new Plan(project, this.planName, this.planKey);
        plan.description(this.description);

        plan.notifications(this.notifications.stream().map(NotificationModel::asNotification)
                .collect(Collectors.toList()).toArray(new Notification[]{}));

        this.addPluginConfiguration(plan);

        if (this.repository != null) {
            this.repository.addToPlan(plan);
        }
        if (this.linkedRepositories.length > 0) {
            plan.linkedRepositories(this.linkedRepositories);
        }

        if (this.repositoryPolling) {
            plan.triggers(new RepositoryPollingTrigger()
                    .description("Timed polling")
                    .pollEvery(3, TimeUnit.MINUTES));
        }

        ArrayList<Variable> variables = new ArrayList<>();
        if (this.variables != null) {
            for (String key : this.variables.keySet()) {
                variables.add(new Variable(key, this.variables.get(key)));
            }
            plan.variables(variables.toArray(new Variable[variables.size()]));
        }

        plan.stages(this.stages.stream().map(StageModel::asStage).collect(Collectors.toList()).toArray(new Stage[]{}));

        plan.planBranchManagement(this.branchManagement.asPlanBranchManagement());

        if (this.dependencies != null) this.dependencies.addToPlan(plan);

        return plan;
    }

    private void addPluginConfiguration(Plan plan) {
        // this is the basic configuration needed
        plan.pluginConfigurations(
            new ConcurrentBuilds().useSystemWideDefault(false),
            new AllOtherPluginsConfiguration()
        );
    }
}
