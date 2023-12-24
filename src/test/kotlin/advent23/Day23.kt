package advent23

import org.junit.jupiter.api.Test

class Day23 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(94, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(2366, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.solve2(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(154, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }

    object Solution {
        val EMPTY = 1.shl(4)
        val VISITED = EMPTY.shl(1)
        fun solve(txt: String): Any {
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
            val startX = maze.first().indexOf(EMPTY)
            val startY = 1
            val runtime = Runtime(maze)
            return runtime.findMaxPath(Coords(startX, startY), Direction.North, 1)
        }

        fun solve2(txt: String): Any {
            TODO("Not yet implemented")
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
                        sequenceOf(dirFrom, dirFrom.right(), dirFrom.left()).maxOf {
                            findMaxPath(pos.move(it), it, path + 1)
                        }
                    }
                }
                maze[pos.y][pos.x] = oldV
                return res
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

