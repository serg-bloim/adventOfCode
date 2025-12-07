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
            val actual = solve(load_test(), ::isValid)
            assertEquals(1227775554, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), ::isValid)
            result.println("Result: $actual")
            assertEquals(55916882972, actual)
            logger.info { "Result: $actual" }
        }

        @Test
        fun testIsValid() {
            assertTrue { isValid(101) }
            assertTrue { !isValid(1010) }
        }

        fun isValid(n: Long): Boolean {
            val str = n.toString()
            if (str.length % 2 == 0) {
                val halfIndex = str.length / 2
                val first = str.subSequence(0, halfIndex)
                val second = str.subSequence(halfIndex, str.length)
                if (first.contentEquals(second)) {
                    return false
                }
            }
            return true
        }
    }

    fun solve(txt: String, isValid: (Long) -> Boolean): Any {
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

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), ::isValid)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(4174379265, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), ::isValid)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(76169125915, actual)
        }

        @Test
        fun testIsValid() {
            assertTrue { isValid(101) }
            assertTrue { !isValid(1010) }
            assertTrue { !isValid(111) }
        }

        fun <T> Sequence<T?>.allEqual(): Boolean = distinct().count() == 1

        fun isValid(n: Long): Boolean {
            val str = n.toString()
            for (chunkSize in 1..str.length / 2) {
                if (str.length % chunkSize == 0) {
                    if (str.asSequence().chunked(chunkSize).allEqual())
                        return false
                }
            }
            return true
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