package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class Day13 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.sumReflections(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(405, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.sumReflections(load_prod())
            println("Result: $actual")
            assertEquals(37113, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.sumReflections(load_test(), 1)
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(400, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.sumReflections(load_prod(), 1)
            println("Result: $actual")
            assertEquals(30449, actual)
        }
    }

    object Solution {
        fun sumReflections(txt: String, smudges: Int = 0): Any {
            val total = txt.lineSequence()
                .map { it.trim() }
                .chunked { it.isEmpty() }
                .map { block ->
                    val horizontalReflections = findReflections(block.toList(), smudges).toList()
                    val verticalReflections = findReflections(transpose(block), smudges).toList()

                    verticalReflections.sum() + horizontalReflections.sum() * 100
                }.sum()
            return total
        }

        fun transpose(block: Sequence<String>): List<String> {
            val inpIter = block.iterator()
            val first = inpIter.next()
            val rows = first.map { ch -> StringBuilder().also { it.append(ch) } }
            for (inpRow in inpIter) {
                rows.asSequence().zip(inpRow.asSequence()).forEach { (acc, ch) ->
                    acc.append(ch)
                }
            }
            return rows.map { it.toString() }
        }

        private fun findReflections(block: List<CharSequence>, smudges: Int): Sequence<Int> {
            for (split in 0..<block.size - 1) {
                var left = split
                var right = left + 1
                var differences = 0
                while (left >= 0 && right < block.size) {
                    differences += diff(block[left], block[right])
                    if (differences > smudges) break
                    left--
                    right++
                }
                if (differences == smudges) {
                    return sequenceOf(split + 1)
                }
            }
            return sequenceOf(0)
        }

        private fun diff(str1: CharSequence, str2: CharSequence): Int {
            return str1.zip(str2) { a, b -> if (a == b) 0 else 1 }.sum()
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

    class Tests {
        @Test
        fun testTranspose() {
            val txt = """
                123
                456
                789
                abc
            """.trimIndent()
            val transposed = Solution.transpose(txt.lineSequence())
            val expected = """
                147a
                258b
                369c
            """.trimIndent().lines()
            assertContentEquals(expected, transposed)
        }

        @Test
        fun testSingleBlock() {
            val txt = """
                ...##.###.#
                ...##.###.#
                ##..#....#.
                ##.....#.##
                #.##..#.#.#
                ..##...#.#.
                ...#####..#
            """.trimIndent()
            val actual = Solution.sumReflections(txt)
            assertEquals(100, actual)
        }

        @Test
        fun testSingleBlock2() {
            val txt = """
                    #.##..##.
                    ..#.##.#.
                    ##......#
                    ##......#
                    ..#.##.#.
                    ..##..##.
                    #.#.##.#.
            """.trimIndent()
            val actual = Solution.sumReflections(txt, 1)
            assertEquals(3, actual)
        }
    }
}