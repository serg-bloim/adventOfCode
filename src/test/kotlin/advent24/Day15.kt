package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.Field
import utils.chunked
import utils.dbg
import utils.move
import utils.result
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.test.assertEquals

class Day15 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(10092, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(1505963, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val (field, moves) = parseInput(txt)
            var robotPos = field.findCoords { it == Cell.Robot }!!
            field[robotPos] = Cell.Empty
            for (dir in moves) {
                val cellsInDirection = generateSequence(robotPos) { it.move(dir) }
                    .drop(1)
                val nextWallOrEmptyXY = cellsInDirection.filter { field[it] != Cell.Box }.first()
                if (field[nextWallOrEmptyXY] == Cell.Empty) {
                    robotPos = robotPos.move(dir)
                    if (field[robotPos] == Cell.Box) {
                        field[robotPos] = Cell.Empty
                        field[nextWallOrEmptyXY] = Cell.Box
                    }
                }
                dbg.println()
                dbg.println(123)
                dbg.println(field.toString {
                    when (it) {
                        Cell.Empty -> "."
                        Cell.Wall -> "#"
                        Cell.Box -> "O"
                        Cell.Robot -> "@"
                    }
                })
            }

            var sum = 0
            field.forEachIndexed { xy, cell ->
                sum += when (cell) {
                    Cell.Box -> xy.y * 100 + xy.x
                    else -> 0
                }
            }
            return sum
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(9021, actual)
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

        fun parseInput(txt: String): Pair<Field<Cell>, List<Direction>> {
            val lines = txt.lineSequence().filter { it.isNotEmpty() }.toList()
            val fieldData = lines.asSequence()
                .takeWhile { it.startsWith('#') }
                .map { line ->
                    line.asSequence()
                        .map {
                            when (it) {
                                '.' -> Cell.Empty
                                '#' -> Cell.Wall
                                'O' -> Cell.Box
                                '@' -> Cell.Robot
                                else -> throw Exception("Wrong cell type $it")
                            }
                        }.toMutableList()
                }.toList()
            val moves = lines.asSequence()
                .dropWhile { it.startsWith('#') }
                .flatMap { it.asSequence() }
                .map {
                    when (it) {
                        '>' -> Direction.East
                        '<' -> Direction.West
                        '^' -> Direction.South
                        'v' -> Direction.North
                        else -> throw Exception("Wrong move type $it")
                    }
                }.toList()
            return Pair(Field(fieldData), moves)
        }
    }

    enum class Cell {
        Empty,
        Wall,
        Box,
        Robot
    }
}