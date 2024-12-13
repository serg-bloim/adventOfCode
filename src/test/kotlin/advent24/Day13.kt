package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Coords
import utils.dbg
import utils.result
import kotlin.math.min
import kotlin.test.assertEquals

class Day13 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(480, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(28887, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val machines = parseInput(txt)
            return machines.sumOf { (btnA, btnB, prize) -> calculateTokensToWin(btnA, btnB, prize) }
        }
    }

    private fun calculateTokensToWin(btnA: Coords, btnB: Coords, prize: Coords): Int {
        val minTokens = sequence {
            for (btnAPushes in 0..100) {
                for (btnBPushes in 0..100) {
                    if (btnA.x * btnAPushes + btnB.x * btnBPushes == prize.x &&
                        btnA.y * btnAPushes + btnB.y * btnBPushes == prize.y
                    )
                        yield(3 * btnAPushes + btnBPushes)
                }
            }
        }.minOrNull()
        val res = minTokens ?: 0
        return res
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

        fun solve(txt: String): Any {
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

        fun parseInput(txt: String): List<Triple<Coords, Coords, Coords>> {
            val btnRE = """Button \w: X\+(\d+), Y\+(\d+)""".toRegex()
            val prizeRE = """Prize: X=(\d+), Y=(\d+)""".toRegex()
            val data = txt.lineSequence()
                .filter { it.isNotEmpty() }
                .chunked(3)
                .map { (btnA, btnB, prize) ->
                    val (_, btnAX, btnAY) = btnRE.matchEntire(btnA)!!.groupValues
                    val (_, btnBX, btnBY) = btnRE.matchEntire(btnB)!!.groupValues
                    val (_, prizeX, prizeY) = prizeRE.matchEntire(prize)!!.groupValues
                    Triple(
                        Coords(btnAX.toInt(), btnAY.toInt()),
                        Coords(btnBX.toInt(), btnBY.toInt()),
                        Coords(prizeX.toInt(), prizeY.toInt())
                    )
                }.toList()
            return data
        }
    }
}