package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result
import kotlin.test.assertTrue

class Day2 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(1227775554, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(55916882972, actual)
            logger.info { "Result: $actual" }
        }

        @Test
        fun testIsValid() {
            assertTrue { isValid(101) }
            assertTrue { !isValid(1010) }
        }

        fun solve(txt: String): Any {
            val data = parseInput(txt)
            var sum = 0L
            for (range in data) {
                val (start, end) = range
                for (n in start..end) {
                    if (!isValid(n)) {
                        sum += n
                    }
                }
            }
            return sum
        }

        fun isValid(n: Long): Boolean {
            val str = n.toString()
            if (str.length % 2 == 0) {
                val halfIndex = str.length / 2
                val first = str.subSequence(0, halfIndex)
                val second = str.subSequence(halfIndex, str.length)
                if( first.contentEquals(second)){
                    return false
                }
            }
            return true
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

        fun parseInput(txt: String): Sequence<Pair<Long, Long>> {
            val data = txt.splitToSequence(',')
                .map { part ->
                    val (a, b) = part.trim().split('-').map { it.toLong() }
                    Pair(a, b)
                }
            return data
        }
    }
}