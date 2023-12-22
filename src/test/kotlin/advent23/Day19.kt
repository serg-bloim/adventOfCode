package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Day19 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(19114, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }

    object Solution {
        val partRe = Regex("""\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)\}""")
        val workflowRe = Regex("""(\w+)\{(.+),(\w+)\}""")
        val ruleRe = Regex("""(\w)(<|>)(\d+):(\w+)""")
        fun solve(txt: String): Any {
            val (wfs, parts) = txt.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .partition { it.startsWith('{') }
                .let { (parts, rules) ->
                    Pair(
                        rules.map { parseWorkflow(it) },
                        parts.map {
                            val (x, m, a, s) = partRe.matchEntire(it)!!.destructured
                            Part(x.toInt(), m.toInt(), a.toInt(), s.toInt())
                        }
                    )
                }
            val wfLookup = wfs.associateBy { it.name }
            fun process(p: Part): Boolean {
                var dst = "in"
                val terminalDst = listOf("A", "R")
                while (dst !in terminalDst) {
                    val wf = wfLookup[dst]!!
                    dst = wf.rules.firstOrNull { it.accepts(p) }?.let { it.dst } ?: wf.def
                }
                return dst == "A"
            }
            return parts.asSequence()
                .filter { process(it) }
                .onEach { println("Part $it accepted") }
                .sumOf { it.x + it.m + it.a + it.s }
        }

        private fun parseWorkflow(str: String): Workflow {
            val (name, rulesStr, def) = workflowRe.matchEntire(str)!!.destructured
            val rules = rulesStr.split(',').map {
                val (param, op, n, dst) = ruleRe.matchEntire(it)!!.destructured
                val p = Part(1, 2, 3, 4)
                (Part::a)(p)
                Rule(param, op, n.toInt(), dst)
            }
            return Workflow(name, rules, def)
        }
    }

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int)
    data class Rule(val param: String, val op: String, val n: Int, val dst: String) {
        val paramExtr = when (param) {
            "x" -> Part::x
            "m" -> Part::m
            "a" -> Part::a
            "s" -> Part::s
            else -> throw IllegalArgumentException()
        }
        val compRes = if (op == "<") -1 else 1
        fun accepts(p: Part): Boolean {
            val partN = paramExtr(p)
            return partN.compareTo(n) == compRes
        }
    }

    data class Workflow(val name: String, val rules: List<Rule>, val def: String)

    class Tests {
        @Test
        fun test_rule() {
            val rule = Rule("x", "<", 10, "dst")
            val part1 = Part(1, 2, 3, 4)
            val part2 = Part(11, 12, 13, 14)
            val part3 = Part(10, 12, 13, 14)
            assertTrue { rule.accepts(part1) }
            assertFalse { rule.accepts(part2) }
            assertFalse { rule.accepts(part3) }
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
}

