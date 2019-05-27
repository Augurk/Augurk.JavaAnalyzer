Feature: Analyze Local Method Calls

Scenario: a local method is called within the entrypoint
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When a local method is called within the entrypoint'
    | Kind      | Local | Level | Signature                                                                                                   |
    | WHEN      |       | 0     | io.github.augurk.javaanalyzer.cucumis.LocalMethodCallSteps.whenALocalMethodIsCalledWithinTheEntrypoint()    |
    | PUBLIC    | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gherkin.onWater(io.github.augurk.javaanalyzer.cucumis.WaterEventArgs) |
    | PROTECTED | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gherkin.grow()                                                        |
    | PUBLIC    |       | 3     | java.io.PrintStream.println(java.lang.String)                                                               |

Scenario: an explicit base method is called within the entrypoint
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When an explicit base method is called within the entrypoint'
    | Kind      | Local | Level | Signature                                                                                                        |
    | WHEN      |       | 0     | io.github.augurk.javaanalyzer.cucumis.LocalMethodCallSteps.whenAnExplicitBaseMethodIsCalledWithinTheEntrypoint() |
    | PUBLIC    | true  | 1     | io.github.augurk.javaanalyzer.cucumis.PickyGherkin.onWater(io.github.augurk.javaanalyzer.cucumis.WaterEventArgs) |
    | PUBLIC    | true  | 2     | io.github.augurk.javaanalyzer.cucumis.WaterEventArgs.isAcidFree()                                                |
    | PUBLIC    |       | 2     | java.io.PrintStream.println(java.lang.String)                                                                    |
    | PROTECTED | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gherkin.grow()                                                             |
    | PUBLIC    |       | 3     | java.io.PrintStream.println(java.lang.String)                                                                    |

Scenario: a local method is called within the entrypoint explicitly on this
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When a local method is called within the entrypoint explicitly on this'
    | Kind    | Local | Level | Signature                                                                                                                |
    | WHEN    |       | 0     | io.github.augurk.javaanalyzer.cucumis.LocalMethodCallSteps.WhenALocalMethodIsCalledWithinTheEntrypointExplicitlyOnThis() |
    | PUBLIC  | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gherkin.onPlant(io.github.augurk.javaanalyzer.cucumis.PlantEventArgs)              |
    | PRIVATE | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gherkin.setInitialSize(java.lang.String)                                           |
    | PUBLIC  |       | 3     | java.io.PrintStream.printf(java.lang.String, java.lang.Object...)                                                        |
