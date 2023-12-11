package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge2 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(45000, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        return txt.lineSequence().chunked { it.isEmpty() }
            .map { it.map { it.toInt() }.sum() }.sortedDescending().take(3).sum()
    }

    private fun load_test(): String {
        return Resources().loadString("ch1_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch1_prod.txt")
    }
}
