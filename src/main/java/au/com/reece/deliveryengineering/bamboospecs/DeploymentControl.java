package au.com.reece.deliveryengineering.bamboospecs;

import au.com.reece.deliveryengineering.bamboospecs.models.DeploymentModel;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
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
import java.util.Set;

public class DeploymentControl {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanControl.class);
    /**
     * Run main to publish deployment on Bamboo
     */
    void run(UserPasswordCredentials adminUser, File yamlDeploymentFile, boolean publish) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        DeploymentModel yamlDeployment;
        try {
            yamlDeployment = mapper.readValue(yamlDeploymentFile, DeploymentModel.class);
            Set<ConstraintViolation<DeploymentModel>> violations = validator.validate(yamlDeployment);
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

        BambooServer bambooServer = new BambooServer(yamlDeployment.bambooServer, adminUser);

        if (!publish) {
            yamlDeployment.getDeployment();
            return;
        }

        Deployment deployment = yamlDeployment.getDeployment();
        bambooServer.publish(deployment);

        deployment = yamlDeployment.getDeployment();
        bambooServer.publish(deployment);
    }}
