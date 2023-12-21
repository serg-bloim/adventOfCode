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

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.solve2(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(952408144115, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(122103860427465, actual)
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
                Pair(dir, nStr.toLong())
            }
            return countArea(instructions)
        }

        fun solve2(txt: String): Any {
            val instructions = txt.lines().map {
                val (dirStr, nStr, instr) = inpRe.matchEntire(it)!!.destructured

                val dir = when (instr[5]) {
                    '0' -> Direction.East
                    '1' -> Direction.North
                    '2' -> Direction.West
                    '3' -> Direction.South
                    else -> throw IllegalArgumentException()
                }
                Pair(dir, instr.substring(0, 5).toLong(16))
            }
            return countArea(instructions)
        }

        fun traversePerimeter(instructions: List<Pair<Direction, Long>>) = sequence {
            var start = LongCoords(0, 0)
            for ((dir, n) in instructions) {
                yield(Triple(dir, n, start))
                start = start.move(dir, n)
            }
        }

        private fun countArea(instructions: List<Pair<Direction, Long>>): Long {
            var leftmostDirection =
                traversePerimeter(instructions)
                    .filter { (dir, n, start) -> dir == Direction.South || dir == Direction.North }
                    .minBy { (dir, n, start) -> start.x }
                    .first

            val walls = traversePerimeter(instructions)
                .filter { (dir, n, start) -> dir == leftmostDirection.reversed() }
                .map { (dir, n, start) ->
                    val from = start
                    val to = start.move(dir, n)
                    if (dir == Direction.North) Wall(from, to)
                    else Wall(to, from)
                }
                .sortedBy { it.from.y }
                .toList()


            fun spaceTillNextWall(fromY: Long, toY: Long, x: Long): Long {
                val wall = walls.asSequence()
                    .filter { it.includesY(fromY) && it.from.x > x }
                    .minBy { it.from.x }
                val nextWallY = walls.asSequence()
                    .filter { it.from.y > fromY && it.from.x < wall.from.x && it.from.x > x }
                    .minByOrNull { it.from.y }
                    ?.let { it.from.y }
                    ?: Long.MAX_VALUE
                val lastY = min(toY, nextWallY - 1, wall.to.y)
                val rest = if (lastY < toY) spaceTillNextWall(lastY + 1, toY, x) else 0
                val dx = wall.from.x - x - 1
                val dy = lastY - fromY + 1
                return dx * dy + rest
            }

            fun spaceTillNextWall(from: LongCoords, to: LongCoords): Long {
                if (from.y > to.y) return spaceTillNextWall(to, from)
                return spaceTillNextWall(from.y, to.y, from.x)
            }

            val instructionPairs = instructions.repeatForever()
                .zipWithNext { (dir1, n1), (dir2, n2) -> Triple(dir1, dir2, n2) }
                .take(instructions.size)
            var start = LongCoords(0, 0).move(instructions[0].first, instructions[0].second)
            var area = 0L
            for ((dir_prev, dir, n) in instructionPairs) {
                val end = start.move(dir, n)
                val verticalArea =
                    if (dir == leftmostDirection && n > 1) spaceTillNextWall(start.move(dir), start.move(dir, n - 1))
                    else 0
                val lineArea =
                    when {
                        dir_prev == Direction.East && dir == leftmostDirection -> spaceTillNextWall(start, start)
                        dir_prev == leftmostDirection && dir == Direction.West -> spaceTillNextWall(start, start)
                        else -> 0
                    }
                area += verticalArea + lineArea
                start = end
            }
            val perimeter = instructions.sumOf { it.second }
            return area + perimeter
        }
    }

    data class Wall(val from: LongCoords, val to: LongCoords) {
        val yRange = LongRange(from.y, to.y)

        fun includesY(y: Long) = y in yRange
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
