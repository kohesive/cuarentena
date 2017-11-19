package uy.kohesive.cuarentena

import uy.kohesive.cuarentena.ClassAllowanceDetector.scanClassByteCodeForDesiredAllowances
import uy.kohesive.cuarentena.KotlinPolicies.painlessKotlinBootstrapPolicy
import uy.kohesive.cuarentena.generator.jarfile.KotlinStdlibPolicyGenerator
import uy.kohesive.cuarentena.policy.ALL_CLASS_ACCESS_TYPES
import uy.kohesive.cuarentena.policy.AccessTypes
import uy.kohesive.cuarentena.policy.CuarentenaPolicyLoader
import uy.kohesive.cuarentena.policy.PolicyAllowance

// TODO: this is from first round of exploring and work, needs overhauled to take those learnings into account, simplify
//       and develop the formal Kotlin whitelist, and then the lambda serializeable tiny whitelist.  More checking, more
//       eyes, continue to be more secure.

// TODO: replace all class, package, ... lists with Cuarentena policies

class Cuarentena(val policies: Set<String> = painlessPlusKotlinBootstrapPolicy) {

    companion object {
        private val painlessBaseJavaPolicy: Set<String> by lazy { CuarentenaPolicyLoader.loadPolicy("painless-base-java") }

        internal val painlessPlusKotlinBootstrapPolicy = painlessBaseJavaPolicy + painlessKotlinBootstrapPolicy
        internal fun createKotlinBootstrapCuarentena() = Cuarentena(painlessPlusKotlinBootstrapPolicy)

        val painlessPlusKotlinPolicy: Set<String> by lazy { painlessPlusKotlinBootstrapPolicy + KotlinStdlibPolicyGenerator().generatePolicy() }
        fun createKotlinCuarentena() = Cuarentena(painlessPlusKotlinPolicy)
    }

    fun verifyClassAgainstPoliciesPerClass(newClasses: List<NamedClassBytes>, additionalPolicies: Set<String> = emptySet()): List<VerifyResultsPerClass> {
        val newClassNames = newClasses.map { it.className }
        val filteredClasses = filterKnownClasses(newClasses, additionalPolicies)

        return filteredClasses.map { filteredClass ->
            val filteredClassDesiredAllowances = scanClassByteCodeForDesiredAllowances(listOf(filteredClass))

            val violations = filteredClassDesiredAllowances.allowances
                    .filterNot {
                        // new classes can call themselves, so these can't be violations
                        it.fqnTarget in newClassNames
                    }.filterNot { it.assertAllowance(additionalPolicies) }

            val violationStrings = violations.map { it.resultingViolations(additionalPolicies) }.flatten().toSet()

            VerifyResultsPerClass(
                    violatingClass = filteredClass,
                    scanResults = filteredClassDesiredAllowances,
                    violations = violationStrings
            )
        }.filter {
            it.violations.isNotEmpty()
        }
    }

    fun verifyClassAgainstPolicies(newClasses: List<NamedClassBytes>, additionalPolicies: Set<String> = emptySet()): VerifyResults {
        val filteredClasses = filterKnownClasses(newClasses, additionalPolicies)
        val classScanResults = scanClassByteCodeForDesiredAllowances(filteredClasses)

        val filteredClassNames = filteredClasses.map { it.className }.toSet()

        val violations = classScanResults.allowances
                .filterNot {
                    // new classes can call themselves, so these can't be violations
                    it.fqnTarget in filteredClassNames
                }.filterNot { it.assertAllowance(additionalPolicies) }

        val violationStrings = violations.map { it.resultingViolations(additionalPolicies) }.flatten().toSet()

        return VerifyResults(classScanResults, violationStrings, filteredClasses)
    }

    fun verifyClassNamesAgainstPolicies(classesToCheck: List<String>, additionalPolicies: Set<String> = emptySet()): VerifyNameResults {
        val violations = classesToCheck.map { PolicyAllowance.ClassLevel.ClassAccess(it, setOf(AccessTypes.ref_Class_Instance)) }
                .filterNot { it.assertAllowance(additionalPolicies) }
        val violationStrings = violations.map { it.resultingViolations(additionalPolicies) }.flatten().toSet()
        return VerifyNameResults(violationStrings)
    }

    data class VerifyResultsPerClass(
            val violatingClass: NamedClassBytes,
            val scanResults: ClassAllowanceDetector.ScanState,
            val violations: Set<String>
    )

    data class VerifyResults(val scanResults: ClassAllowanceDetector.ScanState, val violations: Set<String>, val filteredClasses: List<NamedClassBytes>) {
        val failed: Boolean = violations.isNotEmpty()

        fun violationsAsString() = violations.joinToString()
    }

    data class VerifyNameResults(val violations: Set<String>) {
        val failed: Boolean = violations.isNotEmpty()

        fun violationsAsString() = violations.joinToString()
    }

    fun PolicyAllowance.assertAllowance(additionalPolicies: Set<String> = emptySet()): Boolean
            = this.asCheckStrings(true).any { it in policies || it in additionalPolicies }

    fun PolicyAllowance.resultingViolations(additionalPolicies: Set<String> = emptySet()): List<String>
            = this.asCheckStrings(false).filterNot { it in policies || it in additionalPolicies }

    /*
        Remove known classes from the policy from the captured class bytes, because they are not expected to be shipped
     */
    fun filterKnownClasses(newClasses: List<NamedClassBytes>, additionalPolicies: Set<String>): List<NamedClassBytes> {
        return newClasses.filterNot { newClass ->
            PolicyAllowance.ClassLevel.ClassAccess(newClass.className, ALL_CLASS_ACCESS_TYPES).assertAllowance(additionalPolicies)
        }
    }
}
