// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece.deliveryengineering.bamboospecs;

import au.com.reece.deliveryengineering.bamboospecs.models.PermissionModel;
import com.atlassian.bamboo.specs.api.BambooSpec;

import com.atlassian.bamboo.specs.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

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
        PermissionModel yamlPermissions;
        try {
            yamlPermissions = mapper.readValue(yamlFile, PermissionModel.class);
        } catch (IOException e) {
            throw new RuntimeException("Error reading YAML file", e);
        }

        BambooServer bambooServer = new BambooServer(yamlPermissions.bambooServer, adminUser);

        if (publish) {
            yamlPermissions.publish(bambooServer, adminUser);
        }
    }
}
