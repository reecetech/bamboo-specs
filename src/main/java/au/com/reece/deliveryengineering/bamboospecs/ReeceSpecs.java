package au.com.reece.deliveryengineering.bamboospecs;

import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;

public class ReeceSpecs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReeceSpecs.class);

    public static void main(final String[] args) throws Exception {
        boolean publish = true;

        Options options = new Options();

        options.addOption("t", false, "parse yaml only, do not publish");
        options.addOption("u", true, "Bamboo user to publish as");
        options.addOption("p", true, "Bamboo user's password");
        options.addOption("c", true, "credentials file with Bamboo user login");
        options.addOption("h", false, "display this help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        UserPasswordCredentials adminUser;
        if (cmd.hasOption("u")) {
            String username = cmd.getOptionValue("u");
            String password;
            if (cmd.hasOption("p")) {
                password = cmd.getOptionValue("c");
            } else {
                Console console = System.console();
                char passwordArray[] = console.readPassword("Enter password for '%s': ", username);
                password = new String(passwordArray);
            }
            adminUser = new SimpleUserPasswordCredentials(username, password);
        } else {
            adminUser = new FileUserPasswordCredentials(cmd.getOptionValue("c", "./.credentials"));
        }

        // do we publish?
        if (cmd.hasOption("t")) {
            publish = false;
            LOGGER.info("Parsing yaml only, not publishing");
        }

        String[] remains = cmd.getArgs();

        if (remains.length < 2) {
            LOGGER.error("Error: missing required <command> and <yaml file> arguments");
            printHelp(options);
            return;
        }

        // operate on all files
        for (int i=1; i < remains.length; i++) {
            String filePath = remains[i];
            switch (remains[0]) {
                case "permissions":
                    PermissionsControl.run(adminUser, filePath, publish);
                    break;
                case "plan":
                    PlanControl.run(adminUser, filePath, publish);
                    break;
                case "deployment":
                    DeploymentControl.run(adminUser, filePath, publish);
                    break;
                default:
                    LOGGER.error("Error: unrecognised <command> " + remains[0]);
                    printHelp(options);
                    return;
            }
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("reece-specs [options] <permissions|plan|deployment> <yaml file> ...",
                "options:", options, "");
    }
}
