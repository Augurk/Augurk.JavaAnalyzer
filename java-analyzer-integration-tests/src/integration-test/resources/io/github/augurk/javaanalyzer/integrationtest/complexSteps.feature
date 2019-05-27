Feature: Analyzing Through Complex Structures

Scenario: entrypoint is invoked with unsolvable primitive types
For example unable to implicitly determine type of List.of function with primitives
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint calls list.of function with primitives'
    | Kind             | Local | Level | Signature                                                                                                                                         |
    | WHEN             |       | 0     | io.github.augurk.javaanalyzer.cucumis.ComplexSteps.whenFunctionIsCalledWithListOfWithPrimitives()                                                               |
    | PRIVATE          | true  | 1     | io.github.augurk.javaanalyzer.cucumis.ComplexSteps.waterPlants(io.github.augurk.javaanalyzer.cucumis.Gardener, java.util.List<java.lang.Integer>) |
    | PUBLIC           |       | 2     | java.util.stream.IntStream.sum()                                                                                                                  |
    | PACKAGE_PRIVATE  | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(io.github.augurk.javaanalyzer.cucumis.Plant)                                                 |
    | PACKAGE_PRIVATE  | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(java.util.List<io.github.augurk.javaanalyzer.cucumis.Plant>)                                 |
    | PUBLIC           |       | 4     | java.io.PrintStream.printf(java.lang.String, java.lang.Object...)                                                                                 |
