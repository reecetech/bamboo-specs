package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.Dependencies;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.DependenciesConfiguration;

import java.util.ArrayList;

public class ReeceDependencies {
    public boolean requiresPassing = true;
    public ArrayList<String> plans = new ArrayList<>();

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
