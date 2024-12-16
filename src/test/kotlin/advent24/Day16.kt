package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.Field
import utils.move
import utils.result
import kotlin.test.assertEquals

class Day16 {
    val COSTS_TURN = 1000
    val COSTS_MOVE = 1

    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(7036, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(135536, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)
            val startXY = field.findCoords { it == Cell.Start }!!
            val endXY = field.findCoords { it == Cell.End }!!
            val costs = field.map { IntArray(4) { Int.MAX_VALUE } }
            costs[endXY] = IntArray(4) { 0 }
            val horizon = mutableSetOf(
                Pair(endXY, Direction.East),
                Pair(endXY, Direction.West),
                Pair(endXY, Direction.North),
                Pair(endXY, Direction.South)
            )

            fun getCost(xy: Coords, dir: Direction) = costs[xy][dir.ordinal]
            fun setCost(xy: Coords, dir: Direction, cost: Int) {
                costs[xy][dir.ordinal] = cost
            }

            fun processCell(xy: Coords, dir: Direction, cost: Int) {
                if (field[xy] == Cell.Wall) return
                val existingCost = getCost(xy, dir)
                if (cost < existingCost) {
                    setCost(xy, dir, cost)
                    horizon.add(Pair(xy, dir))
                }
            }
            while (horizon.isNotEmpty()) {
                val (xy, dir) = horizon.first().also { horizon.remove(it) }
                processCell(xy, dir.right(), getCost(xy, dir) + COSTS_TURN)
                processCell(xy, dir.left(), getCost(xy, dir) + COSTS_TURN)
                processCell(xy.move(dir.reversed()), dir, getCost(xy, dir) + COSTS_MOVE)
            }
            return getCost(startXY, Direction.East)
        }
    }


    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
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

        fun parseInput(txt: String): Field<Cell> {
            val ch2Cell = Cell.entries.map { it.ch to it }.toMap()
            val data = txt.lineSequence().map { line ->
                line.map { ch2Cell[it]!! }.toMutableList()
            }.toList()
            return Field(data)
        }
    }

    enum class Cell(val ch: Char) {
        Empty('.'),
        Wall('#'),
        Start('S'),
        End('E')
    }
}