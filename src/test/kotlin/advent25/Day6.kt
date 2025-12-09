package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result
import java.math.BigInteger

class Day6 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(4277556, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(5346286649122, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val (data, opsStr) = parseInput(txt)
            val func: (Long, Long) -> Long = Long::times
            val ops: List<(Long, Long) -> Long> = opsStr.map {
                when (it) {
                    "*" -> Long::times
                    else -> Long::plus
                }
            }
            val answers = data.reduce { row1, row2 -> process(row1, row2, ops) }
            return answers.sum()
        }

        private fun process(row1: List<Long>, row2: List<Long>, ops: List<(Long, Long) -> Long>) =
            row1.zip(row2).zip(ops) { (v1, v2), op -> op(v1, v2) }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(BigInteger.valueOf(3263827), actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(BigInteger.valueOf(10389131401929), actual)
        }

        fun solve(txt: String): Any {
            val originalLines = txt.lines()
            val width = originalLines.map { it.length }.max()
            val transposedLines = IntRange(0, width).reversed()
                .asSequence()
                .map { column ->
                    val buffer = StringBuilder()
                    originalLines.forEach { line ->
                        buffer.append(line.getOrElse(column) { ' ' })
                    }
                    buffer.toString()
                }
                .filterNot { it.isBlank() }
            var group = mutableListOf<Long>()
            var globalRes = BigInteger.valueOf(0)
            for (tline in transposedLines) {
                val (numStr, opStr) = """(\d+)\s*(\+|\*)?""".toRegex().matchEntire(tline.trim())!!.destructured
                val num = numStr.toLong()
                group.add(num)
                val op: ((Long, Long) -> Long)? = when (opStr) {
                    "+" -> Long::plus
                    "*" -> Long::times
                    else -> null
                }
                if (op != null) {
                    val groupRes = group.reduce(op)
                    globalRes = globalRes.add(BigInteger.valueOf(groupRes))
                    group.clear()
                }
            }
            return globalRes
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

        fun parseInput(txt: String): Pair<List<List<Long>>, List<String>> {
            val data = txt.lines()
                .map { line -> line.trim().split("""\s+""".toRegex()) }
            val numbers = data.subList(0, data.size - 1).map { it.map { it.toLong() } }
            val ops = data.last()
            return Pair(numbers, ops)
        }
    }
}