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

package io.github.augurk.plugins.maven.bootstrap;

import java.util.List;

import com.google.common.collect.Lists;
import io.github.augurk.javaanalyzer.core.Analyzer;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.FileManager;
import io.github.augurk.javaanalyzer.core.Reporter;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.filemanager.FileManagerImpl;
import io.github.augurk.javaanalyzer.reporters.augurk.AugurkAgent;
import io.github.augurk.javaanalyzer.reporters.augurk.AugurkReporter;
import io.github.augurk.javaanalyzer.reporters.console.ConsoleReporter;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class JavaAnalyzerBuilder {
    private final Log logger;
    private final MavenProject project;
    private final AnalyzeOptions options;
    private final List<Reporter> reporters;

    public JavaAnalyzerBuilder(Log logger, MavenProject project, AnalyzeOptions options) {
        this.logger = logger;
        this.project = project;
        this.options = options;
        this.reporters = Lists.newArrayList();

        logger.info("Initialize right-side adapters");
    }

    public void withConsoleReporter() {
        reporters.add(new ConsoleReporter());
    }

    public void withAugurkReporter() {
        if (options.getAugurkURL() == null) {
            logger.warn("Augurk instance URL not set, please consider adding augurkUrl to plugin configuration");
            return;
        }

        AugurkAgent agent = new AugurkAgent(options.getAugurkURL());
        reporters.add(new AugurkReporter(options, agent));
    }

    public JavaAnalyzer build() {
        FileManager fileManager = createFileManager();

        logger.info("Initialize domain");
        Analyzer analyzer = new AnalyzerContext(options, fileManager, reporters);

        logger.info("Initialize left-side adapters");
        return new JavaAnalyzer(analyzer);
    }

    private FileManager createFileManager() {
        return new FileManagerImpl(project.getBasedir());
    }
}
