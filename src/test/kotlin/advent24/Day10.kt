package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Field
import utils.neighbors
import utils.result
import kotlin.test.assertEquals

class Day10 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(36, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(461, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)
            var res = 0
            field.forEachIndexed { coords, height ->
                if (height == 0) {
                    res += countTops(field, coords)
                }
            }
            return res
        }


    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(81, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(875, actual)
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)
            var res = 0
            field.forEachIndexed { coords, height ->
                if (height == 0) {
                    res += countTrails(field, coords)
                }
            }
            return res

        }
    }

    private fun countTops(field: Field<Int>, coords: Coords): Int {
        val tops = mutableSetOf<Coords>()
        registerTops(field, coords, tops)
        return tops.size
    }

    private fun countTrails(field: Field<Int>, coords: Coords): Int {
        val tops = mutableSetOf<Coords>()
        return registerTops(field, coords, tops)
    }

    private fun registerTops(field: Field<Int>, coords: Coords, tops: MutableSet<Coords>): Int {
        val height = field[coords]
        if (height == 9) {
            tops.add(coords)
            return 1
        }
        val nextNeighbors = coords.neighbors(field.width - 1, field.height - 1).filter { field[it] == height + 1 }
        var paths = 0
        for (next in nextNeighbors) {
            paths += registerTops(field, next, tops)
        }
        return paths
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): Field<Int> {
            val data = txt.lineSequence().map { line ->
                line.asSequence().map { it.digitToInt() }.toMutableList()
            }.toList()
            return Field(data)
        }
    }
}