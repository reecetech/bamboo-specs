// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.BuildModel;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class BuildControl implements BambooController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildControl.class);
    public static final MediaType JSON_MEDIA_TYPE =MediaType.parse("application/json; charset=utf-8");

    public void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        LOGGER.info("Parsing YAML {}", yamlFile.toPath());

        BuildModel yamlPlan;
        try {
            yamlPlan = mapper.readValue(yamlFile, BuildModel.class);
            Set<ConstraintViolation<BuildModel>> violations = validator.validate(yamlPlan);
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

        Plan plan = yamlPlan.getPlan();
        if (publish) {
            bambooServer.publish(plan);
            try {
                publishLabels(yamlPlan, adminUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info("YAML parsed OK");
        }
    }

    private void publishLabels(BuildModel yamlPlan, UserPasswordCredentials adminUser) throws IOException {
        if (yamlPlan.hasLabels()) {
            OkHttpClient client = new OkHttpClient();

            String url = String.format("%s/rest/api/latest/plan/%s-%s/label.json", yamlPlan.bambooServer, yamlPlan.projectKey, yamlPlan.planKey);

            for (String label : yamlPlan.labels) {
                // Friends don't let friends hand-write JSON
                String json = String.format("{\"name\":\"%s\"}", label);

                RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
                String credentials = Credentials.basic(adminUser.getUsername(), adminUser.getPassword());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Authorization", credentials)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to publish labels. The HTTP request was unsuccessful due to the following: " + response.message());
                } else {
                    LOGGER.info("Added label {} to build {}-{}", label, yamlPlan.projectKey, yamlPlan.planKey);
                }
            }
        }
    }

    public void run(UserPasswordCredentials adminUser, String filePath, boolean publish) {
        run(adminUser, new File(filePath), publish);
    }
}
