package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.dbg
import utils.floodFillVisit
import utils.result
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
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

        @Test
        fun testPrint() {
            val actual = parseInput(load_prod())
            dbg.println(printMapToString(actual.map { Pair(it.component1(), it.component2()) }, 101, 103))
        }

        val robots = parseInput(load_prod())
        @Test
        fun testSimple(){
            testSnapshot(6577)
        }
        fun testSnapshot(moves: Int) {
            val width = 101
            val height = 103
//            println(456)
//            println(456)
//            println(456)
//            println(456)
            val positions = robots.map { (x0, y0, vx, vy) -> moveRobot(moves, x0, y0, vx, vy, width, height) }
            println(printMapToString(positions, width, height))
        }

        @Test
        fun testOrderByCluster() {
            val filename = System.getProperty("user.dir") + "/data.txt"
            val lineRE = """(\d+) : (\d+)""".toRegex()
            val topMoves = File(filename).useLines {
                it.filter { it.isNotEmpty() }
                    .map {
                        val (_, moves, clusterSize) = lineRE.matchEntire(it)!!.groupValues
                        Pair(moves.toInt(), clusterSize.toInt())
                    }.sortedByDescending { it.second }
                    .take(20)
                    .toList()
            }
            for ((move, clusterSize) in topMoves) {
                println("$move : $clusterSize")
            }
        }

        @Test
        fun testProgress() {
            val filename = System.getProperty("user.dir") + "/data.txt"
            val lastMove = File(filename).useLines {
                val lastLine = it.filter { it.isNotEmpty() }.lastOrNull() ?: "0 : 0"
                """(\d+) : (\d+)""".toRegex().matchEntire(lastLine)!!.groupValues[1].toInt()
            }
            val start = lastMove + 1
            val robots = parseInput(load_prod())
            val width = 101
            val height = 103
//            generateSequence(0) { it + 1 }
//                .map { moves -> robots.map { (x0, y0, vx, vy) -> moveRobot(moves, x0, y0, vx, vy, width, height) } }
            val out = PrintWriter(BufferedWriter(FileWriter(filename, true)))
            for (moves in start..start + 100000) {
                val positions = robots.map { (x0, y0, vx, vy) -> moveRobot(moves, x0, y0, vx, vy, width, height) }
                val size = findBiggestCluster(positions, width, height)
                out.println("$moves : $size")
                if (moves % 10000 == 0)
                    out.flush()
//                println(printMapToString(positions, width, height))
//                Thread.sleep(100)
            }
            out.close()
        }

        fun solve(txt: String): Any {
            return 11111111
        }
    }

    private fun findBiggestCluster(positions: List<Pair<Int, Int>>, width: Int, height: Int): Int {
        val data = Array(height) { IntArray(width) { 0 } }
        for ((x, y) in positions) {
            data[y][x] = 1
        }
        val toProcess = positions.asSequence().map { Coords(it.first, it.second) }.toMutableSet()
        val clusters = generateSequence {
            if (toProcess.isEmpty()) return@generateSequence null
            val next = toProcess.first()
            val clusterSize = next.floodFillVisit(width - 1, height - 1) { from, to -> to in toProcess }
                .onEach { toProcess.remove(it) }
                .count()
            clusterSize
        }
        return clusters.max()
    }


//    private fun printMapToString(robots: List<List<Int>>, w: Int, h: Int): String {
//        val data = Array(h) { CharArray(w) { '.' } }
//        robots.forEach {
//            val x = it[0]
//            val y = it[1]
//            data[y][x] = 'O'
//        }
//        return data.joinToString("\n") { it.joinToString("") }
//    }

    private fun printMapToString(robots: List<Pair<Int, Int>>, w: Int, h: Int): String {
        val data = Array(h) { CharArray(w) { '.' } }
        robots.forEach {
            val x = it.first
            val y = it.second
            data[y][x] = 'O'
        }
        return data.joinToString("\n") { it.joinToString("") }
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
        val x = ((x0 + vx * moves) % width + width) % width
        val y = ((y0 + vy * moves) % height + height) % height
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

fun main(args: Array<String>) {
//    val cmd = arrayOf("/bin/sh", "-c", "stty raw </dev/tty")
//    Runtime.getRuntime().exec(cmd).waitFor()
    val task2 = Day14().Task2()
    var moves = args.firstOrNull()?.let { it.toInt() } ?: 0
    println(moves)
    task2.testSnapshot(moves)
    while (true) {
        val c = System.`in`.read().toChar()
        when (c) {
            'n' -> moves++
            '\n' -> moves++
            'b' -> moves--
        }
        if (moves < 0) moves = 0
        print("\u001b[2J")
        println(moves)
        println()
        println()
        task2.testSnapshot(moves)
        Thread.sleep(30)
    }

}