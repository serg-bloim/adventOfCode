package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge3 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(15, actual)
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
                    "A X" -> 3 + 1
                    "A Y" -> 6 + 2
                    "A Z" -> 0 + 3

                    "B X" -> 0 + 1
                    "B Y" -> 3 + 2
                    "B Z" -> 6 + 3

                    "C X" -> 6 + 1
                    "C Y" -> 0 + 2
                    "C Z" -> 3 + 3
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
