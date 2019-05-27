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
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import io.github.augurk.javaanalyzer.filemanager.utils.FileUtils;
import org.junit.Test;

public class DirectoryExplorerTest {
    private static final String SOURCES_ROOT = "testSources/";

    @Test
    public void explore_shouldOnlyListFilesThatMatchFilter() throws Exception {
        // Arrange
        var result = new ArrayList<String>();
        var target = new DirectoryExplorer(
            (level, path, file) -> path.endsWith(".java"),
            (level, path, file) -> result.add(file.getName())
        );

        // Act
        target.explore(FileUtils.loadFileByName(SOURCES_ROOT));

        // Assert
        assertThat(result.size(), is(2));
        assertThat(result.contains("Person.java"), is(true));
        assertThat(result.contains("World.java"), is(true));
    }
}
