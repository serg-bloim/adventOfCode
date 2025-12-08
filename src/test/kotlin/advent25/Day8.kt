package advent25

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertEquals
import utils.pow
import utils.result

typealias Coords3D = Triple<Long, Long, Long>

class Day8 {
    val logger = KotlinLogging.logger {}

    class Solution(junctions: List<Coords3D>) {
        val junction2Circuit = junctions.associateWithTo(mutableMapOf()) { mutableSetOf(it) }
        val pairs = sequence {
            var sublist = junctions
            while (sublist.isNotEmpty()) {
                val first = sublist.first()
                val rest = sublist.subList(1, sublist.size)
                yieldAll(rest.map { Pair(first, it) })
                sublist = rest
            }
        }.toMutableList().also { it.sortBy { (j1, j2) -> distSquared(j1, j2) } }
        val connectionIter = pairs.iterator()
        fun connectNextClosest() {
            assert(connectionIter.hasNext())
            val (j1, j2) = connectionIter.next()
            val circ1 = junction2Circuit[j1]!!
            val circ2 = junction2Circuit[j2]!!
            if (circ1 !== circ2) {
                circ1.addAll(circ2)
                for(j in circ2){
                    junction2Circuit[j] = circ1
                }
            }
        }

        fun distSquared(j1: Coords3D, j2: Coords3D) = (j1.first - j2.first).pow(2) +
                (j1.second - j2.second).pow(2) +
                (j1.third - j2.third).pow(2)
    }

    @Nested
    inner class Task1 {

        @Test
        fun testSmall() {
            val actual = solve(load_test(), 10)
            assertEquals(40, actual)
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod(), 1000)
            result.println("Result: $actual")
            assertEquals(140008, actual)
            logger.info { "Result: $actual" }
        }

        fun solve(txt: String, n: Int): Any {
            val data = parseInput(txt)
            val sol = Solution(data)
            repeat(n) { sol.connectNextClosest() }
            val res = sol.junction2Circuit.values.distinct()
                .asSequence()
                .map { it.size }
                .sortedDescending()
                .take(3)
                .reduce { acc, i -> acc * i }
            return res
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            result.println("Result: $actual")
            logger.info { "Result: $actual" }
            assertEquals(25272, actual)
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

        fun parseInput(txt: String): List<Coords3D> {
            val data = txt.lineSequence().map { line ->
                val (x, y, z) = line.split(',').map { it.toLong() }
                Coords3D(x, y, z)
            }.toList()
            return data
        }
    }
}