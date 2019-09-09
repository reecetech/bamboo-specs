/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.reece.de.bamboospecs.models.deployment.environment;

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
