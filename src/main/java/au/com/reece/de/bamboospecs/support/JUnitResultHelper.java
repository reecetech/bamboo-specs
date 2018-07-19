package au.com.reece.de.bamboospecs.support;

import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class JUnitResultHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitResultHelper.class);

    public static void handleOutcome(Exception exception, long time, String path) {
        String specResultName = path
                .replaceAll(System.getProperty("user.dir"), "")
                .replaceAll("/", "_")
                .replaceAll("-", "_");

        Map<String, Object> values = populateTemplateInformation(exception, time, specResultName);

        JtwigTemplate template = JtwigTemplate.classpathTemplate("resultTemplate.twig");
        JtwigModel model = JtwigModel.newModel(values);

        try {
            File resultDirectory = new File("results");

            resultDirectory.mkdir();

            File destinationFile = new File("results/" + specResultName + ".xml");
            if (!destinationFile.createNewFile()) {
                throw new RuntimeException("Failed to create file");
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
