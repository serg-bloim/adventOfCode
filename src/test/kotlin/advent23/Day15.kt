package advent23

import org.junit.jupiter.api.Test

class Day15 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(1320, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(498538, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            val res = txt.splitToSequence(',')
                .map { hash(it) }
                .sum()
            return res
        }

        private fun hash(str: String): Int {
            val res = str.asSequence()
                .map { it.code }
                .fold(0) { current, new -> ((current + new) * 17) % 256 }
            return res
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

