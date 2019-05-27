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

package io.github.augurk.javaanalyzer.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.github.augurk.javaanalyzer.core.analyzers.AbstractAnalyzer;
import io.github.augurk.javaanalyzer.core.analyzers.InvocationTreeAnalyzer;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.core.strategies.ENWhenStepStrategy;
import io.github.augurk.javaanalyzer.core.utils.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnalyzerContextTest {
    private static final String PROJECT_NAME = "projectName";
    private static final String ANNOTATION_SOURCES_ROOT = "testClasses/annotation";
    private static final String TEST_CLASS_NAME = "SimpleClassWithWhenStep.java";

    private AnalyzeOptions optionsMock;
    private FileManager fileManagerMock;
    private Reporter reporterMock;
    private File testFile;

    private AnalyzerContext target;

    @Before
    public void BeforeEach() throws Exception {
        testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, TEST_CLASS_NAME);

        optionsMock = mock(AnalyzeOptions.class);
        fileManagerMock = mock(FileManager.class);
        reporterMock = mock(Reporter.class);

        when(optionsMock.getProjectName()).thenReturn(PROJECT_NAME);
        when(optionsMock.getLanguageLevel()).thenReturn(ParserConfiguration.LanguageLevel.JAVA_11);

        target = new AnalyzerContext(optionsMock, fileManagerMock, Collections.singletonList(reporterMock));
    }

    @Test
    public void ctor_ShouldRegisterDefinedStrategiesByDefault() {
        assertThat(target.getWhenStepStrategy("en"), notNullValue());
        assertThat(target.getWhenStepStrategy("nl"), notNullValue());
    }

    @Test
    public void startAnalysis_shouldCallReporterOnceWithResult() {
        // Arrange
        configureDoAnswerFileManager();
        var reportCaptor = ArgumentCaptor.forClass(AnalysisReport.class);
        doNothing().when(reporterMock).report(any(AnalysisReport.class));

        // Act
        target.startAnalysis();

        // Assert
        verify(fileManagerMock).walkFileTree(any());
        verify(reporterMock).report(reportCaptor.capture());

        assertThat(reportCaptor.getValue().getAnalyzedProject(), equalTo(PROJECT_NAME));
        assertThat(reportCaptor.getValue().getTimestamp(), notNullValue());
    }


    @Test
    public void startAnalysis_ShouldCallAllProvidedReporters() {
        // Arrange
        var reporter1 = mock(Reporter.class);
        var reporter2 = mock(Reporter.class);
        configureDoAnswerFileManager();

        // Act
        target.addReporter(reporter1);
        target.addReporter(reporter2);
        target.startAnalysis();

        // Assert
        verify(reporterMock).report(any(AnalysisReport.class));
        verify(reporter1).report(any(AnalysisReport.class));
        verify(reporter2).report(any(AnalysisReport.class));
    }

    @Test
    public void parseSourceFile_ShouldReturnCompilationUnitOfProvidedFile() throws Exception {
        // Act
        Optional<CompilationUnit> result = target.parseSourceFile(testFile);

        // Assert
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getClassByName(TEST_CLASS_NAME), notNullValue());
    }

    @Test
    public void parseSourceFile_ShouldReturnEmptyOptionalWhenFileCouldNotBeParsed() {
        // Arrange
        var notExistingFile = new File("someRandomFile");

        // Act
        Optional<CompilationUnit> result =  target.parseSourceFile(notExistingFile);

        // Assert
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void parserSourceFileByQualifiedName_ShouldReturnCompilationUnitOfProvidedQualifiedName() {
        // Arrange
        var qualifiedName = "TestClass";
        when(fileManagerMock.getFileByQualifiedName(anyString())).thenReturn(Optional.of(testFile));

        // Act
        Optional<CompilationUnit> result = target.parserSourceFileByQualifiedName(qualifiedName);

        // Assert
        verify(fileManagerMock).getFileByQualifiedName(qualifiedName);
        assertThat(result.isPresent(), is(true));
    }

    @Test
    public void findByPredicate_ShouldCallWalkFileTreeAndReturnMatchedResult() {
        // Arrange
        Class<ClassOrInterfaceDeclaration> clazzType = ClassOrInterfaceDeclaration.class;
        Predicate<ClassOrInterfaceDeclaration> predicateMock = mock(Predicate.class);
        Consumer<ClassOrInterfaceDeclaration> consumerMock = mock(Consumer.class);

        when(predicateMock.test(any(clazzType))).thenReturn(true);
        doNothing().when(consumerMock).accept(any(clazzType));
        configureDoAnswerFileManager();

        // Act
        target.findByPredicate(clazzType, predicateMock, consumerMock);

        // Assert
        verify(predicateMock).test(any(clazzType));
        verify(consumerMock).accept(any(clazzType));
    }

    @Test
    public void createAnalyzer_ShouldReturnAnalyzer() {
        // Act
        Optional<AbstractAnalyzer> result = target.createAnalyzer(InvocationTreeAnalyzer.class);

        // Assert
        assertThat(result.get(), is(instanceOf(InvocationTreeAnalyzer.class)));
    }

    @Test
    public void createAnalyzer_ShouldReturnEmptyOptionalWhenAnalyzerCouldNotBeCreated() {
        // Act
        Optional<AbstractAnalyzer> result = target.createAnalyzer(AbstractAnalyzer.class);

        // Assert
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void addWhenStepStrategy_ShouldAddStrategyToStrategyMap() {
        // Arrange
        var strategy = new ENWhenStepStrategy();

        // Act
        target.addWhenStepStrategy(strategy);

        // Assert
        assertThat(target.getWhenStepStrategy(strategy.getLanguageCode()), is(Optional.of(strategy)));
    }

    @Test
    public void getWhenStepStrategy_ShouldReturnEmptyOptionalWhenStrategyIsNotFound() {
        // Act
        var result = target.getWhenStepStrategy("invalid");

        // Assert
        assertThat(result, is(Optional.empty()));
    }

    private void configureDoAnswerFileManager() {
        doAnswer(invocation -> {
            ((Consumer<File>) invocation.getArgument(0)).accept(testFile);
            return null;
        }).when(fileManagerMock).walkFileTree(any(Consumer.class));
    }
}
