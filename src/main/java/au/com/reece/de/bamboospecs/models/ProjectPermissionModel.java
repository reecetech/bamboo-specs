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

import au.com.reece.de.bamboospecs.models.permissions.PermissionModel;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class ProjectPermissionModel {
    @NotEmpty
    @NotNull
    public String[] plans;

    @NotEmpty
    @NotNull
    public ArrayList<PermissionModel> permissions;

    public void publishPermissions(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        for (String planKey : this.plans) {
            String[] parts = planKey.split("-");
            PlanIdentifier id = new PlanIdentifier(parts[0], parts[1]);

            Permissions permissions = new Permissions();
            this.permissions.forEach(x -> x.addToPermissions(permissions));

            // Ensure our admin user always has admin permission
            permissions.userPermissions(adminUser.getUsername(), PermissionType.ADMIN);

            permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

            bambooServer.publish(new PlanPermissions(id).permissions(permissions));
        }
    }
}
