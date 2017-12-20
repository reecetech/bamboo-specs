package au.com.reece.deliveryengineering.bamboospecs.models.docker;

import com.atlassian.bamboo.specs.builders.task.DockerRunContainerTask;

public class DockerContainer {
    public String name;

    public String command;

    public String workingDirectory;

    public String environmentVariables;

    public void applyConfig(DockerRunContainerTask docker) {
        if (this.name != null) docker.containerName(this.name);

        if (this.command != null) docker.containerCommand(this.command);

        if (this.workingDirectory != null) docker.containerWorkingDirectory(this.workingDirectory);

        if (this.environmentVariables != null) docker.containerEnvironmentVariables(this.environmentVariables);
    }
}
