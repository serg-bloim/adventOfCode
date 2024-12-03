package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.test.assertEquals

class Day3 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(161, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(163931492, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val mulOperationRegex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
            return mulOperationRegex.findAll(txt)
                .map { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
                .sum()
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(48, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(76911921, actual)
        }

        fun solve(txt: String): Int {
            val anyOpRegex = """mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\)""".toRegex()
            var enabled = true
            var sum = 0
            for (op in anyOpRegex.findAll(txt)) {
                when (op.value) {
                    "do()" -> enabled = true
                    "don't()" -> enabled = false
                    else -> if (enabled) {
                        sum += (op.groupValues[1].toInt() * op.groupValues[2].toInt())
                    }
                }
            }
            return sum
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