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

import com.atlassian.bamboo.specs.api.builders.docker.DockerConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.ArtifactSubscription;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StageJobModel extends BambooBaseModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageJobModel.class);

    public String yamlPath;

    public String include;

    public String name;

    public String key;

    public String description;

    public List<@Valid RequirementModel> requirements;

    public List<@Valid ArtifactModel> artifacts;

    public List<@Valid ArtifactSubscriptionModel> artifactSubscriptions;

    public List<@Valid TaskModel> tasks;

    public List<@Valid TaskModel> finalTasks;

    public String dockerContainer;

    public Job asJob() {
        if (this.include != null) {
            Path includedYaml = Paths.get(this.yamlPath, this.include);
            StageJobModel included = StageJobModel.fromYAML(includedYaml.toString());
            return included.asJob();
        }

        if (this.name == null) throw new IllegalArgumentException("Stage jobs require a 'name'");
        if (this.key == null) throw new IllegalArgumentException("Stage jobs require a 'key'");
        if (this.description == null) throw new IllegalArgumentException("Stage jobs require a 'description'");
        if (this.requirements == null) throw new IllegalArgumentException("Stage jobs require 'requirements'");
        if (this.tasks == null) throw new IllegalArgumentException("Stage jobs require 'tasks'");

        Job job = new Job(this.name, this.key);
        job.description(this.description);

        // Currently we have no need to configure plugins, but we have to explicitly do this
        // here in case older plans have plugin configuration dregs we can't delete
        // through the UI (like clover configs in Diary Notes)
        job.pluginConfigurations(new AllOtherPluginsConfiguration());

        if (this.artifacts != null) {
            Artifact[] artifacts = this.artifacts.stream().map(ArtifactModel::asArtifact)
                    .collect(Collectors.toList()).toArray(new Artifact[]{});
            job.artifacts(artifacts);
        }

        if (this.artifactSubscriptions != null) {
            ArtifactSubscription[] subscriptions = this.artifactSubscriptions.stream()
                    .map(ArtifactSubscriptionModel::asArtifactSubscription)
                    .collect(Collectors.toList()).toArray(new ArtifactSubscription[]{});
            job.artifactSubscriptions(subscriptions);
        }

        Task[] tasks = this.tasks.stream().map(TaskModel::asTask)
                .collect(Collectors.toList()).toArray(new Task[]{});
        job.tasks(tasks);

        if (this.finalTasks != null) {
            tasks = this.finalTasks.stream().map(TaskModel::asTask)
                    .collect(Collectors.toList()).toArray(new Task[]{});
            job.finalTasks(tasks);
        }

        Requirement[] requirements = this.requirements.stream().map(RequirementModel::asRequirement)
                .collect(Collectors.toList()).toArray(new Requirement[]{});
        job.requirements(requirements);

        if (this.dockerContainer != null && !"".equalsIgnoreCase(this.dockerContainer)) {
            job.dockerConfiguration(new DockerConfiguration().image(this.dockerContainer));
        }

        return job;
    }

    public static StageJobModel fromYAML(String filename) {
        LOGGER.info("Parsing job YAML {}", filename);

        File yaml = new File(filename);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        StageJobModel included;
        try {
            included = mapper.readValue(yaml, StageJobModel.class);
            Set<ConstraintViolation<StageJobModel>> violations = validator.validate(included);
            if (!violations.isEmpty()) {
                violations.forEach(x -> LOGGER.error("{}: {}", x.getPropertyPath(), x.getMessage()));
                throw new RuntimeException("Error parsing included job from " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing included job from " + filename, e);
        }

        return included;
    }
}
