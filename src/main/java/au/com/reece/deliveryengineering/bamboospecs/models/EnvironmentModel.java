package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

// TODO document me
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
            Notification notifications[] = this.notifications.stream().map(NotificationModel::asNotification)
                    .collect(Collectors.toList()).toArray(new Notification[]{});
            environment.notifications(notifications);
        }

        if (this.requirements != null) {
            Requirement requirements[] = this.requirements.stream().map(RequirementModel::asRequirement)
                    .collect(Collectors.toList()).toArray(new Requirement[]{});
            environment.requirements(requirements);
        }

        return environment;
    }
}
