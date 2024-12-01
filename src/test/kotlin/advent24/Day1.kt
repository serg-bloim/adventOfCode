package advent24

import utils.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

class Day1 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(11, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(1530215, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val (listA, listB) = parseInput(txt)
            listA.sort()
            listB.sort()
            val distance = listA.zip(listB) { a, b -> (a - b).absoluteValue }.sum()
            return distance
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
        fun parseInput(txt: String): Pair<IntArray, IntArray> {
            val pairs = txt.lineSequence().map { line ->
                line.split("""\s+""".toRegex()).map { it.toInt() }
            }.toList()
            val listA = pairs.map { it[0] }.toIntArray()
            val listB = pairs.map { it[1] }.toIntArray()
            return Pair(listA, listB)
        }
    }
}