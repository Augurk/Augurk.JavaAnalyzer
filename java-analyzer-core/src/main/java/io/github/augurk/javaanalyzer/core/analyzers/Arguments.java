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

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface Arguments extends ResolvableType, QualifiedName {
    default <T> T extractKeyValueFromPairsAs(Class<T> as, String key, NodeList<MemberValuePair> pairs) {
        Expression expression = extractKeyValueFromPairs(key, pairs);
        if (expression == null) return null;

        T result = null;

        if (expression.isClassExpr()) {
            result = as.cast(expression.asClassExpr().getType());
        } else if (expression.isStringLiteralExpr()) {
            result = as.cast(expression.asStringLiteralExpr().asString());
        } else if (expression.isFieldAccessExpr()) {
            result = as.cast(expression.asFieldAccessExpr());
        }

        return result;
    }

    default Expression extractKeyValueFromPairs(String key, NodeList<MemberValuePair> pairs) {
        for (var pair : pairs) {
            if (!pair.getNameAsString().equals(key)) continue;
            return pair.getValue();
        }

        return null;
    }

    default List<ImmutablePair<String, String>> extractArguments(ResolvedMethodDeclaration method, MethodCallExpr expression) {
        List<ImmutablePair<String, String>> argumentsTypes = Lists.newArrayList();
        NodeList<Expression> arguments = expression.getArguments();

        for (int i = 0; i < arguments.size(); i++) {
            String definedType = method.getParam(i).describeType();
            String actualType = resolvedTypeOf(arguments.get(i))
                .map(ResolvedType::describe).orElse(definedType);

            ImmutablePair<String, String> argumentPair = new ImmutablePair<>(definedType, actualType);
            argumentsTypes.add(argumentPair);
        }

        return argumentsTypes;
    }

    default boolean argumentTypesContains(List<ImmutablePair<String, String>> argumentTypes,
                                          ClassOrInterfaceType declaringType) {

        String qualifiedName = qualifiedNameOf(declaringType);

        for (var pair : argumentTypes) {
            if (pair.left.equals(qualifiedName) || pair.right.equals(qualifiedName)) {
                return true;
            }
        }

        return false;
    }
}
