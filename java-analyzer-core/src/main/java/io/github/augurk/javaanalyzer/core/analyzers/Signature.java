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

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

interface Signature {
    default String signatureOf(ResolvedMethodDeclaration method) {
        return method.getSignature();
    }

    default String qualifiedSignatureOf(MethodDeclaration method) {
        return qualifiedSignatureOf(method.resolve());
    }

    default String qualifiedSignatureOf(ResolvedMethodDeclaration method) {
        return method.getQualifiedSignature();
    }
}
