package advent23

import advent23.Day12.Solution.countArrangements
import org.junit.jupiter.api.Test

class Day12 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(21, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(7939, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            return txt.lineSequence()
                .map {
                    val (mask, patternStr) = it.split(' ')
                    countArrangements(mask, patternStr)
                }
                .sum()
        }

        fun countArrangements(mask: String, patternStr: String): Long {
            return countArrangements((mask + '.').toList(), patternStr.split(',').map { it.toInt() })
        }


        fun countArrangements(mask: List<Char>, pattern: List<Int>): Long {
            if (pattern.isEmpty()) {
                return if (mask.all { it.canBeOperational() }) 1 else 0
            }
            val start = mask.indexOfFirst { it != '.' }.takeUnless { it == -1 } ?: 0
            val blockLen = pattern.first()
            if (mask.size - start < minSizeRequired(pattern)) return 0
            if (start >= mask.size) {
                println()
            }
            val retValue = if (mask[start] == '#') {
                if (fitsContiguousBlock(mask, start, blockLen)) {
                    countArrangements(mask.skipStart(start + blockLen + 1), pattern.skipStart(1))
                } else {
                    0
                }
            } else {
                var res = 0L
                if (fitsContiguousBlock(mask, start, blockLen)) {
                    res += countArrangements(mask.skipStart(start + blockLen + 1), pattern.skipStart(1))
                }
                res += countArrangements(mask.skipStart(start + 1), pattern)
                res
            }
            return retValue
        }

        fun minSizeRequired(pattern: List<Int>): Int {
            // We count extra 1, cause we add extra 1 in the beginning
            return pattern.sum() + pattern.size
        }

        fun fitsContiguousBlock(mask: List<Char>, start: Int, blockLen: Int): Boolean {
            return mask.subList(start, start + blockLen).all { it.canBeDamaged() }
                    && mask[start + blockLen].canBeOperational()
        }

        fun Char.canBeDamaged() = this == '#' || this == '?'
        fun Char.canBeOperational() = this == '.' || this == '?'
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

    class Tests {
        @Test
        fun testSingleLine1() {
            val mask = ".??..??...?##."
            val pattern = "1,1,3"
            assertEquals(4, countArrangements(mask, pattern))
        }

        @Test
        fun testSingleLine2() {
            val mask = "##"
            val pattern = "2"
            assertEquals(1, countArrangements(mask, pattern))
        }

        @Test
        fun testSingleLine3() {
            val mask = "???"
            val pattern = "2"
            val actual = countArrangements(mask, pattern)
            assertEquals(2, actual)
        }

        @Test
        fun testSingleLine5() {
            val mask = "????.######..#####."
            val pattern = "1,6,5"
            val actual = countArrangements(mask, pattern)
            assertEquals(4, actual)
        }

        @Test
        fun testSingleLine6() {
            val mask = ".??..??...?##."
            val pattern = "1,1,3"
            val actual = countArrangements(mask, pattern)
            assertEquals(4, actual)
        }

        @Test
        fun testSingleLine7() {
            val mask = "..??...?##."
            val pattern = "1,3"
            val actual = countArrangements(mask, pattern)
            assertEquals(2, actual)
        }

        @Test
        fun testSingleLine8() {
            val mask = "?###????????"
            val pattern = "3,2,1"
            val actual = countArrangements(mask, pattern)
            assertEquals(10, actual)
        }
    }
}

