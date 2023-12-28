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
            val edges = runtime.findBottleNecks(3, start)

            val clusterA = runtime.countNodes(start, ignoreEdges = edges)
            val clusterB = graph.size - clusterA
            return clusterA * clusterB
        }
    }

    class Runtime(val graph: Map<String, Set<String>>) {
        private lateinit var distMap: MutableMap<String, Int>
        val edgePenaltyMap = mutableMapOf<Pair<String, String>, Int>()

        fun findFurthest(start: String): String {
            buildDistanceMap(start)
            return distMap.maxBy { it.value }.key
        }

        private fun buildDistanceMap(start: String) {
            val seen = mutableMapOf<String, Int>()
            var currentIter = mutableSetOf(start)
            var iter = 0
            while (currentIter.isNotEmpty()) {
                iter++
                val nextIter = mutableSetOf<String>()
                for (node in currentIter) {
                    nextIter += (graph[node]!! - seen.keys - currentIter)
                }
                seen.putAll(currentIter.map { Pair(it, iter) })
                currentIter = nextIter
            }
            distMap = seen
        }

        fun getEdgePenalty(fromNode: String, toNode: String, default: Int = 1): Int {
            val key = getEdgeKey(fromNode, toNode)
            return edgePenaltyMap.getOrDefault(key, default)
        }

        private inline fun getEdgeKey(fromNode: String, toNode: String) =
            if (fromNode < toNode) Pair(fromNode, toNode) else Pair(toNode, fromNode)

        fun computeEdgePenalty(node1: String, node2: String, op: (Int) -> Int): Int {
            return edgePenaltyMap.compute(getEdgeKey(node1, node2)) { _, v -> op(v ?: 1) }!!
        }

        fun findBottleNecks(n: Int, start: String): Set<Pair<String, String>> {
            val end1 = findFurthest(start)
            val end2 = findFurthest(end1)
            fun fastestRoute(fromNode: String): List<String> {
                val visited = mutableMapOf<String, Int>()
                val path = linkedSetOf(fromNode)
                var minPathCost = Int.MAX_VALUE
                var minPath = emptyList<String>()
                fun searchRecursively(fromNode: String, cost: Int) {
                    if (fromNode == end1) {
                        if (cost < minPathCost) {
                            minPathCost = cost
                            minPath = path.toList()
                        }
                        return
                    }
                    if (cost >= (visited[fromNode] ?: Int.MAX_VALUE)) {
                        return
                    }
                    visited[fromNode] = cost
                    val neighbors = (graph[fromNode]!! - path).sortedBy { toNode ->
                        val nodeDist = distMap[toNode]!!
                        val edgePenalty = getEdgePenalty(fromNode, toNode)
                        nodeDist + edgePenalty
                    }
                    for (n: String in neighbors) {
                        val costToNextNode = cost + getEdgePenalty(fromNode, n)
                        val potentialMinimalCost = costToNextNode + distMap[n]!!
                        if (potentialMinimalCost >= minPathCost) continue
                        path.add(n)
                        searchRecursively(n, costToNextNode)
                        path.remove(n)

                    }
                }
                searchRecursively(fromNode, 0)
                return minPath
            }
            for (i in 1..n * 10) {
                val minPath = fastestRoute(end2).zipWithNext()
                val visitedPenalty = 1000000
                minPath.forEach { (a, b) -> computeEdgePenalty(a, b) { v -> v + visitedPenalty } }
                val border =
                    edgePenaltyMap.asSequence().map { it.value }.sortedDescending().drop(n - 1).take(2).toList()
                val (before, after) = border
                if (before > after) {
                    break
                }
            }
            return edgePenaltyMap.asSequence().sortedByDescending { it.value }.take(20)
                .onEach { println(it) }
                .map { it.key }
                .toList()
                .take(n)
                .toSet()
        }

        fun countNodes(start: String, ignoreEdges: Set<Pair<String, String>>): Int {
            val visited = mutableSetOf(start)
            val candidates = mutableSetOf(start)
            while (candidates.isNotEmpty()) {
                val node = candidates.first().also { candidates.remove(it) }
                visited += node
                for (neighbor in graph[node]!!) {
                    if (neighbor in visited) continue
                    if (Pair(node, neighbor) in ignoreEdges || Pair(neighbor, node) in ignoreEdges) continue
                    candidates += neighbor
                }
            }
            return visited.size
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

