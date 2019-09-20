package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class StageModel extends DomainModel {
    public String yamlPath;

    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    public List<@Valid StageJobModel> jobs;

    public String include;

    public Stage asStage() {
        Stage stage = new Stage(this.name);
        this.jobs.forEach(x -> x.yamlPath = this.yamlPath);
        return stage.jobs(jobs.stream().map(StageJobModel::asJob).collect(Collectors.toList()).toArray(new Job[]{}));
    }
}
