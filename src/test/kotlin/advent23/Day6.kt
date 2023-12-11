package advent23

import org.junit.jupiter.api.Test
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.test.assertEquals

class Day6 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(288, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            assertEquals(2269432, actual)
        }

        fun solve(txt: String): Any {
            val (times, records) = txt.lineSequence()
                .map {
                    Regex("""\d+""").findAll(it)
                        .map { it.value.toInt() }
                }.toList()
            return times.zip(records, Companion::calcWins).reduce { a, b -> a * b }
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(71503L, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            assertEquals(35865985L, actual)
        }

        fun solve(txt: String): Any {
            val (time, record) = txt.lineSequence()
                .map {
                    Regex("""\d+""").findAll(it)
                        .joinToString("") { it.value }
                        .toLong()
                }.toList()
            return calcWins(time, record)
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

        /**
         * T - time for race
         * R - record
         * x - time accing
         * y - distance
         * y = v(x) * (T-x) = x * (T-x) = -x^2 + Tx
         * x^2 - Tx + R = 0
         * x1 = [T + sqrt(T2 - 4R)]/2
         * T2 = [T - sqrt(T2 - 4R)]/2
         *
         */
        fun calcWins(time: Int, record: Int) = calcWins(time.toLong(), record.toLong()).toInt()
        fun calcWins(time: Long, record: Long): Long {
            val x1 = (time - sqrt((time * time).toDouble() - 4 * record)) / 2
            val x2 = (time + sqrt((time * time).toDouble() - 4 * record)) / 2
            return (ceil(x2) - floor(x1)).toInt() - 1L
        }
    }

    internal class Tests {
        @Test
        fun testCalcWins() {
            assertEquals(4, calcWins(7, 9))
            assertEquals(8, calcWins(15, 40))
            assertEquals(9, calcWins(30, 200))
        }
    }
}

