package advent23

import org.junit.jupiter.api.Test
import kotlin.math.max

class Day16 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.countEnergized(load_test(), 0, 0, Direction.East)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(46, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.countEnergized(load_prod(), 0, 0, Direction.East)
            println("Result: $actual")
            assertEquals(7307, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.countEnergizedMax(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(51, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.countEnergizedMax(load_prod())
            println("Result: $actual")
            assertEquals(12345, actual)
        }

        @Test
        fun testRealSingle() {
            val actual = Solution.countEnergized(load_prod(), 0, 10, Direction.East)
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }

    object Solution {
        fun countEnergized(txt: String, x: Int = 0, y: Int = 0, dir: Direction = Direction.East): Any {
            val field = parseField(txt)
            val visited = Array(field.size) { IntArray(field.first().size) }
            visitRecursively(x, y, dir, field, visited)
            return visited.asSequence().flatMap { it.asSequence() }.count { it != 0 }
        }

        fun visitRecursively(x: Int, y: Int, dir: Direction, field: List<List<Enum<*>?>>, visitedMap: Array<IntArray>) {
//            println("Visited [$x $y]")
            if (y !in field.indices || x !in field.first().indices) return
            val orientation = if (dir == Direction.North || dir == Direction.South) 1 else 2
            val visited = visitedMap[y][x]
            visitedMap[y][x] = visited.or(orientation)
            when (val currentStructure = field[y][x]) {
                null -> {
                    if (visited == orientation) return
                    val (x, y) = nextCell(x, y, dir)
                    visitRecursively(x, y, dir, field, visitedMap)
                }

                is Mirror -> {
                    val nextDir = currentStructure.change(dir)
                    val (x, y) = nextCell(x, y, nextDir)
                    visitRecursively(x, y, nextDir, field, visitedMap)
                }

                is Splitter -> {
                    if (visited == orientation) return
                    for (dir in currentStructure.change(dir)) {
                        val (x, y) = nextCell(x, y, dir)
                        visitRecursively(x, y, dir, field, visitedMap)
                    }
                }
            }
        }

        fun nextCell(x: Int, y: Int, direction: Direction) = when (direction) {
            Direction.North -> Pair(x, y - 1)
            Direction.West -> Pair(x - 1, y)
            Direction.South -> Pair(x, y + 1)
            Direction.East -> Pair(x + 1, y)
        }

        private fun erase(arr2d: Array<IntArray>) {
            for (r in arr2d) {
                for (i in r.indices) {
                    r[i] = 0
                }
            }
        }

        fun countEnergizedMax(txt: String): Int {
            val field = parseField(txt)
            val visited = Array(field.size) { IntArray(field.first().size) }

            val starts =
                field.indices.asSequence().map { y ->
                Triple(0, y, Direction.East)
            }.toList()
//                    field.indices.asSequence().map { y ->
//                Triple(field.first().indices.last, y, Direction.West)
//            }


//            + field.first().indices.asSequence().map { x ->
//                Triple(x, 0, Direction.South)
//            } + field.first().indices.asSequence().map { x ->
//                Triple(x, field.indices.last, Direction.North)
//            }
            var maxCount = 0
            for ((x, y, dir) in starts){
                println("Start $x $y $dir")
                erase(visited)
                visitRecursively(x, y, dir, field, visited)
                val res = visited.asSequence().flatMap { it.asSequence() }.count { it != 0 }
                maxCount = max(maxCount, res)
            }

            return maxCount
        }

        private fun parseField(txt: String): List<List<Enum<*>?>> {
            val field = txt.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map {
                    it.map {
                        when (it) {
                            '/' -> Mirror.NE
                            '\\' -> Mirror.NW
                            '|' -> Splitter.Vertical
                            '-' -> Splitter.Horizontal
                            else -> null
                        }
                    }
                }.toList()
            return field
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

    enum class Mirror(symbol: Char, in1: Direction, out1: Direction) {
        NE('/', Direction.North, Direction.East),
        NW('\\', Direction.North, Direction.West);

        private val transforms =
            mapOf(
                in1 to out1,
                out1 to in1,
                out1.reversed() to in1.reversed(),
                in1.reversed() to out1.reversed()
            )

        fun change(inDir: Direction) = transforms[inDir]!!
    }

    enum class Splitter(symbol: Char, splittingDirection: Direction) {
        Horizontal('-', Direction.North),
        Vertical('|', Direction.East);

        private val splittingOutDirs = listOf(splittingDirection.next(), splittingDirection.reversed().next())
        private val transforms =
            mapOf(
                splittingDirection to splittingOutDirs,
                splittingDirection.reversed() to splittingOutDirs,
                splittingDirection.next() to listOf(splittingDirection.next()),
                splittingDirection.reversed().next() to listOf(splittingDirection.reversed().next())
            )

        fun change(inDir: Direction) = transforms[inDir]!!
    }
}

