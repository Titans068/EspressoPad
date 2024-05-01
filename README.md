# Espresso Pad

Espresso Pad is a simple editor to run snippets of Java code. It does not require classes, or methods but they are supported. It utilizes the JShell APIs under the hood.

## Features
- Syntax highlighting
- Basic code completion
- Can run from entire classes to methods or statements
- Code formatting thanks to [jastyle](https://github.com/AbrarSyed/jastyle)
- Dependency management thanks to [maven-archeologist](https://github.com/square/maven-archeologist)
- Default imports management. Sample default imports include `java.util.stream.*`, `java.util.*`, `java.io.*`
- Basic file management e.g. rename, delete, show in folder, refresh.
- Supports user input
- Brace and quote matching with completion
- Find/replace

# Contributing
Contributions are welcome from the community.
Before submitting a pull request, please make sure to run the tests and ensure that your changes do not introduce any regressions.

# License
Espresso Pad is released under the MIT License. See the [LICENSE](https://www.mit.edu/~amini/LICENSE.md) file for details.