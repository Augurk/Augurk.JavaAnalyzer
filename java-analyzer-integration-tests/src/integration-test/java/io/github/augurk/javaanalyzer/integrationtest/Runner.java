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

package io.github.augurk.javaanalyzer.integrationtest;

import java.io.File;
import java.util.Collections;

import io.github.augurk.javaanalyzer.core.Analyzer;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.FileManager;
import io.github.augurk.javaanalyzer.core.Reporter;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.core.options.JavaVersion;
import io.github.augurk.javaanalyzer.filemanager.FileManagerImpl;

class IntegrationTestReporter implements Reporter {
    public AnalysisReport report;

    @Override
    public void report(AnalysisReport report) {
        this.report = report;
    }
}

public class Runner {
    private static final String PROJECT_NAME = "Cucumis";
    private static final String PROJECT_VERSION = "0.0.0";

    public AnalysisReport run(File projectRoot) {
        ClassLoader classLoader = new IntegrationTestClassLoader(projectRoot);
        AnalyzeOptions options = new AnalyzeOptions(classLoader, JavaVersion.JAVA_11, PROJECT_NAME, PROJECT_VERSION, "");

        IntegrationTestReporter reporter = new IntegrationTestReporter();
        FileManager fileManager = new FileManagerImpl(projectRoot);

        Analyzer analyzer = new AnalyzerContext(options, fileManager, Collections.singletonList(reporter));

        analyzer.startAnalysis();
        return reporter.report;
    }
}
