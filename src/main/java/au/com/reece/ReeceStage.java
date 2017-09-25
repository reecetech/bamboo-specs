package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;

import java.util.ArrayList;

public class ReeceStage {
    private String name;
    private ArrayList<ReeceJob> jobs = new ArrayList<>();

    public Stage asStage(Plan plan) {
        Stage stage = new Stage(this.name);
        ArrayList<Job> l = new ArrayList<Job>();
        for (ReeceJob job : this.jobs) {
            l.add(job.asJob(plan));
        }
        return stage.jobs(l.toArray(new Job[l.size()]));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ReeceJob> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<ReeceJob> jobs) {
        this.jobs = jobs;
    }
}
