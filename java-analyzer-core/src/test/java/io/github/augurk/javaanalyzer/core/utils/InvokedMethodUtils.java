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

package io.github.augurk.javaanalyzer.core.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import io.github.augurk.javaanalyzer.core.collectors.InvokedMethod;
import io.github.augurk.javaanalyzer.core.domain.InvocationKind;
import org.apache.commons.lang3.tuple.ImmutablePair;

public final class InvokedMethodUtils {
    public static final InvocationKind INVOCATION_KIND = InvocationKind.PUBLIC;
    public static final List<ImmutablePair<String, String>> ARGUMENT_TYPES = Collections.emptyList();
    public static final String SIGNATURE = "signature";
    public static final String[] INTERFACE_DEFINITIONS = new String[] {};
    public static final boolean IS_LOCAL = true;

    public static void isEqual(InvokedMethod result, InvocationKind kind, String declaringType,
                               List<ImmutablePair<String, String>> argumentTypes, String signature, boolean isLocal) {

        isEqual(result, kind, declaringType, signature, isLocal);

        for (var pair : argumentTypes) {
            assertThat(result.getArgumentTypes().contains(pair), is(true));
        }
    }

    public static void isEqual(InvokedMethod result, InvocationKind kind, String declaringType,
                               String signature, boolean isLocal) {

        assertThat(result.getDeclaringTypeName(), equalTo(declaringType));
        assertThat(result.getKind(), equalTo(kind));
        assertThat(result.getSignature(), equalTo(signature));
        assertThat(result.isLocal(), is(isLocal));
    }

    public static InvokedMethod createInvokedMethodMock(String signature) {
        return createInvokedMethodMock(signature, ARGUMENT_TYPES);
    }

    public static InvokedMethod createInvokedMethodMock() {
        return createInvokedMethodMock(ARGUMENT_TYPES);
    }

    public static InvokedMethod createInvokedMethodMock(List<ImmutablePair<String, String>> argumentTypes) {
        return createInvokedMethodMock(SIGNATURE, argumentTypes);
    }

    public static InvokedMethod createInvokedMethodMock(String signature, List<ImmutablePair<String, String>> argumentTypes) {
        var invokedMethodMock = mock(InvokedMethod.class);

        when(invokedMethodMock.getKind()).thenReturn(INVOCATION_KIND);
        when(invokedMethodMock.getArgumentTypes()).thenReturn(argumentTypes);
        when(invokedMethodMock.getSignature()).thenReturn(signature);
        when(invokedMethodMock.isLocal()).thenReturn(IS_LOCAL);
        when(invokedMethodMock.getInterfaceDefinitions()).thenReturn(INTERFACE_DEFINITIONS);

        return invokedMethodMock;
    }

    private InvokedMethodUtils() {
        // Class should not be instantiated
    }
}
