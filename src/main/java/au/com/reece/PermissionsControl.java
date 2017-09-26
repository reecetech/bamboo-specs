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
    void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish) {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ReecePermissions yamlPermissions;
        try {
            yamlPermissions = mapper.readValue(yamlFile, ReecePermissions.class);
        } catch (IOException e) {
            System.out.println("Error reading YAML file");
            e.printStackTrace();
            return;
        }

        BambooServer bambooServer = new BambooServer(yamlPermissions.bambooServer, adminUser);

        if (!yamlPermissions.gather()) return;

        if (publish) {
            yamlPermissions.publish(bambooServer, adminUser);
        }
    }
}
