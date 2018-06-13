package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.BambooYamlFileModel;
import au.com.reece.de.bamboospecs.models.exception.InvalidSyntaxException;
import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ReeceSpecs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReeceSpecs.class);

    public static void main(final String[] args) throws Exception {
        Options options = getCommandLineOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        UserPasswordCredentials adminUser = setupCredentials(cmd);

        boolean publish = determinePublishing(cmd);

        if (cmd.getArgList().isEmpty()) {
            LOGGER.error("Error: missing required <yaml file(s)>");
            printHelp(options);
            return;
        }

        for (String path : cmd.getArgList()) {
            BambooYamlFileModel bambooFile = readAndValidateYamlFile(path);
            BambooController controller = getBambooController(path, bambooFile);
            controller.run(adminUser, path, publish);
        }
    }

    private static boolean determinePublishing(CommandLine cmd) {
        if (cmd.hasOption("t")) {
            LOGGER.info("Parsing yaml only, not publishing");
            return false;
        }
        return true;
    }

    @NotNull
    private static UserPasswordCredentials setupCredentials(CommandLine cmd) {
        UserPasswordCredentials adminUser;
        if (cmd.hasOption("u")) {
            String username = cmd.getOptionValue("u");
            String password;
            if (cmd.hasOption("p")) {
                password = cmd.getOptionValue("p");
            } else {
                password = new String(System.console().readPassword("Enter password for '%s': ", username));
            }
            adminUser = new SimpleUserPasswordCredentials(username, password);
        } else {
            adminUser = new FileUserPasswordCredentials(cmd.getOptionValue("c", "./.credentials"));
        }
        return adminUser;
    }

    @NotNull
    private static Options getCommandLineOptions() {
        Options options = new Options();

        options.addOption("t", false, "Parse yaml only, do not publish");
        options.addOption("u", true, "Bamboo user to publish as");
        options.addOption("p", true, "Bamboo user's password");
        options.addOption("c", true, "Credentials file with Bamboo user login");
        options.addOption("h", false, "Display this help");

        return options;
    }

    @NotNull
    private static BambooController getBambooController(String path, BambooYamlFileModel bambooFile) {
        BambooController controller;
        switch (bambooFile.getFileType()) {
            case PLAN:
                controller = new PlanControl();
                break;
            case DEPLOYMENT:
                controller = new DeploymentControl();
                break;
            case PERMISSIONS:
                controller = new PermissionsControl();
                break;
            case PLAN_INCLUDE:
            case DEPLOY_INCLUDE:
                controller = new NoOpController();
                break;
            default:
                throw new RuntimeException(String.format("File %s is unknown (%s) - not processing", path, bambooFile.getFileType()));
        }
        return controller;
    }

    private static BambooYamlFileModel readAndValidateYamlFile(String path) {
        try {
            BambooYamlFileModel bambooFile;

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            bambooFile = mapper.readValue(new File(path), BambooYamlFileModel.class);

            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Set<ConstraintViolation<BambooYamlFileModel>> violations = validator.validate(bambooFile);
            if (!violations.isEmpty()) {
                violations.forEach(x -> LOGGER.error("{}: {}", x.getPropertyPath(), x.getMessage()));
                throw new InvalidSyntaxException("Validation errors occurred - please see above");
            }
            return bambooFile;
        } catch (JsonProcessingException e) {
            throw new InvalidSyntaxException(e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading YAML file", e);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("reece-specs [options] <yaml file> ...","options:", options, "");
    }
}
