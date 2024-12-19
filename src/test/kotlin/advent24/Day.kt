package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.test.assertEquals

class Day {
    @Nested
    inner class Task1 {

        @Test
        fun generateInputFiles() {
            val base = Path(System.getProperty("user.dir"))
            val className = this::class.java.enclosingClass.simpleName
            val dir = base.resolve("src/test/resources").resolve(this::class.java.packageName).resolve("input")
            listOf("test", "prod")
                .map { "${className.lowercase()}_$it.txt" }
                .map { dir.resolve(it) }
                .forEach {
                    try {
                        it.createFile()
                        println("Created file://$it")
                        Runtime.getRuntime().exec("git add $it")
                    } catch (e: Exception) {
                        println("Failed to create file://$it")
                    }
                }
        }

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(555555555, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(555555555, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val data = parseInput(txt)
            return 325354
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Any {
            return 11111111
        }
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): List<List<Int>> {
            val data = txt.lineSequence().map { line ->
                line.split("""\s+""".toRegex()).map { it.toInt() }
            }.toList()
            return data
        }
    }
}