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

import static io.github.augurk.javaanalyzer.core.analyzers.Patterns.AUTOMATION_TARGET_PATTERN;
import static io.github.augurk.javaanalyzer.core.analyzers.Patterns.CUCUMBER_JAVA_PACKAGE_PATTERN;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.collectors.AutomationTarget;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.domain.OverloadHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationEntryPointAnalyzer extends AbstractAnalyzer implements Signature, Arguments, Overloads {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationEntryPointAnalyzer.class);

    private AutomationTarget automationTarget;

    public AnnotationEntryPointAnalyzer(AnalyzerContext context) {
        super(context);
    }

    @Override
    public void visit(MethodDeclaration method, InvocationCollector collector) {
        super.visit(method, collector);
        Optional<AnnotationExpr> annotationExpr = isWhenStep(method);
        annotationExpr.ifPresent(expr -> handleWhenExpression(method, expr, collector));
    }

    @Override
    public void visit(NormalAnnotationExpr annotation, InvocationCollector collector) {
        var matcher = AUTOMATION_TARGET_PATTERN.matcher(qualifiedNameOf(annotation));
        if (!matcher.matches()) return;

        NodeList<MemberValuePair> pairs = annotation.getPairs();
        var declaringType = extractKeyValueFromPairsAs(ClassOrInterfaceType.class, "declaringType", pairs);
        var targetMethod = extractKeyValueFromPairsAs(String.class, "targetMethod", pairs);
        var overloadHandling = extractKeyValueFromPairsAs(FieldAccessExpr.class, "overloadHandling", pairs);

        automationTarget = buildAutomationTarget(declaringType, targetMethod, overloadHandling);
    }

    private Optional<AnnotationExpr> isWhenStep(MethodDeclaration method) {
        AnnotationExpr expression = null;

        for (var annotation : method.getAnnotations()) {
            var packageName = annotation.resolve().getPackageName();
            var matcher = CUCUMBER_JAVA_PACKAGE_PATTERN.matcher(packageName);

            if (matcher.matches()) {
                String languageCode = matcher.group(1).toLowerCase();
                expression = executeStrategy(languageCode, annotation);
                break;
            }
        }

        return Optional.ofNullable(expression);
    }

    private AnnotationExpr executeStrategy(String languageCode, AnnotationExpr expression) {
        var strategy = context.getWhenStepStrategy(languageCode);
        boolean isWhenStep = strategy.map(s -> s.isWhenStep(expression.getNameAsString())).orElse(false);
        return isWhenStep ? expression : null;
    }

    private Optional<String> extractWhenLiteralStringValue(AnnotationExpr annotationExpression) {
        Expression whenExpression;

        if (annotationExpression.isSingleMemberAnnotationExpr()) {
            whenExpression = annotationExpression.asSingleMemberAnnotationExpr().getMemberValue();
        } else {
            NodeList<MemberValuePair> pairs = annotationExpression.asNormalAnnotationExpr().getPairs();
            whenExpression = extractKeyValueFromPairs("value", pairs);
        }

        var whenExpressionString = whenExpression != null && whenExpression.isStringLiteralExpr()
            ? whenExpression.asStringLiteralExpr().getValue()
            : null;

        return Optional.ofNullable(whenExpressionString);
    }

    private void handleWhenExpression(MethodDeclaration method, AnnotationExpr expression, InvocationCollector collector) {
        logger.info("Found entry point: {}", method.getNameAsString());
        String signature = qualifiedSignatureOf(method);
        String whenExpressionString = extractWhenLiteralStringValue(expression).orElse("");

        collector.beginRootInvocation(signature, whenExpressionString, automationTarget);
        stepIntoWith(InvocationTreeAnalyzer.class, currentCompilationUnit, method);
        collector.endRootInvocation();
    }

    private AutomationTarget buildAutomationTarget(ClassOrInterfaceType type, String methodName, FieldAccessExpr overloadExpr) {
        OverloadHandling overloadHandling = overloadHandlingOf(overloadExpr);

        List<String> overloads = compilationUnitOf(qualifiedNameOf(type))
            .map(unit -> overloadSignaturesOf(unit, methodName))
            .orElse(Collections.emptyList());

        return new AutomationTargetImpl(type, overloads, overloadHandling);
    }
}
