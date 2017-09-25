package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;

public class ReeceArtifact {
    private String name;
    private String pattern;
    private String location;

    public Artifact asArtifact() {
        return new Artifact(this.name).copyPattern(this.pattern).location(this.location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
