Feature: Analyze Through Interfaces
  The Java analyzer will attempt to resolve the concrete type with an interface
  when it is clearly declared in code.

Scenario: entrypoint is an interface implementation
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is an interface implementation'
    | Kind   | Local | Level | Signature                                                                                        |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.InterfaceSteps.whenEntrypointIsAnInterfaceImplementation() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                                     |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                                    |

Scenario: entrypoint (lambda) is an interface implementation
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When (lambda) entrypoint is an interface implementation'
    | Kind   | Local | Level | Signature                                                                         |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.InterfaceLambdaSteps.InterfaceLambdaSteps() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                      |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                     |

Scenario: entrypoint is invoked after invocation on interface
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is invoked after invocation on interface'
    | Kind   | Local | Level | Signature                                                                                                |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.InterfaceSteps.whenEntrypointIsInvokedAfterInvocationOnInterface() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.MockedGardener.plant()                                     |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                                             |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                                            |

Scenario: entrypoint (lambda) is invoked after invocation on interface
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When (lambda) entrypoint is invoked after invocation on interface'
    | Kind   | Local | Level | Signature                                                                         |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.InterfaceLambdaSteps.InterfaceLambdaSteps() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.MockedGardener.plant()              |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                      |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                     |

Scenario: entrypoint is invoked through an interface implementation
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is invoked through an interface implementation'
    | Kind      | Local | Level | Signature                                                                                                      |
    | WHEN      |       | 0     | io.github.augurk.javaanalyzer.cucumis.InterfaceSteps.whenEntrypointIsInvokedThroughAnInterfaceImplementation() |
    | PUBLIC    | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.MockedGardener.waterPlants()                                     |
    | PUBLIC    | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gherkin.onWater(io.github.augurk.javaanalyzer.cucumis.WaterEventArgs)    |
    | PROTECTED | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gherkin.grow()                                                           |
    | PUBLIC    |       | 4     | java.io.PrintStream.println(java.lang.String)                                                                  |

Scenario: entrypoint (lambda) is invoked through an interface implementation
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When (lambda) entrypoint is invoked through an interface implementation'
    | Kind      | Local | Level | Signature                                                                                                   |
    | WHEN      |       | 0     | io.github.augurk.javaanalyzer.cucumis.InterfaceLambdaSteps.InterfaceLambdaSteps()                           |
    | PUBLIC    | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.MockedGardener.waterPlants()                                  |
    | PUBLIC    | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gherkin.onWater(io.github.augurk.javaanalyzer.cucumis.WaterEventArgs) |
    | PROTECTED | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gherkin.grow()                                                        |
    | PUBLIC    |       | 4     | java.io.PrintStream.println(java.lang.String)                                                               |
