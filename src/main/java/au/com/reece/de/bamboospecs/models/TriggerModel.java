package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.enums.TriggerType;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TriggerModel {
    @NotNull
    public TriggerType type;

    @NotNull
    @NotEmpty
    public String description;

    public Trigger asTrigger() {
        switch (this.type) {
            case AFTER_SUCCESSFUL_BUILD_PLAN:
                return new AfterSuccessfulBuildPlanTrigger().description(this.description);
            case AFTER_STASH_COMMIT:
                return new BitbucketServerTrigger().description(this.description);
            default:
                throw new RuntimeException("Unexpected 'type' value from yaml " + this.type);
        }
    }
}
