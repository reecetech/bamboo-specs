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
    }
}
