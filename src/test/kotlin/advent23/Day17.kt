package advent23

import org.junit.jupiter.api.Test
import java.util.Stack

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
            assertEquals(12345, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            val city = txt.lines().map { it.map { it.digitToInt() } }
            val xMax = city.first().indices.last
            val yMax = city.indices.last
            val cache = Array(3) { Array(city.size) { IntArray(city.first().size) { Int.MAX_VALUE } } }
            search(0, 0, 0, Direction.East, 0, city, cache)
            return cache.minOf { it[yMax][xMax] } - city[0][0]
        }

        private fun search(
            x: Int,
            y: Int,
            straight: Int,
            dir: Direction,
            cost: Int,
            city: List<List<Int>>,
            cache: Array<Array<IntArray>>
        ) {
            if (y !in city.indices || x !in city.first().indices) return
            val selfCost = city[y][x]
            val cacheCost = cache[straight][y][x]
            val currentCost = cost + selfCost
            if (currentCost >= cacheCost) return
            cache[straight][y][x] = currentCost
            if(straight < 2){
                val (xNew, yNew) = move(x, y, dir)
                search(xNew, yNew, straight+1, dir, currentCost, city, cache)
            }
            dir.right().let { dir->
                val (xNew, yNew) = move(x, y, dir)
                search(xNew, yNew, 0, dir, currentCost, city, cache)
            }
            dir.left().let { dir->
                val (xNew, yNew) = move(x, y, dir)
                search(xNew, yNew, 0, dir, currentCost, city, cache)
            }
        }

        fun traverse(x: Int, y: Int, straight: Int) {

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

