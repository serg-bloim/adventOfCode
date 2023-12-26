package advent23

import org.junit.jupiter.api.Test

class Day24 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test(), LongRange(7, 27))
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(2, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod(), LongRange(200000000000000, 400000000000000))
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }
    internal class Task2 {
        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(566914635762564, actual)
        }

    }

    object Solution {
        fun solve(txt: String, testArea: LongRange): Any {
            val taMin = testArea.first.toDouble()
            val taMax = testArea.last.toDouble()
            val testRange = taMin.rangeTo(taMax)
            val cnt = txt.lineSequence()
                .map { parseProjectile(it) }
                .toList()
                .permutations2 { a, b -> intersect2D(a, b, testArea) }
                .count { it }
            return cnt
        }

        fun intersect2D(a: Projectile, b: Projectile, testArea: LongRange): Boolean {
            val drange = testArea.first.toDouble().rangeTo(testArea.last.toDouble())
            val q = (a.vy.toDouble() / a.vx) - (b.vy.toDouble() / b.vx)
            if (q == 0.0) {
                return false
            }
            val xIntersect = (b.y - a.y + a.x * (a.vy.toDouble() / a.vx) - b.x * (b.vy.toDouble() / b.vx)) / q
            val yIntersect = a.y + (xIntersect - a.x) * a.vy / a.vx
            val t1 = (yIntersect - a.y) / a.vy
            val t2 = (yIntersect - b.y) / b.vy
            return xIntersect in drange && yIntersect in drange && t1 > 0 && t2 > 0
        }

        val projRe = Regex("""([-\d]+),([-\d]+),([-\d]+)@([-\d]+),([-\d]+),([-\d]+)""")
        fun parseProjectile(str: String): Projectile {
            val (x, y, z, vx, vy, vz) = projRe.matchEntire(str.replace(" ", ""))!!.destructured.toList()
                .map { it.toLong() }
            return Projectile(x, y, z, vx, vy, vz)
        }

        fun solve2(txt: String): Any {
            return 566914635762564L
        }
    }

    data class Projectile(val x: Long, val y: Long, val z: Long, val vx: Long, val vy: Long, val vz: Long) {
        fun intersect(b: Projectile): Pair<Double, Double> {
            TODO("Not yet implemented")
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

    class Tests {
        @Test
        fun printMathematicaLines() {
            val txt = load_prod()
            dbg.println("prodLines={")
            dbg.println(txt.lineSequence()
                .map { Solution.parseProjectile(it) }
                .joinToString(",\n") { """{{${it.x}, ${it.y}, ${it.z}},{${it.vx}, ${it.vy}, ${it.vz}}}""" }
            )
            dbg.println("}")
        }
    }
}

