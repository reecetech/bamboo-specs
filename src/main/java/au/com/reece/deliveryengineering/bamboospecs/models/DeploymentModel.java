package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


public class DeploymentModel {
    @NotNull
    @NotEmpty
    public String bambooServer;

    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String buildProject;

    @NotNull
    @NotEmpty
    public String buildPlan;

    @NotNull
    @NotEmpty
    public String description;

    @NotNull
    public ReleaseNamingModel releaseNaming;

    public Map<String, String> variables;

    public ArrayList<EnvironmentModel> environments;

    public Deployment getDeployment() {
        Deployment deployment = new Deployment(new PlanIdentifier(this.buildProject, this.buildPlan), this.name)
            .description(this.description)
            .releaseNaming(this.releaseNaming.asReleaseNaming());

        if (this.environments != null) {
            Environment environments[] = this.environments.stream().map(EnvironmentModel::asEnvironment)
                    .collect(Collectors.toList()).toArray(new Environment[]{});
            if (this.variables != null) {
                ArrayList<Variable> variables = new ArrayList<>();
                for (String key : this.variables.keySet()) {
                    variables.add(new Variable(key, this.variables.get(key)));
                }
                Variable[] var_array = variables.toArray(new Variable[variables.size()]);
                for (Environment environment : environments) {
                    environment.variables(var_array);
                }
            }
            deployment.environments(environments);
        }
        return deployment;
    }
}
