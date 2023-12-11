package advent22

import advent23.chunked
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.max
import kotlin.test.assertEquals

class Day5 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals("CMZ", actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            assertEquals("VCTFTJQCG", actual)
        }

        fun solve(txt: String): Any {
            val (stacks, moves) = txt.lineSequence()
                .chunked { it.trim().isEmpty() }
                .toList().let { (crates, moves) ->
                    val stacks = crates
                        .map { line ->
                            line.chunked(4).map { it.trim() }
                        }.takeWhile { it[0].toIntOrNull() == null }
                        .toList()
                        .reversed()
                        .asSequence()
                        .fold(listOf<Stack<String>>()) { acc, row ->
                            val infiniteStacks = acc.asSequence() + generateSequence { Stack() }
                            val infiniteRow = row.asSequence() + generateSequence { "" }
                            infiniteStacks.zip(infiniteRow)
                                .take(max(acc.size, row.size))
                                .onEach { (stack, crate) -> if (crate.isNotEmpty()) stack.push(crate) }
                                .map { (stack, crate) -> stack }
                                .toList()
                        }
                    val numRe = Regex("""\d+""")
                    val moves = moves
                        .map { numRe.findAll(it).map { it.value.toInt() }.toList() }
                        .map { (n, src, dst) -> Move(stacks[src - 1], stacks[dst - 1], n) }
                        .toList()
                    Pair(stacks, moves)
                }
            for (m in moves) {
                repeat(m.n) {
                    m.dst.push(m.src.pop())
                }
            }
            return stacks.filterNot { it.empty() }.map { it.peek() }
                .map { it[1] }
                .joinToString("")
        }
    }
    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals("MCD", actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            assertEquals("GCFGLDNJZ", actual)
        }

        fun solve(txt: String): Any {
            val (stacks, moves) = txt.lineSequence()
                .chunked { it.trim().isEmpty() }
                .toList().let { (crates, moves) ->
                    val stacks = crates
                        .map { line ->
                            line.chunked(4).map { it.trim() }
                        }.takeWhile { it[0].toIntOrNull() == null }
                        .toList()
                        .reversed()
                        .asSequence()
                        .fold(listOf<Stack<String>>()) { acc, row ->
                            val infiniteStacks = acc.asSequence() + generateSequence { Stack() }
                            val infiniteRow = row.asSequence() + generateSequence { "" }
                            infiniteStacks.zip(infiniteRow)
                                .take(max(acc.size, row.size))
                                .onEach { (stack, crate) -> if (crate.isNotEmpty()) stack.push(crate) }
                                .map { (stack, crate) -> stack }
                                .toList()
                        }
                    val numRe = Regex("""\d+""")
                    val moves = moves
                        .map { numRe.findAll(it).map { it.value.toInt() }.toList() }
                        .map { (n, src, dst) -> Move(stacks[src - 1], stacks[dst - 1], n) }
                        .toList()
                    Pair(stacks, moves)
                }
            val buffer = Stack<String>()
            for (m in moves) {
                repeat(m.n) { buffer.push(m.src.pop()) }
                repeat(m.n) { m.dst.push(buffer.pop()) }
            }
            return stacks.filterNot { it.empty() }.map { it.peek() }
                .map { it[1] }
                .joinToString("")
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

    data class Move(val src: Stack<String>, val dst: Stack<String>, val n: Int)
}

