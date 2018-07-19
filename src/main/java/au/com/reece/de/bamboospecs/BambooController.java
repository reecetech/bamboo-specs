package au.com.reece.de.bamboospecs;

import au.com.reece.de.bamboospecs.models.BambooYamlFileModel;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import java.io.File;

abstract class BambooController {
    static BambooController getBambooController(String path, BambooYamlFileModel bambooFile) {
        BambooController controller;
        switch (bambooFile.getFileType()) {
            case BUILD:
                controller = new BuildControl();
                break;
            case DEPLOYMENT:
                controller = new DeploymentControl();
                break;
            case PERMISSIONS:
                controller = new PermissionsControl();
                break;
            case BUILD_INCLUDE:
            case DEPLOY_INCLUDE:
                controller = new NoOpController();
                break;
            default:
                throw new RuntimeException(String.format("File %s is unknown (%s) - not processing", path, bambooFile.getFileType()));
        }

        return controller;
    }

    abstract void run(UserPasswordCredentials adminUser, String filePath, boolean publish);

    abstract void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish);
}
