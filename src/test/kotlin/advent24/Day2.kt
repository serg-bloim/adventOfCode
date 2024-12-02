package advent24

import utils.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

class Day2 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(2, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(564, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val reports = parseInput(txt)
            val safe = reports.count { rep ->
                val increasing = rep[1] > rep[0]
                rep.zipWithNext()
                    .all { pair ->
                        val dist = (pair.first - pair.second).absoluteValue in 1..3
                        val dir = pair.second > pair.first == increasing
                        dist && dir
                    }
            }
            return safe
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(31, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(26800609, actual)
        }

        fun solve(txt: String): Int {
            return 42
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

        fun parseInput(txt: String): List<List<Int>> {
            val data = txt.lineSequence().map { line ->
                line.split("""\s+""".toRegex()).map { it.toInt() }
            }.toList()
            return data
        }
    }
}