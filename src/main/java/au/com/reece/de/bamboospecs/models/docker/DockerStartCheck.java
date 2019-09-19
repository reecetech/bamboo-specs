package au.com.reece.de.bamboospecs.models.docker;

import com.atlassian.bamboo.specs.builders.task.DockerRunContainerTask;

public class DockerStartCheck {
    public String url;

    public final int timeout = 120;

    public void applyConfig(DockerRunContainerTask docker) {
        docker.waitToStart(true).serviceURLPattern(this.url).serviceTimeoutInSeconds(this.timeout);
    }
}
