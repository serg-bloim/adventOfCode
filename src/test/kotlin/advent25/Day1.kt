package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.simple.SimpleLogger
import utils.result
import kotlin.test.assertEquals

class Day1 {
    val DIAL_MAX = 100
    init {
//        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG")
    }

    val logger = KotlinLogging.logger {}

    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(3, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(1141, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val data = parseInput(txt)
            val initState = 50
            val DIAL_MAX = 100
            val res = data.asSequence()
                .onEach { step -> logger.debug { "Step: $step" } }
                .runningFold(initState) { acc, step -> (acc + step) % DIAL_MAX }
                .onEach { intermediateState -> logger.debug { "State: $intermediateState" } }
                .count { intermediateState -> intermediateState == 0 }
            return res
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(6, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(6634, actual)
        }

        @Test
        fun testCountZeroPasses() {
            assertEquals(0, countZeroPasses(50, 10))
            assertEquals(0, countZeroPasses(50, -10))
            assertEquals(0, countZeroPasses(-50, -10))
            assertEquals(0, countZeroPasses(-50, 10))

            assertEquals(1, countZeroPasses(50, 50))
            assertEquals(1, countZeroPasses(50, -50))
            assertEquals(1, countZeroPasses(-50, -50))
            assertEquals(1, countZeroPasses(-50, 50))

            assertEquals(2, countZeroPasses(50, 200))
            assertEquals(2, countZeroPasses(50, -200))
            assertEquals(2, countZeroPasses(-50, -200))
            assertEquals(2, countZeroPasses(-50, 200))
        }

        fun solve(txt: String): Any {
            val data = parseInput(txt)
            val initState = 50
            var state = initState
            var zeroPasses = 0
            for (step in data) {
                zeroPasses += countZeroPasses(state, step)
                state = (state + step) % DIAL_MAX
            }
            return zeroPasses
        }

        fun countZeroPasses(state: Int, step: Int): Int {
            if (step < 0) return countZeroPasses(-state, -step)
            val normalizedState = (state + DIAL_MAX) % DIAL_MAX
            val newState = normalizedState + step
            val cycles = newState / DIAL_MAX
            return cycles
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

        fun parseInput(txt: String): List<Int> {
            val data = txt.lineSequence().map { line ->
                val direction = line.first()
                val distance = line.substring(1).toInt()
                if (direction == 'R') distance else -distance
            }.toList()
            return data
        }
    }
}