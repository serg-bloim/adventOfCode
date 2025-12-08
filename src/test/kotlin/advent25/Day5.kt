package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result

class Day5 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(3, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(679, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val (ranges, ids) = parseInput(txt)
            ranges.sortBy { it.first() }
            val res = ids.count { isValid(ranges, it) }

            return res
        }

        private fun isValid(ranges: MutableList<LongRange>, id: Long): Boolean {
            for (range in ranges) {
                if (range.first > id) break
                if (id in range) return true
            }
            return false
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

        fun parseInput(txt: String): Pair<MutableList<LongRange>, List<Long>> {
            val lines = txt.lineSequence().iterator()
            val ranges = lines.asSequence().takeWhile { it.isNotEmpty() }
                .map {
                    val (a, b) = it.split('-').map { it.toLong() }
                    LongRange(a, b)
                }.toMutableList()
            val ids = lines.asSequence()
                .filterNot { it.isEmpty() }
                .map { it.toLong() }
                .toList()
            return Pair(ranges, ids)
        }
    }
}