package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.artifact.ArtifactSubscription;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ArtifactSubscriptionModel {
    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String destination;

    public ArtifactSubscription asArtifactSubscription() {
        return new ArtifactSubscription().artifact(this.name).destination(this.destination);
    }
}
