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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.ArrayMatching.arrayContaining;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InvokedMethodImplTest {
    @Test
    public void ctor_ShouldCreateInvokedMethod() {
        // Arrange
        var interfaceDefinitionMock = mock(MethodDeclaration.class, RETURNS_DEEP_STUBS);
        var methodMock = mock(ResolvedMethodDeclaration.class, RETURNS_DEEP_STUBS);
        var pairMock = (List<ImmutablePair<String, String>>) mock(List.class);
        List<MethodDeclaration> interfaceDefinitions = List.of(interfaceDefinitionMock, interfaceDefinitionMock);

        when(methodMock.getQualifiedSignature()).thenReturn("signature");
        when(methodMock.declaringType().getQualifiedName()).thenReturn("declaringType");
        when(methodMock.isAbstract()).thenReturn(false);
        when(methodMock.accessSpecifier()).thenReturn(Modifier.Keyword.PRIVATE);

        when(interfaceDefinitionMock.resolve().getQualifiedSignature()).thenReturn("signature1", "signature2");

        // Act
        var result = new InvokedMethodImpl(methodMock, interfaceDefinitions, pairMock, true);

        // Assert
        assertThat(result.getKind(), is(InvocationKind.PRIVATE));
        assertThat(result.isLocal(), is(true));
        assertThat(result.getSignature(), equalTo("signature"));
        assertThat(result.getDeclaringTypeName(), equalTo("declaringType"));
        assertThat(result.getArgumentTypes(), is(pairMock));
        assertThat(result.getInterfaceDefinitions(), arrayContaining("signature1", "signature2"));
    }

    @Test
    public void ctor_ShouldInvocationKindPublicWhenMethodIsAbstract() {
        // Arrange
        List<MethodDeclaration> interfaceDefinitions = Collections.emptyList();
        var methodMock = mock(ResolvedMethodDeclaration.class, RETURNS_DEEP_STUBS);
        var pairMock = (List<ImmutablePair<String, String>>) mock(List.class);
        when(methodMock.isAbstract()).thenReturn(true);

        // Act
        var result = new InvokedMethodImpl(methodMock, interfaceDefinitions, pairMock, true);

        // Assert
        assertThat(result.getKind(), is(InvocationKind.PUBLIC));
    }

    @Test
    public void equals_ShouldMatchObjects() {
        // Arrange
        var pairMockBlack = (List<ImmutablePair<String, String>>) mock(List.class);
        var pairMockRed = (List<ImmutablePair<String, String>>) mock(List.class);
        var methodMockBlack = mock(ResolvedMethodDeclaration.class, RETURNS_DEEP_STUBS);
        var methodMockRed = mock(ResolvedMethodDeclaration.class, RETURNS_DEEP_STUBS);

        when(methodMockBlack.getQualifiedSignature()).thenReturn("signature");
        when(methodMockBlack.declaringType().getQualifiedName()).thenReturn("declaringType");

        when(methodMockRed.getQualifiedSignature()).thenReturn("wrongSignature");
        when(methodMockRed.declaringType().getQualifiedName()).thenReturn("wrongDeclaringType");

        when(pairMockBlack.size()).thenReturn(10);
        when(pairMockRed.size()).thenReturn(20);

        // Act & Assert
        EqualsVerifier.forClass(InvokedMethodImpl.class)
            .suppress(Warning.NULL_FIELDS)
            .suppress(Warning.STRICT_INHERITANCE)
            .withPrefabValues(ResolvedMethodDeclaration.class, methodMockRed, methodMockBlack)
            .withPrefabValues(List.class, pairMockRed, pairMockBlack)
            .verify();
    }
}
