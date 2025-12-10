package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.result
import kotlin.math.min

class Day10 {
    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(7, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(409, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String): Any {
            val machines = parseInput(txt)
            var total = 0L
            for ((lightsGoal, buttons, joltage) in machines) {
                val lights = lightsGoal.map { false }.toBooleanArray()
                val pushes = pickButtons(lights, lightsGoal, buttons)
                total += pushes
                logger.info { pushes }
            }
            return total
        }
    }

    private fun pickButtons(lights: BooleanArray, lightsGoal: List<Boolean>, buttons: List<List<Int>>): Int {
        if (buttons.size == 0) {
            return if (lightsEqual(lights, lightsGoal)) 0 else 99999999
        }
        // don't push the first btn
        val noPush = pickButtons(lights, lightsGoal, buttons.subList(1, buttons.size))
        //push it
        val btn = buttons.first()
        for (light in btn) {
            lights[light] = !lights[light]
        }
        val push = 1 + pickButtons(lights, lightsGoal, buttons.subList(1, buttons.size))
        for (light in btn) {
            lights[light] = !lights[light]
        }
        return min(push, noPush)
    }

    fun lightsEqual(lights: BooleanArray, lightsGoal: List<Boolean>) = lights.zip(lightsGoal) { a, b -> a == b }.all { it }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
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

        fun parseInput(txt: String): List<Triple<List<Boolean>, List<List<Int>>, List<Int>>> {
            val data = txt.lineSequence().map { line ->
                val match = """\[(.+)](.*)\{(.+)}""".toRegex().matchEntire(line)
                val (lightsStr, buttonsStr, joltageStr) = match!!.destructured
                val lights = lightsStr.map { it == '#' }
                val buttons = buttonsStr.trim().split(' ').map { it.removeSurrounding("(", ")").split(",").map { it.toInt() } }
                val joltage = joltageStr.split(',').map { it.toInt() }
                Triple(lights, buttons, joltage)
            }.toList()
            return data
        }
    }
}