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

        fun solve(txt: String, n:Int): Any {
            val data = parseInput(txt)
            val connections = data.associateWith { mutableListOf<Coords3D>() }
            val pairs = sequence {
                var sublist = data
                while (sublist.isNotEmpty()) {
                    val first = sublist.first()
                    val rest = sublist.subList(1, sublist.size)
                    yieldAll(rest.map { Pair(first, it) })
                    sublist = rest
                }
            }.toMutableList()
            pairs.sortBy { (from, to) ->
                (from.first - to.first).pow(2) +
                        (from.second - to.second).pow(2) +
                        (from.third - to.third).pow(2)
            }
            pairs.asSequence().take(n)
                .forEach { (j1, j2) ->
                    connections[j1]!!.add(j2)
                    connections[j2]!!.add(j1)
                }
            val junctions2process = data.toMutableSet()
            val circuits = mutableListOf<Set<Coords3D>>()
            while (junctions2process.isNotEmpty()) {
                val nextOrigin = junctions2process.first()
                val circuit = mutableSetOf<Coords3D>()
                circuits.add(circuit)
                fun traverse(junction: Coords3D) {
                    if(junction !in circuit) {
                        circuit.add(junction)
                        connections[junction]!!.forEach { j -> traverse(j) }
                    }
                }
                traverse(nextOrigin)
                junctions2process.removeAll(circuit)
            }
            val res = circuits.map { it.size }
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

        fun parseInput(txt: String): List<Coords3D> {
            val data = txt.lineSequence().map { line ->
                val (x, y, z) = line.split(',').map { it.toLong() }
                Coords3D(x, y, z)
            }.toList()
            return data
        }
    }
}