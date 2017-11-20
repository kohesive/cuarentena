package uy.kohesive.cuarentena.generator.jarfile

import uy.kohesive.cuarentena.Cuarentena


class KotlinStdlibPolicyGenerator(classLoader: ClassLoader = KotlinStdlibPolicyGenerator::class.java.classLoader) : JarAllowancesGenerator(
        jarFiles = ClassPathUtils.findKotlinStdLibOrEmbeddedCompilerJars(classLoader).map { it.path },
        preFilterPackageWhiteList = WhiteListedPrefixes,
        postFilterPackageWhiteList = WhiteListedPrefixes,
        postFilterClassBlackList = BlackListedClasses,
        postFilterPackageBlackList = BlackListedPackages,
        scanMode = ScanMode.SAFE,
        verifier = Cuarentena(Cuarentena.painlessPlusKotlinBootstrapPolicy),
        useClassLoader = classLoader
) {

    companion object {
        private val BlackListedPackages = listOf(
                "kotlin.io",
                "kotlin.concurrent",
                "kotlin.coroutines"
        )

        private val BlackListedClasses = emptySet<String>()

        private val WhiteListedPrefixes = listOf(
                "kotlin"
        )
    }

}

