package advent23

import org.junit.jupiter.api.Test
import kotlin.math.max

class Day21 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test(), 6)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(16, actual)
        }

        @Test
        fun testSmaller() {
            val actual = Solution.solve(load_test(), 1)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(2, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod(), 64)
            println("Result: $actual")
            assertEquals(3809, actual)
        }

    }

    object Solution {
        fun solve(txt: String, steps: Int = 64): Any {
            var startX = -1
            var startY = -1
            val map = txt.lineSequence()
                .mapIndexed { y, line ->
                    line.mapIndexed { x, ch ->
                        when (ch) {
                            '#' -> Int.MIN_VALUE
                            'S' -> {
                                startX = x
                                startY = y
                                Int.MAX_VALUE
                            }

                            else -> Int.MAX_VALUE
                        }
                    }.toMutableList()
                }.toList()
            colorMap(map, Coords(startX, startY), 0, steps)
            val even = steps % 2
            return map.sumOf { it.count { it >= 0 && it < Int.MAX_VALUE && it % 2 == even } }
        }

        private fun colorMap(map: List<MutableList<Int>>, pos: Coords, dist: Int, maxDist: Int) {

            val v = map[pos.y][pos.x]
            if (dist < v && dist <= maxDist) {
                map[pos.y][pos.x] = dist
                pos.neighbors().forEach { colorMap(map, it, dist + 1, maxDist) }
            }
        }
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(prefix: Any = ""): String {
            return Resources().loadString("${res_prefix}_test$prefix.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }
    }
}

