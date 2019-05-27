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

package io.github.augurk.javaanalyzer.core.domain;

public enum InvocationKind {
    NONE("None"),
    WHEN("When"),
    PUBLIC("Public"),
    PRIVATE("Private"),
    PROTECTED("Internal"),
    PACKAGE_PRIVATE("Internal");

    private final String value;

    InvocationKind(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
