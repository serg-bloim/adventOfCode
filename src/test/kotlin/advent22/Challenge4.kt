package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge4 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(12, actual)
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
            .map {
                when (it) {
                    "A X" -> 0 + 3
                    "A Y" -> 3 + 1
                    "A Z" -> 6 + 2

                    "B X" -> 0 + 1
                    "B Y" -> 3 + 2
                    "B Z" -> 6 + 3

                    "C X" -> 0 + 2
                    "C Y" -> 3 + 3
                    "C Z" -> 6 + 1
                    else -> throw Exception()
                }
            }
            .sum()
        return res
    }

    private fun load_test(): String {
        return Resources().loadString("ch3_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch3_prod.txt")
    }
}
