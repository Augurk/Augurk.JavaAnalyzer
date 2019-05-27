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

package io.github.augurk.javaanalyzer.integrationtest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import io.github.augurk.javaanalyzer.core.domain.AnalysisReport;
import io.github.augurk.javaanalyzer.core.domain.Invocation;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;

public class AnalyzerStepdef {
    private static final File PROJECT_DIR = new File(System.getProperty("user.dir"));
    private static final File CUCUMIS_PATH =
        new File (PROJECT_DIR.getParentFile() +  "/analyzable-projects/cucumis");

    private static final String KIND_COLUMN = "Kind";
    private static final String LOCAL_COLUMN = "Local";
    private static final String LEVEL_COLUMN = "Level";
    private static final String SIGNATURE_COLUMN = "Signature";
    private static final String AUTOMATION_TARGETS = "AutomationTargets";

    private String projectName;
    private AnalysisReport report;
    private int currentTableRow;

    @Given("{string} contains feature files")
    public void containsFeatureFiles(String projectName) {
        this.projectName = projectName;
    }

    @When("an analysis is run")
    public void anAnalysisIsRun() {
        report = new Runner().run(CUCUMIS_PATH);
    }

    @Then("the resulting report contains {string}")
    public void theResultingReportContains(String when, DataTable table) {
        if (report == null || report.getRootInvocations().isEmpty()) {
            fail("WHEN step should be defined within this project before using this THEN step");
        }

        if (!when.startsWith("When")) {
            fail("WHEN text should start with 'When '");
        }

        assertThat(report.getAnalyzedProject(), equalTo(projectName));
        Invocation rootInvocation = containsWhenStep(when);
        assertRootInvocation(rootInvocation, table);
    }

    private Invocation containsWhenStep(String when) {
        Invocation rootInvocation = report.getRootInvocations()
            .stream()
            .filter(in -> Arrays.asList(in.getRegularExpression()).contains(when))
            .findFirst().orElse(null);

        assertThat("Analysis report should contain when step", rootInvocation, notNullValue());
        return rootInvocation;
    }

    private void assertRootInvocation(Invocation rootInvocation, DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps();
        currentTableRow = 0;

        assertAutomationTargets(currentTableRow, table, rootInvocation);
        assertInvocation(currentTableRow, table, rootInvocation);
        assertInvocationTree(currentTableRow + 1, table, rootInvocation.getInvocations());

        assertThat("All rows in DataTable should be asserted", currentTableRow + 1, is(table.size()));
    }

    private void assertAutomationTargets(int level, List<Map<String, String>> table, Invocation rootInvocation) {
        Map<String, String> row = table.get(level);
        if (!row.containsKey(AUTOMATION_TARGETS)) return;
        String[] automationTargets = row.get(AUTOMATION_TARGETS).split(",");
        assertThat("Automation target should match", rootInvocation.getAutomationTargets(), equalTo(automationTargets));
    }

    private void assertInvocationTree(int level, List<Map<String, String>> table, Deque<Invocation> invocations) {
        for (var invocation : invocations) {
            currentTableRow += 1;
            assertInvocation(level, table, invocation);

            if (invocation.getInvocations() != null && !invocation.getInvocations().isEmpty()) {
                assertInvocationTree(level + 1, table, invocation.getInvocations());
            }
        }
    }

    private void assertInvocation(int level, List<Map<String, String>> table, Invocation invocation) {
        InvocationKind expectedKind = InvocationKind.valueOf(table.get(currentTableRow).get(KIND_COLUMN));
        assertThat(createMessage("Kind should match"), invocation.getKind(), is(expectedKind));

        boolean expectedLocal = Boolean.parseBoolean(table.get(currentTableRow).get(LOCAL_COLUMN));
        assertThat(createMessage("Local should match"), invocation.isLocal(), is(expectedLocal));

        int expectedLevel = Integer.parseInt(table.get(currentTableRow).get(LEVEL_COLUMN));
        assertThat(createMessage("Level should match"), level, is(expectedLevel));

        String expectedSignature = table.get(currentTableRow).get(SIGNATURE_COLUMN);
        assertThat(createMessage("Signature should match"), invocation.getSignature(), equalTo(expectedSignature));
    }

    private String createMessage(String message) {
        return String.format("%s (row: %d)", message, currentTableRow + 1);
    }
}
