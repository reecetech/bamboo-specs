// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.PermissionFileModel;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class PermissionsControl extends BambooController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsControl.class);

    public void run(UserPasswordCredentials adminUser, String filePath, boolean publish) {
        run(adminUser, new File(filePath), publish);
    }

    public void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PermissionFileModel yamlPermissions;
        try {
            yamlPermissions = mapper.readValue(yamlFile, PermissionFileModel.class);
            Set<ConstraintViolation<PermissionFileModel>> violations = validator.validate(yamlPermissions);
            if (!violations.isEmpty()) {
                violations.forEach(x -> LOGGER.error("{}: {}", x.getPropertyPath(), x.getMessage()));
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading YAML file: " + e.getMessage(), e);
        }

        BambooServer bambooServer = new BambooServer(yamlPermissions.bambooServer, adminUser);

        if (publish) {
            yamlPermissions.publish(bambooServer, adminUser);
        } else {
            LOGGER.info("YAML parsed OK");
        }
    }
}
