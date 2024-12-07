package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.permutations
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
            assertEquals(1582598718861L, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val equations = parseInput(txt)
            val operatorScope = listOf(Operator.Plus, Operator.Times)
            val sum = equations.filter { (res, nums) -> testEq(res, nums, operatorScope) }.sumOf { (res, nums) -> res }
            return sum
        }
    }

    private fun testEq(res: Long, nums: List<Long>, operatorScope: List<Operator>): Boolean {
        for (ops in generateOperators(nums.size - 1, operatorScope)) {
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
        Times(Long::times),
        Combine(::comb);
    }

    private fun generateOperators(n: Int, operatorScope: List<Operator>): Sequence<Sequence<Operator>> {
        return sequence {
            val radix = operatorScope.size
            val max = radix.pow(n) - 1
            val container = Array(n) { Operator.Plus }
            for (perm in 0..max) {
                var perm2 = perm.toString(radix).padStart(n, '0')
                for (i in 0 until n) {
                    container[i] = operatorScope[perm2[i].digitToInt(radix)]
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
            assertEquals(11387L, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(165278151522644L, actual)
        }

        @Test
        fun testPermutations() {
            val scope = listOf(0, 1, 2)
            for (p in scope.permutations(3)) {
                val msg = p.map { it.digitToChar() }.joinToString("")
                result.println(msg)
                println(msg)
            }
        }

        fun solve(txt: String): Long {
            val equations = parseInput(txt)
            val operatorScope = listOf(Operator.Plus, Operator.Times, Operator.Combine)
            val sum = equations.filter { (res, nums) -> testEq(res, nums, operatorScope) }.sumOf { (res, _) -> res }
            return sum

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

        fun comb(a: Long, b: Long) = (a.toString() + b.toString()).toLong()
    }
}