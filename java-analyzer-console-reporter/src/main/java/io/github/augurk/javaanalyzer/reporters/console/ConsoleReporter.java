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

package io.github.augurk.javaanalyzer.reporters.console;

import io.github.augurk.javaanalyzer.core.Reporter;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleReporter implements Reporter {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleReporter.class);
    private static final String LOG_HEADER_SEPARATOR = "-".repeat(72);

    public ConsoleReporter() {
        logger.info("Add adapter: {}", this.getClass().getCanonicalName());
    }

    @Override
    public void report(AnalysisReport report) {
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("CONSOLE REPORTER, project: {}, version: {}", report.getAnalyzedProject(), report.getVersion());
        logger.info(LOG_HEADER_SEPARATOR);

        for (var invocation : report.getRootInvocations()) {
            logger.info("ROOT: {} (automationTargets: {})", invocation.getSignature(), invocation.getAutomationTargets());
            printInvocations(0, invocation);
        }
    }

    private void printInvocations(int level, Invocation invocation) {
        for (var in : invocation.getInvocations()) {
            String reportString = ".".repeat(Math.max(0, level * 3))
                .concat("... ")
                .concat(in.getSignature());

            logger.info("{}", reportString);
            if (in.getInvocations() != null) printInvocations(level + 1, in);
        }
    }
}
