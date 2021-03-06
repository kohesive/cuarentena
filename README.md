[![Kotlin](https://img.shields.io/badge/kotlin-1.2.21-blue.svg)](http://kotlinlang.org)  [![Maven Central](https://img.shields.io/maven-central/v/uy.kohesive.cuarentena/cuarentena.svg)](https://mvnrepository.com/artifact/uy.kohesive.cuarentena) [![CircleCI branch](https://img.shields.io/circleci/project/kohesive/cuarentena/master.svg)](https://circleci.com/gh/kohesive/cuarentena/tree/master) [![Issues](https://img.shields.io/github/issues/kohesive/cuarentena.svg)](https://github.com/kohesive/cuarentena/issues?q=is%3Aopen) [![DUB](https://img.shields.io/dub/l/vibe-d.svg)](https://github.com/kohesive/cuarentena/blob/master/LICENSE) [![Kotlin Slack](https://img.shields.io/badge/chat-kotlin%20slack%20%23kohesive-orange.svg)](http://kotlinslackin.herokuapp.com)


# Cuarentena

Cuarentena can be used to verify that a JVM class is safe in that it does not call
anything outside of a provided whitelist file.  This can be used for a plugin model
that must verify down to the method call level.

This includes static and instance class references, read/write of properties,
and static and instance method calls.  

Cuarentena comes with a provided whitelist based on the one developed for the 
Painless Scripting engine (found in Elasticsearch).  It is then extended to include
a few missing but commonly used safe references, and then also scans the Kotlin
runtime to create and extended whitelist for anything there that verifies against
the original Painless allowances.

Cuarentena use cases:

* verify a plugin only calls what you want it to call.
* verify a script is safe to execute
* create binary scripts that are safe to execute

### Gradle /Maven

Add add the following dependency:

```
uy.kohesive.cuarentena:cuarentena:1.0.0-BETA-08
```

Subprojects:

* `cuarentena-policy-painless` a set of generated `Cuarentena` policy rules from [Painless Scripting](https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting-painless.html) whitelists
* `cuarentena-policy-painless-kotlin` a set of `Cuarentena` policy rules to extend the Painless set for Kotlin stdlib, and safe lambda serialization

TODO:  

* security reviews for `Cuarentena`

## Special Thanks

![YourKit logo](https://www.yourkit.com/images/yklogo.png)

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/)
and [YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/),
innovative and intelligent tools for profiling Java and .NET applications.

