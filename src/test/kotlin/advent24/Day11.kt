package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.test.assertEquals

class Day11 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), 25)
            assertEquals(55312, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 25)
            result.println("Result: $actual")
            assertEquals(197157, actual)
            println("Result: $actual")
        }
    }

    fun solve(txt: String, transforms: Int): Any {
        val stones = parseInput(txt)
        val cache = mutableMapOf<Pair<Long, Int>, Long>()
        return stones.map { stone ->
            countStones(stone, transforms, cache)
        }.sum()
    }

    private fun countStones(initStone: Long, transforms: Int, cache: MutableMap<Pair<Long, Int>, Long>): Long {
        if (transforms == 0) return 1
        cache[Pair(initStone, transforms)]?.let { return it }
        val result = if (initStone == 0L) {
            countStones(1, transforms - 1, cache)
        } else {
            val str = initStone.toString()
            if (str.length % 2 == 0) {
                val stone1 = str.subSequence(0, str.length / 2).toString().toLong()
                val stone2 = str.subSequence(str.length / 2, str.length).toString().toLong()
                countStones(stone1, transforms - 1, cache) + countStones(stone2, transforms - 1, cache)
            } else {
                countStones(initStone * 2024, transforms - 1, cache)
            }
        }
        cache[Pair(initStone, transforms)] = result
        return result
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), 25)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(55312, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 75)
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(234430066982597, actual)
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

        fun parseInput(txt: String): List<Long> {
            val data = txt.split(' ').map { it.toLong() }
            return data
        }
    }
}