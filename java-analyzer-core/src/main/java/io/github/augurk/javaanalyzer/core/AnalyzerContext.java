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

package io.github.augurk.javaanalyzer.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.augurk.javaanalyzer.core.analyzers.AbstractAnalyzer;
import io.github.augurk.javaanalyzer.core.analyzers.EntryPointAnalyzer;
import io.github.augurk.javaanalyzer.core.collectors.InvocationCollector;
import io.github.augurk.javaanalyzer.core.options.AnalyzeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyzerContext implements Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(AnalyzerContext.class);

    private final Map<String, WhenStepStrategy> whenStrategies;

    private final AnalyzeOptions options;
    private final FileManager fileManager;
    private final List<Reporter> reporters;
    private final InvocationCollector collector;

    public AnalyzerContext(AnalyzeOptions options, FileManager fileManager, List<Reporter> reporters) {
        logger.info("Add domain: {}", this.getClass().getCanonicalName());

        this.options = options;
        this.fileManager = fileManager;
        this.reporters = new ArrayList<>(reporters);
        this.collector = new InvocationCollector(options);
        this.whenStrategies = new HashMap<>();

        registerDefaultStrategies();
        configureSymbolSolver();
    }

    public AnalyzeOptions getOptions() {
        return options;
    }

    public InvocationCollector getCollector() {
        return collector;
    }

    @Override
    public void startAnalysis() {
        var startTime = Instant.now();
        logger.info("Start analysis");

        var visitor = new EntryPointAnalyzer(this);
        fileManager.walkFileTree(visitor::visit);

        var  endTime = Instant.now();
        long timeElapsed = Duration.between(startTime, endTime).toMillis();
        logger.info("Analysis complete, completed in {} ms", timeElapsed);

        reporters.forEach(reporter -> reporter.report(collector.getReport()));
    }

    public Optional<WhenStepStrategy> getWhenStepStrategy(String languageCode) {
        WhenStepStrategy strategy = whenStrategies.get(languageCode);

        if (strategy == null) {
            logger.warn("Can not find WHEN step strategy for language code: {}", languageCode);
        }

        return Optional.ofNullable(strategy);
    }

    public void addReporter(Reporter reporter) {
        reporters.add(reporter);
    }

    public void addWhenStepStrategy(WhenStepStrategy strategy) {
        whenStrategies.put(strategy.getLanguageCode(), strategy);
    }

    /**
     * Return new analyzer instance for provided type.
     *
     * @param type Type to return analyzer instance of
     * @return Optional containing analyzer instance
     */
    public <T extends AbstractAnalyzer> Optional<AbstractAnalyzer> createAnalyzer(Class<T> type) {
        AbstractAnalyzer instance = null;

        try {
            instance = type.getDeclaredConstructor(this.getClass()).newInstance(this);
        } catch (NoSuchMethodException | InstantiationException
            | IllegalAccessException | InvocationTargetException e) {
            logger.error("Unable to create instance of analyser", e);
        }

        return Optional.ofNullable(instance);
    }

    /**
     * Return compilation unit for the provided file.
     *
     * @param file File to return compilation unit
     * @return Optional containing the compilation unit
     */
    public Optional<CompilationUnit> parseSourceFile(File file) {
        CompilationUnit compilationUnit = null;

        try {
            compilationUnit = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            logger.error("Unable to parse Java source file", e);
        }

        return Optional.ofNullable(compilationUnit);
    }

    /**
     * Return compilation unit for the given qualified name (e.g. com.example.Clazz).
     *
     * @param qualifiedName qualified name to search for
     * @return Optional containing the compilation unit when found
     */
    public Optional<CompilationUnit> parserSourceFileByQualifiedName(String qualifiedName) {
        Optional<File> file = fileManager.getFileByQualifiedName(qualifiedName);
        return file.flatMap(this::parseSourceFile);
    }

    /**
     * Walk project directory for .java files and match them against the provided predicate.
     *
     * @param type Node type to search for
     * @param predicate Predicate to match the nodes with
     * @param consumer callback called for each type matching the predicate
     */
    public <T extends Node> void findByPredicate(Class<T> type, Predicate<T> predicate, Consumer<T> consumer) {
        fileManager.walkFileTree(file ->
            this.parseSourceFile(file).ifPresent(compilationUnit ->
                compilationUnit.findAll(type, predicate).forEach(consumer)
        ));
    }

    private void configureSymbolSolver() {
        var typeSolver = new CombinedTypeSolver(
            new ClassLoaderTypeSolver(options.getClassLoader()),
            new ReflectionTypeSolver(false)
        );

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        StaticJavaParser.getConfiguration().setLanguageLevel(options.getLanguageLevel());
    }
}
