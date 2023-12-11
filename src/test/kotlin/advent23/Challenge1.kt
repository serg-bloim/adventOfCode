package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge1 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(142, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        return txt.lineSequence().filterNot { it.isNullOrEmpty() }
            .map {
                val digits = it.filter { it.isDigit() }
                val first = digits.first()
                val last = digits.last()
//            println("$first$last")
                first.digitToInt() * 10 + last.digitToInt()
            }.sum()
    }

    private fun load_test(): String {
        return Resources().loadString("ch1_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch1_prod.txt")
    }
}
