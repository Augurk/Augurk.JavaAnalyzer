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

package io.github.augurk.javaanalyzer.reporters.augurk;

import java.util.Deque;
import java.util.List;

import io.github.augurk.javaanalyzer.core.Reporter;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AugurkReporter implements Reporter {
    private static final Logger logger = LoggerFactory.getLogger(AugurkReporter.class);
    private static final String LOG_HEADER_SEPARATOR = "-".repeat(72);

    private static final String ANALYZED_PROJECT = "AnalyzedProject";
    private static final String VERSION = "Version";
    private static final String TIMESTAMP = "Timestamp";

    private static final String ROOT_INVOCATIONS = "RootInvocations";
    private static final String INVOCATIONS = "Invocations";
    private static final String INVOCATION_KIND = "Kind";
    private static final String INVOCATION_SIGNATURE = "Signature";

    private static final String REGULAR_EXPRESSIONS = "RegularExpressions";
    private static final String INTERFACE_DEFINITIONS = "InterfaceDefinitions";
    private static final String AUTOMATION_TARGETS = "AutomationTargets";
    private static final String LOCAL = "Local";

    private final AnalyzeOptions options;
    private AugurkAgent augurkAgent;

    public AugurkReporter(AnalyzeOptions options, AugurkAgent augurkAgent) {
        logger.info("Add adapter: {}", this.getClass().getCanonicalName());
        this.options = options;
        this.augurkAgent = augurkAgent;
    }

    @Override
    public void report(AnalysisReport report) {
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("AUGURK REPORTER, project: {}, version: {}", report.getAnalyzedProject(), report.getVersion());
        logger.info(LOG_HEADER_SEPARATOR);
        logger.info("Report to Augurk instance on: {}", options.getAugurkURL());

        var jsonReport = new JSONObject();
        jsonReport.put(ANALYZED_PROJECT, report.getAnalyzedProject());
        jsonReport.put(VERSION, report.getVersion());
        jsonReport.put(TIMESTAMP, report.getTimestamp());
        mapRootInvocations(report.getRootInvocations(), jsonReport);

        String analyzedProject = report.getAnalyzedProject();
        String version = report.getVersion();
        String body = jsonReport.toString();
        var statusCode = augurkAgent.postReport(analyzedProject, version, body);

        logger.info("Done reporting to Augurk, received status code {}", statusCode);
    }

    private void mapRootInvocations(List<Invocation> rootInvocations, JSONObject jObject) {
        for (var rootInvocation : rootInvocations) {
            var invocationJSON = new JSONObject();

            invocationJSON.put(INVOCATION_KIND, rootInvocation.getKind().toString());
            invocationJSON.put(INVOCATION_SIGNATURE, rootInvocation.getSignature());
            invocationJSON.put(REGULAR_EXPRESSIONS, rootInvocation.getRegularExpression());
            invocationJSON.put(AUTOMATION_TARGETS, rootInvocation.getAutomationTargets());

            jObject.append(ROOT_INVOCATIONS, invocationJSON);
            mapInvocations(rootInvocation.getInvocations(), invocationJSON);
        }
    }

    private void mapInvocations(Deque<Invocation> invocations, JSONObject jObject) {
        for (var invocation : invocations) {
            var invocationJSON = new JSONObject();

            invocationJSON.put(INVOCATION_KIND, invocation.getKind().toString());
            invocationJSON.put(INVOCATION_SIGNATURE, invocation.getSignature());
            invocationJSON.put(INTERFACE_DEFINITIONS, invocation.getInterfaceDefinitions());
            invocationJSON.put(LOCAL, invocation.isLocal());

            jObject.append(INVOCATIONS, invocationJSON);
            mapInvocations(invocation.getInvocations(), invocationJSON);
        }
    }
}
