package advent23

import org.junit.jupiter.api.Test
import java.lang.Exception
import kotlin.math.max
import kotlin.test.assertEquals

internal class Challenge3 {
    @Test
    fun testSmall() {
        val actual = solve(load_test())
        println("Result: $actual")
        assertEquals(8, actual)
    }

    @Test
    fun testReal() {
        val actual = solve(load_prod())
        println("Result: $actual")
    }

    fun solve(txt: String): Int {
        val test = Turn(12, 13, 14)
        return txt.lineSequence()
            .map { parseLine(it) }
            .filter { it.maxTurn.allLessEqualThen(test) }
            .map { it.id }
//            .onEach { println("Game $it") }
            .sum()
    }

    val gameIdRe = Regex("""Game (\d+):""")
    val colorRe = Regex("""(?<num>\d+) (?<color>\w+)(?<end>;?)""")

    data class Turn(var red: Int = 0, var green: Int = 0, var blue: Int = 0) {
        fun maxWith(b: Turn) = Turn(max(red, b.red), max(green, b.green), max(blue, b.blue))
        fun allLessEqualThen(other: Turn) = red <=other.red && green <= other.green && blue <= other.blue
    }
    data class Game(val id:Int, val maxTurn: Turn)
    private fun parseLine(line: String): Game {
        val match = gameIdRe.matchAt(line, 0)!!
        val gameId = match.groupValues[1].toInt()
        val turns = colorRe.findAll(line).map {
                val (numStr, color) = it.destructured
                val num = numStr.toInt()
                when (color) {
                    "red" -> Turn(red = num)
                    "green" -> Turn(green = num)
                    "blue" -> Turn(blue = num)
                    else -> throw Exception()
                }
            }
        val maxTurn = turns.reduce { a, b -> a.maxWith(b) }
        return Game(gameId, maxTurn)
    }

    private fun load_test(): String {
        return Resources().loadString("ch3_test.txt")
    }

    private fun load_prod(): String {
        return Resources().loadString("ch3_prod.txt")
    }
}

