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

import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class PermissionModel extends DomainModel {
    public Set<String> groups;
    public Set<String> users;

    public Set<String> getUsers() {
        return users == null ? new HashSet<>() : users;
    }

    public Set<String> getGroups() {
        return groups == null ? new HashSet<>() : groups;
    }

    public boolean allLoggedInUsers = false;

    @NotNull
    @NotEmpty
    public Set<PermissionType> grant;

    public void addToPermissions(Permissions permissions) {
        PermissionType[] permissionArray = this.grant.toArray(new PermissionType[this.grant.size()]);

        // Set user grant first
        for (String user : this.getUsers()) {
            permissions.userPermissions(user, permissionArray);
        }

        for (String group : this.getGroups()) {
            permissions.groupPermissions(group, permissionArray);
        }

        if (this.allLoggedInUsers) {
            permissions.loggedInUserPermissions(permissionArray);
        }
    }
}
