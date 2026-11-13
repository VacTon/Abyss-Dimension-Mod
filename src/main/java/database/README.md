Inside `dependencies` block of `build.gradle`:
```groovy
// SQLite JDBC
//This states the dependency
implementation "org.xerial:sqlite-jdbc:3.51.1.0"

//And this is the JAR-in-JAR packaging
include "org.xerial:sqlite-jdbc:3.51.1.0"
```