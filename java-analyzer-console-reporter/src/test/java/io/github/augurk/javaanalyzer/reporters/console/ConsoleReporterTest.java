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

package io.github.augurk.javaanalyzer.reporters.console;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayDeque;
import java.util.List;

import ch.qos.logback.core.Appender;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleReporterTest {
    private Appender appenderMock;
    private ArgumentCaptor<Appender> appenderCaptor;

    @InjectMocks
    private ConsoleReporter target;

    @Before
    public void beforeEach() {
        appenderMock = mock(Appender.class);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(appenderMock);

        appenderCaptor = ArgumentCaptor.forClass(Appender.class);
    }

    @Test
    public void report_ShouldWriteAnalysisReportToConsole() {
        // Arrange
        var reportMock = mock(AnalysisReport.class);
        var rootInvocationMock = mock(Invocation.class);
        var firstInvocationMock = mock(Invocation.class);
        var secondInvocationMock = mock(Invocation.class);
        var thirdInvocation = mock(Invocation.class);

        when(reportMock.getAnalyzedProject()).thenReturn("AnalyzedProject");
        when(reportMock.getRootInvocations()).thenReturn(List.of(rootInvocationMock));
        when(rootInvocationMock.getSignature()).thenReturn("rootInvocationSignature");
        when(rootInvocationMock.getInvocations()).thenReturn(new ArrayDeque<>() {{
            add(firstInvocationMock);
            add(secondInvocationMock);
        }});

        when(firstInvocationMock.getSignature()).thenReturn("firstInvocationSignature");
        when(firstInvocationMock.getInvocations()).thenReturn(new ArrayDeque<>() {{
            add(thirdInvocation);
        }});

        when(secondInvocationMock.getSignature()).thenReturn("secondInvocationSignature");
        when(thirdInvocation.getSignature()).thenReturn("thirdInvocationSignature");

        // Act
        target.report(reportMock);

        // Assert
        verify(appenderMock, times(7)).doAppend(appenderCaptor.capture());
    }
}
