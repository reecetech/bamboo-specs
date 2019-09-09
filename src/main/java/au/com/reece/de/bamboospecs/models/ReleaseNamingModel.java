package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


// TODO document me
public class ReleaseNamingModel {
    @NotNull
    @NotEmpty
    public String pattern;

    public final boolean autoIncrement = false;

    public String[] autoIncrementVariables;

    public ReleaseNaming asReleaseNaming() {
        ReleaseNaming releaseNaming = new ReleaseNaming(this.pattern);
        releaseNaming = releaseNaming.autoIncrement(this.autoIncrement);

        if (this.autoIncrementVariables != null) {
            releaseNaming = releaseNaming.variablesToAutoIncrement(this.autoIncrementVariables);
        }
        return releaseNaming;
    }
}
