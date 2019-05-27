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

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAugurkMojo extends AbstractMojo {
    protected static final String PROPERTY_LOG_FORMAT = "%-16s: %s";
    protected static final String LOG_HEADER_SEPARATOR = "-".repeat(72);
    protected final Log logger = getLog();

    /**
     * The current Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * The maven target compiler version.
     */
    @Parameter(defaultValue = "${maven.compiler.target}")
    private String compilerTarget;

    /**
     * The base directory of the project.
     */
    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File projectRoot;

    /**
     * The name of the project
     */
    @Parameter(defaultValue = "${project.name}", readonly = true)
    private String projectName;

    /**
     * The version of the project.
     */
    @Parameter(defaultValue = "${project.version}", readonly = true)
    private String projectVersion;

    /**
     * Flag to skip execution of the Augurk Maven plugin goals. Defaults to false.
     */
    @Parameter(property = "skipAugurk", defaultValue = "false")
    private boolean skipAugurk;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipAugurk) {
            return;
        }

        doExecute();
    }

    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

    public String getCompilerTarget() {
        return compilerTarget;
    }

    public MavenProject getProject() {
        return project;
    }

    public File getProjectRoot() {
        return projectRoot;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void logProperty(String property, String value) {
        value = value != null ? value : "";
        logger.info(String.format(PROPERTY_LOG_FORMAT, property, value));
    }
}
