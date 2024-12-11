package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.dbg
import utils.result
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.test.assertEquals

class Day11 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(55312, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(197157, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val stones = parseInput(txt)
            return stones.map { stone ->
                countStones(stone, 25)
            }.sum()
        }
    }

    private fun countStones(initStone: Long, transforms: Int): Int {
        if (transforms == 0) {
            dbg.print("$initStone ")
            return 1
        }
        if (initStone == 0L) return countStones(1, transforms - 1)
        val str = initStone.toString()
        if (str.length % 2 == 0) {
            val stone1 = str.subSequence(0, str.length / 2).toString().toLong()
            val stone2 = str.subSequence(str.length / 2, str.length).toString().toLong()
            return countStones(stone1, transforms - 1) + countStones(stone2, transforms - 1)
        }
        return countStones(initStone * 2024, transforms - 1)
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Any {
            return 11111111
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