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

package io.github.augurk.javaanalyzer.runners.console;

import io.github.augurk.javaanalyzer.core.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private final Analyzer analyzer;

    public Application(Analyzer analyzer) {
        logger.info("Add adapter: {}", this.getClass().getCanonicalName());

        this.analyzer = analyzer;
    }

    public void run() {
        analyzer.startAnalysis();
    }
}