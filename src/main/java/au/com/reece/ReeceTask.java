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

import javax.annotation.Nullable;

public class ReeceTask {
    private ReeceTaskType type;
    private String description;
    private String body;
    private String resultFrom;

    @Nullable
    public Task asTask(Plan plan) {
        switch (this.type) {
            case VCS:
                return new VcsCheckoutTask().description(this.description)
                        .checkoutItems(new CheckoutItem().defaultRepository());
            case SCRIPT:
                PlanIdentifier id = plan.getIdentifier();
                String projectPlanKey = id.getProjectKey() + "-" + id.getPlanKey();
                String body = this.body.replace("%(projectPlanKey)s", projectPlanKey);
                return new ScriptTask().description(this.description).inlineBody(body);
            case JUNIT:
                return new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                        .description(this.description)
                        .resultDirectories(this.resultFrom);
            default:
                // shouldn't actually be possible, given we load via enum
                return null;
        }
    }

    public ReeceTaskType getType() {
        return type;
    }

    public void setType(ReeceTaskType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResultFrom() {
        return resultFrom;
    }

    public void setResultFrom(String resultFrom) {
        this.resultFrom = resultFrom;
    }
}
