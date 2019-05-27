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

import java.util.function.Predicate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

interface Predicates {
    static boolean isInCurrentCompilationUnit(CompilationUnit unit, ResolvedMethodDeclaration method) {
        return unit.findFirst(ClassOrInterfaceDeclaration.class, isInCurrentCompilationUnit(method)).isPresent();
    }

    static Predicate<ClassOrInterfaceDeclaration> isInCurrentCompilationUnit(ResolvedMethodDeclaration declaration) {
        return cid -> {
            String qualifiedName = declaration.declaringType().getQualifiedName();
            return cid.resolve().getQualifiedName().equals(qualifiedName);
        };
    }

    static Predicate<ClassOrInterfaceDeclaration> isTargetClassOrInterfaceDeclaration(String qualifiedName) {
        return cid -> cid.resolve().getQualifiedName().equals(qualifiedName);
    }

    static Predicate<ClassOrInterfaceType> isTargetClassOrInterfaceType(String qualifiedName) {
        return cit -> cit.resolve().getQualifiedName().equals(qualifiedName);
    }

    static Predicate<ClassOrInterfaceDeclaration> isDerivedTypeOf(ClassOrInterfaceDeclaration declaration) {
        String qualifiedName = declaration.resolve().getQualifiedName();

        return cid -> cid.getExtendedTypes().stream()
            .anyMatch(isTargetClassOrInterfaceType(qualifiedName));
    }

    static Predicate<MethodDeclaration> isTargetMethodDeclaration(String signature) {
        return md -> md.resolve().getSignature().equals(signature);
    }

    static Predicate<MethodDeclaration> isTargetMethod(String methodName) {
        return md -> md.getNameAsString().equals(methodName);
    }

    static Predicate<MethodDeclaration> isTargetMethod(String methodName, int params) {
        return md -> md.getNameAsString().equals(methodName) && md.getParameters().size() == params;
    }
}
