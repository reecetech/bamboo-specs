package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class DeploymentPermissionModel {
    @NotEmpty
    @NotNull
    public String name;

    @NotEmpty
    @NotNull
    public ArrayList<PermissionModel> permissions;

    @NotEmpty
    @NotNull
    public ArrayList<EnvironmentPermissionModel> environments;

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
