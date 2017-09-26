package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;

import java.util.ArrayList;

public class ReeceJob {
    public String name;
    public String key;
    public String description;
    public ArrayList<ReeceArtifact> artifacts = new ArrayList<>();
    public ArrayList<ReeceTask> tasks = new ArrayList<>();
    public ArrayList<String> requirements = new ArrayList<>();

    public Job asJob(Plan plan) {
        Job job = new Job(this.name, this.key);
        job.description(this.description);

        ArrayList<Artifact> artifacts = new ArrayList<>();
        for (ReeceArtifact artifact : this.artifacts) {
            Artifact a = artifact.asArtifact();
            if (a != null) artifacts.add(a);
        }
        job.artifacts(artifacts.toArray(new Artifact[artifacts.size()]));

        ArrayList<Task> tasks = new ArrayList<>();
        for (ReeceTask task : this.tasks) {
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
