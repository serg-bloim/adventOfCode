package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.test.assertEquals

class Day19 {
    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(6, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(209, actual)
            println("Result: $actual")
        }

        @Test
        fun testSeq() {
            val nums = (0..10).toList()
            val evens = nums.asSequence().filter { it % 2 == 0 }
            val max = evens.max()
            val cnt = evens.count()
            println("Max: $max, Count: $cnt")
        }

        fun solve(txt: String): Any {
            val (patterns, designs) = parseInput(txt)
            return designs.count { design -> isPossible(design, patterns) }
        }

        private fun isPossible(design: CharSequence, patterns: List<String>): Boolean {
            val matchingPatterns = patterns.asSequence().filter { design.startsWith(it) }
            if (matchingPatterns.any { it.length == design.length }) {
                return true
            }
            for (pat in matchingPatterns) {
                if (isPossible(design.drop(pat.length), patterns)) return true
            }
            return false
        }
    }

    @Nested
    inner class Task2 {
        val cache = mutableMapOf<CharSequence, Long>()

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(16, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(777669668613191, actual)
        }

        fun solve(txt: String): Any {
            val (patterns, designs) = parseInput(txt)
            var i = 0
            return designs
//                .onEachIndexed { index, s -> println("Processing $index/${designs.size}") }
                .sumOf { design ->
                    println("Processing ${i++}/${designs.size}")
                    isPossible(design, patterns)
                }
        }

        private fun isPossible(design: CharSequence, patterns: List<String>): Long {
            if (design in cache) return cache[design]!!
            val matchingPatterns = patterns.asSequence().filter { design.startsWith(it) }
            var res = 0L
            for (pat in matchingPatterns) {
                if (pat.length == design.length)
                    res += 1
                else
                    res += isPossible(design.drop(pat.length), patterns)
            }
            cache[design] = res
            return res
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

        fun parseInput(txt: String): Pair<List<String>, List<String>> {
            val lines = txt.lineSequence()
                .filter { it.isNotEmpty() }
                .iterator()
            val patterns = lines.next().split(", ").toList()
            val designs = lines.asSequence().toList()
            return Pair(patterns, designs)
        }
    }
}