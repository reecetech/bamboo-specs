package au.com.reece.deliveryengineering.bamboospecs.models.docker;

import com.atlassian.bamboo.specs.builders.task.DockerRunContainerTask;

public class DockerStartCheck {
    public String url;

    public int timeout = 120;

    public void applyConfig(DockerRunContainerTask docker) {
        docker.waitToStart(true).serviceURLPattern(this.url).serviceTimeoutInSeconds(this.timeout);
    }
}
