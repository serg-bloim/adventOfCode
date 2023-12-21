package advent23

import org.junit.jupiter.api.Test

class Day18 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(62, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(47675, actual)
        }

    }

    object Solution {
        val inpRe = Regex("""(\w) (\d+) \(#(\w+)\)""")
        fun solve(txt: String): Any {
            val instructions = txt.lines().map {
                val (dirStr, nStr) = inpRe.matchEntire(it)!!.destructured
                val dir = when (dirStr) {
                    "R" -> Direction.East
                    "L" -> Direction.West
                    "U" -> Direction.South
                    "D" -> Direction.North
                    else -> throw IllegalArgumentException()
                }
                Pair(dir, nStr.toInt())
            }
            var leftmostDirection = Direction.East
            var leftmostPos = 1
            val walls = buildMap<Int, MutableList<Int>> {
                var pos = Coords(0, 0)
                instructions.forEach { (dir, n) ->
                    val end = pos.move(dir, n)
                    when (dir) {
                        Direction.West, Direction.East -> {}

                        Direction.North, Direction.South -> {
                            if (pos.x < leftmostPos) {
                                leftmostPos = pos.x
                                leftmostDirection = dir
                            }
                            generateSequence(pos) { it.move(dir) }
                                .take(n + 1)
                                .forEach { getOrPut(it.y, ::mutableListOf).add(it.x) }
                        }
                    }
                    pos = end
                }
                this.values.forEach { it.sort() }
            }

            fun spaceTillNextWall(pos: Coords) = walls[pos.y]!!.first { it > pos.x } - pos.x - 1

            val instructionPairs = instructions.repeatForever()
                .zipWithNext { (dir1, n1), (dir2, n2) -> Triple(dir1, dir2, n2) }
                .take(instructions.size)
            var start = Coords(0, 0).move(instructions[0].first, instructions[0].second)
            var area = 0
            for ((dir_prev, dir, n) in instructionPairs) {
                val end = start.move(dir, n)
                val verticalArea = if (dir == leftmostDirection) generateSequence(start) { it.move(dir) }
                    .drop(1)
                    .take(n - 1)
                    .sumOf { pos -> spaceTillNextWall(pos) }
                else 0
                val lineArea =
                    when {
                        dir_prev == Direction.East && dir == leftmostDirection -> spaceTillNextWall(start)
                        dir_prev == leftmostDirection && dir == Direction.West -> spaceTillNextWall(start)
                        else -> 0
                    }
                area += verticalArea + lineArea
                start = end
            }
            val perimeter = instructions.sumOf { it.second }
            return area + perimeter
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
    }
}

