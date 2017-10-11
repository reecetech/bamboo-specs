package au.com.reece.deliveryengineering.bamboospecs.models;

import au.com.reece.deliveryengineering.bamboospecs.models.enums.TaskType;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.TestParserTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TaskModel extends DomainModel {
    @NotNull
    public TaskType type;

    @NotNull
    @NotEmpty
    public String description;

    public String body;

    public String resultFrom;

    public List<RepositoryModel> repositories;

    public boolean cleanCheckout = false;

    public boolean defaultRepository = false;

    public Task asTask(Plan plan) {
        switch (this.type) {
            case VCS:
                VcsCheckoutTask task = new VcsCheckoutTask().description(this.description);
                if (this.repositories == null || this.defaultRepository) {
                    task.checkoutItems(new CheckoutItem().defaultRepository());
                } else {
                    for (RepositoryModel vcs : this.repositories) {
                        task.checkoutItems(vcs.asCheckoutItem());
                    }
                }
                if (this.cleanCheckout) {
                    task.cleanCheckout(true);
                }
                return task;
            case SCRIPT:
                if (this.body == null) {
                    throw new RuntimeException("Missing 'body' value from yaml for SCRIPT");
                }
                PlanIdentifier id = plan.getIdentifier();
                String projectPlanKey = id.getProjectKey() + "-" + id.getPlanKey();
                String body = this.body.replace("%(projectPlanKey)s", projectPlanKey);
                return new ScriptTask().description(this.description).inlineBody(body);
            case JUNIT:
                if (this.resultFrom == null) {
                    throw new RuntimeException("Missing 'resultFrom' value from yaml for JUNIT");
                }
                return new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                        .description(this.description)
                        .resultDirectories(this.resultFrom);
            default:
                // shouldn't actually be possible, given we load via enum
                throw new RuntimeException("Unexpected 'type' value from yaml " + this.type);
        }
    }
}
