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

import java.util.Collections;
import java.util.List;

import com.github.javaparser.ParserConfiguration;

public class AnalyzeOptions {
    private final ClassLoader classLoader;
    private final JavaVersion javaVersion;
    private final String projectName;
    private final String version;
    private final String augurkURL;
    private final List<String> filter;

    public AnalyzeOptions(ClassLoader classLoader, JavaVersion javaVersion, String projectName,
                          String version, String augurkURL, List<String> filter) {

        this.classLoader = classLoader;
        this.javaVersion = javaVersion;
        this.projectName = projectName;
        this.version = version;
        this.augurkURL = augurkURL;
        this.filter = filter;
    }

    public AnalyzeOptions(ClassLoader classLoader, JavaVersion javaVersion, String projectName,
                          String version, String augurkURL) {

        this(classLoader, javaVersion, projectName, version, augurkURL, Collections.emptyList());
    }

    public AnalyzeOptions(ClassLoader classLoader, JavaVersion javaVersion, String projectName,
                          String version, List<String> filter) {

        this(classLoader, javaVersion, projectName, version, null, filter);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ParserConfiguration.LanguageLevel getLanguageLevel() {
        return javaVersion.getLevel();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getVersion() {
        return version;
    }

    public String getAugurkURL() {
        return augurkURL;
    }

    public boolean filterIsEmpty() {
        return filter == null || filter.isEmpty();
    }

    public boolean filterContains(String value) {
        return filter.contains(value);
    }
}
