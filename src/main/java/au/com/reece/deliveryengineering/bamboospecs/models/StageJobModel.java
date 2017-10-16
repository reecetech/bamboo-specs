package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StageJobModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String key;

    @NotNull
    @NotEmpty
    public String description;

    @NotNull
    @NotEmpty
    public List<@Valid RequirementModel> requirements;

    public List<@Valid ArtifactModel> artifacts;

    @NotNull
    public List<@Valid TaskModel> tasks;

    public List<@Valid TaskModel> finalTasks;

    public Job asJob() {
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

        return job;
    }
}
