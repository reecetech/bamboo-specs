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
package au.com.reece.de.bamboospecs.support;

import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class JUnitResultHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitResultHelper.class);

    public void handleOutcome(Exception exception, long time, String path) {
        String specResultName = path
                .replaceAll(System.getProperty("user.dir"), "")
                .replaceAll("/", "_")
                .replaceAll("-", "_");

        Map<String, Object> values = populateTemplateInformation(exception, time, specResultName);

        JtwigTemplate template = JtwigTemplate.classpathTemplate("resultTemplate.twig");
        JtwigModel model = JtwigModel.newModel(values);

        try {
            File resultDirectory = new File("results");

            if (!resultDirectory.mkdir()) {
                throw new RuntimeException("Failed to create directory at path " + resultDirectory.getAbsolutePath());
            }

            File destinationFile = new File("results/" + specResultName + ".xml");

            if (destinationFile.exists()) {
                String resultFileName = "results/" + specResultName + LocalTime.now().toString() + ".xml";
                LOGGER.warn("Destination XML file already exists - creating as {}", resultFileName);
                destinationFile = new File(resultFileName);
            }

            if (!destinationFile.createNewFile()) {
                throw new RuntimeException("Failed to create file " + destinationFile.getAbsolutePath());
            }
            template.render(model, new FileOutputStream(destinationFile));
            LOGGER.debug("Wrote JUnit XML to {}", destinationFile.getAbsolutePath());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @NotNull
    private static Map<String, Object> populateTemplateInformation(Exception exception, long time, String specResultName) {
        Map<String, Object> values = new HashMap<>();

        values.put("time", time / 1000.0);
        values.put("specName", specResultName);

        if (exception == null) {
            values.put("successful", true);
        } else {
            values.put("successful", false);
            values.put("failureMessage", exception.getMessage());
            values.put("stacktrace", exception.getStackTrace());
        }
        return values;
    }

}
