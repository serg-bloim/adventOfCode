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

    fun addBorder(field: List<String>, ch: Char): List<String> {
        val borderLine = ch.repeatAsSequence(field[0].length + 2).joinToString("")
        val newField = (sequenceOf(borderLine) +
                field.asSequence().map { "$ch$it$ch" } +
                sequenceOf(borderLine)).toList()
        return newField
    }

    fun IntRange.withoutBorder(n: Int) = IntRange(first + n, last - n)
    fun hasRoll(data: List<String>, x: Int, y: Int) = data[y][x] == '@'


    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(43, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Any {
            val inp = addBorder(parseInput(txt), '.')
            val noRollPenalty = 1000
            val data = inp.map { IntArray(it.length) { -noRollPenalty } }
            // For each roll cell bump all its neighbors by +1. This way each cell will have # of rolls around it.
            // Cells that don't contain rolls themselves have a penalty, so they always expect to be negative.
            for (y in data.indices.withoutBorder(1)) {
                for (x in data[0].indices.withoutBorder(1)) {
                    if (hasRoll(inp, x, y)) {
                        for (dx in -1..1) {
                            for (dy in -1..1) {
                                data[y + dy][x + dx] += 1
                            }
                        }
                        data[y][x] += (noRollPenalty - 1)
                    }
                }
            }
            val candidates4Removal = mutableSetOf<Pair<Int, Int>>()
            for (y in data.indices.withoutBorder(1)) {
                for (x in data[0].indices.withoutBorder(1)) {
                    val surroundingRolls = data[y][x]
                    if (surroundingRolls in 0..3) {
                        candidates4Removal.add(Pair(x, y))
                    }
                }
            }
            var removedRolls = 0
            while (candidates4Removal.isNotEmpty()) {
                val (x, y) = candidates4Removal.first().also { candidates4Removal.remove(it) }
                data[y][x] = -1
                removedRolls += 1
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        data[y + dy][x + dx] -= 1
                        if (data[y + dy][x + dx] == 3) {
                            candidates4Removal.add(Pair(x, y))
                        }
                    }
                }
            }
            return removedRolls
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