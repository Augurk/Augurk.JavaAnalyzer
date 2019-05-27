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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import org.junit.Before;
import org.junit.Test;

public class ArgumentsTest {
    private class ArgumentsMock implements Arguments { }
    private ArgumentsMock target;

    @Before
    public void beforeEach() {
        target = new ArgumentsMock();
    }

    @Test
    public void extractKeyValueFromPairs_ShouldReturnKeyValueFromKeyValuePairList() {
        // Arrange
        var pair1 = mock(MemberValuePair.class);
        var pair2 = mock(MemberValuePair.class);
        var exprMock = mock(Expression.class);

        var nodeList = new NodeList<>(pair1, pair2);

        when(pair1.getNameAsString()).thenReturn("aValue");
        when(pair2.getNameAsString()).thenReturn("value");
        when(pair2.getValue()).thenReturn(exprMock);

        // Act
        var result = target.extractKeyValueFromPairs("value", nodeList);

        // Assert
        assertThat(result, is(exprMock));
    }

    @Test
    public void extractKeyValueFromPairs_ShouldReturnNullByEmptyNodeList() {
        // Arrange
        var nodeList = new NodeList<MemberValuePair>();

        // Act
        var result = target.extractKeyValueFromPairs("value", nodeList);

        // Assert
        assertThat(result, nullValue());
    }

    @Test
    public void extractKeyValueFromPairs_ShouldReturnNullWhenNoMatch() {
        // Arrange
        var pair = mock(MemberValuePair.class);
        var nodeList = new NodeList<>(pair);

        when(pair.getNameAsString()).thenReturn("aValue");

        // Act
        var result = target.extractKeyValueFromPairs("value", nodeList);

        // Assert
        assertThat(result, nullValue());
    }

    @Test
    public void extractKeyValueFromPairsAs_ShouldReturnCastedStringForStringLiteralExpr() {
        // Arrange
        var pair = mock(MemberValuePair.class);
        var nodeList = new NodeList<>(pair);

        when(pair.getNameAsString()).thenReturn("value");
        when(pair.getValue()).thenReturn(new StringLiteralExpr("actualValue"));

        // Act
        var result = target.extractKeyValueFromPairsAs(String.class, "value", nodeList);

        // Assert
        assertThat(result, instanceOf(String.class));
        assertThat(result, equalTo("actualValue"));
    }

    @Test
    public void extractKeyValueFromPairsAs_ShouldReturnCastedClassOrInterfaceTypeForClassExpr() {
        // Arrange
        var pair = mock(MemberValuePair.class);
        var nodeList = new NodeList<>(pair);
        var classOrInterfaceType = new ClassOrInterfaceType();
        var classExpr = new ClassExpr(classOrInterfaceType);

        when(pair.getNameAsString()).thenReturn("value");
        when(pair.getValue()).thenReturn(classExpr);

        // Act
        var result = target.extractKeyValueFromPairsAs(ClassOrInterfaceType.class, "value", nodeList);

        // Assert
        assertThat(result, instanceOf(ClassOrInterfaceType.class));
        assertThat(result, equalTo(classOrInterfaceType));
    }

    @Test
    public void extractKeyValueFromPairsAs_ShouldReturnCastedFieldAccessExprForFieldAccessExpr() {
        // Arrange
        var pair = mock(MemberValuePair.class);
        var nodeList = new NodeList<>(pair);
        var expression = new FieldAccessExpr();

        when(pair.getNameAsString()).thenReturn("value");
        when(pair.getValue()).thenReturn(expression);

        // Act
        var result = target.extractKeyValueFromPairsAs(FieldAccessExpr.class, "value", nodeList);

        // Assert
        assertThat(result, instanceOf(FieldAccessExpr.class));
        assertThat(result, equalTo(expression));
    }

    @Test
    public void extractKeyValueFromPairsAs_ShouldReturnNullValueWhenTypeIsInvalid() {
        // Arrange
        var pair = mock(MemberValuePair.class);
        var nodeList = new NodeList<>(pair);
        var expression = mock(Expression.class);

        when(pair.getNameAsString()).thenReturn("value");
        when(pair.getValue()).thenReturn(expression);

        // Act
        var result = target.extractKeyValueFromPairsAs(FieldAccessExpr.class, "value", nodeList);

        // Assert
        assertThat(result, nullValue());
    }

    @Test
    public void extractArguments_ShouldReturnListOfMethodArgumentPairs() {
        // Arrange
        var methodMock = mock(ResolvedMethodDeclaration.class, RETURNS_DEEP_STUBS);
        var exprMock = mock(MethodCallExpr.class);
        var arg1Mock = mock(Expression.class, RETURNS_DEEP_STUBS);
        var arg2Mock = mock(Expression.class, RETURNS_DEEP_STUBS);
        var nodeList = new NodeList<>(arg1Mock, arg2Mock);

        when(exprMock.getArguments()).thenReturn(nodeList);
        when(arg1Mock.calculateResolvedType().describe()).thenReturn("arg1");
        when(arg2Mock.calculateResolvedType().describe()).thenReturn("arg2");
        when(methodMock.getParam(anyInt()).describeType()).thenReturn("param1", "param2");

        // Act
        var result = target.extractArguments(methodMock, exprMock);

        // Assert
        assertThat(result.size(), is(2));

        var first = result.get(0);
        assertThat(first.left, equalTo("param1"));
        assertThat(first.right, equalTo("arg1"));

        var second = result.get(1);
        assertThat(second.left, equalTo("param2"));
        assertThat(second.right, equalTo("arg2"));
    }

    @Test
    public void extractArguments_ShouldReturnEmptyListWhenMethodDoesNotContainParameters() {
        // Arrange
        var methodMock = mock(ResolvedMethodDeclaration.class);
        var exprMock = mock(MethodCallExpr.class);

        when(exprMock.getArguments()).thenReturn(new NodeList<>());

        // Act
        var result = target.extractArguments(methodMock, exprMock);

        // Assert
        assertThat(result.isEmpty(), is(true));
    }
}
