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
