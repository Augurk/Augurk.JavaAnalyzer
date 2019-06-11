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

import static io.github.augurk.javaanalyzer.core.analyzers.Patterns.CLASS_METHOD_CALL_PATTERN;
import static io.github.augurk.javaanalyzer.core.analyzers.Predicates.isTargetClassOrInterfaceDeclaration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

interface TargetType extends QualifiedName {
    default Optional<ClassOrInterfaceDeclaration> findTargetType(CompilationUnit unit, String qualifiedTypeName) {
        var predicate = isTargetClassOrInterfaceDeclaration(qualifiedTypeName);
        return unit.findFirst(ClassOrInterfaceDeclaration.class, predicate);
    }

    default <T extends Node> Optional<T> findTargetType(Class<T> type, CompilationUnit unit, Predicate<T> predicate) {
        List<T> nodes = unit.findAll(type, predicate);
        T node = nodes.size() == 1 ? nodes.get(0) : null;
        return Optional.ofNullable(node);
    }

    default <T extends NodeWithType> List<T> findTypesOf(ClassOrInterfaceDeclaration type, String signature,
                                                         FindNodesOfTypeFunction<ClassOrInterfaceType, T> findFunc) {

        return type.getImplementedTypes().stream()
            .map(interfaceType -> findFunc.apply(interfaceType, signature))
            .flatMap(Optional::stream)
            .collect(Collectors.toUnmodifiableList());
    }

    default ClassOrInterfaceType variableTypeOf(MethodCallExpr expression,
                                                Map<String, ClassOrInterfaceType> variableTypeMap) {

        var matcher = CLASS_METHOD_CALL_PATTERN.matcher(expression.toString());

        return matcher.matches()
            ? variableTypeMap.getOrDefault(matcher.group(1), null)
            : null;
    }

    default boolean isSameType(ClassOrInterfaceDeclaration type, ClassOrInterfaceType otherType) {
        return qualifiedNameOf(type).equals(qualifiedNameOf(otherType));
    }
}
