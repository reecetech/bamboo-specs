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

import au.com.reece.de.bamboospecs.models.deployment.DeploymentPermissionModel;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class PermissionFileModel extends BambooYamlFileModel {
    @NotNull
    @NotEmpty
    public Set<@Valid ProjectPermissionModel> projects;

    public Set<@Valid DeploymentPermissionModel> deployments;

    public void publish(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        this.projects.forEach(x -> x.publishPermissions(bambooServer, adminUser));
        if (this.deployments != null) {
            this.deployments.forEach(x -> x.publishPermissions(bambooServer, adminUser));
        }
    }
}
