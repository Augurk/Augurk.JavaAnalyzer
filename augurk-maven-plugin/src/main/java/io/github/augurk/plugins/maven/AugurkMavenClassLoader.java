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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

class AugurkMavenClassLoader extends ClassLoader {
    AugurkMavenClassLoader(MavenProject project) throws MojoExecutionException {
        super(init(project));
    }

    private static ClassLoader init(MavenProject project) throws MojoExecutionException {
        ClassLoader pluginClassLoader = AnalyzeMojo.class.getClassLoader();
        URL[] testClasspath = urlListOf(testElementListOf(project)).toArray(URL[]::new);
        return new URLClassLoader(testClasspath, pluginClassLoader.getParent());
    }

    @SuppressWarnings("unchecked")
    private static List<String> testElementListOf(MavenProject project) throws MojoExecutionException {
        try {
            return project.getTestClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Dependency resolution failed", e);
        }
    }

    private static List<URL> urlListOf(List<String> elementList) throws MojoExecutionException {
        List<URL> classes = Lists.newArrayList();

        for (String element : elementList) {
            classes.add(urlOf(new File(element))); // NOSONAR
        }

        return classes;
    }

    private static URL urlOf(File file) throws MojoExecutionException {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Malformed URL", e);
        }
    }
}
