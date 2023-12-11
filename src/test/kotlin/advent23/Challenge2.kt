package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge2 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(281, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        return txt.lineSequence().filterNot { it.isNullOrEmpty() }
            .map {
                val digits = parseDigits(it).toList()
                val first = digits.first()
                val last = digits.last()
                first * 10 + last
            }.sum()
    }

    private fun parseDigits(line: String): Sequence<Int> {
        val mapping = buildMap<String, Int> {
            for (i in 0..9) {
                put(i.toString(), i)
            }
            put("zero", 0)
            put("one", 1)
            put("two", 2)
            put("three", 3)
            put("four", 4)
            put("five", 5)
            put("six", 6)
            put("seven", 7)
            put("eight", 8)
            put("nine", 9)
        }
        return line.indices.asSequence()
            .map { start ->
                mapping.asSequence()
                    .filter { line.startsWith(it.key, start, false) }
                    .map { it.value }.firstOrNull()
            }
            .filterNotNull()
    }

    private fun load_test(): String {
        return Resources().loadString("ch2_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch1_prod.txt")
    }
}

