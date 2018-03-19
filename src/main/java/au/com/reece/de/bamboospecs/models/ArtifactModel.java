package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ArtifactModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String pattern;

    @NotNull
    @NotEmpty
    public String location;

    public Artifact asArtifact() {
        return new Artifact(this.name).copyPattern(this.pattern).location(this.location);
    }
}
