package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class PermissionModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String bambooServer;

    @NotNull
    @NotEmpty
    public Set<String> projects;

    public Set<String> groups;
    public Set<String> users;

    @NotNull
    @NotEmpty
    public Set<PermissionType> grant;

    public void publish(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        // Iterate over each project configured

        for (String projectKey : projects) {
            String[] parts = projectKey.split("-");
            PlanIdentifier id = new PlanIdentifier(parts[0], parts[1]);

            Permissions permissions = new Permissions();

            // Ensure our admin user always has admin permission
            permissions.userPermissions(adminUser.getUsername(), PermissionType.ADMIN);

            PermissionType[] permissionArray = (PermissionType[]) grant.toArray();

            // Set user grant first
            for (String user : users) {
                permissions.userPermissions(user, permissionArray);
            }

            for (String group : groups) {
                permissions.groupPermissions(group, permissionArray);
            }

            permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

            bambooServer.publish(new PlanPermissions(id).permissions(permissions));
        }
    }
}
