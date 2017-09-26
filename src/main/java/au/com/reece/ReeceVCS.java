package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;

public class ReeceVCS extends CheckRequired {
    @Required public String name;
    public String path = "";

    public CheckoutItem asCheckoutItem() {
        if (!this.checkRequired()) return null;
        CheckoutItem vcs = new CheckoutItem().repository(new VcsRepositoryIdentifier().name(this.name));
        if (!this.path.isEmpty()) vcs.path(this.path);
        return vcs;
    }
}
