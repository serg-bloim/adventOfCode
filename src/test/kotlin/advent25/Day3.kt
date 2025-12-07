package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.pow
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

        private fun findMaxJoltage(batteries: List<Long>): Long {
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
            assertEquals(3121910778619, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(172787336861064, actual)
        }

        fun solve(txt: String): Any {
            val data = parseInput(txt)
            return data.sumOf { findMaxJoltage(it, 12) }
        }

        private fun findMaxJoltage(batteries: List<Long>, n: Int): Long {
            if (n == 1) {
                return batteries.max()
            }
            val firstMax = batteries.asSequence()
                .take(batteries.size - (n - 1))
                .withIndex()
                .maxBy { it.value }
            val rest = findMaxJoltage(batteries.subList(firstMax.index + 1, batteries.size), n - 1)
            return firstMax.value * 10L.pow(n - 1) + rest
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

        fun parseInput(txt: String): List<List<Long>> {
            val data = txt.lineSequence().map { line ->
                line.asSequence().map { it.digitToInt().toLong() }.toList()
            }.toList()
            return data
        }
    }
}