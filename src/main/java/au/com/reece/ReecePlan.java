package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;

import java.util.ArrayList;
import java.util.List;

public class ReecePlan {
    private String bambooServer;
    private String projectKey;
    private String projectName;
    private String planKey;
    private String planName;
    private String description;
    private ReeceRepository repository;
    private boolean repositoryPolling;
    private List<ReeceNotification> notifications = new ArrayList<>();
    private List<ReeceStage> stages = new ArrayList<>();
    private ReeceDependencies dependencies;

    public Plan getPlan() {
        Project project = new Project().key(this.getProjectKey());
        Plan plan = new Plan(project, this.getPlanName(), this.getPlanKey());
        plan.description(this.description);

        this.addPluginConfiguration(plan);

        if (this.repository != null) {
            this.repository.addToPlan(plan);
        }

        if (this.repositoryPolling) {
            plan.triggers(new RepositoryPollingTrigger().description("Timed polling"));
        }

        ArrayList<Notification> notifications = new ArrayList<>();
        for (ReeceNotification notification: this.notifications) {
            Notification n = notification.forPlan();
            if (n != null) notifications.add(n);
        }
        plan.notifications(notifications.toArray(new Notification[notifications.size()]));

        ArrayList<Stage> stages = new ArrayList<>();
        for (ReeceStage stage: this.stages) {
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

    public boolean isRepositoryPolling() {
        return repositoryPolling;
    }

    public void setRepositoryPolling(boolean repositoryPolling) {
        this.repositoryPolling = repositoryPolling;
    }

    public ReeceDependencies getDependencies() {
        return dependencies;
    }

    public void setDependencies(ReeceDependencies dependencies) {
        this.dependencies = dependencies;
    }

    public String getBambooServer() {
        return bambooServer;
    }

    public void setBambooServer(String bambooServer) {
        this.bambooServer = bambooServer;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPlanKey() {
        return planKey;
    }

    public void setPlanKey(String planKey) {
        this.planKey = planKey;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReeceRepository getRepository() {
        return repository;
    }

    public void setRepository(ReeceRepository repository) {
        this.repository = repository;
    }

    public List<ReeceNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<ReeceNotification> notifications) {
        this.notifications = notifications;
    }

    public List<ReeceStage> getStages() {
        return stages;
    }

    public void setStages(List<ReeceStage> stages) {
        this.stages = stages;
    }
}
