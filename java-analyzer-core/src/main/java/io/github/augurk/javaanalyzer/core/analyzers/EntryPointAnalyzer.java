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

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.augurk.javaanalyzer.core.AnalyzerContext;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryPointAnalyzer extends AbstractAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(EntryPointAnalyzer.class);

    public EntryPointAnalyzer(AnalyzerContext context) {
        super(context);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration type, InvocationCollector collector) {
        AnalyzeOptions options = context.getOptions();
        String typeName = type.getNameAsString();

        if (!options.filterIsEmpty() && !options.filterContains(typeName)) {
            return;
        }

        logger.info("Analyse class \"{}\" for entry points", type.getNameAsString());
        NodeList<ClassOrInterfaceType> implementedTypes = type.getImplementedTypes();

        var analyzerType = implementedTypes.isNonEmpty()
            ? LambdaEntryPointAnalyzer.class
            : AnnotationEntryPointAnalyzer.class;

        stepIntoWith(analyzerType, currentCompilationUnit, type);
    }
}
