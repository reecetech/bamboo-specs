package au.com.reece.de.bamboospecs;

import com.atlassian.bamboo.specs.util.UserPasswordCredentials;

import java.io.File;

public interface BambooController {
    void run(UserPasswordCredentials adminUser, String filePath, boolean publish);
    void run(UserPasswordCredentials adminUser, File yamlFile, boolean publish);
}
