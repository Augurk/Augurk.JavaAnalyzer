Feature: Analyze With Inheritance

Scenario: entrypoint is invoked on inherited automation class
There should be no trace of the inherited class, as the method is not overridden
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is invoked on inherited automation class'
    | Kind   | Local | Level | Signature                                                                                                  |
    | WHEN   |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenEntrypointIsInvokedOnInheritedAutomationClass() |
    | PUBLIC | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()                                              |
    | PUBLIC |       | 2     | java.io.PrintStream.println(java.lang.String)                                                              |

Scenario: entrypoint is invoked through an inherited automation class
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When entrypoint is invoked through an inherited automation class'
    | Kind     | Local | Level | Signature                                                                                                         |
    | WHEN     |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenEntrypointIsInvokedThroughAnInheritedAutomationClass() |
    | PUBLIC   | true  | 1     | io.github.augurk.javaanalyzer.cucumis.support.InheritedGardener.plantGherkinAndWaterIt()                          |
    | PUBLIC   | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.plantGherkin()                                                     |
    | PUBLIC   |       | 3     | java.io.PrintStream.println(java.lang.String)                                                                     |
    | PUBLIC   | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.waterPlants()                                                      |
    | PUBLIC   |       | 3     | java.io.PrintStream.println(java.lang.String)                                                                     |

Scenario: same method is invoked with different concrete types
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When same method is invoked with different concrete types'
    | Kind            | Local | Level | Signature                                                                                                    |
    | WHEN            |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenSameMethodIsInvokedWithDifferentConcreteTypes()   |
    | PUBLIC          | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.harvestGherkin(io.github.augurk.javaanalyzer.cucumis.Gherkin) |
    | PUBLIC          |       | 2     | java.io.PrintStream.println(java.lang.String)                                                                |
    | PACKAGE_PRIVATE | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gherkin.cutVine()                                                      |
    | PUBLIC          |       | 3     | java.io.PrintStream.println(java.lang.String)                                                                |
    | PUBLIC          | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Gardener.harvestGherkin(io.github.augurk.javaanalyzer.cucumis.Gherkin) |
    | PUBLIC          |       | 2     | java.io.PrintStream.println(java.lang.String)                                                                |
    | PACKAGE_PRIVATE | true  | 2     | io.github.augurk.javaanalyzer.cucumis.PickyGherkin.cutVine()                                                 |
    | PACKAGE_PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gherkin.cutVine()                                                      |
    | PUBLIC          |       | 4     | java.io.PrintStream.println(java.lang.String)                                                                |
    | PUBLIC          |       | 3     | java.io.PrintStream.println(java.lang.String)                                                                |

Scenario: an instance method is invoked from its base
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When an instance method is invoked from its base'
    | Kind    | Local | Level | Signature                                                                                          |
    | WHEN    |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenAnInstanceMethodIsInvokedFromItsBase()  |
    | PUBLIC  | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Plant.bloom()                                                |
    | PUBLIC  | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Melothria.wither()                                           |
    | PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Melothria.rot()                                              |
    | PUBLIC  |       | 4     | java.io.PrintStream.println(java.lang.String)                                                      |

Scenario: a base method is called from a far off generations
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When a base method is called from a far off generations'
    | Kind            | Local | Level | Signature                                                                                             |
    | WHEN            |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenABaseMethodIsCalledFromFarOffGenerations() |
    | PACKAGE_PRIVATE | true  | 1     | io.github.augurk.javaanalyzer.cucumis.StubbornGherkin.cutVine()                                       |
    | PACKAGE_PRIVATE | true  | 2     | io.github.augurk.javaanalyzer.cucumis.PickyGherkin.cutVine()                                          |
    | PACKAGE_PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gherkin.cutVine()                                               |
    | PUBLIC          |       | 4     | java.io.PrintStream.println(java.lang.String)                                                         |
    | PUBLIC          |       | 3     | java.io.PrintStream.println(java.lang.String)                                                         |
    | PUBLIC          |       | 2     | java.io.PrintStream.print(java.lang.String)                                                           |

Scenario: this actually means that
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When this actually means that'
    | Kind    | Local | Level | Signature                                                                          |
    | WHEN    |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenThisActuallyMeansThat() |
    | PUBLIC  | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Plant.freezeAndThaw()                        |
    | PUBLIC  | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Melothria.wither()                           |
    | PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Melothria.rot()                              |
    | PUBLIC  |       | 4     | java.io.PrintStream.println(java.lang.String)                                      |
  
Scenario: an inherited instance method is invoked indirectly
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When an inherited instance method is invoked indirectly'
    | Kind            | Local | Level | Signature                                                                                                               |
    | WHEN            |       | 0     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.whenAnInheritedInstanceMethodIsInvokedIndirectly()               |
    | PRIVATE         | true  | 1     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.prepareAndCutVine(io.github.augurk.javaanalyzer.cucumis.Gherkin) |
    | PRIVATE         | true  | 2     | io.github.augurk.javaanalyzer.cucumis.InheritanceSteps.cutTheVine(io.github.augurk.javaanalyzer.cucumis.Gherkin)        |
    | PACKAGE_PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.PickyGherkin.cutVine()                                                            |
    | PACKAGE_PRIVATE | true  | 4     | io.github.augurk.javaanalyzer.cucumis.Gherkin.cutVine()                                                                 |
    | PUBLIC          |       | 5     | java.io.PrintStream.println(java.lang.String)                                                                           |
    | PUBLIC          |       | 4     | java.io.PrintStream.println(java.lang.String)                                                                           |
