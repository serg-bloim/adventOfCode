package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.Field
import utils.dbg
import utils.move
import utils.result
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
            assertEquals(1543141, actual)
        }

        fun solve(txt: String): Any {
            val (field, moves) = parseInput2(txt)
            var robotPos = field.findCoords { it == Cell.Robot }!!
            for (dir in moves) {
                if (canCellMove(field, robotPos, dir)) {
                    moveCell(field, robotPos, dir)
                    robotPos = robotPos.move(dir)
                }
            }

            var sum = 0
            field.forEachIndexed { xy, cell ->
                sum += when (cell) {
                    Cell.LBox -> xy.y * 100 + xy.x
                    else -> 0
                }
            }
            return sum
        }

        private fun moveCell(field: Field<Cell>, src: Coords, dir: Direction) {
            val dst = src.move(dir)
            val isDirHorizontal = dir == Direction.East || dir == Direction.West
            when (field[dst]) {
                Cell.Empty -> field[dst] = field[src]
                Cell.RBox -> {
                    moveCell(field, dst, dir)
                    if (!isDirHorizontal) {
                        val boxSecondHalf = dst.move(Direction.West)
                        moveCell(field, boxSecondHalf, dir)
                    }
                    field[dst] = field[src]
                }
                Cell.LBox -> {
                    moveCell(field, dst, dir)
                    if (!isDirHorizontal) {
                        val boxSecondHalf = dst.move(Direction.East)
                        moveCell(field, boxSecondHalf, dir)
                    }
                    field[dst] = field[src]
                }
                else -> TODO()
            }
            field[src] = Cell.Empty
        }

        private fun canCellMove(field: Field<Cell>, src: Coords, dir: Direction): Boolean {
            val dst = src.move(dir)
            // If this direction is vertical we need to check second halves of boxes, if horizontal - no.
            val isDirHorizontal = dir == Direction.East || dir == Direction.West
            return when (field[dst]) {
                Cell.Empty -> true
                Cell.Wall -> false
                Cell.RBox -> canCellMove(field, dst, dir)
                        && (isDirHorizontal || canCellMove(field, dst.move(Direction.West), dir))
                Cell.LBox -> canCellMove(field, dst, dir)
                        && (isDirHorizontal || canCellMove(field, dst.move(Direction.East), dir))
                else -> TODO()
            }
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

        fun parseInput2(txt: String): Pair<Field<Cell>, List<Direction>> {
            val lines = txt.lineSequence()
                .filter { it.isNotEmpty() }

                .toList()
            val fieldData = lines.asSequence()
                .takeWhile { it.startsWith('#') }
                .map { line ->
                    line.flatMap {
                        when (it) {
                            '#' -> "##"
                            'O' -> "[]"
                            '@' -> "@."
                            else -> ".."
                        }.asIterable()
                    }
                }
                .map { line ->
                    line.asSequence()
                        .map {
                            when (it) {
                                '.' -> Cell.Empty
                                '#' -> Cell.Wall
                                'O' -> Cell.Box
                                '@' -> Cell.Robot
                                ']' -> Cell.RBox
                                '[' -> Cell.LBox
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
        Robot,
        RBox,
        LBox
    }
}