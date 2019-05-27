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

package io.github.augurk.javaanalyzer.filemanager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import io.github.augurk.javaanalyzer.core.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileManagerImpl implements FileManager {
    private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

    private final File projectRoot;
    private final Map<String, File> files;

    private boolean filesIndexed;

    public FileManagerImpl(File projectRoot) {
        logger.info("Add adapter: {}", this.getClass().getCanonicalName());

        this.projectRoot = projectRoot;
        files = new HashMap<>();
    }

    @Override
    public Optional<File> getFileByQualifiedName(String qualifiedName) {
        if (!filesIndexed) {
            indexFiles();
        }

        var file = files.get(qualifiedName);
        return Optional.ofNullable(file);
    }

    @Override
    public void walkFileTree(Consumer<File> handler) {
        exploreDirectory((level, path, file) -> handler.accept(file));
        filesIndexed = true;
    }

    private void indexFiles() {
        filesIndexed = true;
        exploreDirectory((level, path, file) -> addFileToCache(path, file));
        logger.info("Indexed Java source file, found {} files", files.size());
    }

    private void exploreDirectory(FileHandler handler) {
        new DirectoryExplorer(
            (level, path, file) -> path.endsWith(Constants.JAVA_EXTENSION),
            (level, path, file) -> {
                addFileToCache(path, file);
                handler.handle(level, path, file);
            }
        ).explore(projectRoot);
    }

    private void addFileToCache(String path, File file) {
        String qualifiedName = createQualifiedNameFromPath(path);
        boolean isNew = files.putIfAbsent(qualifiedName, file) == null;
        if (isNew) logger.trace("Indexed file: {}", qualifiedName);
    }

    private String createQualifiedNameFromPath(String path) {
        Matcher matcher = Constants.JAVA_SOURCES_REGEX.matcher(path);
        return matcher.replaceAll("$1")
            .replace("/", ".");
    }
}
