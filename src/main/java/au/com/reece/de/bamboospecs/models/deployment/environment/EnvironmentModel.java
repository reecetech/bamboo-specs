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

import au.com.reece.de.bamboospecs.models.NotificationModel;
import au.com.reece.de.bamboospecs.models.RequirementModel;
import au.com.reece.de.bamboospecs.models.TaskModel;
import au.com.reece.de.bamboospecs.models.TriggerModel;
import com.atlassian.bamboo.specs.api.builders.Variable;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EnvironmentModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentModel.class);

    public String yamlPath;

    @NotNull
    @NotEmpty
    public String environment;

    @NotNull
    @NotEmpty
    public String description;

    @NotNull
    @NotEmpty
    public List<@Valid TaskModel> tasks;

    public List<@Valid TriggerModel> triggers;

    public List<@Valid NotificationModel> notifications;

    public List<@Valid RequirementModel> requirements;

    public Map<String, String> variables;

    public String includedTasks;

    public Environment asEnvironment() {
        List<Task> tasks = addTasks();

        Environment environment = new Environment(this.environment)
                .tasks(tasks.toArray(new Task[0]))
                .description(this.description);

        if (this.triggers != null) {
            Trigger[] triggers = this.triggers.stream().map(TriggerModel::asTrigger)
                    .collect(Collectors.toList()).toArray(new Trigger[]{});
            environment.triggers(triggers);
        }

        if (this.notifications != null) {
            Notification notifications[] = this.notifications.stream().map(NotificationModel::asNotification)
                    .collect(Collectors.toList()).toArray(new Notification[]{});
            environment.notifications(notifications);
        }

        if (this.requirements != null) {
            Requirement requirements[] = this.requirements.stream().map(RequirementModel::asRequirement)
                    .collect(Collectors.toList()).toArray(new Requirement[]{});
            environment.requirements(requirements);
        }

        if (this.variables != null) {
            ArrayList<Variable> variables = new ArrayList<>();
            for (String key : this.variables.keySet()) {
                variables.add(new Variable(key, this.variables.get(key)));
            }
            environment.variables(variables.toArray(new Variable[0]));
        }
        return environment;
    }

    private List<Task> addTasks() {
        List<Task> tasks = new ArrayList<>();
        if (this.tasks != null) {
            this.tasks.forEach(x -> x.yamlPath = yamlPath);
            tasks.addAll(this.tasks.stream().map(TaskModel::asTask).collect(Collectors.toList()));
        }
        if (this.includedTasks != null) {
            Path includedYaml = Paths.get(this.yamlPath, this.includedTasks);
            tasks.addAll(EnvironmentModel.tasksFromYAML(includedYaml.toString()));
        }
        return tasks;
    }

    private static List<Task> tasksFromYAML(String filename) {
        LOGGER.info("Parsing tasks YAML {}", filename);

        File yaml = new File(filename);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        TaskModel[] included;
        try {
            included = mapper.readValue(yaml, TaskModel[].class);
            for (TaskModel task : included) {
                Set<ConstraintViolation<TaskModel>> violations = validator.validate(task);
                if (!violations.isEmpty()) {
                    violations.forEach(x -> LOGGER.error("{}: {}", x.getPropertyPath(), x.getMessage()));
                    throw new RuntimeException("Error parsing included task from " + filename);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing included tasks from " + filename, e);
        }
        return Arrays.stream(included).map(TaskModel::asTask).collect(Collectors.toList());
    }
}
