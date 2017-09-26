package au.com.reece.deliveryengineering.bamboospecs;

import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ReeceSpecs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReeceSpecs.class);

    public static void main(final String[] args) throws Exception {
        UserPasswordCredentials adminUser = new FileUserPasswordCredentials("./.credentials");
        boolean publish = true;

        Options options = new Options();

        options.addOption("t", false, "parse yaml only, do not publish");
        options.addOption("h", false, "display this help");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
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
        } else if (remains[0].equals("permissions")) {
            new PermissionsControl().run(adminUser, new File(remains[1]), publish);
        } else if (remains[0].equals("plan")) {
            new PlanControl().run(adminUser, new File(remains[1]), publish);
        } else {
            LOGGER.error("Error: unrecognised <command> " + remains[0]);
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("reece-specs [options] <command> <yaml file>",
                "options:", options, "");
    }

}
