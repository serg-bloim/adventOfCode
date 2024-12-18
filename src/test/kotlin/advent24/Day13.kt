package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.result
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.test.assertEquals

class Day13 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(480L, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(28887L, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Long {
            val machines = parseInput(txt)
            return machines.sumOf { (btnA, btnB, prize) -> calculateTokensToWin(btnA, btnB, prize) }
        }
    }

    private fun calculateTokensToWin(btnA: LongPair, btnB: LongPair, prize: LongPair): Long {
        val maxPushesB = min(prize.x / btnB.x, prize.y / btnB.y)
        val minTokens = sequence {
            for (btnBPushes in maxPushesB downTo 0) {
                val dx = prize.x - btnBPushes * btnB.x
                val dy = prize.y - btnBPushes * btnB.y
                if (dx % btnA.x == 0L && dy % btnA.y == 0L) {
                    val btnAPushes = dx / btnA.x
                    if (btnAPushes == dy / btnA.y)
                        yield(btnBPushes + btnAPushes * 3)
                }
            }
        }.minOrNull()
        val res = minTokens ?: 0
        return res
    }

    private fun calculateTokensToWin2(btnA: LongPair, btnB: LongPair, prize: LongPair): Long {
        val ax = btnA.x.toDouble()
        val ay = btnA.y.toDouble()
        val bx = btnB.x.toDouble()
        val by = btnB.y.toDouble()
        val px = prize.x.toDouble()
        val py = prize.y.toDouble()

        val x = (py - by*px/bx) / (ay/ax - by/bx)
        val an = (x/ax).roundToLong()
        val intersectionX = btnA.x * an
        val bn = (prize.x - intersectionX) / btnB.x

        if (btnA.x * an + btnB.x * bn == prize.x &&
            btnA.y * an + btnB.y * bn == prize.y){
            return an * 3 + bn
        }
        return 0
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
            val machines = parseInput(txt)
            return machines.sumOf { (btnA, btnB, prize) -> calculateTokensToWin2(btnA, btnB, prize + 10000000000000L) }

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

        fun parseInput(txt: String): List<Triple<LongPair, LongPair, LongPair>> {
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
                        LongPair(btnAX.toLong(), btnAY.toLong()),
                        LongPair(btnBX.toLong(), btnBY.toLong()),
                        LongPair(prizeX.toLong(), prizeY.toLong())
                    )
                }.toList()
            return data
        }
    }
}

private typealias LongPair = Pair<Long, Long>

private val LongPair.x: Long
    get() = this.first

private val LongPair.y: Long
    get() = this.second

private operator fun LongPair.plus(n: Long) = LongPair(x + n, y + n)
