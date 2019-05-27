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

import com.github.javaparser.ParserConfiguration;

public enum JavaVersion {
    JAVA_8(ParserConfiguration.LanguageLevel.JAVA_8),
    JAVA_9(ParserConfiguration.LanguageLevel.JAVA_9),
    JAVA_10(ParserConfiguration.LanguageLevel.JAVA_10),
    JAVA_11(ParserConfiguration.LanguageLevel.JAVA_11),
    JAVA_12(ParserConfiguration.LanguageLevel.JAVA_12);

    private final ParserConfiguration.LanguageLevel level;

    JavaVersion(ParserConfiguration.LanguageLevel level) {
        this.level = level;
    }

    public ParserConfiguration.LanguageLevel getLevel() {
        return level;
    }
}
