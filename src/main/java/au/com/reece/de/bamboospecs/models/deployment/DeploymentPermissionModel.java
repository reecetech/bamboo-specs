package au.com.reece.de.bamboospecs.models.deployment;

import au.com.reece.de.bamboospecs.models.deployment.environment.EnvironmentPermissionModel;
import au.com.reece.de.bamboospecs.models.PermissionModel;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DeploymentPermissionModel {
    @NotEmpty
    @NotNull
    public String name;

    @NotEmpty
    @NotNull
    public List<PermissionModel> permissions;

    @NotEmpty
    @NotNull
    public List<EnvironmentPermissionModel> environments;

    public void publishPermissions(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        Permissions permissions = new Permissions();

        for (PermissionModel perm: this.permissions) {
            perm.addToPermissions(permissions);
        }

        // Ensure our admin user always has admin permission
        permissions.userPermissions(adminUser.getUsername(), PermissionType.VIEW, PermissionType.EDIT);

        permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

        bambooServer.publish(new DeploymentPermissions(this.name).permissions(permissions));

        // now publish all the environment permissions
        this.environments.forEach(x -> x.publishPermissions(bambooServer, adminUser, this.name));
    }
}
