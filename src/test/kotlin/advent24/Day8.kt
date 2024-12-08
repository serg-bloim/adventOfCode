package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.Field
import utils.Primes
import utils.greatestCommonDivisor
import utils.permutations2
import utils.result
import utils.withinBox
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

class Day8 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(14, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(379, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val field = parseInput(txt)
            val antennas = mutableMapOf<Char, MutableList<Coords>>()
            field.forEachIndexed { xy, v -> if (v != '.') antennas.computeIfAbsent(v) { mutableListOf() }.add(xy) }
            val antinodes = mutableSetOf<Coords>()
            for ((freq, antennas) in antennas) {
                for ((antenna1, antenna2) in antennas.permutations2()) {
                    val (an1, an2) = findAntinodes(antenna1, antenna2)
                    antinodes.add(an1)
                    antinodes.add(an2)
                }
            }
            return antinodes.count { it.withinBox(field.width, field.height) }
        }
    }

    private fun findAntinodes(a1: Coords, a2: Coords): Pair<Coords, Coords> {
        val dx = a2.x - a1.x
        val dy = a2.y - a1.y
        val node1 = Coords(a1.x - dx, a1.y - dy)
        val node2 = Coords(a2.x + dx, a2.y + dy)
        return Pair(node1, node2)
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(34, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(1339, actual)
        }

        fun solve(txt: String): Int {
            val field = parseInput(txt)
            Primes.getUntil((field.width + field.height).toLong())
            val antennas = mutableMapOf<Char, MutableList<Coords>>()
            field.forEachIndexed { xy, v -> if (v != '.') antennas.computeIfAbsent(v) { mutableListOf() }.add(xy) }
            val antinodes = mutableSetOf<Coords>()
            for ((freq, antennas) in antennas) {
                for ((antenna1, antenna2) in antennas.permutations2()) {
                    for (antinode in findAntinodesContinuously(antenna1, antenna2, field)) {
                        antinodes.add(antinode)
                    }
                }
            }
            return antinodes.count { it.withinBox(field.width, field.height) }

        }
    }

    private fun findAntinodesContinuously(a1: Coords, a2: Coords, field: Field<Char>): Sequence<Coords> {
        val dx = a2.x - a1.x
        val dy = a2.y - a1.y
        val lcd = greatestCommonDivisor(dx.absoluteValue.toLong(), dy.absoluteValue.toLong())
        val min_dx = dx / lcd.toInt()
        val min_dy = dy / lcd.toInt()
        return sequence {
            yieldAll(generateSequence(a1) { Coords(it.x + min_dx, it.y + min_dy) }
                .takeWhile { it.withinBox(field.width, field.height) })
            yieldAll(generateSequence(a1) { Coords(it.x - min_dx, it.y - min_dy) }
                .drop(1)
                .takeWhile { it.withinBox(field.width, field.height) })
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
            val data = txt.lines().map { line ->
                line.toCharArray().toMutableList()
            }.toList()
            return Field(data)
        }
    }
}