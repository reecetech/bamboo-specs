package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;

public class RepositoryModel extends DomainModel {
    public String name;
    public String projectKey;
    public String repositorySlug;

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
