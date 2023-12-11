package advent22

import advent23.pow
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day25 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals("2=-1=0", actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        @Test
        fun printDecimals() {
            load_prod().lineSequence()
                .forEach { snafu ->
                    println("$snafu  -  ${snafu2long(snafu)}")
                }
            val sum = load_prod().lineSequence().map { snafu2long(it) }.sum()
            println("Sum: $sum")
        }
        @Test
        fun printSnafu2Int() {
            val snafu = "1=101-=111-0==--"
            assertEquals(19566887194, snafu2long(snafu))
        }

        fun solve(txt: String): Any {
            val sum = txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map {
                    snafu2long(it)
                }.sum()
            println("Sum :" + sum)
            return long2snafu(sum)
        }

        private fun long2snafu(v: Long): String {
            var num = sequence {
                var buffer = v
                while (buffer != 0L) {
                    var a = (buffer % 5).toInt()
                    buffer /= 5
                    if (a > 2) {
                        a -= 5
                        buffer++
                    }
                    if (a < -2) {
                        a += 5
                        buffer--
                    }
                    yield(a)
                }
            }.map {
                when (it) {
                    -2 -> '='
                    -1 -> '-'
                    else -> it.digitToChar()
                }
            }.joinToString("").reversed()
            return num
        }

        private fun snafu2long(snafu: String): Long {
            return snafu.reversed()
                .asSequence()
                .mapIndexed { index, digit ->
                    val decimal = when (digit) {
                        '=' -> -2
                        '-' -> -1
                        else -> digit.digitToInt()
                    }
                    val pow = 5L.pow(index)
                    decimal * pow
                }.sum()
        }
    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(1623178306, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Any {

            return ""
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
    }
}

