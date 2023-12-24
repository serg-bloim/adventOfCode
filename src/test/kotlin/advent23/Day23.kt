package advent23

import advent23.Day23.Solution.parseInput2
import advent23.Day23.Solution.solve
import org.junit.jupiter.api.Test

class Day23 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(Solution.parseInput(load_test()))
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(94, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(Solution.parseInput(load_prod()))
            println("Result: $actual")
            assertEquals(2366, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(parseInput2(load_test()))
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(154, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(parseInput2(load_prod()))
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }

    object Solution {
        val EMPTY = 1.shl(4)
        val VISITED = EMPTY.shl(1)
        fun solve(maze: List<MutableList<Int>>): Any {
            val startX = maze.first().indexOf(EMPTY)
            val startY = 1
            val runtime = Runtime(maze)
            return runtime.findMaxPath(Coords(startX, startY), Direction.North, 1)
        }

        fun parseInput(txt: String): List<MutableList<Int>> {
            val maze = txt.lineSequence()
                .map { line ->
                    line.map {
                        when (it) {
                            '#' -> -1
                            '<' -> Direction.West.ordinal
                            '>' -> Direction.East.ordinal
                            '^' -> Direction.South.ordinal
                            'v' -> Direction.North.ordinal
                            '.' -> EMPTY
                            else -> throw IllegalArgumentException()
                        }
                    }.toMutableList()
                }
                .toList()
            return maze
        }

        fun parseInput2(txt: String): List<MutableList<Int>> {
            val maze = parseInput(txt)
            maze.forEachIndexed { y, line -> line.forEachIndexed { x, v -> if (v in 0..3) maze[y][x] = EMPTY } }
            return maze
        }

        class Runtime(val maze: List<MutableList<Int>>) {
            fun findMaxPath(pos: Coords, dirFrom: Direction, path: Int): Int {
                val oldV = maze[pos.y][pos.x]
                if (oldV < 0) return 0
                if (oldV.and(VISITED) > 0) return 0
                maze[pos.y][pos.x] = oldV.or(VISITED)
                val res = when {
                    pos.y == maze.lastIndex -> path
                    oldV < 4 -> {
                        val dir = Direction.entries[oldV]
                        findMaxPath(pos.move(dir), dir, path + 1)
                    }

                    else -> {
                        val (newPos, newDir, newPath) = findJunction(pos, dirFrom)
                        if (newPath > 0) {
                            findMaxPath(newPos, newDir, path + newPath)
                        } else {
                            sequenceOf(dirFrom, dirFrom.right(), dirFrom.left()).maxOf {
                                findMaxPath(pos.move(it), it, path + 1)
                            }
                        }
                    }
                }
                maze[pos.y][pos.x] = oldV
                return res
            }

            private fun findJunction(start: Coords, dirFrom: Direction): Triple<Coords, Direction, Int> {
                var pos = start
                var dir = dirFrom
                var path = 0
                while (true) {
                    val dirs =
                        sequenceOf(dir, dir.right(), dir.left())
                            .filter {
                                val newPos = pos.move(it)
                                newPos.y in maze.indices &&
                                        maze[newPos.y][newPos.x] == EMPTY
                            }
                            .toList()
                    if (dirs.size != 1) return Triple(pos, dir, path)
                    dir = dirs.first()
                    pos = pos.move(dir)
                    path++
                }
            }

            private fun findSinglePath(): Direction? {
                TODO("Not yet implemented")
            }

        }
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(prefix: Any = ""): String {
            return Resources().loadString("${res_prefix}_test$prefix.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }
    }
}

