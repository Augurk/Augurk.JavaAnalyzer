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

package io.github.augurk.javaanalyzer.core.strategies;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.github.augurk.javaanalyzer.core.WhenStepStrategy;
import org.junit.Before;
import org.junit.Test;

public class ENWhenStepStrategyTest {
    private WhenStepStrategy target;

    @Before
    public void beforeEach() {
        target = new ENWhenStepStrategy();
    }

    @Test
    public void isWhenStep_ShouldReturnTrueWhenExpressionMatches() {
        assertThat(target.getLanguageCode(), equalTo("en"));
        assertThat(target.isWhenStep("when"), is(true));
    }

    @Test
    public void isWhenStep_ShouldReturnFalseWhenExpressionNotMatches() {
        assertThat(target.isWhenStep("invalid"), is(false));
    }
}
