package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.RateLimiter
import utils.assertEquals
import utils.result
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

typealias Gradient = List<Double>

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
        if (buttons.isEmpty()) {
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

    enum class StateResult { NOT_COMPLETE, COMPLETE, OVERFLOW }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(33, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(55555555, actual)
        }

        @Test
        fun findExclusiveButtons() {
            val machines = parseInput(load_prod())
            machines.forEachIndexed { i, (lights, buttons, joltages) ->
                val destinations = Array(joltages.size) { 0 }
                for (btn in buttons) {
                    for (dst in btn) {
                        destinations[dst] += 1
                    }
                }

                logger.info { "$i ${destinations.toList()}" }
            }
        }

        @Test
        fun printEquation() {
            val machines = parseInput(load_prod())
            val (lights, buttons, joltages) = machines.first()
            val varnames = 'a'..'z'
            for ((i, jolt) in joltages.withIndex()) {
                print(jolt.toString().padStart(5) + " = ")
                for ((btn, varname) in buttons.zip(varnames)) {
                    val out = varname.takeIf { i in btn }?.let { "$it + " } ?: ""
                    print(out.padStart(5))
                }
                println()
            }
        }

        fun solve(txt: String): Any {
            val machines = parseInput(txt)
            val total = machines.sumOf { (lightsGoal, buttons, joltage) ->
                val pushes = getJoltagePushes(buttons, joltage)
                logger.info { "Working on $joltage = $pushes" }
                pushes
            }
            return total
        }

        fun getJoltagePushes(buttons: List<List<Int>>, goal: List<Int>): Int {
            val state = goal.map { 0 }.toMutableList()
            val buttonGrads = buttons.map { normalize(it) }
            return getJoltagePushesRec(state, buttons, goal, 0, penalty, buttonGrads)
        }

        val logRL = RateLimiter()
        val penalty = 9999999
        private fun getJoltagePushesRec(state: MutableList<Int>, buttons: List<List<Int>>, goal: List<Int>, pushesDone: Int, minimal: Int, buttonGrads: List<Gradient>): Int {
            if (pushesDone >= minimal) return penalty
            when (checkStateIfFinished(state, goal)) {
                StateResult.COMPLETE -> return pushesDone
                StateResult.OVERFLOW -> return penalty
                StateResult.NOT_COMPLETE -> {} // continue searching
            }
            logRL.onEach(100000) {
                logger.info { "pushesDone: $pushesDone minimal: $minimal" }
            }
            val diff = goal.zip(state) { g, s -> g - s }
            val diffGrad = normalize(diff)
            val buttonScore = buttonGrads.map { dist(it, diffGrad) }
            val plan = buttonScore.withIndex().sortedBy { indexed -> indexed.value }
//                .sortedBy { it.index }
                .map { it.index }
            var minimal = minimal
            for (btnIndex in plan) {
                pushButton(state, buttons[btnIndex])
                val res = getJoltagePushesRec(state, buttons, goal, pushesDone + 1, minimal, buttonGrads)
                unpushButton(state, buttons[btnIndex])
                minimal = min(minimal, res)
            }
            return minimal
        }

        private fun pushButton(state: MutableList<Int>, indices2inc: List<Int>) = changeState(indices2inc, state, 1)

        private fun unpushButton(state: MutableList<Int>, indices2dec: List<Int>) = changeState(indices2dec, state, -1)

        private fun changeState(indices2dec: List<Int>, state: MutableList<Int>, diff: Int) {
            indices2dec.forEach { state[it] += diff }
        }

        private fun dist(grad1: Gradient, grad2: Gradient): Double = sqrt(grad1.zip(grad2) { g1, g2 -> g1 - g2 }.sumOf { it * it })

        private fun normalize(vec: List<Int>): Gradient {
            val magnitude = sqrt(vec.sumOf { (it * it).toDouble() })
            return vec.map { it.toDouble() / magnitude }
        }


        private fun checkStateIfFinished(state: MutableList<Int>, goal: List<Int>): StateResult {
            var hasLess = false
            var hasMore = false
            var hasEq = false
            for ((s, g) in state.zip(goal)) {
                when {
                    s < g -> hasLess = true
                    s > g -> hasMore = true
                    else -> hasEq = true
                }
            }
            return when {
                !hasLess && !hasMore -> StateResult.COMPLETE
                !hasMore -> StateResult.NOT_COMPLETE
                else -> StateResult.OVERFLOW
            }
        }

        private fun checkNoOverflow(state: MutableList<Int>, goal: List<Int>) = state.zip(goal).all { (s, g) -> s <= g }
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

    @Test
    fun testSolveLinEqSys() {
        /**
         * x = 1
         * y = 2
         * z = 3
         * 1x  1y  3z = 12
         * 3x  1y  1z = 8
         * 1x  3y  1z = 10
         *
         */
        val coefs = arrayOf(
            arrayOf(1, 1, 3),
            arrayOf(3, 1, 1),
            arrayOf(1, 3, 1)
        )
        val consts = arrayOf(12, 8, 10)
        solveLinearEqSystem(coefs, consts)
        println(coefs)
        println(consts)
    }

    @Test
    fun testSolveLinEqSys2() {
        /**
         * x = 1
         * y = 2
         * z = 3
         * 1x  1y  3z = 12
         * 3x  1y  1z = 8
         * 1x  3y  1z = 10
         *
         */
        val coefs = """
            01111101
            11110100
            10111100
            00000111
            10011101
            00100110
            11111001
            00011010
            10111000
        """.trimIndent().lines().filterNot { it.isBlank() }.map { it.asSequence().map { it.toString().toInt() }.toList().toTypedArray() }.toTypedArray()
        val consts = arrayOf(56, 47, 33, 28, 34, 27, 55, 4, 24)
        solveLinearEqSystem(coefs, consts)
        println(coefs)
        println(consts.toList())
    }

    @Test
    fun testSimulateButtonPushes() {
        val machines = parseInput(load_test())
        val (lights, buttons, joltages) = machines.drop(1).first()
        val consts = joltages.toTypedArray()
        val coefs = consts.indices.map { ind -> buttons.map { btn -> ind in btn }.map { if (it) 1 else 0 }.toTypedArray() }.toTypedArray()
        solveLinearEqSystem(coefs, consts)
        val pushes = consts.toList()
        println(pushes.sum())
        println(pushes)
        val state = joltages.map { 0 }.toMutableList()
        for ((btn, n) in buttons.zip(pushes)) {
            for (i in btn) {
                state[i] += n
            }
        }
        println(state)
        println(joltages)
    }

    fun solveLinearEqSystem(coefs: Array<Array<Int>>, consts: Array<Int>) {
        fun <T> Array<T>.swap(i: Int, j: Int) {
            if (i == j) return
            val tmp = get(i)
            set(i, get(j))
            set(j, tmp)
        }

        fun swapRows(i: Int, j: Int) {
            coefs.swap(i, j)
            consts.swap(i, j)
        }

        fun subtractRow(subtrFrom: Int, subtrWhat: Int, n: Int) {
            for (ind in coefs[0].indices) {
                coefs[subtrFrom][ind] -= coefs[subtrWhat][ind] * n
            }
            consts[subtrFrom] -= consts[subtrWhat] * n
        }

        fun divRow(rowInd: Int, div: Int) {
            for (ind in coefs[0].indices) {
                assert(coefs[rowInd][ind] % div == 0)
                coefs[rowInd][ind] /= div
            }
            assert(consts[rowInd] % div == 0)
            consts[rowInd] /= div
        }

        fun rowDivisible(rowInd: Int, n: Int) =
            if (abs(n) == 1) true
            else (consts[rowInd] % n == 0) && coefs[rowInd].all { it % n == 0 }

        for (j in coefs[0].indices) {
            // Find any row below j that has non-zero value at j and swap into j pos

            val nonNullRowInd = coefs.withIndex()
                .firstOrNull() { (index, row) -> index >= j && row[j] != 0 && rowDivisible(index, row[j]) }
                ?.index
            if (nonNullRowInd == null){
                val check = coefs.withIndex().all { (index, row) -> index >= j && row[j] == 0 }
                assert(check)
            }

            if (nonNullRowInd != null) {
                if (!(coefs[nonNullRowInd][j] == 1)) {
                    1 + 1
                }
                swapRows(j, nonNullRowInd)
                if (coefs[j][j] != 1) {
                    //normalize row
                    val div = coefs[j][j]
                    divRow(j, div)
                }
                // make sure all following rows have 0 in j column by subtracting j row from it
                for (k in (j + 1) until coefs.size) {
                    val times = coefs[k][j]
                    subtractRow(k, j, times)
                }
            } else {
                1 + 1
            }
        }
        for (j in coefs[0].indices.reversed()) {
            for (k in 0 until j) {
                val times = coefs[k][j]
                subtractRow(k, j, times)
            }
        }
    }

}