package advent23

import advent23.Day10.Helper.move
import advent23.Day10.Helper.withinBox
import advent23.Day10.Helper.x
import advent23.Day10.Helper.y
import org.junit.jupiter.api.Test
import kotlin.math.min

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

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test(), 3, 10)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(94, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod(), 3, 10)
            println("Result: $actual")
            assertEquals(936, actual)
        }

    }

    object Solution {
        var minPath = 0
        var minLimit = 0
        var maxLimit = 0
        fun solve(txt: String, minLimit: Int = 0, maxLimit: Int = 3): Any {
            this.minLimit = minLimit
            this.maxLimit = maxLimit
            val city = txt.lines().map { it.map { it.digitToInt() } }
            minPath = findBadPath(city, minLimit, maxLimit)
            val cache = Array(4) { Array(city.size) { IntArray(city.first().size) { Int.MAX_VALUE } } }
            start(Direction.East, city, cache)
            start(Direction.North, city, cache)
            return minPath
        }

        private fun findBadPath(city: List<List<Int>>, minLimit: Int, maxLimit: Int): Int {
            val width = city.size
            val height = city.first().size
            var dx = width-1
            var dy = height-1
            var pos = Coords(0, 0)
            var cost = 0
            fun move(dir: Direction, delta: Int): Int {
                val nextStep = if (delta < maxLimit) delta else min(maxLimit, delta - minLimit)
                var moved = 0
                for (fwd in 0..<nextStep) {
                    cost += city[pos.y][pos.x]
                    pos = pos.move(dir)
                    moved++
                }
                return moved
            }
            while (dx > 0 || dy > 0) {
                while (dx >= dy) {
                    dx -= move(Direction.East, dx)
                }
                while (dy > dx) {
                    dy -= move(Direction.North, dy)
                }
            }
            return cost
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
            for (fwd in 0..<maxLimit) {
                currentCost += city[midBLock.y][midBLock.x]
                if (currentCost >= minPath) return
                if (fwd >= minLimit) {
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

