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

package io.github.augurk.javaanalyzer.core.analyzers;

import java.util.regex.Pattern;

final class Patterns {
    public static final Pattern CUCUMBER_JAVA_PACKAGE_PATTERN = Pattern.compile("^cucumber\\.api\\.java\\.(.*)\\.*$");
    public static final Pattern CUCUMBER_JAVA8_PACKAGE_PATTERN = Pattern.compile("^cucumber\\.api\\.java8\\.(.*)$");
    public static final Pattern SUPER_CALL_PATTERN = Pattern.compile("^(?:.*\\.)?super\\..*$");
    public static final Pattern CLASS_METHOD_CALL_PATTERN = Pattern.compile("^([a-zA-Z_$][a-zA-Z_$0-9]*)\\..*$");
    public static final Pattern AUTOMATION_TARGET_PATTERN =
        Pattern.compile("^io.github.augurk.javaanalyzer.annotations.AutomationTarget$");

    private Patterns() {
        // this class should not be instantiated
    }
}
