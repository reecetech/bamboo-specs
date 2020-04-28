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
package au.com.reece.de.bamboospecs.models.deployment;

import au.com.reece.de.bamboospecs.models.BambooYamlFileModel;
import au.com.reece.de.bamboospecs.models.IncludeEnvironmentsModel;
import au.com.reece.de.bamboospecs.models.ReleaseNamingModel;
import au.com.reece.de.bamboospecs.models.deployment.environment.EnvironmentModel;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeploymentModel extends BambooYamlFileModel {

    public String yamlPath;

    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String buildProject;

    @NotNull
    @NotEmpty
    public String buildPlan;

    @NotNull
    @NotEmpty
    public String description;

    @NotNull
    public ReleaseNamingModel releaseNaming;

    public Map<String, String> variables;

    public List<EnvironmentModel> environments;

    public IncludeEnvironmentsModel includeEnvironments;

    private final ArrayList<EnvironmentModel> collectedEnvironments = new ArrayList<>();

    public DeploymentPermissionsModel permissions;

    public Deployment asDeployment() {
        Deployment deployment = new Deployment(new PlanIdentifier(this.buildProject, this.buildPlan), this.name)
                .description(this.description)
                .releaseNaming(this.releaseNaming.asReleaseNaming());

        // collect all the environments
        collectAllEnvironments();

        // convert to array of Bamboo Environment
        Environment[] environments = this.collectedEnvironments.stream()
                .map(EnvironmentModel::asEnvironment)
                .toArray(Environment[]::new);

        // attach our "global" variables
        attachVariablesToEnvironments(environments);

        return deployment.environments(environments);
    }

    private void collectAllEnvironments() {
        if (this.environments != null) {
            this.environments.forEach(environmentModel -> environmentModel.yamlPath = this.yamlPath);
            this.collectedEnvironments.addAll(this.environments);
        }
        if (this.includeEnvironments != null) {
            this.includeEnvironments.addEnvironments(this.collectedEnvironments, this.yamlPath);
        }
    }

    private void attachVariablesToEnvironments(Environment[] environments) {
        if (this.variables != null) {
            ArrayList<Variable> variables = new ArrayList<>();
            for (String key : this.variables.keySet()) {
                variables.add(new Variable(key, this.variables.get(key)));
            }
            Variable[] var_array = variables.toArray(new Variable[0]);
            for (Environment environment : environments) {
                environment.variables(var_array);
            }
        }
    }

    public void publish(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        Deployment deployment = this.asDeployment();
        bambooServer.publish(deployment);

        if (this.permissions != null) {
            Permissions permissions = new Permissions();

            // Ensure our admin user always has admin permission
            permissions.userPermissions(adminUser.getUsername(), PermissionType.VIEW, PermissionType.EDIT);

            permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

            // add the project permissions and publish those
            this.permissions.project.addToPermissions(permissions);
            bambooServer.publish(new DeploymentPermissions(this.name).permissions(permissions));

            // now the per-environment permissions
            permissions = new Permissions();
            this.permissions.environment.addToPermissions(permissions);
            for (EnvironmentModel e : this.collectedEnvironments) {
                bambooServer.publish(new EnvironmentPermissions(this.name).environmentName(e.environment).permissions(permissions));
            }
        }
    }
}
