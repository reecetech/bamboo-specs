package au.com.reece.de.bamboospecs;

import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import org.junit.Test;

import java.io.File;

public class DeploymentIncludedTasksTest {
    
    private static final boolean PUBLISH = false;
    
    @Test
    public void verifyThatIncludedTaskProcessed() {
        DeploymentControl control = new DeploymentControl();
        UserPasswordCredentials users = new SimpleUserPasswordCredentials("xx", "xx");
        File file = new File(getClass().getResource("/deployment/branch-service/deployment-included-tasks.yaml").getPath());
        control.run(users, file, PUBLISH);
    }
    
    @Test
    public void verifyTasksProcessed() {
        DeploymentControl control = new DeploymentControl();
        UserPasswordCredentials users = new SimpleUserPasswordCredentials("xx", "xx");
        File file = new File(getClass().getResource("/deployment/branch-service/deployment-inline-tasks.yaml").getPath());
        control.run(users, file, PUBLISH);
    }
    
    @Test
    public void verifyBothTasksAndIncludedTasksProcessed() {
        DeploymentControl control = new DeploymentControl();
        UserPasswordCredentials users = new SimpleUserPasswordCredentials("xx", "xx");
        File file = new File(getClass().getResource("/deployment/branch-service/deployment-combined.yaml").getPath());
        control.run(users, file, PUBLISH);
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailWhenRequiredPropertyMissingOnIncludedTasks() {
        DeploymentControl control = new DeploymentControl();
        UserPasswordCredentials users = new SimpleUserPasswordCredentials("xx", "xx");
        File file = new File(getClass().getResource("/deployment/branch-service/deployment-included-tasks-missing-property.yaml").getPath());
        control.run(users, file, PUBLISH);
    }
    
    @Test
    public void verifyIncludedTasksWithIncludedEnvironment() {
        DeploymentControl control = new DeploymentControl();
        UserPasswordCredentials users = new SimpleUserPasswordCredentials("xx", "xx");
        File file = new File(getClass().getResource("/deployment/branch-service/deployment.yaml").getPath());
        control.run(users, file, PUBLISH);
    }
}
