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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.core.utils.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

@RunWith(MockitoJUnitRunner.class)
public class EntryPointAnalyzerTest {
    private static final String ANNOTATION_SOURCES_ROOT = "testClasses/annotation";
    private static final String LAMBDA_SOURCES_ROOT = "testClasses/lambda";

    @Mock private AnalyzerContext contextMock;
    @Mock private AnalyzeOptions optionsMock;

    @InjectMocks
    private EntryPointAnalyzer target;

    @Before
    public void beforeEach() {
        var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver(false));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);

        when(contextMock.getOptions()).thenReturn(optionsMock);
        when(optionsMock.filterIsEmpty()).thenReturn(true);
    }

    @Test
    public void visit_ShouldContinueWhenFilterIsEmpty() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithWhenStep.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, fileName);

        // Act
        executeTest(testFile, AnnotationEntryPointAnalyzer.class, atLeastOnce());

        // Assert
        verify(optionsMock).filterIsEmpty();
    }

    @Test
    public void visit_ShouldStopVisitWhenFilterIsNotEmptyAndFilterNotContainsValue() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithWhenStep.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, fileName);

        when(optionsMock.filterIsEmpty()).thenReturn(false);
        when(optionsMock.filterContains(anyString())).thenReturn(false);

        // Act
        executeTest(testFile, AnnotationEntryPointAnalyzer.class, never());

        // Assert
        verify(optionsMock).filterIsEmpty();
        verify(optionsMock).filterContains("SimpleClassWithWhenStep");
    }

    @Test
    public void visit_ShouldContinueWhenFilterContainsValue() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithWhenStep.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, fileName);

        when(optionsMock.filterIsEmpty()).thenReturn(false);
        when(optionsMock.filterContains(anyString())).thenReturn(true);

        // Act
        executeTest(testFile, AnnotationEntryPointAnalyzer.class, atLeastOnce());

        // Assert
        verify(optionsMock).filterIsEmpty();
        verify(optionsMock).filterContains("SimpleClassWithWhenStep");
    }

    @Test
    public void visit_ShouldReturnAnnotationEntryPointAnalyzerByDefault() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithWhenStep.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, fileName);

        // Act
        executeTest(testFile, AnnotationEntryPointAnalyzer.class, atLeastOnce());
    }

    @Test
    public void visit_ShouldReturnLambdaEntryPointAnalyzerWhenClassImplementsInterface() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithBlockLambdaWhenStep.java";
        var testFile = FileUtils.loadFileByName(LAMBDA_SOURCES_ROOT, fileName);

        // Act
        executeTest(testFile, LambdaEntryPointAnalyzer.class, atLeastOnce());
    }

    private void executeTest(File testFile, Class<? extends AbstractAnalyzer> expectedAnalyzer, VerificationMode mode) throws Exception {
        // Arrange
        var cu = StaticJavaParser.parse(testFile);

        var collectorMock = mock(InvocationCollector.class);
        var analyzerMock = mock(LambdaEntryPointAnalyzer.class);

        when(contextMock.parseSourceFile(any(File.class))).thenReturn(Optional.of(cu));
        when(contextMock.getCollector()).thenReturn(collectorMock);
        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(analyzerMock));

        // Act
        target.visit(testFile);

        // Assert
        verify(contextMock).parseSourceFile(testFile);
        verify(contextMock, mode).createAnalyzer(expectedAnalyzer);
    }
}
