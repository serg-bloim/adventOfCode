package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.Field
import utils.dbg
import utils.floodFillVisit
import utils.move
import utils.result
import utils.withinBox
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

private const val HAS_WALL = -1

class Day20 {
    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test(), 25)
            assertEquals(4, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 100)
            result.println("Result: $actual")
            assertEquals(1445, actual)
            println("Result: $actual")
        }

        fun solve(txt: String, threshold: Int): Any {
            // Adding an extra layer of walls around perimeter to avoid edge checks in the future
            val initField = parseInput(txt).withBorder(1, '#')
            val startXY = initField.findCoords { it == 'S' }!!
            val endXY = initField.findCoords { it == 'E' }!!

            val distToStartField  = initField.map {
                when (it) {
                    '#' -> HAS_WALL
                    else -> Int.MAX_VALUE
                }
            }
            val distToEndField = distToStartField.copy()
            floodFill(distToStartField, startXY)
            floodFill(distToEndField, endXY)
            val optimalTime = distToStartField[endXY]

            var cheatsOvenThreshold = 0
            distToStartField.forEachIndexed { cheatStartXY, dist2start ->
                if (dist2start != HAS_WALL)
                    for (cheatEndXY in neighborsDist2(cheatStartXY)) {
                        val dist2end = distToEndField[cheatEndXY]
                        if (dist2end != HAS_WALL) {
                            val cheatPathCost = dist2start + dist2end + 2
                            val cheatGain = optimalTime - cheatPathCost
                            if (cheatGain >= threshold)
                                cheatsOvenThreshold++
                        }
                    }
            }
            return cheatsOvenThreshold
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), 76)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(55555555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 100)
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555555, actual)
        }

        fun solve(txt: String, threshold: Int): Any {
            return 555
        }
    }

    fun floodFill(field: Field<Int>, initXY: Coords) {
        field[initXY] = 0
        initXY.floodFillVisit { from, to ->
            if (field[to] == Int.MAX_VALUE) {
                field[to] = field[from] + 1
                true
            } else {
                false
            }
        }.count() // Any terminating operation works to run the sequence through the algorithm
    }

    private fun neighborsDist2(xy: Coords) = sequenceOf(
        xy.move(Direction.North).move(Direction.North),
        xy.move(Direction.North).move(Direction.North.right()),
        xy.move(Direction.East).move(Direction.East),
        xy.move(Direction.East).move(Direction.East.right()),
        xy.move(Direction.South).move(Direction.South),
        xy.move(Direction.South).move(Direction.South.right()),
        xy.move(Direction.West).move(Direction.West),
        xy.move(Direction.West).move(Direction.West.right()),
    )

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): Field<Char> {
            val data = txt.lineSequence().map { line ->
                line.toMutableList()
            }.toList()
            return Field(data)
        }
    }
}