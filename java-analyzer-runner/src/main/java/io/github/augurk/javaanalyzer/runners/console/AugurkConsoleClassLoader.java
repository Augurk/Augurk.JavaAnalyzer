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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class AugurkConsoleClassLoader extends ClassLoader {
    private static final String SRC_TARGET_DIR = "target/classes";
    private static final String TEST_TARGET_DIR = "target/test-classes";

    protected AugurkConsoleClassLoader(File projectRoot) {
        super(init(projectRoot));
    }

    private static ClassLoader init(File projectRoot) {
        ClassLoader applicationClassLoader = Main.class.getClassLoader();

        URL srcClasspath = urlOf(projectRoot, SRC_TARGET_DIR);
        URL testClasspath = urlOf(projectRoot, TEST_TARGET_DIR);

        return new URLClassLoader(new URL[] {
            srcClasspath, testClasspath,
        }, applicationClassLoader.getParent());
    }

    private static URL urlOf(File base, String path) {
        return urlOf(new File(base, path));
    }

    private static URL urlOf(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new UnsupportedOperationException("Malformed URL", e);
        }
    }
}
