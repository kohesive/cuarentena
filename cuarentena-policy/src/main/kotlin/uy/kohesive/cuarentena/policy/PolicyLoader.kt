package uy.kohesive.cuarentena.policy

import java.io.InputStream
import java.util.*

object CuarentenaPolicyLoader {
    val validShortName = """^[\w_\-\d]+$""".toRegex()

    fun String.ensurePrefix(prefix: Char): String {
        return if (this.startsWith(prefix)) this else "$prefix$this"
    }

    fun loadPolicy(shortName: String, classLoader: ClassLoader = javaClass.classLoader): Set<String> {
        if (!validShortName.matches(shortName)) throw IllegalArgumentException("Short name is not valid [a..z, A..Z, 0..9, _, -]")
        val propertyResource = "META-INF/cuarentena/$shortName.properties"
        val propInput = classLoader.getResourceAsStream(propertyResource)
                ?: throw IllegalStateException("Descriptor for $shortName was not found in classpath:  $propertyResource")

        val properties = Properties().apply { load(propInput) }
        val policyResource = properties.getProperty("resource") ?: throw IllegalStateException("Descriptor file $propertyResource does not contain `resource` property")
        val policyInput =  classLoader.getResourceAsStream(policyResource)
                ?: throw IllegalStateException("Policy file $shortName was not found in expected classpath location:  $policyResource")
        return policyInput.bufferedReader().use { stream ->
            stream.lineSequence().toSet()
        }
    }
}