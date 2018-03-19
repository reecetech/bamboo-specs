package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.enums.FileType;
import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.util.FileUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@BambooSpec
public class AutoDeploy {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDeploy.class);

    public static void main(String[] args) {
        File specificationPath = new File("./specifications");
        if (!specificationPath.exists()
                || Objects.requireNonNull(specificationPath.listFiles()).length < 1) {
            throw new IllegalStateException("Failed to find Bamboo specifications");
        } else {
            for (File file : Objects.requireNonNull(specificationPath.listFiles())) {
                LOGGER.info("Processing the contents of {}", file.toPath());
                processSpecificationDirectory(file);
            }
        }
    }

    private static void processSpecificationDirectory(File directory) {
        if (Objects.requireNonNull(directory.listFiles()).length < 1) {
            LOGGER.warn("There were no yaml files found to process in directory {}", directory.toPath());
        } else {
            List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
            files.stream().sorted(Comparator.comparing(AutoDeploy::getFileCode)).forEach(AutoDeploy::processSpecificationFile);
        }
    }

    private static Integer getFileCode(File file) {
        FileType type = FileType.getFromFile(file);
        switch (type) {
            case PLAN:
                return 1;
            case DEPLOYMENT:
                return 2;
            case PERMISSIONS:
                return 3;
            default:
                return 4;
        }
    }

    private static void processSpecificationFile(File file) {
        LOGGER.info("Processing file {}", file.toPath());
        FileType type = FileType.getFromFile(file);
        switch (type) {
            case PLAN:
                PlanControl.run(getUser(), file, true);
                break;
            case DEPLOYMENT:
                DeploymentControl.run(getUser(), file, true);
                break;
            case PERMISSIONS:
                PermissionsControl.run(getUser(), file, true);
                break;
        }
    }

    private static UserPasswordCredentials getUser() {
        return new FileUserPasswordCredentials();
    }
}
