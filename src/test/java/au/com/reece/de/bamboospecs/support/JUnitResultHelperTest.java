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

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JUnitResultHelperTest {
    private JUnitResultHelper testInstance;

    @Before
    public void setUp() throws Exception {
        File resultsDirectory = new File("results");
        FileUtils.deleteDirectory(resultsDirectory);
        testInstance = new JUnitResultHelper();
    }

    @Test
    public void handleOutcome_passedTest() throws IOException {
        testInstance.handleOutcome(null, 1000, "alastair/test/plan.yaml");

        String result = getResultFile();

        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<testsuite errors=\"0\" skipped=\"0\" tests=\"1\" time=\"1.0\" failures=\"0\" name=\"au.com.reece.de.bamboospecs\">\n" +
                "    \n" +
                "        <testcase time=\"1.0\" name=\"alastair_test_plan.yaml\"/>\n" +
                "    \n" +
                "</testsuite>\n";

        Diff xmlDiff = DiffBuilder
                .compare(expectedResult)
                .withTest(result)
                .checkForSimilar()
                .build();

        assertThat(xmlDiff.hasDifferences(), is(false));
    }

    @Test
    public void handleOutcomeFileExists() {
        testInstance.handleOutcome(null, 1000, "alastair/test/plan.yaml");

        testInstance.handleOutcome(null, 1000, "alastair/test/plan.yaml");

        File destination = new File("results");
        assertThat(destination.exists(), is(true));

        assertThat(Objects.requireNonNull(destination.listFiles()).length, is(2));
    }

    @NotNull
    private String getResultFile() throws IOException {
        File resultFile = new File("results/alastair_test_plan.yaml.xml");
        assertThat(resultFile.exists(), is(true));

        return new String(Files.readAllBytes(resultFile.toPath()), Charset.defaultCharset());
    }

    @Test
    public void handleOutcome_failedTest() throws IOException {
        RuntimeException exception = new RuntimeException("Oopsie doodle!");

        testInstance.handleOutcome(exception, 1000, "alastair/test/plan.yaml");

        String result = getResultFile();

        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<testsuite errors=\"0\" skipped=\"0\" tests=\"1\" time=\"1.0\" failures=\"0\" name=\"au.com.reece.de.bamboospecs\">\n" +
                "    \n" +
                "        <testcase time=\"1.0\" name=\"alastair_test_plan.yaml\">\n" +
                "            \n" +
                "            <failure type=\"junit.framework.AssertionFailedError\"\n" +
                "                     message=\"Oopsie doodle!\">\n" +
                "            </failure>\n" +
                "            \n" +
                "        </testcase>\n" +
                "    \n" +
                "</testsuite>\n";

        Diff xmlDiff = DiffBuilder
                .compare(expectedResult)
                .withTest(result)
                .withDifferenceEvaluator((comparison, outcome) -> {
                    if (comparison.getType() == ComparisonType.TEXT_VALUE
                            && comparison.getControlDetails().getTarget().getParentNode().getLocalName().equalsIgnoreCase("failure")) {
                        return ComparisonResult.SIMILAR;
                    } else {
                        return outcome;
                    }
                })
                .checkForSimilar()
                .build();

        assertThat(xmlDiff.toString(), xmlDiff.hasDifferences(), is(false));
    }
}
