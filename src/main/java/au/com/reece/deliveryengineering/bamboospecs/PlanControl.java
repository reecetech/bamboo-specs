// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece.deliveryengineering.bamboospecs;

import au.com.reece.deliveryengineering.bamboospecs.models.ProjectModel;
import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanControl {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanControl.class);
    /**
     * Run main to publish plan on Bamboo
     */
    void run(UserPasswordCredentials adminUser, String filePath, boolean publish) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        LOGGER.info("Parsing YAML {}", filePath);

        File yamlFile = new File(filePath);
        ProjectModel yamlPlan;
        try {
            yamlPlan = mapper.readValue(yamlFile, ProjectModel.class);
            Set<ConstraintViolation<ProjectModel>> violations = validator.validate(yamlPlan);
            if (!violations.isEmpty()) {
                violations.forEach(x -> LOGGER.error("{}: {}", x.getPropertyPath(), x.getMessage()));
                return;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            return;
        } catch (IOException e) {
            throw new RuntimeException("Error reading YAML file", e);
        }

        // set the file path to the yaml file for includes
        yamlPlan.yamlPath = yamlFile.getParentFile().getAbsolutePath();

        BambooServer bambooServer = new BambooServer(yamlPlan.bambooServer, adminUser);

        if (!publish) {
            yamlPlan.getPlan();
            return;
        }

        // publish twice in case we're creating a new plan which requires it to be done twice :/
        Plan plan = yamlPlan.getPlan();
        bambooServer.publish(plan);
        bambooServer.publish(plan);
    }
}
