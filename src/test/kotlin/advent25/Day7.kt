package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result

class Day7 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(21, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(1560, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val (startPos, splitters) = parseInput(txt)
            val beams = mutableSetOf(startPos)
            var splitEvents = 0
            for (layer in splitters) {
                for (beam in beams.toList()) {
                    if (beam in layer) {
                        beams.remove(beam)
                        beams.add(beam + 1)
                        beams.add(beam - 1)
                        splitEvents += 1
                    }
                }
            }
            return splitEvents
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

        fun parseInput(txt: String): Pair<Int, List<Set<Int>>> {
            val lines = txt.lines()
            val startPos = lines.first().indexOf('S')
            val splitters = lines.asSequence()
                .drop(1)
                .map { line ->
                    line.asSequence()
                        .withIndex()
                        .filter { it.value == '^' }
                        .map { it.index }
                        .toSet()
                }
                .filterNot { it.isEmpty() }
                .toList()
            return Pair(startPos, splitters)
        }
    }
}