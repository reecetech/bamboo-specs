package au.com.reece.de.bamboospecs.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class IncludeEnvironmentsModel {

    @NotNull
    @NotBlank
    public String from;

    @NotNull
    public String[] environments;

    public void addEnvironments(ArrayList<EnvironmentModel> environments, String yamlPath) {
        Path includedYaml = Paths.get(yamlPath, this.from);
        EnvironmentsModel included = EnvironmentsModel.readYAML(includedYaml.toString());
        for (String name : this.environments) {
            environments.add(included.get(name));
        }
    }
}
