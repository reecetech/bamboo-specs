package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.BambooYamlFileModel;
import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReeceSpecs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReeceSpecs.class);

    public static void main(final String[] args) throws Exception {
        runSpecs(args);
    }

    private static void runSpecs(String[] args) throws ParseException {
        Options options = getCommandLineOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            printHelp(options);
            return;
        }

        UserPasswordCredentials adminUser = setupCredentials(cmd);

        boolean publish = determinePublishing(cmd);

        if (cmd.getArgList().isEmpty()) {
            printHelp(options);
            throw new RuntimeException("Error: missing required file(s)");
        }

        for (String path : cmd.getArgList()) {
            runFileProcess(adminUser, publish, path);
        }
    }

    private static void runFileProcess(UserPasswordCredentials adminUser, boolean publish, String path) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Exception exception = null;
        try {
            BambooYamlFileModel bambooFile;
            bambooFile = readAndValidateYamlFile(path);
            BambooController controller = BambooController.getBambooController(path, bambooFile);
            controller.run(adminUser, path, publish);
        } catch (Exception ex) {
            exception = ex;
        } finally {
            stopWatch.stop();
        }
        handleOutcome(exception, stopWatch.getTime(), path);
    }

    private static void handleOutcome(Exception exception, long time, String path) {
        Map<String, Object> values = new HashMap<>();
        String specResultName = path.replaceAll("/", "_").replaceAll("-", "_");
        values.put("time", time/1000.0);
        values.put("specName", specResultName);

        if (exception == null) {
            values.put("successful", true);
        } else {
            values.put("successful", false);
            values.put("failureMessage", exception.getMessage());
            values.put("stacktrace", exception.getStackTrace());
        }

        JtwigTemplate template = JtwigTemplate.classpathTemplate("resultTemplate.twig");
        JtwigModel model = JtwigModel.newModel(values);

        try {
            File resultDirectory = new File("results");

            resultDirectory.mkdir();
            
            File destinationFile = new File("results/" + specResultName + ".xml");
            if (! destinationFile.createNewFile()) {
                throw new RuntimeException("Failed to create file");
            }
            template.render(model, new FileOutputStream(destinationFile));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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

    private static BambooYamlFileModel readAndValidateYamlFile(String path) {

        try {
            BambooYamlFileModel bambooFile;

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            bambooFile = mapper.readValue(new File(path), BambooYamlFileModel.class);

            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

            Set<ConstraintViolation<BambooYamlFileModel>> violations = validator.validate(bambooFile);
            if (!violations.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                violations.forEach(x -> builder.append(String.format("%s: %s%n", x.getPropertyPath(), x.getMessage())));
                throw new RuntimeException(String.format("Validation errors occurred:%n%s", builder.toString()));
            }
            return bambooFile;
        } catch (Exception e) {
            throw new RuntimeException("Error reading YAML file", e);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("reece-specs [options] <yaml file> ...", "options:", options, "");
    }
}
