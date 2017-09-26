package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.Dependencies;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.DependenciesConfiguration;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Set;

public class DependencyModel extends DomainModel {
    @NotNull
    @NotEmpty
    public Boolean requiresPassing;

    @NotNull
    @NotEmpty
    public Set<String> plans;

    public void addToPlan(Plan plan) {
        ArrayList<PlanIdentifier> children = new ArrayList<>();
        for (String idString : this.plans) {
            String[] parts = idString.split("-");
            children.add(new PlanIdentifier(parts[0], parts[1]));
        }
        plan.dependencies(new Dependencies()
                .configuration(new DependenciesConfiguration()
                        .requireAllStagesPassing(this.requiresPassing))
                .childPlans(children.toArray(new PlanIdentifier[children.size()])));
    }
}
