package advent23

import org.junit.jupiter.api.Test
import java.io.PrintStream
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

private val <A, B> Pair<A, B>.left: A
    get() {
        return first
    }

private val <A, B> Pair<A, B>.right: B
    get() {
        return second
    }

class Day8 {
    internal class Task1 {
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
            println("Result: $actual")
            assertEquals(18113, actual)
        }

        fun solve(txt: String): Any {

            val lineRe = Regex("""(\w+) = \((\w+), (\w+)\)""")
            val iterator = txt.lineSequence().map { it.trim() }.filterNot { it.isEmpty() }.iterator()
            val instructions = iterator.next().asSequence().map {
                when (it) {
                    'L' -> Pair<String, String>::first
                    'R' -> Pair<String, String>::second
                    else -> throw Exception()
                }
            }.toList()
            val navigation =
                iterator.asSequence().map { lineRe.matchEntire(it) }.filterNotNull().map { it.destructured }
                    .associate { (loc, left, right) -> Pair(loc, Pair(left, right)) }
            var current = "AAA"
            var steps = 0
            for (dir in instructions.repeatForever()) {
                current = dir(navigation[current]!!)
                steps++
                if (current == "ZZZ") break
            }
            return steps
        }
    }

    internal class Task2 {
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
            println("Result: $actual")
            assertEquals(35557986113, actual)
        }

        fun solve2(txt: String): Any {
            val lineRe = Regex("""(\w+) = \((\w+), (\w+)\)""")
            val iterator = txt.lineSequence().map { it.trim() }.filterNot { it.isEmpty() }.iterator()
            val instructions = iterator.next().toList()
            val navigation =
                iterator.asSequence().map { lineRe.matchEntire(it) }.filterNotNull().map { it.destructured }
                    .associate { (loc, left, right) -> Pair(loc, Pair(left, right)) }
            var locs = navigation.keys.filter { it.endsWith('A') }.toMutableList()
            for ((step, dir) in instructions.repeatForever().withIndex()) {
                for (i in locs.indices) {
                    val old = locs[i]
                    val nav = navigation[old]!!
                    val new = if (dir == 'L') nav.left else nav.right
                    locs[i] = new
                }
                if (step % 1000_000 == 0) {
                    println(step)
                }
                if (locs.all { it.endsWith('Z') }) {
                    return step + 1
                }
            }
            return -1
        }

        fun solve(txt: String): Any {
            val lineRe = Regex("""(\w+) = \((\w+), (\w+)\)""")
            val iterator = txt.lineSequence().map { it.trim() }.filterNot { it.isEmpty() }.iterator()
            val instructions = iterator.next().toList()
            val navigation =
                iterator.asSequence().map { lineRe.matchEntire(it) }.filterNotNull().map { it.destructured }
                    .associate { (loc, left, right) -> Pair(loc, Pair(left, right)) }
            val startLocs = navigation.keys.filter { it.endsWith('A') }
            val cycleDescriptors = startLocs.map {
                findCycle(it, instructions, navigation)
            }
            for (cd in cycleDescriptors) {
                dbg.println("------")
                dbg.println("${cd.loc} / ${cd.start} - ${cd.end}")
                dbg.println()
                for ((index, a) in generatePath(cd.loc, instructions, navigation).take(15).withIndex()) {
                    val (loopInd, dir, loc) = a
                    dbg.println("$index $loopInd $loc $dir")
                }
            }
            val finishPoints = cycleDescriptors.map {
                val finishes = generateCycle(it, instructions, navigation)
                    .mapIndexed { ci, move -> Pair(ci, move.location) }
                    .filter { it.second.endsWith('Z') }
                    .map { it.first }
                    .toList()
                    .first()
                Pair(it, finishes)
            }.toList()
            val res = finishPoints
                .map { (descr, finish_in_cycle) -> Pair(finish_in_cycle.toLong(), descr.cyclesNum * instructions.size) }
                .reduce { (x1, p1), (x2, p2) ->
                    val new_cycle = meet(x1, p1, x2, p2)
                    new_cycle
                }
//            val primeNumbers = genPrimes(finishPoints.maxBy { it.second }.second)
//            val lcmFactors = finishPoints.map { it.second }
//                .map { primeFactors(it, primeNumbers) }
//                .reduce { acc, numberFactors -> acc.merge(numberFactors) { a, b -> max(a, b) } }
//            val steps = defactorize(lcmFactors) + instructions.size
//            return steps
            return res.first + instructions.size
        }

        private fun defactorize(factors: Map<Int, Int>): Long {
            return factors.asSequence()
                .map { (a, b) -> a.toLong() * b }
                .sum()
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

    class Tests {
        @Test
        fun testCycles() {
            val txt = load_prod()
            val lineRe = Regex("""(\w+) = \((\w+), (\w+)\)""")
            val iterator = txt.lineSequence().map { it.trim() }.filterNot { it.isEmpty() }.iterator()
            val instructions = iterator.next().toList()
            val navigation =
                iterator.asSequence().map { lineRe.matchEntire(it) }.filterNotNull().map { it.destructured }
                    .associate { (loc, left, right) -> Pair(loc, Pair(left, right)) }
            val starts = navigation.keys.filter { it.endsWith('A') }
            val cycles = starts.associateWith { findCycle(it, instructions, navigation) }
            with(dbg) {
                for ((start, cycle) in cycles) {
                    println(start)
                    println(cycle)
                    val path = generatePath(start, instructions, navigation)
                        .iterator()

                    val preCycle = path.asSequence().take((instructions.size * cycle.start).toInt()).toList()
                    val cycle1 = path.asSequence().take(cycle.lengthAbsolute.toInt()).toList()
                    val cycle2 = path.asSequence().take(cycle.lengthAbsolute.toInt()).toList()
                    assertContentEquals(cycle1, cycle2)
                }
            }

//            val cycle = generatePath(start, instructions, navigation)
//                .drop(instructions.size)
//                .take((cycleLength.second - cycleLength.first) * instructions.size)
//                .toList()
//            cycle.forEach { dbg.println(it) }
        }

        @Test
        fun printCycle() {
            val txt = load_prod()
            val lineRe = Regex("""(\w+) = \((\w+), (\w+)\)""")
            val iterator = txt.lineSequence().map { it.trim() }.filterNot { it.isEmpty() }.iterator()
            val instructions = iterator.next().toList()
            val navigation =
                iterator.asSequence().map { lineRe.matchEntire(it) }.filterNotNull().map { it.destructured }
                    .associate { (loc, left, right) -> Pair(loc, Pair(left, right)) }
            val starts = navigation.keys.filter { it.endsWith('A') }
            val cycles = starts.associateWith { findCycle(it, instructions, navigation) }
            with(dbg) {
                val start = "BXA"
                val cycle = cycles[start]
                println(start)
                println(cycle)
                val path = generatePath(start, instructions, navigation)
                for ((ind, move) in path.take(100000).withIndex()) {
                    val (ii, dir, loc) = move
                    println(
                        "${ii.toString().padStart(6)} $loc -> $dir"
                    )
                }
            }
        }

        @Test
        fun testMeet() {
            val params = listOf(0, 6, 5, 5).map { it.toLong() }
            val (x1, p1, x2, p2) = params
            printDebugInfo(dbg, x1, p1, x2, p2)
            val expected = meetBF(x1, p1, x2, p2)
            val actual = meet(x1, p1, x2, p2)
            assertEquals(expected, actual)
        }

        @Test
        fun testMeetR() {
            val params = listOf(0, 6, 5, 5).map { it.toLong() }
            val (x1, p1, x2, p2) = params
            printDebugInfo(dbg, x1, p1, x2, p2)
            val expected = meetR(p1, p2, x2)
            val actual = meetBF(x1, p1, x2, p2)
            assertEquals(expected, actual)
        }

        @Test
        fun testPrintMeets() {
            val p2 = 13
            for (x2 in 0 until p2) {
                val params = listOf(0, 5, x2, p2).map { it.toLong() }
                val (x1, p1, x2, p2) = params
                val expected = meetBF(x1, p1, x2, p2)
                dbg.println("x2 = $x2,   u = ${(expected.first - x2) / p2}   p2 % p1 = ${p2 % p1}    x2 % p1 = ${(x2 % p1)}    p1 - x2 % p1 = ${p1 - (x2 % p1)}")
                printDebugInfo(dbg, x1, p1, x2, p2)
                dbg.println()
                dbg.println()
//                val actual = meet(x1, p1, x2, p2)
//                assertEquals(expected, actual)
            }
        }

        private fun printDebugInfo(printer: PrintStream, x1: Long, p1: Long, x2: Long, p2: Long) {
            val x1 = x1.toInt()
            val x2 = x2.toInt()
            val p1 = p1.toInt()
            val p2 = p2.toInt()
            fun wrap(v: Any, padding: String = " ") = padding + v + padding
            with(printer) {
                val limit = leastCommonMultiple(p1.toLong(), p2.toLong()).toInt()
//                for (i in 0 until limit) {
//                    val tens = i / 10
//                    print(wrap(tens.toString()[0]))
//                }
//                printer.println()
//                for (i in 0 until limit) {
//                    val ones = i % 10
//                    print(wrap(ones.toString()[0]))
//                }
                for (i in 0 until limit) {
                    val ones = i % 10
                    val str = when {
                        i >= 100 -> i.toString()
                        i >= 10 -> " " + i
                        else -> wrap(i)
                    }
                    print(str)
                }
                printer.println()
                for (i in 0 until limit / p1) {
                    print(" ┌─")
                    repeat(p1 - 2) { print(wrap("─", "─")) }
                    print("─┐ ")
                }
                println()
                for (i in 0 until limit) {
                    val matches1 = (i - x1) % p1 == 0
                    val matches2 = (i - x2) % p2 == 0
                    val ch = when {
                        matches1 && matches2 -> 'Ø'
                        matches1 -> 'O'
                        matches2 -> 'X'
                        else -> '.'
                    }
                    print(wrap(ch))
                }
                println()
                for (i in 0 until limit / p2) {
                    print(" └─")
                    repeat(p2 - 2) { print(wrap("─", "─")) }
                    print("─┘ ")
                }
                println()
            }

        }

        @Test
        fun testMeetBruteForce() {
            var i = 0
            for (p1 in 5..100L) {
                for (x1 in 0 until p1) {
                    for (p2 in 5..100L) {
                        for (x2 in 0 until p2) {
//                            println("$p1 - $x1 - $p2 - $x2")
//                            val expected = meetBF(x1, p1, x2, p2)
//                            if (expected.first == -1L) continue
                            val actual = meet(x1, p1, x2, p2)
//                            assertEquals(expected, actual, "meet($x1, $p1, $x2, $p2)")
                        }
                    }
                }
            }
            assertEquals(Pair(8L, 14L), meetBF(0, 2, 1, 7))
        }
    }
}

