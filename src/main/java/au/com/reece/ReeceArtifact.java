package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;

import javax.annotation.Nullable;

public class ReeceArtifact extends CheckRequired {
    @Required public String name;
    @Required public String pattern;
    @Required public String location;

    @Nullable
    public Artifact asArtifact() {
        if (!this.checkRequired()) return null;
        return new Artifact(this.name).copyPattern(this.pattern).location(this.location);
    }
}
