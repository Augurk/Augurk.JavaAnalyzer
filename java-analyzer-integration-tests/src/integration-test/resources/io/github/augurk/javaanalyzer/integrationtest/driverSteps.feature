Feature: Analyzing Through Drivers
  While designing your automation layer, the usage of drivers is highly recommended.
  As such, the C# Analyzer supports this pattern. However, there are some constraints
  to take into consideration...

Scenario: entrypoint is invoked through a driver directly
As long as the driver invokes the methodes on the testable class directly,
the results will be complete.
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is invoked through a driver directly'
    | Kind   | Local | Level | Signature                                                                                         |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.DriverSteps.whenEntrypointIsInvokedThroughADriverDirectly() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.GardenerDriver.waterPlants()                        |
    | PUBLIC | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                                      |
    | PUBLIC |       | 3     | java.io.PrintStream.println(java.lang.String)                                                     |

Scenario: entrypoint is indirectly invoked through a driver
If the driver uses the testable class via an interface,
the analyzer will stop at the invocation tot the interface.
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is indirectly invoked through a driver'
    | Kind   | Local | Level | Signature                                                                                            |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.DriverSteps.whenEntrypointIsIndirectlyInvokedThroughADriver()  |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.GardenerDriver.waterPlantsIndirectly()                 |
    | PUBLIC | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Person.waterPlants()                                           |
