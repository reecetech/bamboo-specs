package au.com.reece.deliveryengineering.bamboospecs.models;

import au.com.reece.deliveryengineering.bamboospecs.models.enums.TriggerType;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TriggerModel {
    @NotNull
    @NotBlank
    public TriggerType type;

    @NotNull
    @NotBlank
    public String description;

    public Trigger asTrigger() {
        switch (this.type) {
            case AFTER_SUCCESSFUL_BUILD_PLAN:
                return new AfterSuccessfulBuildPlanTrigger().description(this.description);
            default:
                throw new RuntimeException("Unexpected 'type' value from yaml " + this.type);
        }
    }
}
