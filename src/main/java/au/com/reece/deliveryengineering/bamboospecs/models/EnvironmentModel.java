package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.task.Task;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

// TODO document me
public class EnvironmentModel {
    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public ArrayList<TaskModel> tasks;

    public Environment asEnvironment() {
        return new Environment(this.name).tasks(this.tasks.toArray(new Task[this.tasks.size()]));
    }
}
