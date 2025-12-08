package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.repeatAsSequence
import utils.result

class Day4 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(13, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(1349, actual)
            logger.info { "Result: $actual" }
        }

        fun IntRange.withoutBorder(n: Int) = IntRange(first + n, last - n)
        private fun hasRoll(data: List<String>, x: Int, y: Int) = data[y][x] == '@'

        fun solve(txt: String): Any {
            val data = addBorder(parseInput(txt), '.')
            var rollsAccessible = 0
            for (y in data.indices.withoutBorder(1)) {
                for (x in data[0].indices.withoutBorder(1)) {
                    if (hasRoll(data, x, y)) {
                        var rolls = 0
                        for (dx in -1..1) {
                            for (dy in -1..1) {
                                if (hasRoll(data, x + dx, y + dy)) rolls += 1
                            }
                        }
                        if (rolls < 5) rollsAccessible += 1
                    }
                }
            }
            return rollsAccessible
        }
    }

    private fun addBorder(field: List<String>, ch: Char): List<String> {
        val borderLine = ch.repeatAsSequence(field[0].length + 2).joinToString("")
        val newField = (sequenceOf(borderLine) +
                field.asSequence().map { "$ch$it$ch" } +
                sequenceOf(borderLine)).toList()
        return newField
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

        fun parseInput(txt: String): List<String> {
            val data = txt.lines()
                .filterNot { it.isEmpty() }
                .map { ".$it." }
            return data
        }
    }
}