data class Move(
    val location: String,
    val instructionInd: Int,
    val dir: Char,
)

fun generatePath(
    start: String, instructions: List<Char>, navigation: Map<String, Pair<String, String>>
) = sequence {
    var current = start
    for ((ii, dir) in instructions.withIndex().repeatForever()) {
        val loc = navigation[current]!!
        yield(Move(current, ii, dir))
        current = if (dir == 'L') loc.left else loc.right
    }
}

fun generateCycle(
    desc: CycleDescriptor,
    instructions: List<Char>,
    navigation: Map<String, Pair<String, String>>
) = generatePath(desc.loc, instructions, navigation).drop(instructions.size * desc.start.toInt())
    .take((desc.cyclesNum.toInt()) * instructions.size)

fun findCycle(
    start: String,
    instructions: List<Char>,
    navigation: Map<String, Pair<String, String>>
): CycleDescriptor {
    val visited = mutableMapOf<String, Int>()
    val (from, to) = generatePath(start, instructions, navigation)
        .filter { move -> move.instructionInd == 0 }
        .map { it.location }
        .mapIndexed { cycle, loc ->
            val prev = visited.put(loc, cycle)
            prev?.let { Pair(prev, cycle) }
        }
        .filterNotNull()
        .first()

    return CycleDescriptor(from.toLong(), to.toLong(), start, instructions.size)
}

