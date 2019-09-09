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
package au.com.reece.de.bamboospecs.models.deployment;

import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


// TODO document me
public class ReleaseNamingModel {
    @NotNull
    @NotEmpty
    public String pattern;

    public final boolean autoIncrement = false;

    public String[] autoIncrementVariables;

    public ReleaseNaming asReleaseNaming() {
        ReleaseNaming releaseNaming = new ReleaseNaming(this.pattern);
        releaseNaming = releaseNaming.autoIncrement(this.autoIncrement);

        if (this.autoIncrementVariables != null) {
            releaseNaming = releaseNaming.variablesToAutoIncrement(this.autoIncrementVariables);
        }
        return releaseNaming;
    }
}
