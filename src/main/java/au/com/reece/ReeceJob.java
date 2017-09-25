package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.api.builders.task.Task;

import java.util.ArrayList;

public class ReeceJob {
    private String name;
    private String key;
    private String description;
    private ArrayList<ReeceArtifact> artifacts = new ArrayList<>();
    private ArrayList<ReeceTask> tasks = new ArrayList<>();
    private ArrayList<String> requirements = new ArrayList<>();

    public Job asJob(Plan plan) {
        Job job = new Job(this.name, this.key);
        job.description(this.description);

        ArrayList<Artifact> artifacts = new ArrayList<>();
        for (ReeceArtifact artifact : this.artifacts) {
            artifacts.add(artifact.asArtifact());
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

    public ArrayList<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(ArrayList<String> requirements) {
        this.requirements = requirements;
    }

    public ArrayList<ReeceTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<ReeceTask> tasks) {
        this.tasks = tasks;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ReeceArtifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(ArrayList<ReeceArtifact> artifacts) {
        this.artifacts = artifacts;
    }

}
