package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result

class Day6 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(4277556, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(5346286649122, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val (data, opsStr) = parseInput(txt)
            val func: (Long, Long) -> Long = Long::times
            val ops: List<(Long, Long) -> Long> = opsStr.map {
                when (it) {
                    "*" -> Long::times
                    else -> Long::plus
                }
            }
            val answers = data.reduce { row1, row2 -> process(row1, row2, ops) }
            return answers.sum()
        }

        private fun process(row1: List<Long>, row2: List<Long>, ops: List<(Long, Long) -> Long>) =
            row1.zip(row2).zip(ops) { (v1, v2), op -> op(v1, v2) }
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

        fun parseInput(txt: String): Pair<List<List<Long>>, List<String>> {
            val data = txt.lines()
                .map { line -> line.trim().split("""\s+""".toRegex()) }
            val numbers = data.subList(0, data.size - 1).map { it.map { it.toLong() } }
            val ops = data.last()
            return Pair(numbers, ops)
        }
    }
}