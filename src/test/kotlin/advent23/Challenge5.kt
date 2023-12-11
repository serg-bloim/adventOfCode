package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

typealias Mask = Array<BooleanArray>

internal class Challenge5 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(4361, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        val numRe = Regex("""\d+""")
        val lines = txt.lines()
        val mask = buildMask(lines)
        return lines.asSequence().flatMapIndexed { y, line ->
            numRe.findAll(line).filter {
                it.range.any { x -> mask[y][x] }
            }.map { it.value.toInt() }
        }.sum()
    }

    private fun buildMask(lines: List<String>): Mask {
        val mask = Mask(lines.size) { BooleanArray(lines[0].length) }
        for (y0 in lines.indices) {
            val line = lines[y0]
            for (x0 in lines.indices) {
                val ch = line[x0]
                if (ch.isDigit() || ch == '.')
                    continue
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

