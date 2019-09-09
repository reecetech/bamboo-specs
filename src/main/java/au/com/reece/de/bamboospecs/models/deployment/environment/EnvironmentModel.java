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

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvironmentModel {
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

    public Environment asEnvironment() {
        Task[] tasks = this.tasks.stream().map(TaskModel::asTask)
                .collect(Collectors.toList()).toArray(new Task[]{});
        Environment environment = new Environment(this.environment).tasks(tasks)
                .description(this.description);

        if (this.triggers != null) {
            Trigger[] triggers = this.triggers.stream().map(TriggerModel::asTrigger)
                    .collect(Collectors.toList()).toArray(new Trigger[]{});
            environment.triggers(triggers);
        }

        if (this.notifications != null) {
            Notification[] notifications = this.notifications.stream().map(NotificationModel::asNotification)
                    .collect(Collectors.toList()).toArray(new Notification[]{});
            environment.notifications(notifications);
        }

        if (this.requirements != null) {
            Requirement[] requirements = this.requirements.stream().map(RequirementModel::asRequirement)
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
}
