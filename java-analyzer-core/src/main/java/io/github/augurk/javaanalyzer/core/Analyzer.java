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

import io.github.augurk.javaanalyzer.core.strategies.ENWhenStepStrategy;
import io.github.augurk.javaanalyzer.core.strategies.NLWhenStepStrategy;

/**
 * Analyzer adapter definition.
 */
public interface Analyzer {
    /**
     * Start analysis on given project directory (project root).
     */
    void startAnalysis();

    /**
     * Add new reporter
     *
     * @param reporter reporter to add
     */
    void addReporter(Reporter reporter);

    /**
     * Add new when step strategy to the analyzer. An example can be found
     * {@link ENWhenStepStrategy} here.
     *
     *
     * @param strategy when step strategy to add
     */
    void addWhenStepStrategy(WhenStepStrategy strategy);

    default void registerDefaultStrategies() {
        addWhenStepStrategy(new ENWhenStepStrategy());
        addWhenStepStrategy(new NLWhenStepStrategy());
    }
}
