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
package au.com.reece.de.bamboospecs.models.deployment.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class EnvironmentsModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentsModel.class);

    @NotNull
    public EnvironmentModel[] environments;

    private String filename;

    public EnvironmentModel get(String name) {
        for (EnvironmentModel e : this.environments) {
            if (e.environment.equals(name)) {
                return e;
            }
        }
        throw new RuntimeException(
                String.format("Missing environment '%s' in included yaml '%s'", name, this.filename));
    }

    public static EnvironmentsModel readYAML(String filename) {
        LOGGER.info("Parsing environments YAML {}", filename);

        File yaml = new File(filename);


        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        EnvironmentsModel included;
        try {
            included = mapper.readValue(yaml, EnvironmentsModel.class);
            Set<ConstraintViolation<EnvironmentsModel>> violations = validator.validate(included);
            if (!violations.isEmpty()) {
                violations.forEach(x -> LOGGER.error("{}: {}", x.getPropertyPath(), x.getMessage()));
                throw new RuntimeException("Error parsing included environments from " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing included environments from " + filename, e);
        }
        included.filename = filename;

        return included;
    }
}
