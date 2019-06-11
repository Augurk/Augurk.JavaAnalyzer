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

import static io.github.augurk.javaanalyzer.core.analyzers.Predicates.isDerivedTypeOf;
import static io.github.augurk.javaanalyzer.core.analyzers.Predicates.isTargetMethod;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.google.common.collect.Maps;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.collectors.InvokedMethod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvocationTreeAnalyzer extends AbstractAnalyzer implements ResolvableType, Signature, Arguments, Expression {
    private static Logger logger = LoggerFactory.getLogger(InvocationTreeAnalyzer.class);

    private Map<String, ClassOrInterfaceType> variableTypeMap;
    private List<ImmutablePair<String, String>> argumentTypes;
    private List<MethodDeclaration> interfaceDefinitions;

    public InvocationTreeAnalyzer(AnalyzerContext context) {
        super(context);
        argumentTypes = Collections.emptyList();
        interfaceDefinitions = Collections.emptyList();
        variableTypeMap = Maps.newHashMap();
    }

    @Override
    public void visit(VariableDeclarator variable, InvocationCollector collector) {
        var visitor = new GenericVisitorAdapter<ClassOrInterfaceType, Void>() {
            @Override
            public ClassOrInterfaceType visit(ObjectCreationExpr object, Void arg) {
                return object.getType();
            }
        };

        variableTypeMap.put(variable.getNameAsString(), visitor.visit(variable, null));
        super.visit(variable, collector);
    }

    @Override
    public void visit(MethodCallExpr expression, InvocationCollector collector) {
        Consumer<ResolvedMethodDeclaration> consumer = method -> handleSolvableMethodCallExpr(method, expression);
        Runnable orElse = () -> handleUnsolvableMethodCallExpr(expression);
        resolvedMethodDeclarationOf(expression).ifPresentOrElse(consumer, orElse);
    }

    private void handleSolvableMethodCallExpr(ResolvedMethodDeclaration method, MethodCallExpr expression) {
        Consumer<CompilationUnit> consumer = unit -> handleInvocation(unit, method, expression);
        Runnable orElse = () -> collectAndStepOut(method);
        compilationUnitOf(method).ifPresentOrElse(consumer, orElse);
    }

    private void handleUnsolvableMethodCallExpr(MethodCallExpr expression) {
        ClassOrInterfaceType declaringType = variableTypeOf(expression, variableTypeMap);
        CompilationUnit unit = compilationUnitOf(declaringType).orElse(currentCompilationUnit);
        var predicate = isTargetMethod(expression.getNameAsString(), expression.getArguments().size());

        Consumer<MethodDeclaration> consumer = method -> handleInvocation(unit, resolvedMethodDeclarationOf(method), expression);
        Runnable orElse = () -> logger.warn("Unable to resolve suitable type for {}", expression);

        findTargetType(MethodDeclaration.class, unit, predicate).ifPresentOrElse(consumer, orElse);
    }

    private void handleInvocation(CompilationUnit unit, ResolvedMethodDeclaration method, MethodCallExpr expression) {
        argumentTypes = extractArguments(method, expression);

        findTargetType(unit, qualifiedNameOfDeclaringType(method)).ifPresent(targetType -> {
            ClassOrInterfaceType declaringType = variableTypeOf(expression, variableTypeMap);

            if (declaringType != null && targetType.isInterface()) {
                handleInterfaceMethod(declaringType, method);
                return;
            }

            if (declaringType != null && !isSameType(targetType, declaringType)) {
                handleOverrideMethod(declaringType, targetType, method);
                return;
            }

            handleClassMethod(targetType, method, expression);
        });
    }

    private void handleInterfaceMethod(ClassOrInterfaceType declaringType, ResolvedMethodDeclaration method) {
        String qualifiedTypeName = qualifiedNameOf(declaringType);
        String signature = signatureOf(method);
        interfaceDefinitions = interfaceDefinitionsOf(qualifiedTypeName, signature);

        findInvokedMethod(qualifiedTypeName, signature, (unit, invokedMethod) -> {
            collect(invokedMethod);
            findAndStepInto(unit, qualifiedTypeName, signature);
        });
    }

    private void handleClassMethod(ClassOrInterfaceDeclaration type, ResolvedMethodDeclaration method,
                                   MethodCallExpr expression) {

        String signature = signatureOf(method);
        interfaceDefinitions = interfaceDefinitionsOf(type, signature);

        findInvokedMethod(type, signature).ifPresent(invokedMethod -> {
            if (invokedMethod.isAbstract()) {
                handleAbstractMethod(type, signature);
                return;
            }

            String qualifiedTypeName = qualifiedNameOfDeclaringType(method);
            String actualArgumentType = context.getCollector().getActualType(qualifiedTypeName);

            if (!isSuperCall(expression) && !actualArgumentType.equals(qualifiedTypeName)) {
                handleMethodWithDifferentArgumentType(actualArgumentType, signature);
                return;
            }

            compilationUnitOf(qualifiedTypeName)
                .ifPresent(unit -> collectAndStepInto(unit, invokedMethod));
        });
    }

    private void handleOverrideMethod(ClassOrInterfaceType declaringType, ClassOrInterfaceDeclaration type,
                                      ResolvedMethodDeclaration method) {

        String qualifiedTypeName = qualifiedNameOfDeclaringType(method);
        String signature = signatureOf(method);
        String qualifiedContainingTypeName = qualifiedNameOf(declaringType);

        InvokedMethodConsumer consumer = (unit, invokedMethod) -> {
            interfaceDefinitions = interfaceDefinitionsOf(type, signature);
            this.collectAndStepInto(unit, invokedMethod);
        };

        findInvokedMethodOrElse(qualifiedContainingTypeName, signature, consumer, () ->
            handleDerivedType(type, qualifiedTypeName, signature)
        );
    }

    private void handleDerivedType(ClassOrInterfaceDeclaration type, String qualifiedTypeName, String signature) {
        findInvokedMethod(type, qualifiedTypeName, signature, this::collectAndStepInto);
    }

    private void handleMethodWithDifferentArgumentType(String qualifiedTypeName, String signature) {
        findInvokedMethod(qualifiedTypeName, signature, this::collectAndStepInto);
    }

    private void handleAbstractMethod(ClassOrInterfaceDeclaration type, String signature) {
        context.findByPredicate(ClassOrInterfaceDeclaration.class, isDerivedTypeOf(type), derivedType ->
           findInvokedMethod(derivedType, signature).ifPresent(invokedMethod -> {
               collect(invokedMethod);

               derivedType.findCompilationUnit()
                   .ifPresent(cu -> stepInto(cu, invokedMethod));
           }));
    }

    private List<MethodDeclaration> interfaceDefinitionsOf(String qualifiedTypeName, String signature) {
        return compilationUnitOf(qualifiedTypeName)
            .flatMap(unit -> findTargetType(unit, qualifiedTypeName)
                .map(type -> interfaceDefinitionsOf(type, signature))
        ).orElse(Collections.emptyList());
    }

    private List<MethodDeclaration> interfaceDefinitionsOf(ClassOrInterfaceDeclaration type, String signature) {
        return findTypesOf(type, signature, this::findInterfaceDefinitions);
    }

    private void findAndStepInto(CompilationUnit unit, String qualifiedTypeName, String signature) {
        findInvokedMethod(unit, qualifiedTypeName, signature, this::stepInto);
    }

    private void collectAndStepInto(CompilationUnit unit, MethodDeclaration method) {
        InvokedMethod invokedMethod = invokedMethodOf(method);
        if (context.getCollector().isAlreadyCollected(invokedMethod)) return;

        collect(invokedMethod);
        stepInto(unit, method);
    }

    private void collectAndStepOut(ResolvedMethodDeclaration method) {
        collect(method, false);
        stepOut();
    }

    private void collect(MethodDeclaration method) {
        collect(resolvedMethodDeclarationOf(method), true);
    }

    private void collect(ResolvedMethodDeclaration method, boolean isLocal) {
        collect(invokedMethodOf(method, isLocal));
    }

    private void collect(InvokedMethod method) {
        context.getCollector().collect(method);
    }

    private InvokedMethod invokedMethodOf(MethodDeclaration method) {
        return invokedMethodOf(resolvedMethodDeclarationOf(method), true);
    }

    private InvokedMethod invokedMethodOf(ResolvedMethodDeclaration method, boolean isLocal) {
        return new InvokedMethodImpl(method, interfaceDefinitions, argumentTypes, isLocal);
    }

    private void stepInto(CompilationUnit unit, MethodDeclaration method) {
        stepIntoWith(InvocationTreeAnalyzer.class, unit, method);
        stepOut();
    }

    private void stepOut() {
        context.getCollector().stepOut();
    }
}
