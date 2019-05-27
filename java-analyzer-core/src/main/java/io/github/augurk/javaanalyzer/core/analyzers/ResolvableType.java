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

import java.util.Optional;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

interface ResolvableType {
    default Optional<ResolvedMethodDeclaration> resolvedMethodDeclarationOf(MethodCallExpr expression) {
        try {
            return Optional.of(expression.resolve());
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    default Optional<ResolvedType> resolvedTypeOf(Expression expression) {
        try  {
            return Optional.of(expression.calculateResolvedType());
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    default ResolvedMethodDeclaration resolvedMethodDeclarationOf(MethodDeclaration method) {
        return method.resolve();
    }
}
