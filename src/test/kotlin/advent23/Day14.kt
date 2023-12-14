package advent23

import org.junit.jupiter.api.Test

class Day14 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(136, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(109466, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            val field = txt.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
            val width = field.first().length
            val height = field.size
            fun genCol(x: Int) = field.asSequence().map { it[x] }
            val loads = field.first().mapIndexed { col, _ ->

                var slot = 0
                var loads = 0
                for ((i, ch) in genCol(col).withIndex()) {
                    when (ch) {
                        '#' -> slot = i + 1
                        'O' -> {
                            loads += (height - slot)
                            slot++
                        }
                        else -> {}
                    }
                }
                loads
            }.sum()
            return loads
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

