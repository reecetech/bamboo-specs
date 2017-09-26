package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import javax.annotation.Nullable;
import java.util.*;

public class ReecePermissions extends CheckRequired {
    @Required public String bambooServer;
    public List<ReecePermission> permissions;
    private HashMap<String, ReecePlanPermissions> rpp = new HashMap<>();

    boolean gather() {
        if (!this.checkRequired()) return false;

        boolean ok = true;
        for (ReecePermission p: this.permissions) {
            if (!p.addPermissions(rpp)) ok = false;
        }
        return ok;
    }

    void publish(BambooServer bambooServer, UserPasswordCredentials adminUser) {
        // Iterate over each project configured
        for (Map.Entry pair: this.rpp.entrySet()) {
            String[] parts = ((String)pair.getKey()).split("-");
            PlanIdentifier id = new PlanIdentifier(parts[0], parts[1]);

            Permissions permissions = new Permissions();

            // Ensure our admin user always has admin permission
            permissions.userPermissions(adminUser.getUsername(), PermissionType.ADMIN);

            ReecePlanPermissions pp = (ReecePlanPermissions)pair.getValue();

            // Set user permissions first
            for (Map.Entry user_pair: pp.users.entrySet()) {
                PermissionType[] types = (PermissionType[])user_pair.getValue();
                permissions.userPermissions((String)user_pair.getKey(), types);
            }

            // Now set group permissions
            for (Map.Entry group_pair: pp.groups.entrySet()) {
                PermissionType[] types = (PermissionType[])group_pair.getValue();
                permissions.groupPermissions((String)group_pair.getKey(), types);
            }

            permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();

            bambooServer.publish(new PlanPermissions(id).permissions(permissions));
        }
    }
}
