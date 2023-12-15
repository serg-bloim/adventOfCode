package advent23

import org.junit.jupiter.api.Test

class Day15 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(1320, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(498538, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.solve2(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(145, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(286278, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            val res = txt.splitToSequence(',')
                .map { hash(it) }
                .sum()
            return res
        }

        fun hash(str: String): Int {
            val res = str.asSequence()
                .map { it.code }
                .fold(0) { current, new -> ((current + new) * 17) % 256 }
            return res
        }

        fun solve2(txt: String): Any {
            val boxes = Array(256) { LinkedHashMap<String, Lens>() }
            val lensRe = Regex("""(\w+)(-|=)(\d*)""")
            val lenses = txt.splitToSequence(',').map {
                val ( lbl, op, focal) = lensRe.matchEntire(it)!!.destructured
                Lens(lbl, op.first(), focal.takeIf { it.isNotEmpty() }?.toInt() ?: 0)
            }
            for (lens in lenses) {
                val hsh = hash(lens.lbl)
                val box = boxes[hsh]
                if (lens.op == '-') {
                    box.remove(lens.lbl)
                }
                if (lens.op == '=') {
                    val existing = box[lens.lbl]
                    if (existing != null) {
                        existing.focal = lens.focal
                    } else {
                        box.put(lens.lbl, lens)
                    }
                }
            }
            var res = 0
            for ((bi, box) in boxes.withIndex()) {
                for ((li, lens) in box.values.withIndex()) {
                    val lensFocal = (1 + bi) * (1 + li) * lens.focal
                    res += lensFocal
                }
            }
            return res
        }
    }

    data class Lens(val lbl: String, val op: Char, var focal: Int)

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }
    }

    object Tests {
        @Test
        fun test_hash() {
            println(Solution.hash("rn"))
            println(Solution.hash("cm"))
        }
    }
}
