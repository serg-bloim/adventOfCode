package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.pow
import utils.result
import kotlin.test.assertEquals

class Day7 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(3749L, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(555555555, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val equations = parseInput(txt)
            val sum = equations.filter { (res, nums) -> testEq(res, nums) }.sumOf { (res, nums) -> res }
            return sum
        }
    }

    private fun testEq(res: Long, nums: List<Long>): Boolean {
        for (ops in generateOperators(nums.size - 1)) {
            if (eval(nums, ops) == res) return true
        }
        return false
    }

    private fun eval(nums: List<Long>, ops: Sequence<Operator>): Long {
        return nums.asSequence().drop(1).zip(ops)
            .fold(nums.first()) { total, (n, op) -> op.func(total, n) }
    }

    enum class Operator(val func: (Long, Long) -> Long) {
        Plus(Long::plus),
        Times(Long::times)
    }

    private fun generateOperators(n: Int): Sequence<Sequence<Operator>> {
        return sequence {
            val max = 2.pow(n) - 1
            val container = Array(n) { Operator.Plus }
            for (perm in 0..max) {
                var perm2 = perm
                for (i in 0 until n) {
                    container[i] = if (perm2.and(1) == 1) Operator.Plus else Operator.Times
                    perm2 = perm2.shr(1)
                }
                yield(container.asSequence())
            }
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Int {
            return 11111111
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

        fun parseInput(txt: String): List<Pair<Long, List<Long>>> {
            val data = txt.lineSequence().map { line ->
                val (res, nums) = line.split(':')
                Pair(res.toLong(), nums.trim().split(' ').map { it.toLong() })
            }.toList()
            return data
        }
    }
}