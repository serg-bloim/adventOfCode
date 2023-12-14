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

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.solve2(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(64, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(94585, actual)
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

        fun solve2(txt: String): Any {
            val field = txt.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
            val roundRocks = field.map { it.toCharArray() }.toTypedArray()
            val northView = FieldView(roundRocks, 0)
            val westView = FieldView(roundRocks, 1)
            val southView = FieldView(roundRocks, 2)
            val eastView = FieldView(roundRocks, 3)
            val cache = mutableMapOf<Pair<Any, Int>, Int>()
            val cycle = generateSequence {
                tilt(northView)
                tilt(westView)
                tilt(southView)
                tilt(eastView)
                roundRocks
            }
                .mapIndexed { i, _ ->
                    val key = createCacheKey(roundRocks)
                    val load = calcLoad(roundRocks)
                    val oldInd: Int? = cache.put(Pair(key, load), i)
                    Triple(oldInd, i, load)
                }
                .filter { (oldIndex, _, _) -> oldIndex != null }
                .map { Triple(it.first!!, it.second, it.third) }
                .first()

            val cycleLength = cycle.second - cycle.first
            val pos = (1000000000 - cycle.first - 1) % cycleLength + cycle.first
            val load = cache.asSequence()
                .filter { (key, ind) -> ind == pos }
                .map { it.key.second }
                .first()
            return load
        }

        private fun createCacheKey(field: Array<CharArray>): Any {
            return field.map { it.concatToString() }.joinToString("")
        }

        private fun calcLoad(field: Array<CharArray>): Int {
            val width = field.first().indices
            val height = field.size
            var load = 0
            for (y in field.indices) {
                for (x in width) {
                    if (field[y][x] == 'O') {
                        load += height - y
                    }
                }
            }
            return load
        }

        fun tilt(view: FieldView) {
            for (x in view.xIndices) {
                var slot = 0
                for (y in view.yIndices) {
                    val ch = view.get(x, y)
                    when (ch) {
                        '#' -> slot = y + 1
                        'O' -> {
                            if (slot != y) {
                                view.set(x, slot, 'O')
                                view.set(x, y, '.')
                            }
                            slot++
                        }
                    }
                }
            }
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

    class FieldView(val origin: Array<CharArray>, val turn: Int) {
        val flipX = turn in listOf(1, 2)
        val flipY = turn in listOf(2, 3)
        val switch = turn in listOf(1, 3)
        val xLast = (if (switch) origin.size else origin.first().size) - 1
        val yLast = (if (!switch) origin.size else origin.first().size) - 1
        val xIndices = IntRange(0, xLast)
        val yIndices = IntRange(0, yLast)
        fun get(x: Int, y: Int): Char {
            var x = if (flipX) xLast - x else x
            var y = if (flipY) yLast - y else y
            if (switch) {
                val temp = x
                x = y
                y = temp
            }
            return origin[y][x]
        }

        fun set(x: Int, y: Int, ch: Char) {
            var x = if (flipX) xLast - x else x
            var y = if (flipY) yLast - y else y
            if (switch) {
                val temp = x
                x = y
                y = temp
            }
            origin[y][x] = ch
        }

    }

    class Tests {
        @Test
        fun test_View() {
            val field = load_test().lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
            val width = field.first().length
            val height = field.size
            val roundRocks = field.map { it.toCharArray() }.toTypedArray()
            val north = FieldView(roundRocks, 1)
            Solution.tilt(north)
            roundRocks.forEach { dbg.println(it.joinToString("")) }
        }
    }
}


