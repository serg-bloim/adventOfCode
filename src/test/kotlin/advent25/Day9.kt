package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.permutations2
import utils.repeatForever
import utils.result
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day9 {
    data class CoordXY(val x: Int, val y: Int)
    data class Boundary(val x1: Int, val x2: Int, val yOffset: Int, val dirTowardsBigger: Boolean) {
        operator fun contains(coord: Int) = coord in x1..x2
    }

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
            assertEquals(24, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(1577956170, actual)
        }

        fun solve(txt: String): Any {
            val redTiles = parseInput(txt)
            val leftmostCouple =
                (redTiles.asSequence() + sequenceOf(redTiles.first())) // account for connection from last to first
                    .zipWithNext() // form connections between two adj. red tiles
                    .filter { (r1, r2) -> r1.x == r2.x } // consider only vertical ones
                    .minBy { (r1, r2) -> r1.x } // find leftmost
            val redContourStartingLeftmost = redTiles.repeatForever(2)
                .dropWhile { it != leftmostCouple.first }
                .take(redTiles.size)
                .toList()
            // We need to know which side of a path is the shape's inwards
            val inwardsToRight = leftmostCouple.second.y > leftmostCouple.first.y
            val allSides = redContourStartingLeftmost.repeatForever(2).zipWithNext().take(redContourStartingLeftmost.size)
            val boundaries = allSides
                .filter { (t1, t2) -> t1.y == t2.y }
                .map { (t1, t2) ->
                    val from = min(t1.x, t2.x)
                    val to = max(t1.x, t2.x)
                    val dir = (t1.x < t2.x) xor inwardsToRight
                    Boundary(from, to, t1.y, dir)
                }
                .sortedBy { it.yOffset }
                .toList()
            val allRectsSorted = redTiles.permutations2()
                .sortedByDescending { (t1, t2) -> calcArea(t1, t2) }
                .toList()
            val (t1, t2) = allRectsSorted
                .asSequence()
                .filter { (t1, t2) -> isValidRect(t1, t2, boundaries) }
                .first()
            val maxArea = calcArea(t1, t2)
            logger.info { "Max rect: $t1 x $t2 = $maxArea" }
            return maxArea
        }
    }

    private fun isValidRect(
        t1: CoordXY,
        t2: CoordXY,
        boundaries: List<Boundary>
    ): Boolean {
        val bottomLeft = CoordXY(min(t1.x, t2.x), min(t1.y, t2.y))
        val topRight = CoordXY(max(t1.x, t2.x), max(t1.y, t2.y))
        val leftSide = generateSequence(bottomLeft.y) { it + 1 }
            .takeWhile { it < topRight.y }
            .map { CoordXY(bottomLeft.x, it) }
        val topSide = generateSequence(bottomLeft.x) { it + 1 }
            .takeWhile { it <= topRight.x }
            .map { CoordXY(it, topRight.y) }
        val rightSide = generateSequence(bottomLeft.y) { it + 1 }
            .takeWhile { it <= topRight.y }
            .map { CoordXY(topRight.x, it) }
        val bottomSide = generateSequence(bottomLeft.x) { it + 1 }
            .takeWhile { it <= topRight.x }
            .map { CoordXY(it, bottomLeft.y) }
        val perimiter = leftSide + topSide + rightSide + bottomSide
        return perimiter.all { t ->
            if (tileCache.size > 6000000) {
                logger.info { "-----Cache restart------" }
                tileCache.clear()
            }
            tileCache.computeIfAbsent(t) { hasSupportingBoundary(t.x, t.y, boundaries) }
        }
    }

    val tileCache = mutableMapOf<CoordXY, Boolean>()
    private fun hasSupportingBoundary(coord: Int, offset: Int, boundaries: List<Boundary>): Boolean {
        // We can optimize by doing better search, at least smth like a binary search
        val closestBoundary = boundaries.asSequence()
            .filter { coord in it && it.yOffset <= offset }
            .maxByOrNull { it.yOffset }
        if (closestBoundary == null) return false
        if (closestBoundary.yOffset == offset) return true
        if (coord == closestBoundary.x1 || coord == closestBoundary.x2) return true
        return closestBoundary.dirTowardsBigger
    }

    private fun calcArea(t1: CoordXY, t2: CoordXY): Long =
        (abs(t1.x - t2.x) + 1).toLong() * (abs(t1.y - t2.y) + 1)

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): List<CoordXY> {
            val data = txt.lineSequence().map { line ->
                val (x, y) = line.split(',').map { it.toInt() }
                CoordXY(x, y)
            }.toList()
            return data
        }
    }
}