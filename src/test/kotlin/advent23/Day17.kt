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

    data class Args(val x: Int, val y: Int, val straight: Int, val dir: Direction, val cost: Int)
    object Solution {
        fun solve(txt: String): Any {
            val city = txt.lines().map { it.map { it.digitToInt() } }
            val xMax = city.first().indices.last
            val yMax = city.indices.last
            val cache = Array(3) { Array(city.size) { IntArray(city.first().size) { Int.MAX_VALUE } } }
            val stack = Stack<Args>()
            stack.push(Args(0, 0, 0, Direction.East, 0))
            while (stack.isNotEmpty()) {
                search(stack, city, cache)
            }
            return cache.minOf { it[yMax][xMax] } - city[0][0]
        }

        private fun search(
            stack: Stack<Args>,
            city: List<List<Int>>,
            cache: Array<Array<IntArray>>
        ) {
            val (x, y, straight, dir, cost) = stack.pop()
            if (y !in city.indices || x !in city.first().indices) return
            val selfCost = city[y][x]
            val cacheCost = cache[straight][y][x]
            val currentCost = cost + selfCost
            if (currentCost >= cacheCost) return
            if (currentCost >= cache.last().last().last()) return
            for (s in straight..2){
                cache[s][y][x] = currentCost
            }
            dir.right().let { dir ->
                val (xNew, yNew) = move(x, y, dir)
                stack.push(Args(xNew, yNew, 0, dir, currentCost))
            }
            dir.left().let { dir ->
                val (xNew, yNew) = move(x, y, dir)
                stack.push(Args(xNew, yNew, 0, dir, currentCost))
            }
            if (straight < 2) {
                val (xNew, yNew) = move(x, y, dir)
                stack.push(Args(xNew, yNew, straight + 1, dir, currentCost))
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

