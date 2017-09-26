// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanControl {
    /**
     * Run main to publish plan on Bamboo
     */
    void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ReecePlan yamlPlan;
        try {
            yamlPlan = mapper.readValue(yamlFile, ReecePlan.class);
        } catch (IOException e) {
            System.out.println("Error reading YAML file");
            e.printStackTrace();
            return;
        }

        BambooServer bambooServer = new BambooServer(yamlPlan.getBambooServer(), adminUser);

        if (!publish) {
            yamlPlan.getPlan(true);
            return;
        }

        Plan plan = yamlPlan.getPlan(false);
        if (plan == null) return;
        bambooServer.publish(plan);

        plan = yamlPlan.getPlan(true);
        if (plan == null) return;
        bambooServer.publish(plan);
    }
}