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

package io.github.augurk.javaanalyzer.core.domain;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Invocation {
    private InvocationKind kind;
    private String signature;
    private boolean local;
    private String[] regularExpression;
    private String[] interfaceDefinitions;
    private String[] automationTargets;
    private Deque<Invocation> invocations;

    public Invocation(InvocationKind kind, String signature, String[] interfaceDefinitions, boolean isLocal) {
        this.kind = kind;
        this.signature = signature;
        this.invocations = new ArrayDeque<>();
        this.local = isLocal;
        this.interfaceDefinitions = Arrays.copyOf(interfaceDefinitions, interfaceDefinitions.length);
        this.automationTargets = new String[] {};
    }

    public Invocation(String signature, String whenExpression) {
        this(InvocationKind.WHEN, signature, new String[] {}, false);
        regularExpression = new String[] { whenExpression };
    }

    public InvocationKind getKind() {
        return kind;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isLocal() {
        return local;
    }

    public String[] getRegularExpression() {
        return Arrays.copyOf(regularExpression, regularExpression.length);
    }

    public String[] getInterfaceDefinitions() {
        return Arrays.copyOf(interfaceDefinitions, interfaceDefinitions.length);
    }

    public String[] getAutomationTargets() {
        return Arrays.copyOf(automationTargets, automationTargets.length);
    }

    public Deque<Invocation> getInvocations() {
        return invocations;
    }

    protected void addInvocation(Invocation invocation) {
        invocations.add(invocation);
    }

    protected void setAutomationTargets(String[] automationTargets) {
        this.automationTargets = automationTargets;
    }
}
