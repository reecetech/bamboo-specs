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

package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class StageModel extends DomainModel {
    public String yamlPath;

    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    public List<@Valid StageJobModel> jobs;

    public String include;

    public Stage asStage() {
        Stage stage = new Stage(this.name);
        this.jobs.forEach(x -> x.yamlPath = this.yamlPath);
        return stage.jobs(jobs.stream().map(StageJobModel::asJob).collect(Collectors.toList()).toArray(new Job[]{}));
    }
}
