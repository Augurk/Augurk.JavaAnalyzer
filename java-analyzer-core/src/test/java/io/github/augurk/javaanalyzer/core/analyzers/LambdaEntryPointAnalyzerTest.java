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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.WhenStepStrategy;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.strategies.ENWhenStepStrategy;
import io.github.augurk.javaanalyzer.core.utils.FileUtils;
import io.github.augurk.javaanalyzer.core.utils.PirateWhenStepStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

@RunWith(MockitoJUnitRunner.class)
public class LambdaEntryPointAnalyzerTest {
    private static final String LAMBDA_SOURCES_ROOT = "testClasses/lambda";

    @Mock
    private AnalyzerContext contextMock;

    @InjectMocks
    private LambdaEntryPointAnalyzer target;

    @Before
    public void beforeEach() {
        var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver(false));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);
    }

    @Test
    public void visit_ShouldAnalyzeWhenMethodDeclarationWithBlockLambda() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithBlockLambdaWhenStep.java";
        var strategy = new ENWhenStepStrategy();
        var signature = "SimpleClassWithBlockLambdaWhenStep.SimpleClassWithBlockLambdaWhenStep()";
        var whenExpression = "When step with block lambda expression";

        // Act
        executeTest(fileName, signature, whenExpression, strategy, times(1));

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldAnalyzeWhenMethodDeclarationSingleExpressionLambda() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithSingleExpressionLambdaWhenStep.java";
        var strategy = new ENWhenStepStrategy();
        var signature = "SimpleClassWithSingleExpressionLambdaWhenStep.SimpleClassWithSingleExpressionLambdaWhenStep()";
        var whenExpression = "When with single expression lambda";

        // Act
        executeTest(fileName, signature, whenExpression, strategy, times(1));

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldAnalyseWhenLambdaWithTimeout() throws Exception {
        // Arrange
        var fileName = "WhenLambdaWithTimeout.java";
        var strategy = new ENWhenStepStrategy();
        var signature = "WhenLambdaWithTimeout.WhenLambdaWithTimeout()";
        var whenExpression = "When lambda with timeout";

        // Act
        executeTest(fileName, signature, whenExpression, strategy, times(1));

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldWorkWithAnOtherLanguageStrategy() throws Exception {
        // Arrange
        var fileName = "WhenLambdaInPrivateLang.java";
        var strategy = new PirateWhenStepStrategy();
        var signature = "WhenLambdaInPrivateLang.WhenLambdaInPrivateLang()";
        var whenExpression = "This is a pirate when step";

        // Act
        executeTest(fileName, signature, whenExpression, strategy, times(1));

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldStopAnalysisWhenNoDefaultConstructorCouldBeFound() throws Exception {
        // Arrange
        var fileName = "ClassWithoutDefaultConstructor.java";
        var strategy = new ENWhenStepStrategy();

        // Act
        executeTest(fileName, null, null, strategy, never());

        // Assert
        verify(contextMock, never()).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldStopAnalysisWhenNotImplementingCucumberInterface() throws Exception {
        // Arrange
        var fileName = "ClassNotImplementingCucumberInterface.java";
        var strategy = new ENWhenStepStrategy();

        // Act
        executeTest(fileName, null, null, strategy, never());

        // Assert
        verify(contextMock, never()).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldNotAnalyzeWhenLambdaNotSupported() throws Exception {
        // Arrange
        var fileName = "WrongDefinedLambdaWhenStep.java";
        var strategy = new ENWhenStepStrategy();

        // Act
        executeTest(fileName, null, null, strategy, never());

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldNotAnalyzeWhenLambdaWithSingleArgument() throws Exception {
        // Arrange
        var fileName = "WrongDefinedLambdaSingleArgument.java";
        var strategy = new ENWhenStepStrategy();

        // Act
        executeTest(fileName, null, null, strategy, never());

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    @Test
    public void visit_ShouldOnlyAnalyzeWhenLambdaSteps() throws Exception {
        // Arrange
        var fileName = "SimpleClassWithGivenLambda.java";
        var strategy = new ENWhenStepStrategy();

        // Act
        executeTest(fileName, null, null, strategy, never());

        // Assert
        verify(contextMock).getWhenStepStrategy(strategy.getLanguageCode());
    }

    private void executeTest(String fileName, String signature, String whenExpression, WhenStepStrategy strategy, VerificationMode mode) throws Exception {
        // Arrange
        var testFile = FileUtils.loadFileByName(LAMBDA_SOURCES_ROOT, fileName);
        var cu = StaticJavaParser.parse(testFile);
        var methodDeclarations = cu.findAll(ClassOrInterfaceDeclaration.class);

        var collectorMock = mock(InvocationCollector.class);
        when(contextMock.getWhenStepStrategy(anyString())).thenReturn(Optional.ofNullable(strategy));

        // Act
        target.visit(methodDeclarations.get(0), collectorMock);

        // Assert
        verify(contextMock, mode).createAnalyzer(InvocationTreeAnalyzer.class);
        verify(collectorMock, mode).beginRootInvocation(signature, whenExpression, null);
        verify(collectorMock, mode).endRootInvocation();
    }
}
