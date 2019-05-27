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

import static io.github.augurk.javaanalyzer.core.analyzers.Predicates.isInCurrentCompilationUnit;
import static io.github.augurk.javaanalyzer.core.analyzers.Predicates.isTargetMethodDeclaration;

import java.io.File;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;

public abstract class AbstractAnalyzer extends VoidVisitorAdapter<InvocationCollector>
    implements TargetType, QualifiedName {

    protected AnalyzerContext context;
    protected CompilationUnit currentCompilationUnit;

    AbstractAnalyzer(AnalyzerContext context) {
        this.context = context;
    }

    protected void setCurrentCompilationUnit(CompilationUnit compilationUnit) {
        this.currentCompilationUnit = compilationUnit;
    }

    public void visit(File file) {
        var compilationUnit = context.parseSourceFile(file);
        compilationUnit.ifPresent(unit -> {
            currentCompilationUnit = unit;
            super.visit(unit, context.getCollector());
        });
    }

    protected void findInvokedMethod(ClassOrInterfaceDeclaration type, String qualifiedTypeName, String signature,
                                   InvokedMethodConsumer consumer) {

        compilationUnitOf(type).ifPresent(unit ->
            findInvokedMethodOrElse(unit, qualifiedTypeName, signature, consumer, () -> {})
        );
    }

    protected void findInvokedMethod(String qualifiedTypeName, String signature, InvokedMethodConsumer consumer) {
        findInvokedMethodOrElse(qualifiedTypeName, signature, consumer, () -> {});
    }

    protected void findInvokedMethod(CompilationUnit unit, String qualifiedTypeName, String signature,
                                     InvokedMethodConsumer consumer) {

        findInvokedMethodOrElse(unit, qualifiedTypeName, signature, consumer, () -> {});
    }

    protected void findInvokedMethodOrElse(String qualifiedTypeName , String signature, InvokedMethodConsumer consumer,
                                           Runnable orElse) {

        compilationUnitOf(qualifiedTypeName).ifPresent(unit ->
            findInvokedMethodOrElse(unit, qualifiedTypeName, signature, consumer, orElse)
        );
    }

    protected void findInvokedMethodOrElse(CompilationUnit unit, String qualifiedTypeName, String signature,
                                           InvokedMethodConsumer consumer, Runnable orElse) {

        findTargetType(unit, qualifiedTypeName).ifPresent(targetType ->
            findInvokedMethod(targetType, signature).ifPresentOrElse(invokedMethod ->
                consumer.accept(unit, invokedMethod), orElse
            )
        );
    }

    protected Optional<MethodDeclaration> findInvokedMethod(ClassOrInterfaceDeclaration type, String signature) {
        return type.findFirst(MethodDeclaration.class, isTargetMethodDeclaration(signature));
    }

    protected Optional<MethodDeclaration> findInterfaceDefinitions(ClassOrInterfaceType type, String signature) {
        String qualifiedName = qualifiedNameOf(type);

        return compilationUnitOf(qualifiedName).flatMap(unit ->
            findTargetType(unit, qualifiedName).flatMap(targetType ->
                findInvokedMethod(targetType, signature)
            ));
    }

    protected Optional<CompilationUnit> compilationUnitOf(String qualifiedName) {
        return context.parserSourceFileByQualifiedName(qualifiedName);
    }

    protected Optional<CompilationUnit> compilationUnitOf(ClassOrInterfaceDeclaration type) {
        if (type == null) return Optional.empty();
        return type.findCompilationUnit();
    }

    protected Optional<CompilationUnit> compilationUnitOf(ClassOrInterfaceType type) {
        if (type == null) return Optional.empty();
        return type.findCompilationUnit();
    }

    protected Optional<CompilationUnit> compilationUnitOf(ResolvedMethodDeclaration method) {
        return isInCurrentCompilationUnit(currentCompilationUnit, method) ?
            Optional.of(currentCompilationUnit)
            : compilationUnitOf(qualifiedNameOfDeclaringType(method));
    }

    protected <T extends AbstractAnalyzer, A extends Node> void stepIntoWith(Class<T> with, CompilationUnit unit, A node) {
        context.createAnalyzer(with).ifPresent(visitor -> {
            visitor.setCurrentCompilationUnit(unit);
            node.accept(visitor, context.getCollector());
        });
    }
}
