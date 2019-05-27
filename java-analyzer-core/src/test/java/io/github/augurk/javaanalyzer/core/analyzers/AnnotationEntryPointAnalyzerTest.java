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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.WhenStepStrategy;
import io.github.augurk.javaanalyzer.core.collectors.AutomationTarget;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
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
public class AnnotationEntryPointAnalyzerTest {
    private static final String ANNOTATION_SOURCES_ROOT = "testClasses/annotation";

    private InvocationCollector collectorMock;

    @Mock
    private AnalyzerContext contextMock;

    @InjectMocks
    private AnnotationEntryPointAnalyzer target;

    @Before
    public void beforeEach() {
        var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver(false));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);

        collectorMock = mock(InvocationCollector.class);
    }

    @Test
    public void visit_ShouldAnalyzeWhenMethodDeclaration() throws Exception {
        var fileName = "SimpleClassWithWhenStep.java";
        var strategy = new ENWhenStepStrategy();
        var signature = "SimpleClassWithWhenStep.whenStepWithoutCall()";
        var whenExpression = "When step without call";

        executeTest(fileName, signature, whenExpression, strategy, times(1));
    }

    @Test
    public void visit_ShouldHandleWhenAnnotationWithTimeoutValue() throws Exception {
        var fileName = "WhenAnnotationWithTimeout.java";
        var strategy = new ENWhenStepStrategy();
        var signature = "WhenAnnotationWithTimeout.whenAnnotationWithTimeout()";
        var whenExpression = "When annotation with timeout";

        executeTest(fileName, signature, whenExpression, strategy, times(1));
    }

    @Test
    public void visit_ShouldAnalyzeMethodWithMultipleAnnotations() throws Exception {
        var fileName = "MethodWithMultipleAnnotations.java";
        var strategy = new ENWhenStepStrategy();
        var signature = "MethodWithMultipleAnnotations.methodWithMultipleAnnotations()";
        var whenExpression = "Method with multiple annotations";

        executeTest(fileName, signature, whenExpression, strategy, times(1));
    }

    @Test
    public void visit_ShouldWorkWithAnOtherLanguageStrategy() throws Exception {
        var fileName = "WhenStepInPrivateLang.java";
        var strategy = new PirateWhenStepStrategy();
        var signature = "WhenStepInPrivateLang.whenStepInPirateLang()";
        var whenExpression = "This is a pirate when step";

        executeTest(fileName, signature, whenExpression, strategy, times(1));
    }

    @Test
    public void visit_ShouldTerminateWhenCucumberLanguageCanNotBeDetected() throws Exception {
        var fileName = "ClassWithoutWhenAnnotation.java";
        var strategy = new ENWhenStepStrategy();

        executeTest(fileName, null, null, strategy, never());
    }

    @Test
    public void visit_ShouldNotAnalyzerSelfDefinedWhenAnnotation() {
        // Arrange
        var methodDeclarationMock = mock(MethodDeclaration.class);
        var collectorMock = mock(InvocationCollector.class);
        var annotationExprMock = mock(AnnotationExpr.class, RETURNS_DEEP_STUBS);
        var strategyMock = mock(WhenStepStrategy.class);
        var nodeList = new NodeList<>(annotationExprMock);

        when(methodDeclarationMock.getType()).thenReturn(mock(Type.class));
        when(methodDeclarationMock.getModifiers()).thenReturn(mock(NodeList.class));
        when(methodDeclarationMock.getName()).thenReturn(mock(SimpleName.class));
        when(methodDeclarationMock.getParameters()).thenReturn(mock(NodeList.class));
        when(methodDeclarationMock.getReceiverParameter()).thenReturn(Optional.empty());
        when(methodDeclarationMock.getThrownExceptions()).thenReturn(mock(NodeList.class));
        when(methodDeclarationMock.getTypeParameters()).thenReturn(mock(NodeList.class));
        when(methodDeclarationMock.getComment()).thenReturn(Optional.empty());
        when(methodDeclarationMock.getAnnotations()).thenReturn(nodeList);

        when(annotationExprMock.resolve().getPackageName()).thenReturn("not.cucumber.api.java.en.when");

        // Act
        target.visit(methodDeclarationMock, collectorMock);

        // Assert
        verify(annotationExprMock.resolve()).getPackageName();
        verify(strategyMock, never()).isWhenStep(anyString());
        verify(collectorMock, never()).beginRootInvocation(anyString(), anyString(), any(AutomationTarget.class));
    }

    @Test
    public void visit_ShouldNotAnalyzerWhenWhenAnnotationValueNotIsStringLiteral() {
        // Arrange
        var expressionString = "expression";
        var signature = "signature";

        var methodDeclarationMock = mock(MethodDeclaration.class, RETURNS_DEEP_STUBS);
        var collectorMock = mock(InvocationCollector.class);
        var annotationExprMock = mock(AnnotationExpr.class, RETURNS_DEEP_STUBS);
        var strategyMock = mock(WhenStepStrategy.class);
        var invocationMock = mock(Invocation.class);
        var normalAnnotationMock = mock(NormalAnnotationExpr.class);
        var pairMock = mock(MemberValuePair.class);
        var expressionMock = mock(Expression.class);

        var annotationExprs = new NodeList<>(annotationExprMock);
        var pairs = new NodeList<>(pairMock);

        when(contextMock.getWhenStepStrategy(anyString())).thenReturn(Optional.of(strategyMock));
        when(strategyMock.isWhenStep(anyString())).thenReturn(true);

        when(methodDeclarationMock.getAnnotations()).thenReturn(annotationExprs);
        when(methodDeclarationMock.resolve().getQualifiedSignature()).thenReturn(signature);

        when(annotationExprMock.resolve().getPackageName()).thenReturn("cucumber.api.java.en.when");
        when(annotationExprMock.getNameAsString()).thenReturn(expressionString);
        when(annotationExprMock.isSingleMemberAnnotationExpr()).thenReturn(false);
        when(annotationExprMock.asNormalAnnotationExpr()).thenReturn(normalAnnotationMock);

        when(normalAnnotationMock.getPairs()).thenReturn(pairs);
        when(pairMock.getNameAsString()).thenReturn("value");
        when(pairMock.getValue()).thenReturn(expressionMock);
        when(expressionMock.isStringLiteralExpr()).thenReturn(false);

        when(collectorMock.beginRootInvocation(anyString(), anyString(), any())).thenReturn(invocationMock);
        doNothing().when(collectorMock).endRootInvocation();

        // Act
        target.visit(methodDeclarationMock, collectorMock);

        // Assert
        verify(annotationExprMock.resolve()).getPackageName();
        verify(strategyMock).isWhenStep(expressionString);
        verify(collectorMock).beginRootInvocation(signature, "", null);
        verify(collectorMock).endRootInvocation();
    }

    @Test
    public void visit_ShouldExtractAutomationTargetAnnotationWhenDefined() {
        // Arrange
        var qualifiedName = "qualifiedName";
        var qualifiedSignature = "signature";

        var collectorMock = mock(InvocationCollector.class);
        var annotationMock = mock(NormalAnnotationExpr.class, RETURNS_DEEP_STUBS);

        var pairMock = mock(MemberValuePair.class, RETURNS_DEEP_STUBS);
        var fieldAccessMock = mock(FieldAccessExpr.class, RETURNS_DEEP_STUBS);
        var typeMock = mock(ClassOrInterfaceType.class, RETURNS_DEEP_STUBS);

        var compilationUnitMock = mock(CompilationUnit.class);
        var methodDeclarationMock = mock(MethodDeclaration.class, RETURNS_DEEP_STUBS);
        var pairs = new NodeList<>(pairMock);

        when(annotationMock.resolve().getQualifiedName()).thenReturn("io.github.augurk.javaanalyzer.annotations.AutomationTarget");
        when(annotationMock.getPairs()).thenReturn(pairs);

        when(pairMock.getNameAsString()).thenReturn("declaringType", "targetMethod", "overloadHandling");
        when(pairMock.getValue().isFieldAccessExpr()).thenReturn(false, false, true);
        when(pairMock.getValue().isClassExpr()).thenReturn(true, false, false);

        when(pairMock.getValue().asClassExpr().getType()).thenReturn(typeMock);
        when(pairMock.getValue().asFieldAccessExpr()).thenReturn(fieldAccessMock);

        when(typeMock.resolve().getQualifiedName()).thenReturn(qualifiedName);
        when(contextMock.parserSourceFileByQualifiedName(qualifiedName)).thenReturn(Optional.of(compilationUnitMock));
        when(compilationUnitMock.findAll(any(), any(Predicate.class))).thenReturn(List.of(methodDeclarationMock));
        when(methodDeclarationMock.resolve().getQualifiedSignature()).thenReturn(qualifiedSignature);

        // Act
        target.visit(annotationMock, collectorMock);

        // Assert
        verify(pairMock, atLeast(3)).getNameAsString();
        verify(pairMock, atLeast(3)).getValue();
        verify(contextMock).parserSourceFileByQualifiedName(qualifiedName);
        verify(methodDeclarationMock, atLeastOnce()).resolve();
    }

    private void executeTest(String fileName, String signature, String whenExpression, WhenStepStrategy strategy, VerificationMode mode) throws Exception {
        // Arrange
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, fileName);
        var cu = StaticJavaParser.parse(testFile);
        var methodDeclarations = cu.findAll(ClassOrInterfaceDeclaration.class);

        var analyzerMock = mock(InvocationTreeAnalyzer.class);
        var invocationMock = mock(Invocation.class);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(analyzerMock));
        when(contextMock.getWhenStepStrategy(anyString())).thenReturn(Optional.of(strategy));
        when(collectorMock.beginRootInvocation(anyString(), anyString(), any())).thenReturn(invocationMock);
        doNothing().when(collectorMock).endRootInvocation();

        // Act
        target.visit(methodDeclarations.get(0), collectorMock);

        // Assert
        verify(contextMock, mode).createAnalyzer(InvocationTreeAnalyzer.class);
        verify(contextMock, mode).getWhenStepStrategy(strategy.getLanguageCode());
        verify(collectorMock, mode).beginRootInvocation(signature, whenExpression, null);
        verify(collectorMock, mode).endRootInvocation();
    }
}
