package advent23

import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

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
            val actual = Solution.sumDistances(load_prod(), 2)
            println("Result: $actual")
            assertEquals(10292708, actual)
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
            val actual = Solution.sumDistances(load_prod(), 1000000)
            println("Result: $actual")
            assertEquals(10292708, actual)
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
            val distSum = galaxies.permutations2().map { (a, b) -> a.dist(b).toLong() }.sum()
            return distSum
        }

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
