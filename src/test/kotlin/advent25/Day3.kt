package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result

class Day3 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(357, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(555555555, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val data = parseInput(txt)
            return data.sumOf { findMaxJoltage(it) }
        }

        private fun findMaxJoltage(batteries: List<Int>): Int {
            val firstMax = batteries.asSequence()
                .take(batteries.size - 1)
                .withIndex()
                .maxBy { it.value }
            val secondMax = batteries
                .asSequence()
                .drop(firstMax.index + 1)
                .max()
            return firstMax.value * 10 + secondMax
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
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

        fun parseInput(txt: String): List<List<Int>> {
            val data = txt.lineSequence().map { line ->
                line.asSequence().map { it.digitToInt() }.toList()
            }.toList()
            return data
        }
    }
}