# Eclipse Ignore Helper Maven Plugin

This project is a Apache Maven plugin, which can be used to make the Eclipse IDE ignore some warnings on (generated) code.
Using the Eclipse IDE buildin functionality of ignore unwanted warnings on some folders in the classpath, this plugin adds the desired folders to the .classpath file.

## Quality status

Displays the quality state of the `main`-branch from SonarQube.io:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ronnypolley_ecliseignorehelper&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ronnypolley_ecliseignorehelper)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ronnypolley_ecliseignorehelper&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ronnypolley_ecliseignorehelper)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ronnypolley_ecliseignorehelper&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ronnypolley_ecliseignorehelper)

## Usage

Simply add the plugin to our `pom.xml`, like
```
<plugin>
    <groupId>de.random-words</groupId>
	<artifactId>eclipse-ignore-helper-maven-plugin</artifactId>
	<version>1.0.5-SNAPSHOT</version>
    <executions>
        <execution>
            <id>ignore</id>
            <phase>process-resources</phase>
            <goals>
                <goal>ignorePaths</goal>
            </goals>
            <configuration>
                <ignorePaths>
                    <ignorePath>relative_path_1</ignorePath>
                    <ignorePath>relative_path_n</ignorePath>
                </ignorePaths>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This would add the attribute tag `ignore_optional_problems` to the path specified.

```
<classpathentry kind="src" output="target/test-classes" path="relative_path_1">
    <attributes>
        <attribute name="optional" value="true"/>
        <attribute name="maven.pomderived" value="true"/>
        <attribute name="ignore_optional_problems" value="true">
    </attributes>
</classpathentry>
```

## Build Eclipse-Ignore-Helper

As this is a Apache Maven project, it is easy as 
```
mvn verify
```
to build the project.
