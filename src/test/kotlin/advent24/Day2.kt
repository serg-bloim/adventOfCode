package advent24

import utils.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.test.assertEquals

class Day2 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(2, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(564, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val reports = parseInput(txt)
            val safe = reports.count { rep ->
                val increasing = rep[1] > rep[0]
                rep.zipWithNext()
                    .all { pair ->
                        val dist = (pair.first - pair.second).absoluteValue in 1..3
                        val dir = pair.second > pair.first == increasing
                        dist && dir
                    }
            }
            return safe
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(4, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(604, actual)
        }

        fun solve(txt: String): Int {
            val reports = parseInput(txt)
            val safe = reports.count { rep ->
                val unsafeIndex = findFirstUnsafeIndex(rep.asSequence())

                if (unsafeIndex == -1) {
                    true
                } else {
                    // If there is an unsafe index in the sequence,
                    // then try it without the current of two next indexes
                    val s1 = rep.asSequence().withoutIndex(unsafeIndex)
                    val s2 = rep.asSequence().withoutIndex(unsafeIndex + 1)
                    val s3 = rep.asSequence().withoutIndex(unsafeIndex + 2)
                    findFirstUnsafeIndex(s1) == -1
                            || findFirstUnsafeIndex(s2) == -1
                            || findFirstUnsafeIndex(s3) == -1
                }
            }
            return safe
        }

        private fun findFirstUnsafeIndex(measurements: Sequence<Int>): Int {
            return measurements.asSequence().zipWithNext { m1, m2 ->
                // Compares two consecutive measurements
                // If m2 > m1, then returns 1, else returns -1
                // If the difference is 0 or > 3, then returns 0 meaning unsafe distance between m1 and m2
                val diff = m2 - m1
                diff.sign.takeIf { it.absoluteValue <= 3 } ?: 0
            }.zipWithNext()
                .indexOfFirst { (c1, c2) ->
                    // Searching for the first pair that isn't safe
                    c1 != c2 || c1 == 0
                }
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

        fun parseInput(txt: String): List<List<Int>> {
            val data = txt.lineSequence().map { line ->
                line.split("""\s+""".toRegex()).map { it.toInt() }
            }.toList()
            return data
        }
    }
}