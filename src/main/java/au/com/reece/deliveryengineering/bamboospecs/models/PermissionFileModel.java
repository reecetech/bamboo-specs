package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class PermissionFileModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String bambooServer;

    @NotNull
    @NotEmpty
    public Set<@Valid PermissionModel> permissions;

    public void publish(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        // Iterate over each project configured

        for (PermissionModel permission : permissions) {
            for (String projectKey : permission.projects) {
                String[] parts = projectKey.split("-");
                PlanIdentifier id = new PlanIdentifier(parts[0], parts[1]);

                Permissions permissions = new Permissions();

                // Ensure our admin user always has admin permission
                permissions.userPermissions(adminUser.getUsername(), PermissionType.ADMIN);

                PermissionType[] permissionArray = permission.grant.toArray(new PermissionType[permission.grant.size()]);

                // Set user grant first
                for (String user : permission.getUsers()) {
                    permissions.userPermissions(user, permissionArray);
                }

                for (String group : permission.getGroups()) {
                    permissions.groupPermissions(group, permissionArray);
                }

                permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

                bambooServer.publish(new PlanPermissions(id).permissions(permissions));
            }

        }
    }
}
