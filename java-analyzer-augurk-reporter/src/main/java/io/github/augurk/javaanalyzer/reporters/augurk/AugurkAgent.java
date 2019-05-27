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

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AugurkAgent {
    private static final String REPORT_ENDPOINT = "%s/api/v2/products/%s/versions/%s/analysis/reports";

    private final Logger logger = LoggerFactory.getLogger(AugurkAgent.class);
    private final String baseURL;

    public AugurkAgent(String baseURL) {
        this.baseURL = baseURL;
    }

    public int postReport(String analyzedProject, String version, String body) {
        String requestURL = String.format(REPORT_ENDPOINT, baseURL, analyzedProject, version);
        StringEntity payload = new StringEntity(body, ContentType.APPLICATION_JSON);
        Request request = Request.Post(requestURL).body(payload);
        return doRequest(request);
    }

    private int doRequest(Request request) {
        try {
            return request.execute()
                .returnResponse()
                .getStatusLine()
                .getStatusCode();
        } catch (IOException e) {
            logger.error("Unable to fulfill HTTP request", e);
        }

        return HttpStatus.SC_SERVICE_UNAVAILABLE;
    }
}
