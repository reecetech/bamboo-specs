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
