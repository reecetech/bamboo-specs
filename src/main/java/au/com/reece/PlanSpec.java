// Note: SSL fix needed, install CA certs for bamboo.reecenet.org using these instructions:
// https://confluence.atlassian.com/kb/connecting-to-ssl-services-802171215.html

package au.com.reece;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.Dependencies;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.DependenciesConfiguration;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.builders.notification.PlanCompletedNotification;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.TestParserTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;
import com.atlassian.bamboo.specs.util.*;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {
        UserPasswordCredentials adminUser = new FileUserPasswordCredentials("./.credentials");

//        BambooServer bambooServer = new BambooServer("http://localhost:8080/", adminUser);
        BambooServer bambooServer = new BambooServer("https://bamboo.reecenet.org/bamboo", adminUser);

        Plan plan = new Plan(new Project().key(new BambooKey("BST")).name("Bamboo Spec Testing"),
            "Spec Testing", new BambooKey("ST"));

        plan = new PlanSpec().createPlan(plan);

//        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());

        bambooServer.publish(planPermission);
    }

    private PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        return new PlanPermissions(new PlanIdentifier("BST", "ST"))
                .permissions(new Permissions()
                        .userPermissions("yaps", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.ADMIN)
                        .userPermissions("thumca", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.ADMIN)
                        .userPermissions("shahh", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.ADMIN)
                        .userPermissions("vergarae", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.ADMIN)
                        .userPermissions("dooleyj", PermissionType.EDIT, PermissionType.VIEW, PermissionType.ADMIN, PermissionType.CLONE, PermissionType.BUILD)
                        .userPermissions("poultonj", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.ADMIN)
                        .userPermissions("joneri", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD, PermissionType.CLONE, PermissionType.ADMIN)
                        .loggedInUserPermissions(PermissionType.VIEW)
                        .anonymousUserPermissionView());
    }

    Plan createPlan(Plan plan) {
        return plan.description("This is a test plan for bamboo specs")
                .pluginConfigurations(new ConcurrentBuilds()
                        .useSystemWideDefault(false))
                .stages(new Stage("Test Stage")
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
            .linkedRepositories("Bamboo Spec Test Project");

//                .triggers(new RepositoryPollingTrigger()
//                        .description("Timed polling"))
//                .planBranchManagement(new PlanBranchManagement()
//                        .createForVcsBranch()
//                        .delete(new BranchCleanup()
//                                .whenRemovedFromRepositoryAfterDays(7)
//                                .whenInactiveInRepositoryAfterDays(30))
//                        .notificationLikeParentPlan())
//                .notifications(new Notification()
//                        .type(new PlanCompletedNotification())
//                        .recipients(new AnyNotificationRecipient(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-slack:recipient.slack"))
//                                .recipientString("https://hooks.slack.com/services/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|#cyborg-dev")));

    }
}