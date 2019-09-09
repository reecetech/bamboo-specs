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
package au.com.reece.de.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.Dependencies;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.DependenciesConfiguration;

import java.util.ArrayList;
import java.util.Set;

public class DependencyModel extends BambooBaseModel {
    public final boolean requiresPassing = true;

    public Set<String> plans;

    public final boolean none = false;

    public void addToPlan(Plan plan) {
        ArrayList<PlanIdentifier> children = new ArrayList<>();
        if (this.none) {
            plan.dependencies(new Dependencies());
            return;
        }

        if (this.plans == null) {
            throw new RuntimeException("dependencies must be either 'none' or must list plans");
        }

        for (String idString : this.plans) {
            String[] parts = idString.split("-");
            children.add(new PlanIdentifier(parts[0], parts[1]));
        }
        plan.dependencies(new Dependencies()
                .configuration(new DependenciesConfiguration()
                        .requireAllStagesPassing(this.requiresPassing))
                .childPlans(children.toArray(new PlanIdentifier[0])));
    }
}
