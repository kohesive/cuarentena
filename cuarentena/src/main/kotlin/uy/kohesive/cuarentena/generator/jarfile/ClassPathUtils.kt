package uy.kohesive.cuarentena.generator.jarfile

import java.io.File
import kotlin.reflect.KClass

internal object ClassPathUtils {
    fun findKotlinStdLibOrEmbeddedCompilerJars(): List<File> {
        val stdlibCheck = listOf(Pair::class.containingClasspath(""".*\/kotlin-stdlib.*\.jar""".toRegex())).filterNotNull()
        val embdCompilerCheck = listOf(Pair::class.containingClasspath(""".*\/kotlin-compiler-embeddable.*\.jar""".toRegex())).filterNotNull()
        return (stdlibCheck.takeUnless { it.isEmpty() } ?: embdCompilerCheck).assertNotEmpty("Cannot find kotlin stdlib classpath, which is required")
    }


    private fun <T : Any> KClass<T>.containingClasspath(filterJarName: Regex = ".*".toRegex()): File? {
        val clp = "${qualifiedName?.replace('.', '/')}.class"
        val baseList = Thread.currentThread().contextClassLoader.getResources(clp)
                ?.toList()
                ?.map { it.toString() }
        return baseList
                ?.map { url ->
                    zipOrJarUrlToBaseFile(url) ?: qualifiedName?.let { classFilenameToBaseDir(url, clp) }
                            ?: throw IllegalStateException("Expecting a local classpath when searching for class: ${qualifiedName}")
                }
                ?.find {
                    filterJarName.matches(it)
                }
                ?.let { File(it) }
    }


    fun <T : Any> List<T>.assertNotEmpty(error: String): List<T> {
        if (this.isEmpty()) throw IllegalStateException(error)
        return this
    }

    private val zipOrJarRegex = """(?:zip:|jar:file:)(.*)!\/(?:.*)""".toRegex()
    private val filePathRegex = """(?:file:)(.*)""".toRegex()

    private fun zipOrJarUrlToBaseFile(url: String): String? {
        return zipOrJarRegex.find(url)?.let { it.groupValues[1] }
    }

    private fun classFilenameToBaseDir(url: String, resource: String): String? {
        return filePathRegex.find(url)?.let { it.groupValues[1].removeSuffix(resource) }
    }

}
