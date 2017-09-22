package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.exceptions.PropertiesValidationException;
import com.atlassian.bamboo.specs.api.util.EntityPropertiesBuilders;
import org.junit.Test;

public class PlanSpecTest {
    @Test
    public void checkYourPlanOffline() throws PropertiesValidationException {
        Plan plan = new Plan(new Project().key(new BambooKey("BST")).name("Bamboo Spec Testing"),
                "Spec Testing", new BambooKey("ST"));

        plan = new PlanSpec().createPlan(plan);

        EntityPropertiesBuilders.build(plan);
    }
}
