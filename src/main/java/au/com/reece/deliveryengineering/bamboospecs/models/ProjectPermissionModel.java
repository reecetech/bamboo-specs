package au.com.reece.deliveryengineering.bamboospecs.models;

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
