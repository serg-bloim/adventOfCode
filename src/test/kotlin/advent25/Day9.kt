package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.permutations2
import utils.result
import kotlin.math.abs

class Day9 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(50, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(4758121828, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val tilesXY = parseInput(txt)
            val maxArea = tilesXY.permutations2()
//                .onEach { (t1, t2)-> logger.info { "$t1 x $t2 = ${calcArea(t1, t2)}" } }
                .maxOf { (t1, t2) -> calcArea(t1, t2) }
            return maxArea
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
            val tilesXY = parseInput(txt)
            val maxArea = tilesXY.permutations2().maxOf { (t1, t2) -> calcArea(t1, t2) }
            return maxArea
        }
    }

    private fun calcArea(t1: Pair<Long, Long>, t2: Pair<Long, Long>) =
        abs((t1.first - t2.first + 1) * (t1.second - t2.second + 1))

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): List<Pair<Long, Long>> {
            val data = txt.lineSequence().map { line ->
                val (x, y) = line.split(',').map { it.toLong() }
                Pair(x, y)
            }.toList()
            return data
        }
    }
}