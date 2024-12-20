package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Field
import utils.dbg
import utils.floodFillVisit
import utils.result
import kotlin.math.min
import kotlin.test.assertEquals

private const val PATH_PRESENT = -1
private const val PATH_ABSENT = 1
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
            val cells: Sequence<Coords> = start.floodFillVisit(maxX, maxY) { from, to ->
                if (field[to] == CELL_CORRUPTED) return@floodFillVisit false
                field[to] = min(field[from] + 1, field[to])
                true
            }
            cells.count() // It needs any terminating operation to run through the algorithm
            return field[Coords(maxX, maxY)]
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), 6, 6, 12)
            assertEquals("6,1", actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 70, 70, 1024)
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals("20,12", actual)
        }

        @Test
        fun testBinarySearch() {
            val lst = listOf(1, 2, 3, 4, 5, 7, 8, 9)
            val res = lst.binarySearch(3) {
                when (it) {
                    in 0..5 -> -1
                    6 -> 0
                    else -> 1
                }
            }
            val insertionPoint = -res - 1
            result.println("Result: $insertionPoint")
            println("Result: $insertionPoint")
        }

        fun solve(txt: String, maxX: Int, maxY: Int, startingN: Int): Any {
            val bytes = parseInput(txt)
            val field = Field(maxX + 1, maxY + 1) { Int.MAX_VALUE }
            val start = Coords(0, 0)
            val end = Coords(maxX, maxY)
            val res = bytes.indices.toList().binarySearch(fromIndex = startingN) { n ->
                dbg.println("$n : ${bytes[n]}")
                field.forEachIndexed { xy, _ -> field[xy] = Int.MAX_VALUE }

                for (bXY in bytes.asSequence().take(n)) {
                    field[bXY] = CELL_CORRUPTED
                }
                if (hasPath(field, start, end)) PATH_PRESENT else PATH_ABSENT
            }
            val insertionPoint = -res - 2
            val lastByte = bytes[insertionPoint]
            return "${lastByte.x},${lastByte.y}"
        }

        private fun hasPath(field: Field<Int>, start: Coords, end: Coords): Boolean {
            val memoryConnectedToStart =
                start.floodFillVisit(field.width - 1, field.height - 1) { from, to -> field[to] != CELL_CORRUPTED }
            return memoryConnectedToStart.contains(end)
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
            val data = txt.lineSequence().filterNot { it.isEmpty() }.map { line ->
                val (x, y) = line.split(",").map { it.toInt() }
                Coords(x, y)
            }.toList()
            return data
        }
    }
}