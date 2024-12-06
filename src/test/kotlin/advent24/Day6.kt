package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.dbg
import utils.move
import utils.result
import utils.withinBox
import kotlin.test.assertEquals

class Day6 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(41, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(4515, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)
            val lastX = field[0].size - 1
            val lastY = field.size - 1

            val startPos = 1.let {
                for (y in field.indices) {
                    for (x in field[y].indices) {
                        if (field[y][x] == Cell.Visited) {
                            return@let Coords(x, y)
                        }
                    }
                }
                return@let Coords(0, 0)
            }
            var pos = startPos
            var dir = Direction.North
            while (!(pos.x == 0 || pos.y == 0 || pos.x == lastX || pos.y == lastY)) {
                val next = pos.move(dir)
                if (field[next.y][next.x].canNotVisit) {
                    dir = dir.right()
                } else {
                    pos = next
                    field[next.y][next.x] = Cell.Visited
                }
            }
            val visited = field.sumOf { it.count { it == Cell.Visited } }
            return visited
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(6, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            // Accepted result is 1309.
            // Didn't accept 1354
            assertEquals(1309, actual)
        }

        @Test
        fun testRealBruteForce() {
            val actual = solveBruteForce(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            // Accepted result is 1309.
            // Didn't accept 1354
            assertEquals(1309, actual)
        }

        private fun solveBruteForce(txt: String): Int {
            val field = parseInput(txt)

            val startPos = 1.let {
                for (y in field.indices) {
                    for (x in field[y].indices) {
                        if (field[y][x] == Cell.Visited) {
                            return@let Coords(x, y)
                        }
                    }
                }
                return@let Coords(0, 0)
            }

            val potentialObstacles = mutableSetOf<Coords>()
            for (y in field.indices) {
                for (x in field[y].indices) {
                    if (field[y][x].canVisit) {
                        val newObstacle = Coords(x, y)
                        if (tryPlaceObstacle(field, startPos, Direction.North, newObstacle)) {
                            potentialObstacles.add(newObstacle)
                        }
                    }
                }
            }
            return potentialObstacles.size
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)

            val startPos = 1.let {
                for (y in field.indices) {
                    for (x in field[y].indices) {
                        if (field[y][x] == Cell.Visited) {
                            return@let Coords(x, y)
                        }
                    }
                }
                return@let Coords(0, 0)
            }
            val potentialObstacles = mutableSetOf<Coords>()
            patrol(field, startPos, Direction.North) { coords, dir ->
                field[coords.y][coords.x] = Cell.Visited
                val next = coords.move(dir)
                // Very important to only place a new obstacle to not visited cells.
                // Cause if there were an obstacle, it couldn't be visited at the first place.
                // Also, as it is already visited, it has been already tested before.
                if (field[next.y][next.x] == Cell.Empty) {
                    if (tryPlaceObstacle(field, coords, dir, next)) {
                        potentialObstacles.add(next)
                    }
                }
                true
            }

            return potentialObstacles.asSequence().filter { it.withinBox(field[0].size, field.size) }.count()
        }
    }

    private fun tryPlaceObstacle(
        field: List<MutableList<Day6.Cell>>,
        guard: Coords,
        dir: Direction,
        newObstacle: Coords
    ): Boolean {
        field[newObstacle.y][newObstacle.x] = Cell.Obstacle
        val visited = mutableSetOf<Pair<Coords, Direction>>()
        var foundLoop = false
        patrol(field, guard, dir) { pos, dir2 ->
            if (Pair(pos, dir2) in visited) {
                foundLoop = true
                false
            } else {
                visited.add(Pair(pos, dir2))
                true
            }
        }
        field[newObstacle.y][newObstacle.x] = Cell.Empty
        if (foundLoop) {
            dbg.println("Obstacle: $newObstacle Loop length: ${visited.size}")
        }
        return foundLoop
    }

    private fun patrol(
        field: List<MutableList<Cell>>,
        startPos: Coords,
        dir: Direction,
        onStep: (Coords, Direction) -> Boolean
    ) {
        val lastX = field[0].size - 1
        val lastY = field.size - 1
        var pos = startPos
        var dir = dir
        if (pos.x == 0 || pos.y == 0 || pos.x == lastX || pos.y == lastY)
            return
        do {
            if (!onStep(pos, dir)) break
            val next = pos.move(dir)
            if (field[next.y][next.x].canNotVisit) {
                dir = dir.right()
            } else {
                pos = next
            }
        } while (!(pos.x == 0 || pos.y == 0 || pos.x == lastX || pos.y == lastY))
    }

    enum class Cell(val canVisit: Boolean) {

        Empty(true),
        Obstacle(false),
        Visited(true);

        val canNotVisit = !canVisit
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): List<MutableList<Cell>> {
            val data = txt.lineSequence().map { line ->
                line.map {
                    when (it) {
                        '.' -> Cell.Empty
                        '#' -> Cell.Obstacle
                        '^' -> Cell.Visited
                        else -> throw Exception("Bad input")
                    }
                }.toMutableList()
            }.toList().reversed()
            return data
        }
    }
}