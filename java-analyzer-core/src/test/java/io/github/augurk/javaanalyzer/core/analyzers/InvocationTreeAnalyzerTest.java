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

import static io.github.augurk.javaanalyzer.core.utils.InvokedMethodUtils.isEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.ArrayMatching.arrayContaining;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.collectors.InvokedMethod;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;
import io.github.augurk.javaanalyzer.core.utils.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvocationTreeAnalyzerTest {
    private static final String ANNOTATION_SOURCES_ROOT = "testClasses/annotation";

    @Mock private AnalyzerContext contextMock;
    @Mock private Invocation invocationMock;
    @Mock private InvocationCollector collectorMock;

    @Captor private ArgumentCaptor<InvokedMethod> invokedMethodCaptor;

    @InjectMocks
    private InvocationTreeAnalyzer target;

    @Before
    public void beforeEach() {
        when(contextMock.getCollector()).thenReturn(collectorMock);

        var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver(false));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);
    }

    @Test
    public void visit_ShouldStepIntoCalledClass() throws Exception {
        // Arrange
        var testClazz = "SimpleClassWithWhenStepAndCall.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(2)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PUBLIC, "CallableClazz", "CallableClazz.sayHello()", true);
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
        verify(collectorMock, times(2)).stepOut();
    }

    @Test
    public void visit_ShouldStepIntoInterfaceImplementedMethod() throws Exception {
        // Arrange
        var testClazz = "InterfaceImplementationWhenStep.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(2)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        var firstInvocation = invocations.get(0);
        isEqual(firstInvocation, InvocationKind.PUBLIC, "SayHelloImpl", "SayHelloImpl.sayHello(java.lang.String)", true);
        assertThat(firstInvocation.getInterfaceDefinitions(), arrayContaining("SayHello.sayHello(java.lang.String)"));
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
        verify(collectorMock, times(2)).stepOut();
    }

    @Test
    public void visit_ShouldCollectInterfaceDefinitionsOfTargetClass() throws Exception {
        // Arrange
        var testClazz = "WhenStepUsingInterfaceImplementation.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);
        when(collectorMock.isAlreadyCollected(any(InvokedMethod.class))).thenReturn(false);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(2)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        var firstInvocation = invocations.get(0);
        isEqual(firstInvocation, InvocationKind.PUBLIC, "SayHelloImpl", "SayHelloImpl.sayHello(java.lang.String)", true);
        assertThat(firstInvocation.getInterfaceDefinitions(), arrayContaining("SayHello.sayHello(java.lang.String)"));
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
        verify(collectorMock, times(2)).stepOut();
    }

    @Test
    public void visit_ShouldCollectInterfaceDefinitionsOfOverrideMethodsInSameClass() throws Exception {
        // Arrange
        var testClazz = "WhenStepUsingInterfaceImplementationInSameClass.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        when(collectorMock.getActualType(anyString())).thenAnswer(a -> a.getArgument(0));
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);
        when(collectorMock.isAlreadyCollected(any(InvokedMethod.class))).thenReturn(false);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(3)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        var firstInvocation = invocations.get(0);
        isEqual(firstInvocation, InvocationKind.PUBLIC, "SayHelloImpl", "SayHelloImpl.sayHello()", true);
        assertThat(firstInvocation.getInterfaceDefinitions(), arrayContaining("SayHello.sayHello()"));

        var secondInvocation = invocations.get(1);
        isEqual(secondInvocation, InvocationKind.PUBLIC, "SayHelloImpl", "SayHelloImpl.sayHello(java.lang.String)", true);
        assertThat(secondInvocation.getInterfaceDefinitions(), arrayContaining("SayHello.sayHello(java.lang.String)"));

        isEqual(invocations.get(2), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
        verify(collectorMock, times(3)).stepOut();
    }

    @Test
    public void visit_ShouldStepIntoMethodsWithinTheSameClass() throws Exception {
        // Arrange
        var testClazz = "ClassWithMethodCallsWithinClass.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        when(collectorMock.getActualType(anyString())).thenAnswer(a -> a.getArgument(0));
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(4)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PRIVATE, "ClassWithMethodCallsWithinClass", "ClassWithMethodCallsWithinClass.methodCall1()", true);
        isEqual(invocations.get(1), InvocationKind.PRIVATE, "ClassWithMethodCallsWithinClass", "ClassWithMethodCallsWithinClass.methodCall2(java.lang.String)", true);
        isEqual(invocations.get(2), InvocationKind.PUBLIC, "SayHello", "SayHello.sayHello(java.lang.String)", true);
        isEqual(invocations.get(3), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
    }

    @Test
    public void visit_ShouldStepIntoConcreteTypeOfAbstractMethod() throws Exception {
        // Arrange
        var testClazz = "ClassWithAbstractMethod.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        var cid = entryPoint.left.getClassByName("ChildClass");
        doAnswer(a -> {
            ((Consumer) a.getArgument(2)).accept(cid.get());
            return null;
        }).when(contextMock).findByPredicate(any(), any(), any());

        when(collectorMock.getActualType(anyString())).thenAnswer(a -> a.getArgument(0));
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(4)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PUBLIC, "ChildClass", "ChildClass.sayHello(java.lang.String)", true);
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "ParentClass", "ParentClass.sayHelloBase(java.lang.String)", true);
        isEqual(invocations.get(2), InvocationKind.PUBLIC, "ChildClass", "ChildClass.greet(java.lang.String)", true);
        isEqual(invocations.get(3), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
        verify(collectorMock, times(4)).stepOut();
    }

    @Test
    public void visit_ShouldHandleClassesWithDifferentConcreteType() throws Exception {
        // Arrange
        var testClazz = "ClassWithDifferentConcreteType.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);
        var pair = new ImmutablePair<>("Person", "PickyPerson");

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        when(collectorMock.getActualType(pair.left)).thenReturn(pair.right);
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(3)).collect(invokedMethodCaptor.capture());
        verify(collectorMock).getActualType(pair.left);

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PRIVATE, "ClassWithDifferentConcreteType", List.of(pair), "ClassWithDifferentConcreteType.sayHello(Person)", true);
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "PickyPerson", "PickyPerson.sayHello()", true);
        isEqual(invocations.get(2), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.printf(java.lang.String, java.lang.Object...)", false);
        verify(collectorMock, times(3)).stepOut();
    }

    @Test
    public void visit_ShouldStepIntoTheSameMethodTwiceInARow() throws Exception {
        // Arrange
        var testClazz = "ClassWithRecursiveCall.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        when(collectorMock.getActualType(anyString())).thenAnswer(a -> a.getArgument(0));
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);
        when(collectorMock.isAlreadyCollected(any(InvokedMethod.class))).thenReturn(false).thenReturn(true);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(2)).isAlreadyCollected(any(InvokedMethod.class));
        verify(collectorMock, times(2)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PUBLIC, "RecursivePerson", "RecursivePerson.sayHello()", true);
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
    }

    @Test
    public void visit_ShouldNotStepIntoInterfaceWhenDeclaringTypeIsNull() throws Exception {
        // Arrange
        var testClazz = "ClassWithIndirectInterfaceCall.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));

        when(collectorMock.getActualType(anyString())).thenAnswer(a -> a.getArgument(0));
        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(2)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PUBLIC, "PersonDriver", "PersonDriver.sayHiIndirectly()", true);
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "Person", "Person.sayHi()", true);
    }

    @Test
    public void visit_ShouldBeAbleToAnalyzeImplicitTypedMethodCalls() throws Exception {
        // Arrange
        var testClazz = "UsingListOf.java";
        var testFile = FileUtils.loadFileByName(ANNOTATION_SOURCES_ROOT, testClazz);
        var entryPoint = getTargetMethod(testFile);

        when(contextMock.createAnalyzer(any())).thenReturn(Optional.of(new InvocationTreeAnalyzer(contextMock)));
        when(contextMock.parserSourceFileByQualifiedName(anyString())).thenReturn(Optional.of(entryPoint.left));
        when(contextMock.parserSourceFileByQualifiedName("java.io.PrintStream")).thenReturn(Optional.empty());

        when(collectorMock.collect(any(InvokedMethod.class))).thenReturn(invocationMock);

        // Act
        target.visit(entryPoint.right, collectorMock);

        // Assert
        verify(collectorMock, times(2)).collect(invokedMethodCaptor.capture());

        var invocations = invokedMethodCaptor.getAllValues();
        isEqual(invocations.get(0), InvocationKind.PUBLIC, "Callable", "Callable.setList(java.util.List<java.lang.Integer>)", true);
        isEqual(invocations.get(1), InvocationKind.PUBLIC, "java.io.PrintStream", "java.io.PrintStream.println(java.lang.String)", false);
    }

    private ImmutablePair<CompilationUnit, MethodDeclaration> getTargetMethod(File testFile) throws Exception {
        return getTargetMethod(testFile, 0);
    }

    private ImmutablePair<CompilationUnit, MethodDeclaration> getTargetMethod(File testFile, int methodIndex) throws Exception {
        var cu = StaticJavaParser.parse(testFile);
        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
        target.setCurrentCompilationUnit(cu);
        return ImmutablePair.of(cu, methodDeclarations.get(methodIndex));
    }
}
