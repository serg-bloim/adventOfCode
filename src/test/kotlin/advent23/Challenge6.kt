package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Challenge6 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(467835, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        val gears = mutableMapOf<Coords, MutableList<Int>>()
        val numRe = Regex("""\d+""")
        val lines = txt.lines()
        val mask = buildMask(lines)
        lines.asSequence().forEachIndexed { y, line ->
            numRe.findAll(line).forEach {
                val num = it.value.toInt()
                getNumberBoundary(
                    it.range,
                    y
                )                                      // Calc the boundary around the number
                    .filter { (x, y) -> x in line.indices && y in lines.indices }   // Make sure we are not processing coords out of the map.
                    .filter { lines[it.y][it.x] == '*' }                   // Get those cells that have a gear in it
                    .forEach { gearCoords -> gears.computeIfAbsent(gearCoords) { mutableListOf() }.add(num) }  // Add the number into a respective list for a particular gear
            }
        }
        return gears.values.asSequence()
            .filter { it.size == 2 }
            .map {
                val (a, b) = it
                a * b
            }
            .sum()
    }

    private fun getNumberBoundary(xRange: IntRange, y: Int) = sequence {
        yieldAll(xRange.map { x -> Coords(x, y - 1) })
        yieldAll(xRange.map { x -> Coords(x, y + 1) })
        for (dy in -1..1) {
            yield(Coords(xRange.first - 1, y + dy))
        }
        for (dy in -1..1) {
            yield(Coords(xRange.last + 1, y + dy))
        }
    }

    private fun buildMask(lines: List<String>): Mask {
        val mask = Mask(lines.size) { BooleanArray(lines[0].length) }
        for (y0 in lines.indices) {
            val line = lines[y0]
            for (x0 in lines.indices) {
                val ch = line[x0]
                if (ch == '*')
                    for (dx in -1..1) {
                        for (dy in -1..1) {
                            if (dx == 0 && dy == 0)
                                continue
                            val x = x0 + dx
                            val y = y0 + dy
                            mask[y][x] = true
                        }
                    }
            }
        }
        return mask
    }

    private fun load_test(): String {
        return Resources().loadString("ch5_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch5_prod.txt")
    }
}