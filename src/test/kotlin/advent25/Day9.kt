package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day9 {
    data class CoordXY(val x: Int, val y: Int)
    data class Boundary(val c1: Int, val c2: Int, val offset: Int, val dirTowardsBigger: Boolean) {
        operator fun contains(coord: Int) = coord in c1..c2
    }

    val logger = KotlinLogging.logger {}

    enum class TILE_COLOR { RED, GREEN, NONE }

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
            assertEquals(55555555, actual)
        }
        @Test
        fun testReal2() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(55555555, actual)
        }

        @Test
        fun testIsValid() {
            val redTiles = parseInput(load_test())
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
            val allSides =
                redContourStartingLeftmost.repeatForever(2).zipWithNext().take(redContourStartingLeftmost.size)
            val verticalBoundaries = allSides
                .filter { (t1, t2) -> t1.x == t2.x }
                .map { (t1, t2) ->
                    val from = min(t1.y, t2.y)
                    val to = max(t1.y, t2.y)
                    val dir = (t1.y < t2.y) xor !inwardsToRight
                    Boundary(from, to, t1.x, dir)
                }
                .sortedBy { it.offset }
                .toList()
            val horizontalBoundaries = allSides
                .filter { (t1, t2) -> t1.y == t2.y }
                .map { (t1, t2) ->
                    val from = min(t1.x, t2.x)
                    val to = max(t1.x, t2.x)
                    val dir = (t1.x < t2.x) xor inwardsToRight
                    Boundary(from, to, t1.y, dir)
                }
                .sortedBy { it.offset }
                .toList()
            logger.info { "Horizontal boundaries" }
            horizontalBoundaries.forEach { b -> logger.info { b } }
            logger.info { "Vertical boundaries" }
            verticalBoundaries.forEach { b -> logger.info { b } }
            val rects = listOf(
                Pair(CoordXY(8, 2), CoordXY(9, 3)),
                Pair(CoordXY(7, 1), CoordXY(11, 5)),
                Pair(CoordXY(7, 1), CoordXY(12, 5)),
                Pair(CoordXY(7, 1), CoordXY(10, 5)),
                Pair(CoordXY(7, 1), CoordXY(11, 6)),
            )
            rects.forEach { (t1, t2) ->
                val valid = isValidRect(t1, t2, verticalBoundaries, horizontalBoundaries)
                logger.info { "Rect $t1 x $t2 is valid: $valid" }
            }
            logger.info { }
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
            val allSides =
                redContourStartingLeftmost.repeatForever(2).zipWithNext().take(redContourStartingLeftmost.size)
            val verticalBoundaries = allSides
                .filter { (t1, t2) -> t1.x == t2.x }
                .map { (t1, t2) ->
                    val from = min(t1.y, t2.y)
                    val to = max(t1.y, t2.y)
                    val dir = (t1.y < t2.y) xor !inwardsToRight
                    Boundary(from, to, t1.x, dir)
                }
                .sortedBy { it.offset }
                .toList()
            val horizontalBoundaries = allSides
                .filter { (t1, t2) -> t1.y == t2.y }
                .map { (t1, t2) ->
                    val from = min(t1.x, t2.x)
                    val to = max(t1.x, t2.x)
                    val dir = (t1.x < t2.x) xor inwardsToRight
                    Boundary(from, to, t1.y, dir)
                }
                .sortedBy { it.offset }
                .toList()
            val rl = RateLimiter()
            val maxArea = redTiles.permutations2()
//                .onEach { (t1, t2) ->
//                    if(t1==CoordXY(9,5) && t2== CoordXY(2,3))
//                        1+1
//                    val valid = isValidRect(t1, t2, verticalBoundaries, horizontalBoundaries)
//                    val area = calcArea(t1, t2)
//                    logger.info { "Rect $t1 x $t2 is valid: $valid, area: $area" }
//                }
                .sortedByDescending { (t1, t2) -> calcArea(t1, t2) }
                .onEach { (t1, t2) -> rl.onEach(1){ i-> logger.info { "$i Invalid rect: $t1 x $t2, area: ${calcArea(t1, t2)}" } }}
                .filter { (t1, t2) -> isValidRect(t1, t2, verticalBoundaries, horizontalBoundaries) }
                .first()
                .let { (t1, t2) -> calcArea(t1, t2) }
            return maxArea
        }

        fun sign(v: Int) = if (v == 0) 0 else v / abs(v)
    }

    private fun isValidRect(
        t1: CoordXY,
        t2: CoordXY,
        verticalBoundaries: List<Boundary>,
        horizontalBoundaries: List<Boundary>
    ): Boolean {
        val bottomLeft = CoordXY(min(t1.x, t2.x), min(t1.y, t2.y))
        val topRight = CoordXY(max(t1.x, t2.x), max(t1.y, t2.y))
        val leftSide = generateSequence(bottomLeft.y) { it + 1 }
            .takeWhile { it <= topRight.y }
            .map { CoordXY(bottomLeft.x, it) }
        val leftSideValid = leftSide.all { t ->

            hasSupportingBoundary(t.y, t.x, verticalBoundaries)
        }
        val rightSide = generateSequence(bottomLeft.y) { it + 1 }
            .takeWhile { it <= topRight.y }
            .map { CoordXY(topRight.x, it) }
        val rightSideValid = rightSide.all { t ->
            hasSupportingBoundary(t.y, t.x, verticalBoundaries)
        }

        val bottomSide = generateSequence(bottomLeft.x) { it + 1 }
            .takeWhile { it <= topRight.x }
            .map { CoordXY(it, bottomLeft.y) }
        val bottomSideValid = bottomSide.all { t ->
            hasSupportingBoundary(t.x, t.y, horizontalBoundaries)
        }
        val topSide = generateSequence(bottomLeft.x) { it + 1 }
            .takeWhile { it <= topRight.x }
            .map { CoordXY(it, topRight.y) }
        val topSideValid = topSide.all { t ->
            hasSupportingBoundary(t.x, t.y, horizontalBoundaries)
        }
        return leftSideValid && rightSideValid && topSideValid && bottomSideValid
    }
    val greenCache = mutableMapOf<CoordXY, Boolean>()
    private fun hasSupportingBoundary(coord: Int, offset: Int, boundaries: List<Boundary>): Boolean {
        // We can optimize by doing better search, at least smth like a binary search
        val closestBoundary = boundaries.asSequence()
            .filter { coord in it && it.offset <= offset }
            .maxByOrNull { it.offset }
        if (closestBoundary == null) return false
        if (closestBoundary.offset == offset) return true
        return closestBoundary.dirTowardsBigger
    }

    private fun calcArea(t1: CoordXY, t2: CoordXY) =
        abs((t1.x.toLong() - t2.x + 1) * (t1.y - t2.y + 1))

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