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

import java.util.Deque;

import com.google.common.collect.Queues;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;

public class InvocationCollector {
    private final AnalysisReport report;

    private Deque<InvokedMethod> callStack;
    private InvocationWrapper rootInvocation;
    private InvocationWrapper currentInvocation;

    public InvocationCollector(AnalyzeOptions options) {
        String projectName = options.getProjectName();
        String projectVersion = options.getVersion();

        report = new AnalysisReport(projectName, projectVersion);
        callStack = Queues.newArrayDeque();
    }

    public AnalysisReport getReport() {
        return report;
    }

    public Invocation beginRootInvocation(String signature, String whenExpression, AutomationTarget automationTarget) {
        automationTarget = automationTarget == null
            ? DefaultAutomationTarget.getInstance()
            : automationTarget;

        InvocationWrapper invocation = new InvocationWrapper(signature, whenExpression, automationTarget);
        rootInvocation = invocation;
        return step(invocation);
    }

    public void endRootInvocation() {
        rootInvocation.process();
        report.addRootInvocation(rootInvocation);
        callStack = Queues.newArrayDeque();
        rootInvocation = currentInvocation = null;
    }

    public Invocation stepOut() {
        callStack.poll();
        var parent = currentInvocation.getParent();
        return parent == null ? rootInvocation : step(parent);
    }

    public Invocation collect(InvokedMethod method) {
        callStack.push(method);
        InvocationWrapper invocation = currentInvocation.addInvocation(method);
        return step(invocation);
    }

    public boolean isAlreadyCollected(InvokedMethod invokedMethod) {
        return callStack.contains(invokedMethod);
    }

    public String getActualType(String qualifiedName) {
        return currentInvocation.findArgumentType(qualifiedName);
    }

    private Invocation step(InvocationWrapper invocation) {
        currentInvocation = invocation;
        return currentInvocation;
    }
}
