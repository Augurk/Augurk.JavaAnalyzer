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

import static io.github.augurk.javaanalyzer.core.utils.InvokedMethodUtils.createInvokedMethodMock;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import io.github.augurk.javaanalyzer.core.analyzers.InvokedMethodImpl;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.core.utils.InvokedMethodUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvocationCollectorTest {
    private static final String PROJECT_NAME = "projectName";
    private static final String PROJECT_VERSION = "projectVersion";

    private ResolvedMethodDeclaration resolvedMethodMock;
    private InvocationCollector target;

    @Before
    public void BeforeEach() {
        resolvedMethodMock = mock(ResolvedMethodDeclaration.class, Mockito.RETURNS_DEEP_STUBS);
        var optionsMock = mock(AnalyzeOptions.class);

        when(optionsMock.getProjectName()).thenReturn(PROJECT_NAME);
        when(optionsMock.getVersion()).thenReturn(PROJECT_VERSION);

        when(resolvedMethodMock.declaringType().getQualifiedName()).thenReturn("QualifiedName");
        when(resolvedMethodMock.accessSpecifier()).thenReturn(Modifier.Keyword.PUBLIC);

        target = new InvocationCollector(optionsMock);
    }

    @Test
    public void ctor_ShouldInstantiateAnalysisReport() {
        // Assert
        assertThat(target.getReport(), instanceOf(AnalysisReport.class));
    }

    @Test
    public void beginRootInvocation_ShouldCreateNewInvocation() {
        // Arrange
        var signature = "Signature";
        var whenExpression = "whenExpression";

        // Act
        var result = target.beginRootInvocation(signature, whenExpression, null);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getKind(), equalTo(InvocationKind.WHEN));
        assertThat(result.getSignature(), equalTo(signature));
        assertThat(result.getRegularExpression().length, is(1));
        assertThat(result.getRegularExpression(), arrayContaining(whenExpression));
        assertThat(result.getInterfaceDefinitions(), emptyArray());
        assertThat(result.getInvocations().isEmpty(), is(true));
    }

    @Test
    public void endRootInvocation_ShouldBuildInvocationTree() {
        // Arrange
        var signature = "Signature";
        var whenExpression = "whenExpression";

        // Act
        target.beginRootInvocation(signature, whenExpression, null);
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result, notNullValue());
        assertThat(result.getAnalyzedProject(), equalTo(PROJECT_NAME));
        assertThat(result.getVersion(), equalTo(PROJECT_VERSION));
        assertThat(result.getRootInvocations().size(), is(1));

        var rootInvocation = result.getRootInvocations().get(0);
        assertThat(rootInvocation.getKind(), equalTo(InvocationKind.WHEN));
        assertThat(rootInvocation.getSignature(), equalTo(signature));
        assertThat(rootInvocation.getRegularExpression().length, is(1));
        assertThat(rootInvocation.getRegularExpression()[0], equalTo(whenExpression));
        assertThat(rootInvocation.getInvocations().isEmpty(), is(true));
    }

    @Test
    public void endRootInvocation_ShouldBuildInvocationTreeWithMultipleRootInvocations() {
        // Act
        var root1 = target.beginRootInvocation("firstSignature", "whenExpression1", null);
        target.endRootInvocation();

        var root2 = target.beginRootInvocation("secondSignature", "whenExpression2", null);
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result, notNullValue());

        var invocations = result.getRootInvocations();
        assertThat(invocations.size(), is(2));
        assertThat(invocations.get(0), is(root1));
        assertThat(invocations.get(1), is(root2));
    }

    @Test
    public void collect_ShouldAddInvocationCreateNewInvocation() {
        // Act
        var root1 = target.beginRootInvocation("rootSignature", "whenExpression", null);
        var invocation1 = target.collect(createInvokedMethodMock());
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result, notNullValue());
        assertThat(result.getRootInvocations().size(), is(1));

        var firstRootInvocation = result.getRootInvocations().get(0);
        assertThat(firstRootInvocation, is(root1));
        assertThat(firstRootInvocation.getInvocations().size(), is(1));

        var firstInvocation = firstRootInvocation.getInvocations().poll();
        assertThat(firstInvocation, is(invocation1));
        assertThat(firstInvocation.isLocal(), is(InvokedMethodUtils.IS_LOCAL));
        assertThat(firstInvocation.getInvocations().isEmpty(), is(true));
    }

    @Test
    public void endRootInvocation_ShouldStepIntoProvidedInvocation() {
        // Arrange
        var root1 = target.beginRootInvocation("rootSignature", "whenExpression", null);
        var invocation1 = target.collect(createInvokedMethodMock());
        var invocation2 = target.collect(createInvokedMethodMock());

        // Act
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result, notNullValue());
        assertThat(result.getRootInvocations().size(), is(1));

        var firstRootInvocation = result.getRootInvocations().get(0);
        assertThat(firstRootInvocation, is(root1));
        assertThat(firstRootInvocation.getInvocations().size(), is(1));

        var firstInvocation = firstRootInvocation.getInvocations().poll();
        assertThat(firstInvocation, is(invocation1));
        assertThat(firstInvocation.getInvocations().size(), is(1));

        var secondInvocation = firstInvocation.getInvocations().poll();
        assertThat(secondInvocation, is(invocation2));
        assertThat(secondInvocation.getInvocations().isEmpty(), is(true));
    }

    @Test
    public void endRootInvocation_ShouldNotProcessWhenAutomationTargetIsNotProvided() {
        // Arrange
        target.beginRootInvocation("rootSignature", "whenExpression", null);

        // Act
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result.getRootInvocations().get(0).getAutomationTargets(), emptyArray());
    }

    @Test
    public void endRootInvocation_ShouldProcessFirstOverloadWhenHandlingSetToFirst() {
        // Arrange
        var overloadSignature1 = "SomeOverloadSignature(2)";
        var overloadSignature2 = "SomeOverloadSignature(1)";
        var automationTargetMock = mock(AutomationTarget.class);

        target.beginRootInvocation("rootSignature", "whenExpression", automationTargetMock);
        target.collect(createInvokedMethodMock(overloadSignature1));
        target.collect(createInvokedMethodMock(overloadSignature2));

        when(automationTargetMock.getOverloadHandling()).thenReturn(OverloadHandling.FIRST);
        when(automationTargetMock.getTargetMethods()).thenReturn(List.of(overloadSignature1, overloadSignature2));

        // Act
        target.endRootInvocation();

        // Assert
        var result = target.getReport().getRootInvocations().get(0).getAutomationTargets();
        assertThat(result, arrayWithSize(1));
        assertThat(result, arrayContaining(overloadSignature1));
    }

    @Test
    public void endRootInvocation_ShouldProcessLastOverloadWhenHandlingSetToLast() {
        // Arrange
        var overloadSignature1 = "SomeOverloadSignature(2)";
        var overloadSignature2 = "SomeOverloadSignature(1)";
        var automationTargetMock = mock(AutomationTarget.class);

        target.beginRootInvocation("rootSignature", "whenExpression", automationTargetMock);
        target.collect(createInvokedMethodMock(overloadSignature1));
        target.collect(createInvokedMethodMock(overloadSignature2));

        when(automationTargetMock.getOverloadHandling()).thenReturn(OverloadHandling.ALL);
        when(automationTargetMock.getTargetMethods()).thenReturn(List.of(overloadSignature1, overloadSignature2));

        // Act
        target.endRootInvocation();

        // Assert
        var result = target.getReport().getRootInvocations().get(0).getAutomationTargets();
        assertThat(result, arrayWithSize(2));
        assertThat(result, arrayContaining(overloadSignature1, overloadSignature2));
    }

    @Test
    public void endRootInvocation_ShouldProcessAllOverloadsWhenHandlingSetToLast() {
        // Arrange
        var overloadSignature1 = "SomeOverloadSignature(2)";
        var overloadSignature2 = "SomeOverloadSignature(1)";
        var automationTargetMock = mock(AutomationTarget.class);

        target.beginRootInvocation("rootSignature", "whenExpression", automationTargetMock);
        target.collect(createInvokedMethodMock(overloadSignature1));
        target.collect(createInvokedMethodMock(overloadSignature2));

        when(automationTargetMock.getOverloadHandling()).thenReturn(OverloadHandling.LAST);
        when(automationTargetMock.getTargetMethods()).thenReturn(List.of(overloadSignature1, overloadSignature2));

        // Act
        target.endRootInvocation();

        // Assert
        var result = target.getReport().getRootInvocations().get(0).getAutomationTargets();
        assertThat(result, arrayWithSize(1));
        assertThat(result, arrayContaining(overloadSignature2));
    }

    @Test
    public void stepOut_ShouldStepOutToTheParentInvocation() {
        // Act
        var root1 = target.beginRootInvocation("rootSignature", "whenExpression", null);
        var invocation1 = target.collect(createInvokedMethodMock());
        var invocation2 = target.collect(createInvokedMethodMock());
        target.stepOut();

        var invocation3 = target.collect(createInvokedMethodMock());
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result, notNullValue());
        assertThat(result.getRootInvocations().size(), is(1));

        var firstRootInvocation = result.getRootInvocations().get(0);
        assertThat(firstRootInvocation, is(root1));
        assertThat(firstRootInvocation.getInvocations().size(), is(1));

        var firstInvocation = firstRootInvocation.getInvocations().poll();
        assertThat(firstInvocation, is(invocation1));
        assertThat(firstInvocation.getInvocations().size(), is(2));

        var secondInvocation = firstInvocation.getInvocations().poll();
        assertThat(secondInvocation, equalTo(invocation2));
        assertThat(secondInvocation.getInvocations().isEmpty(), is(true));

        var thirdInvocation = firstInvocation.getInvocations().poll();
        assertThat(thirdInvocation, is(invocation3));
        assertThat(thirdInvocation.getInvocations().isEmpty(), is(true));
    }

    @Test
    public void stepOut_ShouldStepOutTwiceWhenCalledTwice() {
        // Act
        var root1 = target.beginRootInvocation("RootSignature", "whenExpression", null);
        var invocation1 = target.collect(createInvokedMethodMock());
        var invocation2 = target.collect(createInvokedMethodMock());
        target.stepOut();
        target.stepOut();

        var invocation3 = target.collect(createInvokedMethodMock());
        target.endRootInvocation();

        // Assert
        var result = target.getReport();
        assertThat(result, notNullValue());
        assertThat(result.getRootInvocations().size(), is(1));

        var firstRootInvocation = result.getRootInvocations().get(0);
        assertThat(firstRootInvocation, is(root1));
        assertThat(firstRootInvocation.getInvocations().size(), is(2));

        var firstInvocation = firstRootInvocation.getInvocations().poll();
        assertThat(firstInvocation, is(invocation1));
        assertThat(firstInvocation.getInvocations().size(), is(1));

        var secondInvocation = firstInvocation.getInvocations().poll();
        assertThat(secondInvocation, equalTo(invocation2));
        assertThat(secondInvocation.getInvocations().isEmpty(), is(true));

        var thirdInvocation = firstRootInvocation.getInvocations().poll();
        assertThat(thirdInvocation, is(invocation3));
        assertThat(thirdInvocation.getInvocations().isEmpty(), is(true));
    }

    @Test
    public void isAlreadyCollected_ShouldReturnTrueWhenMethodIsAlreadyCollected() {
        // Arrange
        var invokedMethodMock = createInvokedMethodMock();
        target.beginRootInvocation("rootSignature", "expression", null);
        target.collect(invokedMethodMock);

        // Act
        var result = target.isAlreadyCollected(invokedMethodMock);

        // Assert
        assertThat(result, is(true));
    }

    @Test
    public void isAlreadyCollected_ShouldReturnFalseWhenBeenSteppedOutOf() {
        // Arrange
        var invokedMethodMock = createInvokedMethodMock();
        target.beginRootInvocation("rootSignature", "expression", null);
        target.collect(invokedMethodMock);
        target.stepOut();

        // Act
        var result = target.isAlreadyCollected(invokedMethodMock);

        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void stepOut_ShouldReturnRootWhenNotAbleToStepOut() {
        // Arrange
        var invocation = target.beginRootInvocation("Signature", "whenExpression", null);

        // Act
        var result = target.stepOut();

        // Assert
        assertThat(result, is(invocation));
    }

    @Test
    public void getActualType_ShouldReturnTopMostArgumentType() {
        // Arrange
        List<MethodDeclaration> interfaceDefinitions = Collections.emptyList();
        target.beginRootInvocation("RootSignature", "whenExpression", null);

        var pair1 = new ImmutablePair<>("person", "person");
        target.collect(new InvokedMethodImpl(resolvedMethodMock, interfaceDefinitions, List.of(pair1), true));

        var pair2 = new ImmutablePair<>("person", "picky person");
        target.collect(new InvokedMethodImpl(resolvedMethodMock, interfaceDefinitions, List.of(pair2), true));

        // Act
        var result = target.getActualType("person");

        // Assert
        assertThat(result, equalTo(pair1.right));
    }

    @Test
    public void getActualType_ShouldReturnProvidedTypeWhenNoTypeCouldBeFound() {
        // Arrange
        var type = "person";
        var pair = new ImmutablePair<>("NotAPerson", "NotAPersonImpl");

        target.beginRootInvocation("RootSignature", "whenExpression", null);
        target.collect(createInvokedMethodMock(List.of(pair)));

        // Act
        var result = target.getActualType(type);

        // Assert
        assertThat(result, equalTo(type));
    }
}
