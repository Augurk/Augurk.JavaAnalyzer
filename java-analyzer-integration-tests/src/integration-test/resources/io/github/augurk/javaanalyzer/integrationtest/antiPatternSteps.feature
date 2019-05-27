Feature: Analyzing Anti-patterns
  This feature describes various anti-patterns that might be used in the wild which we do want to support, but do not recommend to use.

Scenario: the automated code cannot be invoked directly
It might happen that the automated code cannot be invoked directly from a when step due to complexity. Therefore we provide a means to annotate
the actual method being tested by a When step.
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When the automated code cannot be invoked directly'
    | Kind    | Local | Level | Signature                                                                                            | AutomationTargets                                         |
    | WHEN    |       | 0     | io.github.augurk.javaanalyzer.cucumis.AntiPatternSteps.whenTheAutomatedCodeCannotBeInvokedDirectly() | io.github.augurk.javaanalyzer.cucumis.Melothria.wither(), |
    | PUBLIC  | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Plant.freezeAndThaw()                                          |                                                           |
    | PUBLIC  | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Melothria.wither()                                             |                                                           |
    | PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Melothria.rot()                                                |                                                           |
    | PUBLIC  |       | 4     | java.io.PrintStream.println(java.lang.String)                                                        |                                                           |

Scenario: only the top level overload should match
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When only the top level overload should match'
    | Kind            | Local | Level | Signature                                                                                                         | AutomationTargets                                                                                  |
    | WHEN            |       | 0     | io.github.augurk.javaanalyzer.cucumis.AntiPatternSteps.whenOnlyTheTopLevelOverloadShouldMatch()                   | io.github.augurk.javaanalyzer.cucumis.Gardener.water(io.github.augurk.javaanalyzer.cucumis.Plant), |
    | PUBLIC          | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Plant.water(io.github.augurk.javaanalyzer.cucumis.Gardener)                 |                                                                                                    |
    | PACKAGE_PRIVATE | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(io.github.augurk.javaanalyzer.cucumis.Plant)                 |                                                                                                    |
    | PACKAGE_PRIVATE | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(java.util.List<io.github.augurk.javaanalyzer.cucumis.Plant>) |                                                                                                    |
    | PUBLIC          |       | 4     | java.io.PrintStream.printf(java.lang.String, java.lang.Object...)                                                 |                                                                                                    |

Scenario: only the lowest level overload should match
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When only the lowest level overload should match'
    | Kind             | Local | Level | Signature                                                                                                         | AutomationTargets                                                                                                  |
    | WHEN             |       | 0     | io.github.augurk.javaanalyzer.cucumis.AntiPatternSteps.WhenOnlyTheLowestLevelOverloadShouldMatch()                | io.github.augurk.javaanalyzer.cucumis.Gardener.water(java.util.List<io.github.augurk.javaanalyzer.cucumis.Plant>), |
    | PUBLIC           | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Plant.water(io.github.augurk.javaanalyzer.cucumis.Gardener)                 |                                                                                                                    |
    | PACKAGE_PRIVATE  | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(io.github.augurk.javaanalyzer.cucumis.Plant)                 |                                                                                                                    |
    | PACKAGE_PRIVATE  | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(java.util.List<io.github.augurk.javaanalyzer.cucumis.Plant>) |                                                                                                                    |
    | PUBLIC           |       | 4     | java.io.PrintStream.printf(java.lang.String, java.lang.Object...)                                                 |                                                                                                                    |

Scenario: all overloads should match
  Given 'Cucumis' contains feature files
  When an analysis is run
  Then the resulting report contains 'When all overloads should match'
    | Kind             | Local | Level | Signature                                                                                                         | AutomationTargets                                                                                                                                                                                                    |
    | WHEN             |       | 0     | io.github.augurk.javaanalyzer.cucumis.AntiPatternSteps.WhenAllOverloadsShouldMatch()                              | io.github.augurk.javaanalyzer.cucumis.Gardener.water(io.github.augurk.javaanalyzer.cucumis.Plant),io.github.augurk.javaanalyzer.cucumis.Gardener.water(java.util.List<io.github.augurk.javaanalyzer.cucumis.Plant>), |
    | PUBLIC           | true  | 1     | io.github.augurk.javaanalyzer.cucumis.Plant.water(io.github.augurk.javaanalyzer.cucumis.Gardener)                 |                                                                                                                                                                                                                      |
    | PACKAGE_PRIVATE  | true  | 2     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(io.github.augurk.javaanalyzer.cucumis.Plant)                 |                                                                                                                                                                                                                      |
    | PACKAGE_PRIVATE  | true  | 3     | io.github.augurk.javaanalyzer.cucumis.Gardener.water(java.util.List<io.github.augurk.javaanalyzer.cucumis.Plant>) |                                                                                                                                                                                                                      |
    | PUBLIC           |       | 4     | java.io.PrintStream.printf(java.lang.String, java.lang.Object...)                                                 |                                                                                                                                                                                                                      |
