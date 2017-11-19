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
uy.kohesive.cuarentena:cuarentena:1.0.0-BETA-01
```

Subprojects:

* `cuarentena-policy-painless` a set of generated `Cuarentena` policy rules from [Painless Scripting](https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting-painless.html) whitelists
* `cuarentena-policy-painless-kotlin` a set of `Cuarentena` policy rules to extend the Painless set for Kotlin stdlib, and safe lambda serialization

TODO:  

* security reviews for `Cuarentena`