data class CycleDescriptor(
    val start: Long,
    val end: Long,
    val loc: String,
    val instructionsSize: Int,
) {
    val cyclesNum = end - start
    val lengthAbsolute = cyclesNum * instructionsSize

}

fun meet(x1: Long, p1: Long, x2: Long, p2: Long): Pair<Long, Long> {
    if (p1 > p2) return meet(x2, p2, x1, p1)
    val period = leastCommonMultiple(p1, p2)
    val a2 = (x2 - x1) % p1
    val n = if (a2 == 0L) 0 else {
        var u = 0L
        while ((u * p2 + x2) % p1 != x1) u++
        u
    }
    return Pair(n * p2 + x2, period)
}

fun meetR(p1: Long, p2: Long, x: Long): Pair<Long, Long> {
    val period = leastCommonMultiple(p1, p2)
    val n = findN(p1, p2, x)
    return Pair(n * p2 + x, period)
}

fun findN(p1: Long, p2: Long, x: Long): Long {
    dbg.println("p1: $p1, p2: $p2, x: $x")
    if (p1.toInt() == 1) return 0
    return 1 + findN((p2 % p1), p1, p1 - (x % p1))
}

fun meetBF(x1: Long, p1: Long, x2: Long, p2: Long): Pair<Long, Long> {
    fun fillGap(start: Long, finish: Long, step: Long): Long {
        val diff = finish - start
        return if (diff % step == 0L) diff
        else (diff / step + 1) * step
    }
    if (p2 < p1) return meetBF(x2, p2, x1, p1)
    val new_period = leastCommonMultiple(p1, p2)
    val dx =
        if (p2 % p1 == 0L) {
            if (x1 == x2) x1 else -1
        } else {
            var x1 = x1
            var x2 = x2
            while (true) {

                if (x2 < x1) x2 += fillGap(x2, x1, p2)
                if (x1 < x2) x1 += fillGap(x1, x2, p1)
                if (x1 == x2) break
                if (x1 > new_period) {
                    x1 = -1
                    x2 = x1
                    break
                }
            }
            x1
        }
    return Pair(dx, new_period)
}

fun meetBF2(x1: Long, p1: Long, x2: Long, p2: Long): Pair<Long, Long> {
    if (p2 < p1) return meetBF2(x2, p2, x1, p1)
    val new_period = leastCommonMultiple(p1, p2)
    val dx = if (p2 % p1 == 0L) {
        if (x1 == x2) x1 else -1
    } else {
        var x1 = x1
        var x2 = x2
        while (true) {
            while (x2 < x1) x2 += p2
            while (x1 < x2) x1 += p1
            if (x1 == x2) break
            if (x1 > new_period) {
                x1 = -1
                x2 = x1
                break
            }
        }
        x1
    }
    return Pair(dx, new_period)
}
