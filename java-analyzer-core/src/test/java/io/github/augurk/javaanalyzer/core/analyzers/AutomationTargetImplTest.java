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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;
import org.junit.Test;

public class AutomationTargetImplTest {
    @Test
    public void ctor_ShouldCreateAutomationTarget() {
        // Arrange
        var typeMock = mock(ClassOrInterfaceType.class, RETURNS_DEEP_STUBS);
        List<String> methods = List.of("signature");
        when(typeMock.resolve().getQualifiedName()).thenReturn("declaringType");

        // Act
        var target = new AutomationTargetImpl(typeMock, methods, OverloadHandling.LAST);

        // Assert
        assertThat(target.getDeclaringType(), equalTo("declaringType"));
        assertThat(target.getTargetMethods(), equalTo(methods));
        assertThat(target.getOverloadHandling(), equalTo(OverloadHandling.LAST));
    }
}
