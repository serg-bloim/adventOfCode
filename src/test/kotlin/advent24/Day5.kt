package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.test.assertEquals

class Day5 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(143, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(4185, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val (ordering, updates) = parseInput(txt)
            val orderingLookup = ordering.toHashSet()
            val res = updates.filter { isCorrect(it, orderingLookup) }
                .map { lst -> lst[lst.size / 2] }
                .sum()
            return res
        }
    }

    private fun isCorrect(update: List<Int>, ordering: HashSet<Pair<Int, Int>>): Boolean {
        if (update.size <= 1) return true
        val a = update.first()
        val rest = update.subList(1, update.size)
        for (b in rest) {
            if (Pair(b, a) in ordering) return false
        }
        return isCorrect(rest, ordering)
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(123, actual)
        }
        @Test
        fun testUnique() {
            val (ordering, updates) = parseInput(load_prod())
            for(line in updates){
                if (line.toSet().size != line.size){
                    println("found")
                }
            }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(4480, actual)
        }

        fun solve(txt: String): Int {
            val (ordering, updates) = parseInput(txt)
            val orderingLookup = ordering.toHashSet()
            val res = updates.filterNot { isCorrect(it, orderingLookup) }
                .map { fix(it, orderingLookup) }
                .map { lst -> lst[lst.size / 2] }
                .sum()
            return res

        }
    }

    private fun fix(updates: List<Int>, orderingLookup: java.util.HashSet<Pair<Int, Int>>): List<Int> {
        val pages = updates.toMutableSet()
        val orderings = orderingLookup.filter { it.first in pages && it.second in pages }.toMutableSet()
        val res = mutableListOf<Int>()
        while (orderings.size > 0){
            val firstPages = orderings.asSequence().map { it.first }.toSet()
            val lastPages = orderings.asSequence().map { it.second }.toSet()
            val pagesWithNoPredecessor = firstPages - lastPages
            res.addAll(pagesWithNoPredecessor)
            orderings.removeIf { it.first in pagesWithNoPredecessor }
        }
        pages.removeAll(res)
        res.addAll(pages)
        return res
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(): String {
            return Resources().loadString("${res_prefix}_test.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }

        fun parseInput(txt: String): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
            val (orderingStr, updatesStr) = txt.split("""\n\n""".toRegex())
            val orders = orderingStr.lineSequence().map {
                val (a, b) = it.split('|').map { it.toInt() }
                Pair(a, b)
            }.toList()
            val updates = updatesStr.lines().map { line ->
                line.split(',').map { it.toInt() }
            }
            return Pair(orders, updates)
        }
    }
}