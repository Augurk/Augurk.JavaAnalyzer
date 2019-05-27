/*
 * Copyright 2019, Augurk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.augurk.javaanalyzer.core.analyzers;

import java.util.List;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.augurk.javaanalyzer.core.collectors.AutomationTarget;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;

public class AutomationTargetImpl implements AutomationTarget, QualifiedName {
    private final ClassOrInterfaceType declaringType;
    private final List<String> targetMethods;
    private final OverloadHandling overloadHandling;

    public AutomationTargetImpl(ClassOrInterfaceType declaringType, List<String> targetMethods, OverloadHandling overloadHandling) {
        this.declaringType = declaringType;
        this.targetMethods = targetMethods;
        this.overloadHandling = overloadHandling;
    }

    @Override
    public String getDeclaringType() {
        return qualifiedNameOf(declaringType);
    }

    @Override
    public List<String> getTargetMethods() {
        return targetMethods;
    }

    @Override
    public OverloadHandling getOverloadHandling() {
        return overloadHandling;
    }
}
