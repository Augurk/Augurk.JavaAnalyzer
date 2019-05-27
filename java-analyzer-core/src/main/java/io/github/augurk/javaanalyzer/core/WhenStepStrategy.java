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

package io.github.augurk.javaanalyzer.core;

/**
 * When step adapter definition, all when step strategies should implement this interface.
 */
public interface WhenStepStrategy {
    /**
     * Should return the language key of the strategy.
     *
     * @return language code of the when strategy
     */
    String getLanguageCode();

    /**
     * Should check if the provided method declaration is annotated with a Cucumber when annotation,
     * for the given language.
     *
     * See <a href="https://docs.cucumber.io/gherkin/reference/#spoken-languages">Cucumber documentation</a>
     * for available language code.
     *
     * @param expressionName name of the expression to check
     * @return annotation expression for the matched annotation
     */
    boolean isWhenStep(String expressionName);
}
