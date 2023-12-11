package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge6 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(70, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        val res = txt.lineSequence()
            .map { it.trim() }
            .filterNot { it.isEmpty() }
            .chunked(3)
            .map { chunk ->
                chunk.map { it.toSet() }
                    .reduce { a, b -> a.intersect(b) }
                    .apply { assertEquals(1, size) }
                    .first()
            }
            .map { ch -> ch.code - if (ch.isUpperCase()) 38 else 96 }
            .sum()
        return res
    }

    private fun load_test(): String {
        return Resources().loadString("ch5_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch5_prod.txt")
    }
}
