package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Direction
import utils.Field
import utils.move
import utils.result
import utils.waterfallVisit
import kotlin.test.assertEquals

class Day12 {
    data class PlotStats(var area: Int, var perimeter: Int)

    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(1930, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(1477924, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val NO_PLOT = 0
            val plotStats = mutableMapOf(NO_PLOT to PlotStats(0, 0))
            val inputField = parseInput(txt)
            val fieldSeparatePlots = inputField.map { NO_PLOT }
            var nextPlotId = 1
            fieldSeparatePlots.forEachIndexed { coords, i ->
                if (i == NO_PLOT) {
                    val plotId = nextPlotId++
                    val plotBlocks = findPlotBlocks(inputField, coords)
                    val area = plotBlocks.onEach { fieldSeparatePlots[it] = plotId }
                        .count()
                    plotStats[plotId] = PlotStats(area, 0)
                }
            }
            val field = fieldSeparatePlots.withBorder(1, NO_PLOT)
            for (y in 0..<field.height - 1) {
                for (x in 0..<field.width - 1) {
                    val coords = Coords(x, y)
                    val cropType = field[coords]
                    val stats = plotStats[cropType]!!
                    sequenceOf(Direction.East, Direction.North)
                        .map { neighborDirection -> coords.move(neighborDirection) }
                        .map { neighbor -> field[neighbor] }
                        .filter { neighborCropType -> neighborCropType != cropType }
                        .forEach { neighborCropType ->
                            plotStats[neighborCropType]!!.perimeter += 1
                            stats.perimeter += 1
                        }
                }
            }
            plotStats.remove(NO_PLOT)
            return plotStats.values.sumOf { it.area * it.perimeter }
        }
    }

    private fun findPlotBlocks(field: Field<Char>, start: Coords): Sequence<Coords> {
        return start.waterfallVisit(field.width - 1, field.height - 1) { from, to -> field[from] == field[to] }
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

        fun parseInput(txt: String): Field<Char> {
            val data = txt.lineSequence().map { line ->
                line.asSequence().toMutableList()
            }.toList()
            return Field(data)
        }
    }
}