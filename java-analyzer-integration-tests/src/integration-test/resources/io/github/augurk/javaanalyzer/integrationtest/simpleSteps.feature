Feature: Analysis of basic steps

Scenario: When calls directly into a single entrypoint
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is invoked directly'
    | Kind   | Local | Level | Signature                                                                          |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.BasicSteps.whenEntryPointIsInvokedDirectly() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()                      |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                      |

Scenario: When (lambda) calls directly into a single entrypoint
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When (lambda) entrypoint is invoked directly'
    | Kind   | Local | Level | Signature                                                                          |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.BasicLambdaSteps.BasicLambdaSteps()          |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()                      |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                      |

Scenario: When entrypoint is surrounded by other invocations
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is surrounded by other invocations'
    | Kind   | Local | Level | Signature                                                                                        |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.BasicSteps.whenEntryPointIsSurroundedByOtherInvocations()  |
    | PUBLIC |       | 1     | java.io.PrintStream.println(java.lang.String)                                                    |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()                                    |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                                    |
    | PUBLIC |       | 1     | java.io.PrintStream.println(java.lang.String)                                                    |

Scenario: When (lambda) entrypoint is surrounded by other invocations
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When (lambda) entrypoint is surrounded by other invocations'
    | Kind   | Local | Level | Signature                                                                  |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.BasicLambdaSteps.BasicLambdaSteps()  |
    | PUBLIC |       | 1     | java.io.PrintStream.println(java.lang.String)                              |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()              |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                              |
    | PUBLIC |       | 1     | java.io.PrintStream.println(java.lang.String)                              |

Scenario: When invokes two seperate entrypoints
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When two separate entrypoints are invoked'
    | Kind   | Local | Level | Signature                                                                        |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.BasicSteps.whenTwoEntryPointsAreInvoked()  |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()                    |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                    |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                     |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                    |

Scenario: When (lambda) invokes two seperate entrypoints
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When (lambda) two separate entrypoints are invoked'
    | Kind   | Local | Level | Signature                                                                  |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.BasicLambdaSteps.BasicLambdaSteps()  |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()              |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                              |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()               |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                              |
