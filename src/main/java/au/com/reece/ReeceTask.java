package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.TestParserTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;
import com.atlassian.bamboo.specs.util.Logger;

import javax.annotation.Nullable;

public class ReeceTask extends CheckRequired {
    private static final Logger log = Logger.getLogger(ReeceTask.class);
    @Required public ReeceTaskType type;
    @Required public String description;
    public String body;
    public String resultFrom;

    @Nullable
    public Task asTask(Plan plan) {
        if (!this.checkRequired()) return null;

        switch (this.type) {
            case VCS:
                return new VcsCheckoutTask().description(this.description)
                        .checkoutItems(new CheckoutItem().defaultRepository());
            case SCRIPT:
                if (this.body == null) {
                    log.info("Missing 'body' value from yaml for SCRIPT");
                    return null;
                }
                PlanIdentifier id = plan.getIdentifier();
                String projectPlanKey = id.getProjectKey() + "-" + id.getPlanKey();
                String body = this.body.replace("%(projectPlanKey)s", projectPlanKey);
                return new ScriptTask().description(this.description).inlineBody(body);
            case JUNIT:
                if (this.resultFrom == null) {
                    log.info("Missing 'resultFrom' value from yaml for JUNIT");
                    return null;
                }
                return new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                        .description(this.description)
                        .resultDirectories(this.resultFrom);
            default:
                // shouldn't actually be possible, given we load via enum
                log.info("Unexpected 'type' value from yaml " + this.type);
                return null;
        }
    }
}
