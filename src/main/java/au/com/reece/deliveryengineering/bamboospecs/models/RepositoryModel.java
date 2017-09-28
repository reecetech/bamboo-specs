package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepositoryModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String projectKey;

    @NotNull
    @NotEmpty
    public String repositorySlug;

    @NotNull
    public String path = "";

    public CheckoutItem asCheckoutItem() {
        CheckoutItem vcs = new CheckoutItem().repository(new VcsRepositoryIdentifier().name(this.name));
        if (!this.path.isEmpty()) vcs.path(this.path);
        return vcs;
    }

    Plan addToPlan(Plan plan) {
        return plan.planRepositories(new BitbucketServerRepository()
                .name(this.name)
                .server(new ApplicationLink().name("Stash"))
                .projectKey(this.projectKey)
                .repositorySlug(this.repositorySlug)
                // set some "default" options
                .repositoryViewer(new BitbucketServerRepositoryViewer())
                .shallowClonesEnabled(true)
                .remoteAgentCacheEnabled(false)
        );
    }
}
