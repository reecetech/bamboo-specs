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

import au.com.reece.de.bamboospecs.support.JUnitResultHelper;
import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReeceSpecsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JUnitResultHelper resultHelper;

    private ReeceSpecs testInstance;

    @Before
    public void setupTest() {
        initMocks(this);

        testInstance = new ReeceSpecs(resultHelper);
    }

    @Test
    public void runFileProcess_invalidFile() {
        testInstance.runFileProcess(new SimpleUserPasswordCredentials("user", "pass"), true, "classpath:/permissions_invalid_yaml.yaml");

        verify(resultHelper).handleOutcome(any(RuntimeException.class), anyLong(), eq("classpath:/permissions_invalid_yaml.yaml"));
    }
}
