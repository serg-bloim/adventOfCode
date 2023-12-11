package advent23

import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.system.measureTimeMillis

class Day11 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.sumDistances(load_test(), 2)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(374, actual)
        }

        @Test
        fun testReal() {
            val txt = load_prod()
            var actual: Any
            val elapsed = measureTimeMillis {
                actual = Solution.sumDistances(txt, 2)
            }
            println("Execution: $elapsed")
            println("Result: $actual")
            kotlin.test.assertEquals(266083745548L, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.sumDistances(load_test(), 100)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(8410, actual)
        }

        @Test
        fun testReal() {
            val txt = load_prod()
            var actual: Any
//            actual = Solution.sumDistances(txt, 1000000)
//            actual = Solution.sumDistances(txt, 1000000)
            val elapsed = measureTimeMillis {
                actual = Solution.sumDistances(txt, 1000000)
            }
            println("Execution: $elapsed ms")
            println("Result: $actual")
            kotlin.test.assertEquals(266083745548, actual)
        }

    }

    object Solution {
        fun sumDistances(txt: String, expansion: Int): Any {
            val expansion = expansion - 1
            val space = txt.lines()
            val emptyRows = space.withIndex()
                .filter { (i, row) -> isEmpty(row.asSequence()) }
                .map { it.index }
            val emptyCols = space[0].indices
                .map { x ->
                    space.indices.asSequence()
                        .map { y -> space[y][x] }
                }
                .withIndex()
                .filter { (i, col) -> isEmpty(col) }
                .map { it.index }
            val expansionMapX = BooleanArray(space[0].length).also {
                for (emptyX in emptyCols) {
                    it[emptyX] = true
                }
            }
            val expansionMapY = BooleanArray(space.size).also {
                for (emptyY in emptyRows) {
                    it[emptyY] = true
                }
            }
            val galaxies = buildList {
                var expansionYAcc = 0
                for ((y, row) in space.withIndex()) {
                    var expansionXAcc = 0
                    if (expansionMapY[y]) expansionYAcc += expansion
                    for ((x, cell) in row.withIndex()) {
                        if (expansionMapX[x]) expansionXAcc += expansion
                        if (cell == '#')
                            this.add(Galaxy(x + expansionXAcc, y + expansionYAcc))
                    }
                }
            }
            val galaxiesClustersPerX = galaxies.groupingBy { it.x }.eachCount()
            val galaxiesClustersPerY = galaxies.groupingBy { it.y }.eachCount()
            val sumPerX = distBetweenClusters(galaxiesClustersPerX)
            val sumPerY = distBetweenClusters(galaxiesClustersPerY)
            return sumPerX + sumPerY
        }

        fun distBetweenClusters(galaxiesPerX: Map<Int, Int>) =
            galaxiesPerX.entries.toList().permutations2().map { (cluster1, cluster2) ->
                val (coord1, n1) = cluster1
                val (coord2, n2) = cluster2
                (coord2 - coord1).absoluteValue.toLong() * n1 * n2
            }.sum()

        private fun isEmpty(line: Sequence<Char>) = line.all { it == '.' }
    }

    data class Galaxy(val x: Int, val y: Int) {
        fun dist(other: Galaxy) = (x - other.x).absoluteValue + (y - other.y).absoluteValue
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
