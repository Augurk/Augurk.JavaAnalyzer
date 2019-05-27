# Augurk.JavaAnalyzer

[![Build Status](https://dev.azure.com/augurk/Augurk/_apis/build/status/Augurk.Augurk.JavaAnalyzer?branchName=master)](https://dev.azure.com/augurk/Augurk/_build/latest?definitionId=8&branchName=master)

Augurk.JavaAnalyzer is an extension to the Augurk ecosystem that will (eventually) allow users of Augurk to visualize the dependencies between their
features, even across their entire product portfolio. To do this, Augurk.JavaAnalyzer uses JavaParser to do static code analysis and tries to figure out
what is being called by the when steps of a feature. All that information is then accumulated and sent to Augurk for analysis.

## Alpha feature
At the time of writing the Augurk.JavaAnalyzer is not yet published to the Maven central repository. But can be used when compiled locally, 
by using the `mvn install` command. The Augurk.JavaAnalyzer contains the maven plugin that will preform the analysis as well as a simple annotations 
library that can help guide the analyzer to make the right choices.

## Setup
The Augurk.JavaAnalyzer can be added to the plugins section of a **pom.xml** file. Before adding the plugin make sure the
plugin is installed in the local `~/.m2/` folder by running the `mvn install` command form the Augurk.JavaAnalyzer project root.

### Setup build info in the POM
The snippet bellow contains a basic example of the plugin configuration.

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>io.github.augurk</groupId>
            <artifactId>augurk-maven-plugin</artifactId>
            <version>[PLUGIN VERSION]</version>
            <configuration>
                <augurkUrl>http://localhost:4071/</augurkUrl>
                <!-- Optionally, enable logging to console -->
                <reportToConsole>false</reportToConsole>
            </configuration>
        </plugin>
        ...
    </plugins>
</build>
```

## Usage
You can analyze a Java project by running this command:
```bash
mvn clean augurk:analyze
```

## Known issues
### Incomplete analysis results
When a var keyword is used in foreach loop, the analyzer is unable to determine the type of the variable. When this happens
the log wil show a message like:
> [WARNING] Unable to resolve suitable type for ...

### Compilation unit containing multiple types
Currently, the analysis logic can only handle compilation units containing one type. When a second type is added to a
compilation unit, only the type matching the filename will be found.
