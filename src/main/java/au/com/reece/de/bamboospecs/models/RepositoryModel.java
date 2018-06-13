package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepositoryModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String name;

    public String projectKey;

    public String repositorySlug;

    public String gitURL;

    public String path;

    public String branch;

    public CheckoutItem asCheckoutItem() {
        CheckoutItem vcs = new CheckoutItem().repository(new VcsRepositoryIdentifier().name(this.name));
        if (this.path != null && !this.path.isEmpty()) vcs.path(this.path);
        return vcs;
    }

    Plan addToPlan(Plan plan) {
<<<<<<< HEAD:src/main/java/au/com/reece/de/bamboospecs/models/RepositoryModel.java
        if (this.projectKey != null && !this.projectKey.isEmpty()) {
=======
        if (this.projectKey != null && this.gitURL == null) {
>>>>>>> origin/master:src/main/java/au/com/reece/deliveryengineering/bamboospecs/models/RepositoryModel.java
            if (this.repositorySlug == null || this.repositorySlug.isEmpty()) {
                throw new RuntimeException("Invalid repository (projectKey AND repositorySlug)");
            }
            BitbucketServerRepository stash = new BitbucketServerRepository()
                .name(this.name)
                .server(new ApplicationLink().name("Stash"))
                .projectKey(this.projectKey)
                .repositorySlug(this.repositorySlug)
                // set some "default" options
                .repositoryViewer(new BitbucketServerRepositoryViewer())
                .shallowClonesEnabled(true)
                .remoteAgentCacheEnabled(false);
            if (this.branch != null && !this.branch.isEmpty()) {
                stash.branch(this.branch);
            }
            return plan.planRepositories(stash);
        } else if (this.gitURL != null && !this.gitURL.isEmpty()) {
            GitRepository git = new GitRepository();
            if (this.name == null || this.name.isEmpty()) {
                throw new RuntimeException("Invalid repository (needs gitURL AND path)");
            }
            git.name(this.name).url(this.gitURL);
            if (this.branch != null) {
                git.branch(this.branch);
            }
            return plan.planRepositories(git);
        }
        throw new RuntimeException("Invalid repository (missing projectKey or gitURL)");
    }
}
