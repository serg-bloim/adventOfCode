package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge7 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(2, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun IntRange.contains(other: IntRange): Boolean = first <= other.first && endInclusive >= other.endInclusive
    fun solve(txt: String): Int {
        val rangeRe = Regex("""(\d+)-(\d+)""")
        val res = txt.lineSequence()
            .map { it.trim() }
            .filterNot { it.isEmpty() }
            .filter {
                val (range1, range2) = rangeRe.findAll(it)
                    .map {
                        val a = it.groupValues[1].toInt()
                        val b = it.groupValues[2].toInt()
                        IntRange(a, b)
                    }.toList()
                range1.contains(range2) || range2.contains(range1)
            }
            .count()
        return res
    }

    private fun load_test(): String {
        return Resources().loadString("ch7_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch7_prod.txt")
    }
}
