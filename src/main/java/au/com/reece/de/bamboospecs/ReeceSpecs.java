/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.common.BambooYamlFileModel;
import au.com.reece.de.bamboospecs.support.JUnitResultHelper;
import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.util.Set;

public class ReeceSpecs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReeceSpecs.class);
    private final JUnitResultHelper resultHelper;

    public static void main(final String[] args) throws Exception {
        ReeceSpecs specs = new ReeceSpecs();
        specs.runSpecs(args);
    }

    private ReeceSpecs() {
        this(new JUnitResultHelper());
    }

    ReeceSpecs(JUnitResultHelper resultHelper) {
        this.resultHelper = resultHelper;
    }

    private void runSpecs(String[] args) throws ParseException {
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

    void runFileProcess(UserPasswordCredentials adminUser, boolean publish, String path) {
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
        resultHelper.handleOutcome(exception, stopWatch.getTime(), path);
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
            LOGGER.error("Exception, {}", e.getMessage());
            throw new RuntimeException("Error reading YAML file: " + e.getMessage());
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("reece-specs [options] <yaml file> ...", "options:", options, "");
    }
}
