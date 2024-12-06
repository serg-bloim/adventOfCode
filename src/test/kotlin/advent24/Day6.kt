package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.move
import utils.result
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.test.assertEquals

class Day6 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(41, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(555555555, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)
            val lastX = field[0].size - 1
            val lastY = field.size - 1

            val startPos = 1.let {
                for (y in field.indices) {
                    for (x in field[y].indices) {
                        if (field[y][x] == 2) {
                            return@let Coords(x, y)
                        }
                    }
                }
                return@let Coords(0, 0)
            }
            var pos = startPos
            var dir = Direction.South
            while (!(pos.x == 0 || pos.y == 0 || pos.x == lastX || pos.y == lastY)) {
                val next = pos.move(dir)
                if (field[next.y][next.x] == 1) {
                    dir = dir.left() // Cause y is inverted
                } else {
                    pos = next
                    field[next.y][next.x] = 2
//                    println("Move to ${pos}")
                }
            }
            val visited = field.sumOf { it.count { it == 2 } }
            return visited
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Int {
            return 11111111
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

        fun parseInput(txt: String): List<MutableList<Int>> {
            val data = txt.lineSequence().map { line ->
                line.map {
                    when (it) {
                        '.' -> 0
                        '#' -> 1
                        '^' -> 2
                        else -> throw Exception("Bad input")
                    }
                }.toMutableList()
            }.toList()
            return data
        }
    }
}