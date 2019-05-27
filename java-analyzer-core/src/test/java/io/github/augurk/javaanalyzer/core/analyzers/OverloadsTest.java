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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;
import org.junit.Before;
import org.junit.Test;

public class OverloadsTest {
    private class Target implements Overloads {}

    private Target target;

    @Before
    public void beforeEach() {
        target = new Target();
    }

    @Test
    public void overloadHandlingOf_ShouldReturnOverloadHandlingAllAsDefault() {
        // Act
        var result = target.overloadHandlingOf(null);

        // Assert
        assertThat(result, equalTo(OverloadHandling.ALL));
    }

    @Test
    public void overloadHandlingOf_ShouldReturnOverloadHandlingForGivenExpressionName() {
        // Arrange
        var expressionMock = mock(FieldAccessExpr.class);
        when(expressionMock.getNameAsString()).thenReturn("FIRST", "LAST", "ALL");

        // Act
        var result1 = target.overloadHandlingOf(expressionMock);
        var result2 = target.overloadHandlingOf(expressionMock);
        var result3 = target.overloadHandlingOf(expressionMock);

        // Assert
        assertThat(result1, equalTo(OverloadHandling.FIRST));
        assertThat(result2, equalTo(OverloadHandling.LAST));
        assertThat(result3, equalTo(OverloadHandling.ALL));
    }
}
