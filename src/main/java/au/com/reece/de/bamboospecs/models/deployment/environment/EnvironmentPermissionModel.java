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

package au.com.reece.de.bamboospecs.models.deployment.environment;

import au.com.reece.de.bamboospecs.models.PermissionModel;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class EnvironmentPermissionModel {
    @NotEmpty
    @NotNull
    public String[] names;

    @NotEmpty
    @NotNull
    public ArrayList<PermissionModel> permissions;

    public void publishPermissions(BambooServer bambooServer, UserPasswordCredentials adminUser, String deploymentName) {
        for (String name : this.names) {

            Permissions permissions = new Permissions();

            for (PermissionModel perm : this.permissions) {
                perm.addToPermissions(permissions);
            }

            // Ensure our admin user always has admin permission
            permissions.userPermissions(adminUser.getUsername(), PermissionType.VIEW, PermissionType.EDIT);

            permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

            bambooServer.publish(new EnvironmentPermissions(deploymentName)
                    .environmentName(name)
                    .permissions(permissions));
        }
    }
}
