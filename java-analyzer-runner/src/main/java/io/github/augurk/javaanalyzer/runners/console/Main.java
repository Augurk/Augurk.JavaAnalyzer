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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.Reporter;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.core.options.JavaVersion;
import io.github.augurk.javaanalyzer.filemanager.FileManagerImpl;
import io.github.augurk.javaanalyzer.reporters.console.ConsoleReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String PROJECT_NAME = "Cucumis";
    private static final String PROJECT_VERSION = "0.0.0";
    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String PROJECT_PATH =  "\\analyzable-projects\\cucumis";

    private static final String LOG_HEADER_SEPARATOR = "----------------------------------------------------";

    public static void main(String[] args) {
        // CONFIGURATION
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("AUGURK JAVA ANALYZER");
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("Start analyzer in: {}", PROJECT_PATH);
        var projectRoot = new File(BASE_PATH, PROJECT_PATH); // NOSONAR
        var classLoader = new AugurkConsoleClassLoader(projectRoot);
        var options = new AnalyzeOptions(classLoader, JavaVersion.JAVA_11, PROJECT_NAME, PROJECT_VERSION, createFilter(args));

        // INITIALIZE HEXAGON
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("INITIALIZE");
        logger.info(LOG_HEADER_SEPARATOR);
        // 1. Instantiate right-side adapter ("go outside the hexagon")
        logger.info("Initialize right-side adapters");
        var fileManager = new FileManagerImpl(projectRoot);

        var reporters = new ArrayList<Reporter>();
        reporters.add(new ConsoleReporter());

        // 2. Instantiate the hexagon
        logger.info("Initialize domain");
        var analyzer = new AnalyzerContext(options, fileManager, reporters);

        // 3. Instantiate the left-side adapter ("I want ask/to go inside")
        logger.info("Initialize left-side adapters");
        var app = new Application(analyzer);

        // RUN APPLICATION
        logger.info("Initialization complete");
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("RUN ANALYSIS");
        logger.info(LOG_HEADER_SEPARATOR);
        app.run();
    }

    private static List<String> createFilter(String[] args) {
        if (args.length == 0) return Collections.emptyList();
        return Collections.singletonList(args[0]);
    }
}
