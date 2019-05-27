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

package io.github.augurk.javaanalyzer.core.options;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.javaparser.ParserConfiguration;
import org.junit.Test;

public class JavaVersionTest {
    @Test
    public void version_EnumShouldContainSupportedVersions() {
        assertThat(JavaVersion.JAVA_8.getLevel(), equalTo(ParserConfiguration.LanguageLevel.JAVA_8));
        assertThat(JavaVersion.JAVA_9.getLevel(), equalTo(ParserConfiguration.LanguageLevel.JAVA_9));
        assertThat(JavaVersion.JAVA_10.getLevel(), equalTo(ParserConfiguration.LanguageLevel.JAVA_10));
        assertThat(JavaVersion.JAVA_11.getLevel(), equalTo(ParserConfiguration.LanguageLevel.JAVA_11));
        assertThat(JavaVersion.JAVA_12.getLevel(), equalTo(ParserConfiguration.LanguageLevel.JAVA_12));
    }
}
