package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Day9 {
    class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution1.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(114L, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution1.solve(load_prod())
            println("Result: $actual")
            assertEquals(2075724761L, actual)
        }
    }

    class Task2 {
        @Test
        fun testSmall() {
            val actual = Solution2.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(2L, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution2.solve(load_prod())
            println("Result: $actual")
            assertEquals(1072L, actual)
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

    object Solution1 {
        fun solve(txt: String): Any {
            var dataSet = txt.lineSequence()
                .map { it.splitToSequence(' ').map { it.toLong() } }

            val sum_of_predictions = dataSet.map { predict(it) }
                .sum()

            return sum_of_predictions
        }

        fun predict(data: Sequence<Long>): Long {
            val components = extractComponents(data)
            val binom = genBinomCoeffs().drop(components.size).first()
            val next = components.asSequence().zip(binom.asSequence()) { comp, binom -> comp * binom }.sum()
            return next
        }

        fun extractComponents(data: Sequence<Long>): List<Long> {
            val components = mutableListOf<Long>()
            val binomSeq = genBinomCoeffs()
            for ((term, binom) in data.zip(binomSeq)) {
                val allButLast = binom
                    .asSequence()
                    .take(binom.size - 1)
                    .zip(components.asSequence()) { bcoef, comp ->
                        bcoef * comp
                    }.sum()
                val next_component = term - allButLast
                components.add(next_component)
            }
            return components
        }

        fun genBinomCoeffs() = sequence {
            yield(longArrayOf(1))
            val init = longArrayOf(1, 1)
            yield(init)
            var last = init
            while (true) {
                val next = LongArray(last.size + 1)
                next[0] = 1
                next[next.size - 1] = 1
                last.asSequence()
                    .zipWithNext { a, b -> a + b }
                    .forEachIndexed { index, v -> next[index + 1] = v }
                yield(next)
                last = next
            }
        }
    }

    object Solution2 {
        fun solve(txt: String): Long {
            var dataSet = txt.lineSequence()
                .map { it.splitToSequence(' ').map { it.toLong() } }
            val sum_of_predictions = dataSet.map { predictBack(it) }.sum()
            return sum_of_predictions
        }

        fun predictBack(data: Sequence<Long>): Long {
            val components = Solution1.extractComponents(data)
            val prev = components.asReversed().asSequence()
                .dropWhile { it == 0L }
                .fold(0L) { acc, a -> a - acc }
            return prev
        }

    }

    class Tests {
        @Test
        fun test_predict() {
            val data = "1 3 6 10 15 21".split(' ').map { it.toLong() }.asSequence()
            val next = Solution1.predict(data)
            assertEquals(28, next)
        }
        @Test
        fun test_predict_back() {
            val data = "10 13 16 21 30 45".split(' ').map { it.toLong() }.asSequence()
            val next = Solution2.predictBack(data)
            assertEquals(5, next)
        }

        @Test
        fun test_binom() {
            val binoms = """
                1
                1 1
                1 2 1
                1 3 3 1
                1 4 6 4 1
                1 5 10 10 5 1
            """.lineSequence().map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { Regex("""\d+""").findAll(it).map { it.value.toLong() }.toList().toLongArray() }
            Solution1.genBinomCoeffs().zip(binoms)
                .forEach { (a, b) -> assertContentEquals(a, b) }
        }
    }

}

