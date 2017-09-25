package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;

public class ReeceRepository {
    private String name;
    private String projectKey;
    private String repositorySlug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getRepositorySlug() {
        return repositorySlug;
    }

    public void setRepositorySlug(String repositorySlug) {
        this.repositorySlug = repositorySlug;
    }

    Plan addToPlan(Plan plan) {
        return plan.planRepositories(new BitbucketServerRepository()
                .name(this.getName())
                .server(new ApplicationLink().name("Stash"))
                .projectKey(this.getProjectKey())
                .repositorySlug(this.getRepositorySlug())
                // set some "default" options
                .repositoryViewer(new BitbucketServerRepositoryViewer())
                .shallowClonesEnabled(true)
                .remoteAgentCacheEnabled(false)
        );
    }
}
