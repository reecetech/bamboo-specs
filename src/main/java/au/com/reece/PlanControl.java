// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.TestParserTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;
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
    void run(UserPasswordCredentials adminUser, File yamlFile) {
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

        bambooServer.publish(yamlPlan.getPlan());
    }

    Plan createPlan(Plan plan) {
        return plan.stages(new Stage("Test Stage")
                        .jobs(new Job("Run Unit Tests",
                                new BambooKey("JOB1"))
                                .artifacts(new Artifact()
                                                .name("PACT contracts")
                                                .copyPattern("**")
                                                .location("pacts"),
                                    new Artifact()
                                            .name("Coverage Report")
                                            .copyPattern("**")
                                            .location("htmlcov"),
                                    new Artifact()
                                            .name("unittest")
                                            .copyPattern("**")
                                            .location("unittest-report"))
                            .tasks(new VcsCheckoutTask()
                                            .description("Checkout Default Repository")
                                            .checkoutItems(new CheckoutItem().defaultRepository()),
                                    new ScriptTask()
                                            .description("Build docker image")
                                            .inlineBody("set -ex\n\nscripts/test_image.sh bamboo/diary-notes-python-service"),
                                    new ScriptTask()
                                            .description("Run unit tests")
                                            .inlineBody("set -ex\nmkdir -p htmlcov\nchmod 777 htmlcov\nmkdir -p unittest-report\nchmod 777 unittest-report\nmkdir -p pacts\nchmod 777 pacts\ndocker run --rm -u root \\\n-v ${bamboo.build.working.directory}/htmlcov:/app/htmlcov:rw \\\n-v ${bamboo.build.working.directory}/unittest-report:/app/unittest-report:rw \\\n-v ${bamboo.build.working.directory}/pacts:/app/pacts:rw \\\n-e PACT_DIR=/app/pacts \\\n-t bamboo/diary-notes-python-service bash -c \"cd /app/ && ./scripts/ci_tests.sh\""),
                                    new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                                            .description("JUnit parsing")
                                            .resultDirectories("**/unittest-report/xml/*.xml"))
                            .requirements(new Requirement("system.docker.executable"),
                                    new Requirement("DOCKER"),
                                    new Requirement("LINUX"))))

//                .triggers(new RepositoryPollingTrigger()
//                        .description("Timed polling"))
//                .planBranchManagement(new PlanBranchManagement()
//                        .createForVcsBranch()
//                        .delete(new BranchCleanup()
//                                .whenRemovedFromRepositoryAfterDays(7)
//                                .whenInactiveInRepositoryAfterDays(30))
//                        .notificationLikeParentPlan())
;
    }
}