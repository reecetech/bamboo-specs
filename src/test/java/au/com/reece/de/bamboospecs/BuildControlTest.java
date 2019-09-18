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

import com.atlassian.bamboo.specs.util.SimpleUserPasswordCredentials;
import com.atlassian.bamboo.specs.util.UserPasswordCredentials;
import org.junit.Test;

import java.io.File;

public class BuildControlTest {
    @Test(expected = RuntimeException.class)
    public void run() {
        BuildControl control = new BuildControl();
        UserPasswordCredentials users = new SimpleUserPasswordCredentials("xx", "xx");
        File file = new File(getClass().getResource("illegalCharacters.yaml").getPath());
        control.run(users, file, false);
    }
}