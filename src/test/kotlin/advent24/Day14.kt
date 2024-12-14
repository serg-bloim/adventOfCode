package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.dbg
import utils.result
import kotlin.test.assertEquals

class Day14 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test(), 11, 7)
            assertEquals(12L, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 101, 103)
            result.println("Result: $actual")
            assertEquals(229421808L, actual)
            println("Result: $actual")
        }

        fun solve(txt: String, width: Int, height: Int): Any {
            var q1 = 0L
            var q2 = 0L
            var q3 = 0L
            var q4 = 0L
            val robots = parseInput(txt)
            robots.map { (x0, y0, vx, vy) -> moveRobot(100, x0, y0, vx, vy, width, height) }
                .onEach { (x, y) -> dbg.println("$x $y") }
                .map { (x, y) ->
                    when (getQuadrant(x, y, width, height)) {
                        1 -> q1++
                        2 -> q2++
                        3 -> q3++
                        4 -> q4++
                        else -> {}
                    }
                }
            return q1 * q2 * q3 * q4
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

    private fun getQuadrant(x: Int, y: Int, width: Int, height: Int): Any {
        if (x == width / 2 || y == height / 2) return 0
        if (x < width / 2) {
            if (y < height / 2) return 1
            else return 2
        } else {
            if (y < height / 2) return 3
            else return 4
        }
    }

    private fun moveRobot(
        moves: Int,
        x0: Int,
        y0: Int,
        vx: Int,
        vy: Int,
        width: Int,
        height: Int
    ): Pair<Int, Int> {
//        val x = generateSequence(x0) { (it + vx + width) % width }.drop(moves).first()
//        val y = generateSequence(y0) { (it + vy + height) % height }.drop(moves).first()
        val x = ((x0 + vx*moves) % width + width) % width
        val y = ((y0 + vy*moves) % height + height) % height
        return Pair(x, y)
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): List<List<Int>> {
            val lineRegex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
            val data = txt.lineSequence().map { line ->
                lineRegex.matchEntire(line)!!.groupValues
                    .drop(1)
                    .map { it.toInt() }
            }.toList()
            return data
        }
    }
}