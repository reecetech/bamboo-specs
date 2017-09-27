package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Set;

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
    public Set<String> requirements;

    @NotNull
    public Set<@Valid ArtifactModel> artifacts;

    @NotNull
    public Set<@Valid TaskModel> tasks;

    public Job asJob(Plan plan) {
        Job job = new Job(this.name, this.key);
        job.description(this.description);

        ArrayList<Artifact> artifacts = new ArrayList<>();
        for (ArtifactModel artifact : this.artifacts) {
            Artifact a = artifact.asArtifact();
            if (a != null) artifacts.add(a);
        }
        job.artifacts(artifacts.toArray(new Artifact[artifacts.size()]));

        ArrayList<Task> tasks = new ArrayList<>();
        for (TaskModel task : this.tasks) {
            Task t = task.asTask(plan);
            if (t != null) tasks.add(t);
        }
        job.tasks(tasks.toArray(new Task[tasks.size()]));

        ArrayList<Requirement> requirements = new ArrayList<>();
        for (String requirement : this.requirements) {
            requirements.add(new Requirement(requirement));
        }
        job.requirements(requirements.toArray(new Requirement[requirements.size()]));

        return job;
    }
}
