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
    object Solution {
        fun sumReflections(txt: String): Any {
            val total = txt.lineSequence()
                .map { it.trim() }
                .chunked { it.isEmpty() }
                .map { block ->
                    val verticalReflections = findReflections(block).toList()
                    val horizontalReflections = findReflections(transpose(block).asSequence()).toList()

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

        private fun findReflections(block: Sequence<CharSequence>): Sequence<Int> {
            return block.map { reflectionsInLine(it).toSet() }
                .reduce { a, b -> a.intersect(b) }
                .asSequence()
        }

        private fun reflectionsInLine(line: CharSequence): Sequence<Int> {
            return sequence {
                for (split in 0..<line.length - 1) {
                    var left = split
                    var right = left + 1
                    var hasReflection = true
                    while (left >= 0 && right < line.length) {
                        if (line[left] != line[right]) {
                            hasReflection = false
                            break
                        }
                        left--
                        right++
                    }
                    if (hasReflection) {
                        yield(split + 1)
                    }
                }
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
    }
}

