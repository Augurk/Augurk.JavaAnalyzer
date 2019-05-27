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

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * File manager adapter definition. All file providers for the analysis tool should implement
 * this interface.
 */
public interface FileManager {
    /**
     * Searches given project directory (project root) for Java source file of the given qualified name
     *
     * @param qualifiedName qualified name of the file, e.g. com.example.ClazzName
     * @return Optional with file if found. When no file has been found should return empty optional
     */
    Optional<File> getFileByQualifiedName(String qualifiedName);

    /**
     * Walk the given project directory (project root) for Java source files. The file handler should be called
     * for each file found in the project directory.
     *
     * @param handler Handler function that will be called for each file
     */
    void walkFileTree(Consumer<File> handler);
}
