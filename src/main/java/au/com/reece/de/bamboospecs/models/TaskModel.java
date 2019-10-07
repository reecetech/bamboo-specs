/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.docker.DockerContainer;
import au.com.reece.de.bamboospecs.models.docker.DockerStartCheck;
import au.com.reece.de.bamboospecs.models.docker.PortMapping;
import au.com.reece.de.bamboospecs.models.docker.VolumeMapping;
import au.com.reece.de.bamboospecs.models.enums.InjectScopeType;
import au.com.reece.de.bamboospecs.models.enums.TaskType;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.task.AnyTask;
import com.atlassian.bamboo.specs.api.builders.task.Task;
import com.atlassian.bamboo.specs.builders.task.*;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;
import com.google.common.base.Strings;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskModel extends DomainModel {
    @NotNull
    public TaskType type;

    // Used by all task types
    @NotNull
    @NotEmpty
    public String description;

    // Used by: SCRIPT
    public String body;

    // Used by: JUNIT
    public String resultFrom;

    // Used by: VCS
    public List<RepositoryModel> repositories;
    public boolean cleanCheckout = false;
    public boolean defaultRepository = false;
    public String cloneDirectory;

    // Used by: DOCKER and SCRIPT
    public String workingDirectory;

    // Used by: DOCKER
    public String image;
    public boolean detach = false;
    public DockerStartCheck serviceStartCheck;
    public String environmentVariables;
    public DockerContainer container;
    public VolumeMapping[] volumeMappings;
    public PortMapping[] portMappings;
    public String cmdLineArguments;

    // legacy attributes from DOCKER
    public String command;
    // end legacy

    // Used by: INJECT
    public String propertiesFile;
    public String namespace;
    public InjectScopeType scope = InjectScopeType.RESULT;

    // Used by: ARTIFACTORY
    public String uploadSpec;

    public Task asTask() {
        switch (this.type) {
            case VCS:
                return getVersionControlTask();
            case SCRIPT:
                return getScriptTask();
            case JUNIT:
                return getJunitTask();
            case TESTNG:
                return getTestngTask();
            case DOCKER:
                return getDockerTask();
            case CLEAN:
                return new CleanWorkingDirectoryTask();
            case ARTEFACT:
                return getArtefactDownloadTask();
            case INJECT:
                return getInjectTask();
            case ARTIFACTORY:
                return getArtifactoryTask();
            default:
                // shouldn't actually be possible, given we load via enum
                throw new RuntimeException("Unexpected 'type' value from yaml " + this.type);
        }
    }

    private Task getArtifactoryTask() {
        if (this.uploadSpec == null || "".equalsIgnoreCase(this.uploadSpec)) {
            throw new RuntimeException("Missing 'uploadSpec' value from yaml for ARTIFACTORY");
        }

        Map<String, String> configurationMap = new HashMap<>();
        configurationMap.put("artifactory.generic.publishBuildInfo", "true");
        configurationMap.put("bintrayConfiguration", "");
        configurationMap.put("bintray.licenses", "");
        configurationMap.put("bintray.repository", "");
        configurationMap.put("artifactory.generic.username", "bamboo");
        configurationMap.put("artifactory.generic.specSourceChoice", "jobConfiguration");
        configurationMap.put("artifactory.generic.resolveRepo", "");
        configurationMap.put("artifactory.generic.deployPattern", "");
        configurationMap.put("artifactory.generic.envVarsExcludePatterns", "*password*,*secret*,*security*,*key*");
        configurationMap.put("bintray.signMethod", "false");
        configurationMap.put("builder.artifactoryGenericBuilder.artifactoryServerId", "0");
        configurationMap.put("bintray.subject", "");
        configurationMap.put("artifactory.generic.file", "");
        configurationMap.put("artifactory.generic.useSpecsChoice", "specs");
        configurationMap.put("bintray.packageName", "");
        configurationMap.put("artifactory.generic.includeEnvVars", "true");
        configurationMap.put("artifactory.generic.artifactSpecs", "");
        configurationMap.put("artifactory.generic.password", "bamboo");
        configurationMap.put("bintray.mavenSync", "");
        configurationMap.put("artifactory.generic.jobConfiguration", this.uploadSpec);
        configurationMap.put("baseUrl", "https://bamboo.reecenet.org/bamboo");
        configurationMap.put("artifactory.generic.envVarsIncludePatterns", "*");
        configurationMap.put("artifactory.generic.artifactoryServerId", "0");
        configurationMap.put("artifactory.generic.resolvePattern", "");
        configurationMap.put("bintray.vcsUrl", "");
        configurationMap.put("builder.artifactoryGenericBuilder.deployableRepo", "Informix");
        configurationMap.put("bintray.gpgPassphrase", "");

        return new AnyTask(new AtlassianModule("org.jfrog.bamboo.bamboo-artifactory-plugin:artifactoryGenericTask"))
                .description(this.description)
                .configuration(configurationMap);
    }

    private Task getInjectTask() {
        InjectVariablesTask task = new InjectVariablesTask().description(this.description);
        if (this.namespace == null || this.namespace.isEmpty()) {
            throw new RuntimeException("Missing 'namespace' value from yaml for INJECT");
        }
        if (this.propertiesFile == null || this.propertiesFile.isEmpty()) {
            throw new RuntimeException("Missing 'propertiesFile' value from yaml for INJECT");
        }
        task.namespace(this.namespace).path(this.propertiesFile);
        switch (this.scope) {
            case LOCAL:
                return task.scopeLocal();
            case RESULT:
                return task.scopeResult();
            default:
                throw new RuntimeException("Unexpected 'scope' value from yaml " + this.scope);
        }
    }

    private Task getVersionControlTask() {
        VcsCheckoutTask task = new VcsCheckoutTask().description(this.description);
        if (this.defaultRepository) {
            if (!Strings.isNullOrEmpty(this.cloneDirectory)) {
                task.checkoutItems(new CheckoutItem().defaultRepository().path(this.cloneDirectory));
            } else {
                task.checkoutItems(new CheckoutItem().defaultRepository());
            }
        }
        if (this.repositories != null) {
            for (RepositoryModel vcs : this.repositories) {
                task.checkoutItems(vcs.asCheckoutItem());
            }
        }
        if (this.cleanCheckout) {
            task.cleanCheckout(true);
        }
        return task;
    }

    private Task getScriptTask() {
        if (this.body == null) {
            throw new RuntimeException("Missing 'body' value from yaml for SCRIPT");
        }
        ScriptTask scriptTask = new ScriptTask().description(this.description).inlineBody(this.body);
        if (this.workingDirectory != null) {
            scriptTask.workingSubdirectory(this.workingDirectory);
        }
        return scriptTask;
    }

    private Task getJunitTask() {
        if (this.resultFrom == null) {
            throw new RuntimeException("Missing 'resultFrom' value from yaml for JUNIT");
        }
        return new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                .description(this.description)
                .resultDirectories(this.resultFrom);
    }

    private Task getTestngTask() {
        if (this.resultFrom == null) {
            throw new RuntimeException("Missing 'resultFrom' value from yaml for TESTNG");
        }
        return new TestParserTask(TestParserTaskProperties.TestType.TESTNG)
                .description(this.description)
                .resultDirectories(this.resultFrom);
    }

    private Task getArtefactDownloadTask() {
        return new ArtifactDownloaderTask().artifacts(new DownloadItem().allArtifacts(true));
    }

    private Task getDockerTask() {
        if (this.image == null) {
            throw new RuntimeException("DOCKER tasks require 'image' to be set");
        }

        // note the serviceURLPattern comes from the Bamboo docs and spec dump and doesn't
        // appear to be sensible to override.
        DockerRunContainerTask docker = new DockerRunContainerTask().imageName(this.image)
                .description(this.description)
                .serviceURLPattern("http://localhost:${docker.port}");

        // legacy options
        if (this.command != null) docker.containerCommand(this.command);
        if (this.workingDirectory != null) docker.containerWorkingDirectory(this.workingDirectory);
        // end legacy

        if (this.container != null) {
            this.container.applyConfig(docker);
        }

        if (this.detach) {
            if (this.container == null || this.container.name == null) {
                throw new RuntimeException("DOCKER detached tasks require 'container' -> 'name' to be set");
            }
            docker.detachContainer(true);
            if (this.serviceStartCheck != null) {
                this.serviceStartCheck.applyConfig(docker);
            }
        }

        if (this.environmentVariables != null) docker.environmentVariables(this.environmentVariables);

        if (this.cmdLineArguments != null) docker.additionalArguments(this.cmdLineArguments);

        docker.clearPortMappings();
        if (this.portMappings != null) {
            for (PortMapping port : this.portMappings) {
                docker.appendPortMapping(port.local, port.container);
            }
        }

        docker.clearVolumeMappings();
        if (this.volumeMappings != null) {
            for (VolumeMapping volume : this.volumeMappings) {
                docker.appendVolumeMapping(volume.local, volume.container);
            }
        }
        return docker;
    }
}
