package au.com.reece.de.bamboospecs;

import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class NoOpController extends BambooController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpController.class);

    @Override
    public void run(UserPasswordCredentials adminUser, String filePath, boolean publish) {
        LOGGER.info("File {} is a an include - not processing", filePath);
    }

    @Override
    public void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish) {
        run(adminUser, yamlFile.getPath(), publish);
    }
}
