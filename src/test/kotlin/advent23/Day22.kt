package advent23

import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min

class Day22 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(505, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution.solve2(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(7, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(71002, actual)
        }

    }

    object Solution {
        val brickRe = Regex("""(\d+),(\d+),(\d+)~(\d+),(\d+),(\d+)""")
        private fun parseBrick(str: String): Brick {
            val (xmin, ymin, zmin, xmax, ymax, zmax) = brickRe.matchEntire(str)!!.groupValues.asSequence().drop(1)
                .map { it.toInt() }
                .toList()
            return Brick(
                min(xmin, xmax), max(xmin, xmax),
                min(ymin, ymax), max(ymin, ymax),
                min(zmin, zmax), max(zmin, zmax)
            )
        }

        fun solve(txt: String): Any {
            val bricks = parseInput(txt)
            val bricksWithSingleSupport =
                bricks.asSequence().map { it.supports }.filter { it.size == 1 }.flatten().distinct().count()
            return bricks.size - bricksWithSingleSupport
        }

        private fun parseInput(txt: String): List<Brick> {
            val bricks = txt.lineSequence()
                .map { parseBrick(it) }
                .sortedBy { it.zmin }
                .toList()
            val openBricks = mutableSetOf<Brick>()
            bricks.forEach { b ->
                val intersects = openBricks.asSequence().filter { b.intersectXY(it) }.toList()
                val topZ = intersects.maxOfOrNull { it.zmax } ?: 0
                val supports = intersects.filter { it.zmax == topZ }
                val dz = b.zmin - topZ - 1
                b.zmin -= dz
                b.zmax -= dz
                openBricks.add(b)
                b.supports = supports
            }
            return bricks
        }

        fun solve2(txt: String): Any {
            val bricks = parseInput(txt).sortedBy { it.zmax }
            return bricks.sumOf { b ->
                val fallenBricks = mutableSetOf(b)
                var fallenCounter = 0
                while (fallenBricks.isNotEmpty()) {
                    val zmax = fallenBricks.minOf { it.zmax }
                    val batch = fallenBricks.filter { it.zmax == zmax }.toSet()
                    fallenBricks.removeAll(batch)
                    val newFallen = bricks.filter { it.zmin == zmax + 1 && batch.containsAll(it.supports) }
                    fallenCounter += newFallen.size
                    fallenBricks.addAll(newFallen)
                }
                fallenCounter
            }
        }
    }


    data class Brick(val xmin: Int, val xmax: Int, val ymin: Int, val ymax: Int, var zmin: Int, var zmax: Int) {
        var supports: List<Brick> = emptyList()
        fun intersectXY(other: Brick): Boolean {
            return IntRange(xmin, xmax).intersectRange(IntRange(other.xmin, other.xmax)).size() > 0 &&
                    IntRange(ymin, ymax).intersectRange(IntRange(other.ymin, other.ymax)).size() > 0
        }
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(prefix: Any = ""): String {
            return Resources().loadString("${res_prefix}_test$prefix.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }
    }
}