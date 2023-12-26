package advent23

import org.junit.jupiter.api.Test

class Day25 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(54, actual)
        }

        /**
         * 10829 is wrong
         */
        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(603368, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            val graph: Map<String, Set<String>> = txt.lineSequence()
                .flatMap {
                    val (first, rest) = it.split(':').map { it.trim() }
                    val fwd = rest.split(' ').map { Pair(first, it) }
                    fwd + fwd.map { (a, b) -> Pair(b, a) }
                }
                .groupingBy { it.first }
                .aggregate { key, accumulator, element, first -> if (accumulator == null) setOf(element.second) else accumulator + element.second }
            val start = graph.keys.asSequence().drop(7).first()
            val runtime = Runtime(graph)
            graph[start]!!.forEach { runtime.commonNeighbors.compute(it) { k, v -> v!! + 1 } }
            runtime.commonNeighborsSet += graph[start]!!
            runtime.visitRecursively(start, start)
//            runtime.visited.filterValues { it > 0 }
//                .forEach { println(it.key) }

            val clusterA = runtime.visited.values.count { it > 0 }
            val clusterB = graph.size - clusterA
            return clusterA * clusterB
        }
    }

    class Runtime(val graph: Map<String, Set<String>>) {
        val commonNeighborsSet = mutableSetOf<String>()
        val commonNeighbors = graph.mapValues { 0 }.toMutableMap()
        val visited = graph.mapValues { 0 }.toMutableMap()
        fun visitRecursively(start: String, from: String) {
            val visits = visited.computeIfPresent(start) { key, v -> v + 1 }!!
            if (visits > 1) return

//            val neighbors = graph[start]!!.filter { commonNeighbors[it]!! > 0 }
//            val neighbors = graph[start]!!.intersect(commonNeighborsSet)
            val neighbors = graph[start]!!
            commonNeighborsSet += graph[start]!!
            graph[start]!!.forEach { commonNeighbors.compute(it) { k, v -> v!! + 1 } }
            for (n in neighbors) {
                if (n == from) continue
                visitRecursively(n, start)
            }
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
        fun visualizeGraph() {
            val graph = load_prod().lineSequence()
                .flatMap {
                    val (first, rest) = it.split(':').map { it.trim() }
                    val fwd = rest.split(' ').map { Pair(first, it) }
                    fwd
                }
            val str = graph.map { (a, b) -> """"$a" <-> "$b"""" }
                .joinToString(",")
            dbg.print("""edgeList = {""")
            dbg.print(str)
            dbg.println("}")
        }
    }
}

