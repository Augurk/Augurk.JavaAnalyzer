Feature: Analyze Generics

Scenario: A generic method is invoked
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When a generic method is invoked'
    | Kind   | Local | Level | Signature                                                                        |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.GenericSteps.whenAGenericMethodIsInvoked() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.harvest(T)                         |
    | PUBLIC |       | 2     | java.io.PrintStream.printf(java.lang.String, java.lang.Object...)                |
    | PUBLIC | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Plant.prune()                               |
