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

package io.github.augurk.plugins.maven;

import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import io.github.augurk.javaanalyzer.core.options.JavaVersion;
import io.github.augurk.plugins.maven.bootstrap.JavaAnalyzerBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Analyze methods being called from when steps
 */
@Mojo(name = "analyze",
    defaultPhase = LifecyclePhase.PROCESS_TEST_SOURCES,
    requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class AnalyzeMojo extends AbstractAugurkMojo {
    /**
     * URL of the Augurk instance the analysis results are reported to.
     */
    @Parameter
    private String augurkUrl;

    /**
     * Flag to skip Augurk reporting. Defaults to true.
     * This flag is set to false when the Augurk url is not provided.
     */
    @Parameter(property = "reportToAugurk", defaultValue = "true")
    private boolean reportToAugurk;


    /**
     * Set to true to report the analysis results to the console. Defaults to false.
     */
    @Parameter(property = "reportToConsole", defaultValue = "false")
    private boolean reportToConsole;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("AUGURK JAVA ANALYZER");
        logger.info(LOG_HEADER_SEPARATOR);

        ClassLoader classLoader = new AugurkMavenClassLoader(getProject()); // NOSONAR
        AnalyzeOptions options = createAnalyzeOptions(classLoader);

        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("RUN ANALYSIS");
        logger.info(LOG_HEADER_SEPARATOR);

        JavaAnalyzerBuilder builder = new JavaAnalyzerBuilder(logger, getProject(), options);
        configureReporters(builder);
        builder.build().run();
    }

    private AnalyzeOptions createAnalyzeOptions(ClassLoader classLoader) throws MojoExecutionException {
        JavaVersion version = getJavaVersion();
        var options = new AnalyzeOptions(classLoader, version, getProjectName(), getProjectVersion(), augurkUrl);

        logProperty("Project name", getProjectName());
        logProperty("Project version", getProjectVersion());
        logProperty("Project root", getProjectRoot().getPath());
        logProperty("Compiler target", getCompilerTarget());
        logProperty("Augurk instance", augurkUrl);

        return options;
    }

    private JavaVersion getJavaVersion() throws MojoExecutionException {
        String versionString = "java_".concat(getCompilerTarget()).toUpperCase();

        try {
            return JavaVersion.valueOf(versionString);
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException("Unsupported compiler target: " + getCompilerTarget());
        }
    }

    private void configureReporters(JavaAnalyzerBuilder builder) {
        if (reportToConsole || augurkUrl == null) builder.withConsoleReporter();
        if (reportToAugurk) builder.withAugurkReporter();
    }
}
