package advent23

import advent23.Day10.Solution1.solve
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val (expected, input) = Tests.getAllTests()[0]
            val actual = solve(input)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(expected, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            assertEquals(6979, actual)
        }
    }

    internal class Task2 {
        @Test
        fun testSmall1() {
            val (expected, input) = Tests.getAllTests()[3]
            val actual = Solution1.countEnclosed(input)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(expected, actual)
        }

        @Test
        fun testSmallAll() {
            for ((expected, input) in Tests.getAllTests().drop(1)) {
                val actual = Solution1.countEnclosed(input)
                println("Result: $actual")
                result.println("Result: $actual")
                assertEquals(expected, actual)
            }
        }

        @Test
        fun testReal() {
            val actual = Solution1.countEnclosed(load_prod())
            println("Result: $actual")
            assertEquals(443, actual)
        }
    }

    object Solution1 {

        fun solve(txt: String): Any {
            val navi = Navigation(txt.lines())
            val loop = genLoop(navi)
            return loop.count() / 2
        }

        fun countEnclosed(txt: String): Int {
            val navi = Navigation(txt.lines())
            val loop = genLoop(navi)
            val loopCoords = loop.map { (cell, dir) -> cell.coords }.toSet()
            // Here we find the leftmost wall of the loop.
            // Therefore, the loop is on the right side of it.
            // It needs to find out what direction such walls should be.
            val insideOrientation = loop
                .filter { (cell, dir) -> cell.kind == '|' }
                .minBy { (cell, dir) -> cell.coords.x }
                .let { (cell, dir) -> cell.getPipe().orientation(dir) }

            fun scanUntilLoop(start: Coords): Int {
                return generateSequence(start) { it.move(Direction.East) }
                    .drop(1) // first is the loop itself, it doesn't count
                    .takeWhile { it !in loopCoords }
                    .count()
            }

            val size = loop
                .filter { (cell, dir) -> cell.kind in "|7J" && cell.getPipe().orientation(dir) == insideOrientation }
                .map { scanUntilLoop(it.first.coords) }
                .sum()
            return size
        }

        private fun genLoop(navi: Navigation): Sequence<Pair<Cell, Direction>> {
            val startCell = findStartingCell(navi)

            val loop = generateSequence(Pair(startCell, startCell.getPipe().dir1)) { (point, dir) ->
                val nextCoord = point.coords.move(dir)
                val nextPoint = navi.getCell(nextCoord)
                if (nextPoint.coords == startCell.coords) null
                else Pair(nextPoint, nextPoint.changeDir(dir.reversed()))
            }
            return loop
        }

        private fun findStartingCell(navi: Navigation): Cell {
            val start = navi.cellsAsSeq().first { (coords, ch) -> ch == 'S' }.coords
            val startingDirections = navi.neighborsAsSeq(start)
                .filter { cell -> cell.hasPipe() && cell.isConnected(start) }
                .map { direction(start, it.coords) }
                .toSet()
            assert(startingDirections.size == 2)
            val startCell = Cell(
                start, when (startingDirections) {
                    setOf(Direction.North, Direction.South) -> '|'
                    setOf(Direction.East, Direction.West) -> '-'
                    setOf(Direction.North, Direction.West) -> 'J'
                    setOf(Direction.North, Direction.East) -> 'L'
                    setOf(Direction.South, Direction.West) -> '7'
                    setOf(Direction.South, Direction.East) -> 'F'
                    else -> '.'
                }
            )
            return startCell
        }

        private fun direction(from: Coords, to: Coords): Direction {
            return when {
                from.x == to.x -> if (to.y > from.y) Direction.North else Direction.South
                from.y == to.y -> if (to.x > from.x) Direction.East else Direction.West
                else -> throw Exception()
            }
        }
    }

    class Navigation(map: List<String>) {
        val neighbors = listOf(Coords(-1, 0), Coords(1, 0), Coords(0, -1), Coords(0, 1))
        val map = map.asReversed()
        val width = map[0].length
        val height = map.size
        fun cellsAsSeq() = sequence {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    yield(getCell(Coords(x, y)))
                }
            }
        }

        fun neighborsAsSeq(coords: Coords) = neighbors.asSequence()
            .map { coords + it }
            .filter { it.withinBox(width, height) }
            .map { getCell(it) }

        fun getCell(coords: Coords) = Cell(coords, getCh(coords))

        private fun getCh(coords: Coords) = map[coords.y][coords.x]
    }

    data class Cell(val coords: Coords, val kind: Char) {
        fun isConnected(other: Coords): Boolean {
            val pipe = getPipe()
            val exit1 = coords.move(pipe.dir1)
            val exit2 = coords.move(pipe.dir2)
            return exit1 == other || exit2 == other
        }

        fun getPipe() = when (kind) {
            '|' -> Pipe.Vertical
            '-' -> Pipe.Horizontal
            'L' -> Pipe.NE
            'J' -> Pipe.NW
            '7' -> Pipe.SW
            'F' -> Pipe.SE
            else -> throw Exception("'$kind' is not a pipe")
        }

        fun changeDir(from: Direction): Direction {
            val pipe = getPipe()
            return when (from) {
                pipe.dir1 -> pipe.dir2
                pipe.dir2 -> pipe.dir1
                else -> throw Exception()
            }
        }

        fun hasPipe() = kind != '.'
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }


    }

    object Tests {
        @Test
        fun testNeighbors() {
            val navi = Navigation(load_test().lines())
            val start = Coords(1, 2)
            val neighbors = navi.neighborsAsSeq(start).map { it.coords }.toSet()
            assertEquals(setOf(Coords(0, 2), Coords(2, 2), Coords(1, 1), Coords(1, 3)), neighbors)
        }

        fun getAllTests(): List<Pair<Int, String>> {
            return load_test().lineSequence().chunked { it.trim().isEmpty() }
                .map { lines ->
                    val iterator = lines.iterator()
                    Pair(iterator.next().trim().toInt(), iterator.asSequence().joinToString("\n"))
                }.toList()
        }
    }

    enum class Pipe(val dir1: Direction, val dir2: Direction) {
        // The order of dir 1 and dir is extremely important
        Vertical(Direction.North, Direction.South),
        Horizontal(Direction.West, Direction.East),
        NE(Direction.North, Direction.East),
        NW(Direction.North, Direction.West),
        SE(Direction.South, Direction.East),
        SW(Direction.West, Direction.South);

        fun orientation(dir: Direction) = when (dir) {
            dir1 -> Orientation.Clockwise
            dir2 -> Orientation.CounterClockwise
            else -> throw Exception()
        }
    }

    enum class Orientation {
        Clockwise,
        CounterClockwise
    }

}

