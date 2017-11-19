package uy.kohesive.cuarentena

import uy.kohesive.cuarentena.generator.jarfile.KotlinStdlibPolicyGenerator
import uy.kohesive.cuarentena.generator.lambda.LambdaAllowancesGenerator
import uy.kohesive.cuarentena.policy.toPolicy


// Not really a test
// TODO: do a real test
fun main(args: Array<String>) {
    val verifyStdlibClasses = KotlinStdlibPolicyGenerator().verifyClasses()

    println("Verified classes:")
    verifyStdlibClasses.verifiedClasses.sorted().forEach { println(" + $it") }
    println()

    println("Black-listed classes:")
    verifyStdlibClasses.perClassViolations.sortedBy { it.violatingClass.className }.forEach {
        println(" - ${it.violatingClass.className}:")
        it.violations.forEach { println("   - $it") }
    }

    println()
    println("FINAL CLASSES ALLOWED")
    KotlinStdlibPolicyGenerator().determineValidClasses().sorted().forEach {
        println("  $it")
    }

    println()
    println("Allowances by example from Lambda")
    val outsidePrimitive = 22
    val allowances = LambdaAllowancesGenerator.generateAllowancesByExampleFromLambda {
        val x = 10 + outsidePrimitive

        emptyList<String>()
        listOf("a")
        listOf("a", "b")
        listOf(1, 2, 3)

        emptySet<String>()
        setOf("a")
        setOf("a", "b")
        setOf(1, 2, 3)

        emptySequence<String>()
        sequenceOf("a")
        sequenceOf("a", "b")
        sequenceOf(1, 2, 3)

        emptyArray<String>()
        arrayOf("a")
        arrayOf(1)
        arrayOf(1L)
        arrayOf(1.0)
        arrayOf(true)
        arrayOf(1, 2, 3)
        arrayOf(1L, 2L, 3L)
        arrayOf(1.0, 2.0, 3.0)
        arrayOf(true, false)


        val lists = listOf("a") + listOf("b") + listOf("c", "d", "e") + listOf("z")

        val s = "stringy $x is next to these $lists"
        val s2 = """$s what $s"""

        val r = """[\w\d]+""".toRegex()
        val p = """[\w\d]+""".toPattern()

        p.toRegex().matches("a99")
        r.matchEntire("asd")?.apply {
            this.groupValues
            this.groups
            this.destructured
            this.value
        }

        "string".takeIf { true }.also { }.takeUnless { false }

        val snullable: String? = ""
        if (snullable != null) {
        } else {
        }
        val snonullable = snullable ?: ""

        true.and(true).or(true).xor(true).not()

    }
    allowances.toPolicy().forEach(::println)
}
