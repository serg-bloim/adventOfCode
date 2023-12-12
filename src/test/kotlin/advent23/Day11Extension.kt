package advent23

import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertEquals

class Day11Extension {
    @Test
    fun genMap() {
        val n = 40_000
        val w = 500
        val h = 500
        val flatMap = BooleanArray(w * h)
        val rand = Random(System.nanoTime())
        var pos = 0
        for (i in 1..n) {
            while (true) {
                val shift = rand.nextInt(w * h)
                pos = (pos + shift) % (w * h)
                if (!flatMap[pos]) {
                    flatMap[pos] = true
                    break
                }
            }
        }
        println("Constructed")
        flatMap.asSequence().map { if (it) '#' else '.' }
            .chunked(w)
            .forEach { line ->
                line.forEach(dbg::print)
                dbg.println()
            }

        println("Printed")
    }
}
