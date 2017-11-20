package uy.kohesive.cuarentena.generator.lambda

import uy.kohesive.cuarentena.ClassAllowanceDetector
import uy.kohesive.cuarentena.NamedClassBytes
import uy.kohesive.cuarentena.policy.PolicyAllowance

object LambdaAllowancesGenerator {

    fun generateAllowancesByExampleFromLambda(lambda: () -> Unit): List<PolicyAllowance.ClassLevel> {
        val classBytes = lambdaToBytes(lambda)
        val goodThings = ClassAllowanceDetector.scanClassByteCodeForDesiredAllowances(listOf(classBytes))
        return goodThings.allowances
    }


    private fun lambdaToBytes(lambda: () -> Unit): NamedClassBytes {
        val serClass = lambda.javaClass
        val className = serClass.name

        return loadClassAsBytes(className, serClass.classLoader)
    }

    private fun loadClassAsBytes(className: String, loader: ClassLoader): NamedClassBytes {
        return NamedClassBytes(className,
                loader.getResourceAsStream(className.replace('.', '/') + ".class").use { it.readBytes() })
    }
}