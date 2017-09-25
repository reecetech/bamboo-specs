// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;

import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;

import com.atlassian.bamboo.specs.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PermissionsControl {
    /**
     * Run main to publish plan on Bamboo
     */
    void run(UserPasswordCredentials adminUser, File yamlFile) {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ReecePermissions yamlPlan;
        try {
            yamlPlan = mapper.readValue(yamlFile, ReecePermissions.class);
        } catch (IOException e) {
            System.out.println("Error reading YAML file");
            e.printStackTrace();
            return;
        }

        BambooServer bambooServer = new BambooServer(yamlPlan.getBambooServer(), adminUser);

        List<ReecePermission> permissionList = yamlPlan.getPermissions();

        Permissions permissions = new Permissions();
        for (ReecePermission p: permissionList) {
            for (String idString: p.getProjects()) {
                String[] parts = idString.split("-");
                PlanIdentifier id = new PlanIdentifier(parts[0], parts[1]);

                // Ensure our admin user always has admin permission
                permissions.userPermissions(adminUser.getUsername(), PermissionType.ADMIN);

                // Set user permissions first
                for (String user : p.getUsers()) {
                    List<PermissionType> values = new ArrayList<>();
                    for (String perm : p.getPermissions()) {
                        values.add(PermissionType.valueOf(perm));
                    }
                    permissions.userPermissions(user, values.toArray(new PermissionType[values.size()]));
                }

                // Now set group permissions
                for (String group : p.getGroups()) {
                    List<PermissionType> values = new ArrayList<>();
                    for (String perm : p.getPermissions()) {
                        values.add(PermissionType.valueOf(perm));
                    }
                    permissions.groupPermissions(group, values.toArray(new PermissionType[values.size()]));
                }
                // Everyone gets view access
                permissions.loggedInUserPermissions(PermissionType.VIEW).anonymousUserPermissionView();
                bambooServer.publish(new PlanPermissions(id).permissions(permissions));
            }
        }
    }
}
