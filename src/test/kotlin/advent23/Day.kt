package advent23

import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.createFile

class Day {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(12345, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }
    object Solution{
        fun solve(txt: String): Any {
            val base = Path(System.getProperty("user.dir"))
            val className = this::class.java.enclosingClass.simpleName
            val dir = base.resolve("src/test/resources").resolve(this::class.java.packageName).resolve("input")
            listOf("test", "prod")
                .map { "${className.lowercase()}_$it.txt" }
                .map { dir.resolve(it) }
                .forEach {
                    try {
                        it.createFile()
                        println("Created $it")
                    } catch (e: Exception) {
                    }
                }
            return 325354
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
    }
}

