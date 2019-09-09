/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.permissions.PermissionFileModel;
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
