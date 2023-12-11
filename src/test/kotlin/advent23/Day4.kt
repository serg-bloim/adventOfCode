package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day4 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(13, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val tokenRe = Regex("""\d+|\|""")
            val res = txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { line ->
                    tokenRe.findAll(line, line.indexOf(':'))
                        .map { it.value }
                        .chunked { it == "|" }
                        .map { it.map { it.toInt() }.toSet() }
                        .reduce { a, b -> a.intersect(b) }
                        .size
                }
                .filter { it > 0 }
//                .onEachIndexed { index, winNumbers -> println("$index - $winNumbers : ${1 shl (winNumbers - 1)}") }
                .map { winNumbers -> 1 shl (winNumbers - 1) }
                .sum()
            return res
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(30, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val tokenRe = Regex("""\d+|\|""")
            val cardWinNumber = txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { line ->
                    tokenRe.findAll(line, line.indexOf(':'))
                        .map { it.value }
                        .chunked { it == "|" }
                        .map { it.map { it.toInt() }.toSet() }
                        .reduce { a, b -> a.intersect(b) }
                        .size
                }.toList()
            val copies = cardWinNumber.asSequence().map { 1 }.toMutableList()
            for( i in cardWinNumber.indices){
                val copyN = copies[i]
                for(j in 1 .. cardWinNumber[i]){
                    copies[i+j] += copyN
                }
            }
            return copies.sum()
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