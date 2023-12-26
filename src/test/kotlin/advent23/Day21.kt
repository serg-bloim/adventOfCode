package advent23

import advent23.Day21.Solution.countEvensInRomb
import advent23.Day21.Solution.countOddsInRomb
import org.junit.jupiter.api.Test

class Day21 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test(), 6)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(16, actual)
        }

        @Test
        fun testSmaller() {
            val actual = Solution.solve(load_test(), 1)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(2, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(3809, actual)
        }

    }

    internal class Task2 {

        @Test
        fun testSmall1() {
            val tests = mapOf(10 to 50, 50 to 1594, 100 to 6536, 1000 to 668697)
            for ((steps, expected) in tests) {
                val actual = Solution.solve(load_test(), steps)
                println("Result: $actual")
                assertEquals(expected, actual)
            }
        }

        @Test
        fun testReal() {
            val steps = 26501365
            val actual = Solution.solve2(load_prod(), steps)
            println("Result: $actual")
            assertEquals(629720570456311, actual)
        }

    }

    object Solution {
        fun parseMap(txt: String): Pair<List<MutableList<Int>>, Coords> {
            var startX = -1
            var startY = -1
            val map = txt.lineSequence().mapIndexed { y, line ->
                line.mapIndexed { x, ch ->
                    when (ch) {
                        '#' -> Int.MIN_VALUE
                        'S' -> {
                            startX = x
                            startY = y
                            Int.MAX_VALUE
                        }

                        else -> Int.MAX_VALUE
                    }
                }.toMutableList()
            }.toList()
            val start = Coords(startX, startY)
            return Pair(map, start)
        }

        fun colorMap(map: List<MutableList<Int>>, pos: Coords, dist: Int, maxDist: Int) {

            val v = map[pos.y][pos.x]
            if (dist < v && dist <= maxDist) {
                map[pos.y][pos.x] = dist
                pos.neighbors(map.width - 1, map.height - 1).forEach { colorMap(map, it, dist + 1, maxDist) }
            }
        }

        fun solve(txt: String, steps: Int = 64): Any {
            val (map, start) = parseMap(txt).let { (map, start) ->
                stretchMap(map, steps, start)
            }
            return calcSteps(map, start, steps)
        }

        fun calcSteps(
            map: List<MutableList<Int>>, start: Coords, endStep: Int, startStep: Int = 0
        ): Int {
            erase(map)
            val steps = endStep - startStep
            colorMap(map, start, 0, steps)
            val even = steps % 2
            return map.sumOf { it.count { it >= 0 && it < Int.MAX_VALUE && it % 2 == even } }
        }

        fun erase(map: List<MutableList<Int>>) {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] > Int.MIN_VALUE) map[y][x] = Int.MAX_VALUE
                }
            }
        }

        fun stretchMap(
            map: List<MutableList<Int>>, steps: Int, start: Coords
        ): Pair<List<MutableList<Int>>, Coords> {
            val dx = kotlin.math.min(start.x, map.width - start.x - 1)
            val dy = kotlin.math.min(start.y, map.height - start.y - 1)
            val nx = 1 + 2 * ceilingDiv(steps - dx, map.width)
            val ny = 1 + 2 * ceilingDiv(steps - dy, map.height)
            val bigmap = map.asSequence().map { it.repeatForever(nx).toMutableList() }.toList().repeatForever(ny)
                .map { it.toMutableList() } // this is done to actually copy the underlying lines, not just a reference
                .toList()
            return Pair(bigmap, Coords(nx / 2 * map.width + start.x, ny / 2 * map.height + start.y))
        }


        fun solve2(txt: String, steps: Int): Any {
            val (map, start) = parseMap(txt)
            val n = steps / map.size
            val wholeEven = calcSteps(map, start, ((map.width + map.height) / 2 + 1) * 2, 0)
            val wholeOdd = calcSteps(map, start, ((map.width + map.height) / 2 + 1) * 2, 1)
            val maxFullMapInd = n - 1
            val black = countOddsInRomb(maxFullMapInd)
            val white = countEvensInRomb(maxFullMapInd)
            val fullMaps =
                if (n % 2 == 0) black * wholeEven + white * wholeOdd else black * wholeOdd + white * wholeEven
            val northCorner = calcSteps(map, start.move(Direction.South, 65), steps, steps - map.height + 1)
            val southCorner = calcSteps(map, start.move(Direction.North, 65), steps, steps - map.height + 1)
            val eastCorner = calcSteps(map, start.move(Direction.West, 65), steps, steps - map.height + 1)
            val westCorner = calcSteps(map, start.move(Direction.East, 65), steps, steps - map.height + 1)
            val startStep1 = n * map.size + 1
            val startStep2 = (n - 1) * map.size + 1
            val ne1 = calcSteps(map, start.move(Direction.South, 65).move(Direction.West, 65), steps, startStep1)
            val ne2 = calcSteps(map, start.move(Direction.South, 65).move(Direction.West, 65), steps, startStep2)
            val nw1 = calcSteps(map, start.move(Direction.South, 65).move(Direction.East, 65), steps, startStep1)
            val nw2 = calcSteps(map, start.move(Direction.South, 65).move(Direction.East, 65), steps, startStep2)
            val sw1 = calcSteps(map, start.move(Direction.North, 65).move(Direction.East, 65), steps, startStep1)
            val sw2 = calcSteps(map, start.move(Direction.North, 65).move(Direction.East, 65), steps, startStep2)
            val se1 = calcSteps(map, start.move(Direction.North, 65).move(Direction.West, 65), steps, startStep1)
            val se2 = calcSteps(map, start.move(Direction.North, 65).move(Direction.West, 65), steps, startStep2)
            val incompleteTriangles1 = maxFullMapInd.toLong() + 1 + 1 - 1
            val incompleteTriangles2 = incompleteTriangles1 - 1

            return fullMaps +
                    northCorner + southCorner + eastCorner + westCorner +
                    (ne1 + nw1 + sw1 + se1) * incompleteTriangles1 +
                    (ne2 + nw2 + sw2 + se2) * incompleteTriangles2
        }

        fun countOddsInRomb(n: Int): Long {
            val n = if (n > 0 && n % 2 == 1) n + 1 else n
            return n.toLong() * n
        }

        fun countEvensInRomb(n: Int): Long {
            val n = if (n % 2 == 0) n + 1 else n
            return n.toLong() * n
        }

        fun <E> List<List<E>>.mapWholeMaps(mapsize: Int, transform: () -> Int) {

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

    class Tests {
        @Test
        fun test_countOddsInRomb() {
            assertEquals(0, countOddsInRomb(0))
            assertEquals(4, countOddsInRomb(1))
            assertEquals(4, countOddsInRomb(2))
            assertEquals(16, countOddsInRomb(3))
            assertEquals(16, countOddsInRomb(4))
            assertEquals(36, countOddsInRomb(5))
        }

        @Test
        fun test_countEvensInRomb() {
            assertEquals(1, countEvensInRomb(0))
            assertEquals(1, countEvensInRomb(1))
            assertEquals(9, countEvensInRomb(2))
            assertEquals(9, countEvensInRomb(3))
            assertEquals(25, countEvensInRomb(4))
            assertEquals(25, countEvensInRomb(5))
        }
    }
}




