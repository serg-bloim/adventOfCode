package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Field
import utils.floodFillVisit
import utils.result
import kotlin.math.min
import kotlin.test.assertEquals

private const val CELL_CORRUPTED = -1

class Day18 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), 6, 6, 12)
            assertEquals(22, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 70, 70, 1024)
            result.println("Result: $actual")
            assertEquals(334, actual)
            println("Result: $actual")
        }

        fun solve(txt: String, maxX: Int, maxY: Int, n: Int): Any {
            val bytes = parseInput(txt).take(n)
            val field = Field(maxX + 1, maxY + 1) { Int.MAX_VALUE }
            for (bXY in bytes) {
                field[bXY] = CELL_CORRUPTED
            }
            val start = Coords(0, 0)
            field[start] = 0
            val cells: Sequence<Coords> = start.floodFillVisit(maxX, maxY)
            { from, to ->
                if (field[to] == CELL_CORRUPTED) return@floodFillVisit false
                field[to] = min(field[from] + 1, field[to])
                true
            }
            cells.forEach { }
            return field[Coords(maxX, maxY)]
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

        fun solve(txt: String): Any {
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

        fun parseInput(txt: String): List<Coords> {
            val data = txt.lineSequence()
                .filterNot { it.isEmpty() }
                .map { line ->
                    val (x, y) = line.split(",").map { it.toInt() }
                    Coords(x, y)
                }.toList()
            return data
        }
    }
}