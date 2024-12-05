package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.math.min
import kotlin.test.assertEquals

class Day4 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(18, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(2530, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val word = "XMAS"
            val strings = parseInput(txt)
            var cnt = 0
            for (str in strings) {
                cnt += findOccurrances(str, word)
                cnt += findOccurrances(str, word.reversed())
            }
            return cnt
        }
    }

    private fun findOccurrances(str: CharSequence, word: String): Int {
        return str.windowedSequence(word.length).count { it == word }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(9, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(1921, actual)
        }

        fun solve(txt: String): Int {
            val field = txt.lines()
            val width = field[0].length
            val height = field.size
            var cnt = 0
            for (x in 1 until width - 1) {
                for (y in 1 until height - 1) {
                    if (checkXMAS(field, x, y)) cnt++
                }
            }
            return cnt
        }

        private fun checkXmasLine(c1: Char, c2: Char, c3: Char): Boolean {
            val line = "" + c1 + c2 + c3
            return line == "MAS" || line == "SAM"
        }

        private fun checkXMAS(field: List<String>, x: Int, y: Int): Boolean {
            return (field[y][x] == 'A'
                    && checkXmasLine(field[y - 1][x - 1], field[y][x], field[y + 1][x + 1])
                    && checkXmasLine(field[y - 1][x + 1], field[y][x], field[y + 1][x - 1]))
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

        fun parseInput(txt: String): Sequence<CharSequence> {
            val lines = txt.lines()
            val width = lines[0].length
            val height = lines.size
            return sequence {
                yieldAll(lines)
                for (x in 0 until width) {
                    val sb = StringBuilder()
                    for (line in lines) {
                        sb.append(line[x])
                    }
                    yield(sb.toString())
                }
                // diag 1
                for (x in 0 until width) {
                    val sb = StringBuilder()
                    val y = 0
                    for (t in 0 until min(width, height)) {
                        if (x + t == width || y + t == height) break
                        sb.append(lines[y + t][x + t])
                    }
                    yield(sb.toString())
                }
                // diag 2
                for (y in 1 until height) {
                    val sb = StringBuilder()
                    val x = 0
                    for (t in 0 until min(width, height)) {
                        if (x + t == width || y + t == height) break
                        sb.append(lines[y + t][x + t])
                    }
                    yield(sb.toString())
                }
                // diag 3
                for (x in 0 until width) {
                    val sb = StringBuilder()
                    val y = 0
                    for (t in 0 until min(width, height)) {
                        if (x - t < 0 || y + t == height) break
                        sb.append(lines[y + t][x - t])
                    }
                    yield(sb.toString())
                }
                // diag 4
                for (y in 1 until height) {
                    val sb = StringBuilder()
                    val x = width - 1
                    for (t in 0 until min(width, height)) {
                        if (x - t < 0 || y + t == height) break
                        sb.append(lines[y + t][x - t])
                    }
                    yield(sb.toString())
                }
            }
        }
    }
}