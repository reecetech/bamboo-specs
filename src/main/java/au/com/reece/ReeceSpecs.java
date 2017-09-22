package au.com.reece;

import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import java.io.File;

public class ReeceSpecs {

    public static void main(final String[] args) throws Exception {
        UserPasswordCredentials adminUser = new FileUserPasswordCredentials("./.credentials");
        File file = new File(args[1]);

        if (args[0].equals("permissions")) {
            new PermissionsControl().run(adminUser, file);
        } else if (args[0].equals("plan")) {
            new PlanControl().run(adminUser, file);
        }
    }

}
