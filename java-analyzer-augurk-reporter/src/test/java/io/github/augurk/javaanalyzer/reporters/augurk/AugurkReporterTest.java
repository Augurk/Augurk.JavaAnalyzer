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

package io.github.augurk.javaanalyzer.reporters.augurk;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Collections;

import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AugurkReporterTest {
    private AnalyzeOptions optionsMock;
    private AugurkAgent agentMock;

    private AugurkReporter target;

    @Before
    public void beforeEach() {
        optionsMock = mock(AnalyzeOptions.class);
        agentMock = mock(AugurkAgent.class);

        target = new AugurkReporter(optionsMock, agentMock);
    }

    @Test
    public void report_ShouldCallAugurkAgentWithJSONString() {
        // Arrange
        var analyzedProject = "project";
        var projectVersion = "version";
        var timestamp = LocalDateTime.now();

        var rootInvocationKind = InvocationKind.WHEN;
        var rootInvocationSignature = "rootSignature";
        var rootInvocationExpressions = new String[] { "When expression" };

        var invocationKind = InvocationKind.PUBLIC;
        var invocationSignature = "Signature";
        var invocationIsLocal = true;

        var reportMock = mock(AnalysisReport.class);
        var rootInvocationMock = mock(Invocation.class);
        var invocationMock = mock(Invocation.class);

        when(reportMock.getAnalyzedProject()).thenReturn(analyzedProject);
        when(reportMock.getVersion()).thenReturn(projectVersion);
        when(reportMock.getTimestamp()).thenReturn(timestamp);
        when(reportMock.getRootInvocations()).thenReturn(Collections.singletonList(rootInvocationMock));

        when(rootInvocationMock.getKind()).thenReturn(rootInvocationKind);
        when(rootInvocationMock.getSignature()).thenReturn(rootInvocationSignature);
        when(rootInvocationMock.getRegularExpression()).thenReturn(rootInvocationExpressions);
        when(rootInvocationMock.getAutomationTargets()).thenReturn(new String[] {});
        when(rootInvocationMock.getInvocations()).thenReturn(new ArrayDeque<>() {{
            add(invocationMock);
        }});

        when(invocationMock.getKind()).thenReturn(invocationKind);
        when(invocationMock.getSignature()).thenReturn(invocationSignature);
        when(invocationMock.isLocal()).thenReturn(invocationIsLocal);
        when(invocationMock.getInvocations()).thenReturn(new ArrayDeque<>());

        var expected = new JSONObject();
        expected.put("AnalyzedProject", analyzedProject);
        expected.put("Version", projectVersion);
        expected.put("Timestamp", timestamp);

        var expectedRootInvocation = new JSONObject();
        expectedRootInvocation.put("Kind", rootInvocationKind.toString());
        expectedRootInvocation.put("Signature", rootInvocationSignature);
        expectedRootInvocation.put("RegularExpressions", rootInvocationExpressions);

        var expectedInvocation = new JSONObject();
        expectedInvocation.put("Kind", invocationKind.toString());
        expectedInvocation.put("Signature", invocationSignature);
        expectedInvocation.put("Local", invocationIsLocal);
        expectedInvocation.put("Invocations", new JSONArray());

        expectedRootInvocation.append("Invocations", expectedInvocation);
        expected.append("RootInvocations", expectedRootInvocation);

        // Act
        target.report(reportMock);

        // Assert
        verify(agentMock).postReport("project", "version", expected.toString());
    }
}
