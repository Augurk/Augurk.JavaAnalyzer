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

import static io.github.augurk.javaanalyzer.core.analyzers.Patterns.CUCUMBER_JAVA8_PACKAGE_PATTERN;

import java.util.Optional;
import java.util.regex.Matcher;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.WhenStepStrategy;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaEntryPointAnalyzer extends AbstractAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(LambdaEntryPointAnalyzer.class);

    private String detectedCucumberLanguage;
    private ResolvedConstructorDeclaration defaultConstructor;

    public LambdaEntryPointAnalyzer(AnalyzerContext context) {
        super(context);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration type, InvocationCollector collector) {
        NodeList<ClassOrInterfaceType> implementedTypes = type.getImplementedTypes();

        boolean isCucumberInterface = implementedTypes.stream().anyMatch(this::isCucumberInterface);
        type.getDefaultConstructor()
            .ifPresent(c -> defaultConstructor = c.resolve());

        if (isCucumberInterface && defaultConstructor != null) {
            logger.info("Found entry point: {}", defaultConstructor.getName());
            super.visit(type, collector);
        }
    }

    @Override
    public void visit(MethodCallExpr expression, InvocationCollector collector) {
        Optional<WhenStepStrategy> strategy = context.getWhenStepStrategy(detectedCucumberLanguage);

        String exprName = expression.getNameAsString();
        NodeList<Expression> arguments = expression.getArguments();

        boolean isWhenStep = strategy.map(s -> s.isWhenStep(exprName)).orElse(false);
        if (!isWhenStep || arguments.size() < 2) return;

        int bodyIndex = arguments.size() - 1;
        boolean supportedStep = arguments.get(0).isStringLiteralExpr();
        supportedStep = supportedStep && arguments.get(bodyIndex).isLambdaExpr();

        if (!supportedStep) {
            logger.warn("Used WHEN step notation is not supported");
            return;
        }

        String whenExpression = arguments.get(0).asStringLiteralExpr().getValue();
        handleLambdaExpression(whenExpression, arguments.get(bodyIndex).asLambdaExpr(), collector);
    }

    private void handleLambdaExpression(String whenExpression, LambdaExpr lambdaExpr, InvocationCollector collector) {
        Statement body = lambdaExpr.getBody();
        collector.beginRootInvocation(defaultConstructor.getQualifiedSignature(), whenExpression, null);
        stepIntoWith(InvocationTreeAnalyzer.class, currentCompilationUnit, body);
        collector.endRootInvocation();
    }

    private boolean isCucumberInterface(ClassOrInterfaceType type) {
        String qualifiedName = qualifiedNameOf(type);

        Matcher matcher = CUCUMBER_JAVA8_PACKAGE_PATTERN.matcher(qualifiedName);
        if (!matcher.matches()) return false;

        detectedCucumberLanguage = matcher.group(1).toLowerCase();
        return true;
    }
}
