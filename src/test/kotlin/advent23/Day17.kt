package advent23

import advent23.Day10.Helper.move
import advent23.Day10.Helper.withinBox
import advent23.Day10.Helper.x
import advent23.Day10.Helper.y
import org.junit.jupiter.api.Test

class Day17 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(102, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(936, actual)
        }

    }

    object Solution {
        var minPath = 0
        fun solve(txt: String): Any {
            minPath = Int.MAX_VALUE
            val city = txt.lines().map { it.map { it.digitToInt() } }
            val cache = Array(4) { Array(city.size) { IntArray(city.first().size) { Int.MAX_VALUE } } }
            start(Direction.East, city, cache)
            start(Direction.North, city, cache)
            return minPath
        }

        private fun start(
            dir: Direction,
            city: List<List<Int>>,
            cache: Array<Array<IntArray>>
        ) {
            val startingPos = Coords(0, 0).move(dir)
            search(startingPos, dir, 0, city, cache)
        }

        private fun search(
            coords: Coords,
            dir: Direction,
            cost: Int,
            city: List<List<Int>>,
            cache: Array<Array<IntArray>>
        ) {
            val (x, y) = coords
            if (y !in city.indices || x !in city.first().indices) return
            var currentCost = cost
            val cacheCost = cache[dir.ordinal][y][x]
            if (currentCost >= cacheCost) return
            cache[dir.ordinal][y][x] = currentCost
            var midBLock = coords
            for (fwd in 0..2) {
                currentCost += city[midBLock.y][midBLock.x]
                if (currentCost >= minPath) return
                if (midBLock.y == city.lastIndex && midBLock.x == city.first().lastIndex) {
                    if (currentCost < minPath) {
                        minPath = currentCost
                    }
                }

                dir.left().let { dir ->
                    search(midBLock.move(dir), dir, currentCost, city, cache)
                }
                dir.right().let { dir ->
                    search(midBLock.move(dir), dir, currentCost, city, cache)
                }
                midBLock = midBLock.move(dir)
                if (!midBLock.withinBox(city.first().size, city.size)) {
                    break
                }
            }
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

