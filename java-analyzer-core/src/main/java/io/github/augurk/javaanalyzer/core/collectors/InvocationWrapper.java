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

package io.github.augurk.javaanalyzer.core.collectors;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;
import org.apache.commons.lang3.tuple.ImmutablePair;

class InvocationWrapper extends Invocation {
    private final InvocationWrapper parent;
    private final List<ImmutablePair<String, String>> argumentTypes;
    private final AutomationTarget automationTarget;

    public InvocationWrapper(String signature, String whenExpression, AutomationTarget automationTarget) {
        super(signature, whenExpression);
        parent = null;
        argumentTypes = Lists.newArrayList();
        this.automationTarget = automationTarget;
    }

    public InvocationWrapper(InvokedMethod method, InvocationWrapper parent) {
        super(method.getKind(), method.getSignature(), method.getInterfaceDefinitions(), method.isLocal());
        this.parent = parent;
        this.argumentTypes = method.getArgumentTypes();
        automationTarget = DefaultAutomationTarget.getInstance();
    }

    public InvocationWrapper getParent() {
        return parent;
    }


    public InvocationWrapper addInvocation(InvokedMethod method) {
        var invocation = new InvocationWrapper(method, this);
        super.addInvocation(invocation);
        return invocation;
    }

    public void process() {
        if (automationTarget instanceof DefaultAutomationTarget) return;
        List<String> automationTargets = process(getInvocations(), null);
        super.setAutomationTargets(automationTargets.toArray(String[]::new));
    }

    public String findArgumentType(String qualifiedName) {
        return findArgumentType(this, qualifiedName, qualifiedName);
    }

    private String findArgumentType(InvocationWrapper invocation, String search, String found) {
        for (var typePair : invocation.argumentTypes) {
            if (typePair.left.equals(search)) {
                found = typePair.right;
                break;
            }
        }

        return invocation.parent != null
            ? findArgumentType(invocation.parent, search, found)
            : found;
    }

    private List<String> process(Deque<Invocation> invocations, List<String> found) {
        if (found == null) found = Lists.newLinkedList();

        for (var invocation : invocations) {
            String signature = invocation.getSignature();

            if (automationTarget.getTargetMethods().contains(signature)) {
                found.add(signature);
            }

            if (!found.isEmpty() && automationTarget.getOverloadHandling() == OverloadHandling.FIRST) {
                return found;
            }

            if (!invocation.getInvocations().isEmpty()) {
                process(invocation.getInvocations(), found);
            }
        }

        return automationTarget.getOverloadHandling() == OverloadHandling.LAST
            ? Collections.singletonList(Iterables.getLast(found, ""))
            : found;
    }
}
