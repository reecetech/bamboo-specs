package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RequirementModel {
    @NotNull
    @NotEmpty
    public String name;

    // add later: matchType, matchValue, etc.

    public Requirement asRequirement() {
        return new Requirement(this.name);
    }
}
