package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day7 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(6440L, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(249390788L, actual)
        }

        fun solve(txt: String): Any {
            return txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { it.split(' ').let { (hand, bid) -> Pair(parse(hand), bid.toLong()) } }
                .sortedWith(compareBy<Pair<Hand, Long>> { it.first.kind }
                    .thenBy { it.first.cardRanks })
                .mapIndexed { rank, (hand, bid) -> (rank + 1) * bid }
                .sorted()
                .sum()
        }

        fun parse(hand: String): Hand {
            val (highest, secondHigh) = (hand.groupingBy { it }
                .eachCount()
                .asSequence()
                .map { it.value }
                .sortedDescending()
                    + sequenceOf(0))
                .toList()
            val kind = when (Pair(highest, secondHigh)) {
                Pair(5, 0) -> 26
                Pair(4, 1) -> 25
                Pair(3, 2) -> 24
                Pair(3, 1) -> 23
                Pair(2, 2) -> 22
                Pair(2, 1) -> 21
                else -> 20
            }
            val cardRanks = hand.map { (rank(it) + 97).toChar() }.joinToString("")
            return Hand(hand, kind, cardRanks)
        }

        private fun rank(card: Char) = "23456789TJQKA".indexOf(card)
    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5905, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(249390788, actual)
        }

        fun solve(txt: String): Any {
            return txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { it.split(' ').let { (hand, bid) -> Pair(parse(hand), bid.toInt()) } }
                .sortedWith(compareBy<Pair<Hand, Int>> { it.first.kind }
                    .thenBy { it.first.cardRanks })
                .mapIndexed { rank, (hand, bid) -> (rank + 1) * bid }
                .sorted()
                .sum()
        }

        private fun parse(hand: String): Hand {
            val distribution = hand.groupingBy { it }
                .eachCount()
                .toMutableMap()
            val j = distribution.remove('J') ?: 0
            val (highest, secondHigh) = (distribution
                .asSequence()
                .map { it.value }
                .sortedDescending()
                    + sequenceOf(0, 0))
                .toList()
            val kind = when (Pair(highest + j, secondHigh)) {
                Pair(5, 0) -> 26
                Pair(4, 1) -> 25
                Pair(3, 2) -> 24
                Pair(3, 1) -> 23
                Pair(2, 2) -> 22
                Pair(2, 1) -> 21
                else -> 20
            }
            val cardRanks = hand.map { (rank(it) + 97).toChar() }.joinToString("")
            return Hand(hand, kind, cardRanks)
        }

        private fun rank(card: Char) = "J23456789TQKA".indexOf(card)
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

    data class Hand(val hand: String, val kind: Int, val cardRanks: String)

    internal class Tests {
        @Test
        fun testHandParsing() {
            val tests = """
                77777 26
                78777 25
                78787 24
                78797 23
                78789 22
                78689 21
                75689 20
                75683 20
                A5683 20
                
            """.trimIndent()
            tests.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map {
                    val (hand, rank) = it.split(" ")
                    Pair(Task1().parse(hand), rank.toInt())
                }
                .forEach { (hand, rank) ->
                    assertEquals(rank, hand.kind, hand.hand)
                }
        }

        @Test
        fun findDuplicates() {
            load_prod().lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { it.split(" ")[0].uppercase() }
                .groupingBy { it }
                .eachCount()
                .asSequence()
                .sortedByDescending { it.value }
                .forEach { dbg.println("${it.key} - ${it.value}") }
        }

        @Test
        fun analyzeBids() {
            val lineRe = Regex("""(\w+) (\d+)""")
            val hands = load_prod().lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { line ->
                    lineRe.matchEntire(line)!!.destructured
                        .let { (hand, bid) -> Pair(Task1().parse(hand), bid.toLong()) }
                }
                .sortedBy { it.second }
                .toList()
                .forEach { dbg.println("${it.first} - ${it.second}") }
        }
    }
}

