package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge5 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(157, actual)
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
                val first = it.subSequence(0, it.length / 2)
                val last = it.subSequence(it.length / 2, it.length)
                val ch = first.toSet().intersect(last.toSet()).apply { assertEquals(1, size, it) }.first()
                ch
            }
//            .onEach { println(it) }
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
