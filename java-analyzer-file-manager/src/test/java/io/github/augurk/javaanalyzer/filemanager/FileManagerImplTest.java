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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import io.github.augurk.javaanalyzer.filemanager.utils.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FileManagerImplTest {
    private static final String SOURCES_ROOT = "testSources/";

    private FileManagerImpl target;

    @Before
    public void BeforeEach() throws Exception {
        target = new FileManagerImpl(FileUtils.loadFileByName(SOURCES_ROOT));
    }

    @Test
    public void walkFileTree_shouldCallHandlerForEachFile() {
        // Arrange
        var result = new ArrayList<String>();

        // Act
        target.walkFileTree(file -> result.add(file.getName()));

        // Assert
        assertThat(result.size(), is(2));
        assertThat(result.contains("Person.java"), is(true));
        assertThat(result.contains("World.java"), is(true));
    }

    @Test
    public void getFile_ShouldReturnFileOfProvidedQualifiedName() {
        // Arrange
        var qualifiedName = "pkg.Person";
        var expectedName = "Person.java";

        // Act
        Optional<File> result = target.getFileByQualifiedName(qualifiedName);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.get().getName(), equalTo(expectedName));
    }

    @Test
    public void getFile_ShouldReturnEmptyOptionalWhenFileIsNotFound() {
        // Arrange
        var qualifiedName = "pkg.someRandomFile";

        // Act
        Optional<File> result = target.getFileByQualifiedName(qualifiedName);

        // Assert
        assertThat(result.isPresent(), is(false));
    }
}
