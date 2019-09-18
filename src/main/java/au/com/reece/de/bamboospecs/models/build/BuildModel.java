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
package au.com.reece.de.bamboospecs.models.build;

import au.com.reece.de.bamboospecs.models.*;
import au.com.reece.de.bamboospecs.models.common.BambooYamlFileModel;
import au.com.reece.de.bamboospecs.validation.NoIllegalCharacters;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildModel extends BambooYamlFileModel {
    public String yamlPath;

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
    @NoIllegalCharacters
    public String description;

    public List<String> labels;

    // TODO deprecate repository
    public RepositoryModel repository;
    public List<@Valid RepositoryModel> repositories;
    public String[] linkedRepositories;

    public Map<String, String> variables;

    // branch management has sensible defaults
    public final PlanBranchManagementModel branchManagement = new PlanBranchManagementModel();

    @NotNull
    public List<@Valid NotificationModel> notifications;

    @NotNull
    public List<@Valid StageModel> stages;

    public DependencyModel dependencies;

    public List<@Valid TriggerModel> triggers;

    public Plan getPlan() {
        Project project = new Project().key(this.projectKey);
        project.name(projectName);
        Plan plan = new Plan(project, this.planName, this.planKey);
        plan.description(this.description);

        plan.notifications(this.notifications.stream().map(NotificationModel::asNotification)
                .collect(Collectors.toList()).toArray(new Notification[]{}));

        this.addPluginConfiguration(plan);

        if (this.repository != null) {
            this.repository.addToPlan(plan);
        }

        if (this.linkedRepositories != null) {
            plan.linkedRepositories(this.linkedRepositories);
        }

        if (this.repositories != null) {
            for (RepositoryModel repos : this.repositories) {
                repos.addToPlan(plan);
            }
        }

        ArrayList<Variable> variables = new ArrayList<>();
        if (this.variables != null) {
            for (String key : this.variables.keySet()) {
                variables.add(new Variable(key, this.variables.get(key)));
            }
            plan.variables(variables.toArray(new Variable[0]));
        }

        this.stages.forEach(x -> x.yamlPath = this.yamlPath);

        plan.stages(this.stages.stream().map(StageModel::asStage)
                .collect(Collectors.toList()).toArray(new Stage[]{}));

        plan.planBranchManagement(this.branchManagement.asPlanBranchManagement());

        if (this.dependencies != null) this.dependencies.addToPlan(plan);

        if (this.triggers != null) {
            plan.triggers(this.triggers.stream().map(TriggerModel::asTrigger)
                    .collect(Collectors.toList()).toArray(new Trigger[]{}));
        }

        return plan;
    }

    private void addPluginConfiguration(Plan plan) {
        // this is the basic configuration needed
        plan.pluginConfigurations(
            new ConcurrentBuilds().useSystemWideDefault(false),
            new AllOtherPluginsConfiguration()
        );
    }

    public Boolean hasLabels() {
        return this.labels != null && !this.labels.isEmpty();
    }
}
