package advent23

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertContentEquals

class UtilsKtTest {

    @Test
    fun testChunked() {
        val txt = "1 2 3; 4; 5 6; 7 8 9 0; 50"
        val chunked =
            Regex("""\d+|;""").findAll(txt).map { it.value }.chunked { it == ";" }.map { it.map(String::toInt).sum() }
                .toList()
        chunked.forEach { println("Chunk: $it") }
        assertContentEquals(listOf(6, 4, 11, 24, 50), chunked)
    }
    @Test
    fun testMemorizingIterator() {
        val nums = IntRange(1, 10).toList()
        assertContentEquals(nums, nums.iterator().memorizing().asSequence().toList())
        val iter = nums.iterator().memorizing()
        iter.next()
        iter.next()
        assertEquals(2, iter.current())
    }

    @Test
    fun genPrimes() {
        val truePrimes = listOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271).map { it.toLong() }
        val genPrimes = Primes.getUntil(truePrimes.max())
        val genPrimes2 = Primes.getUntil(truePrimes.max() + 1)
        val genPrimes3 = Primes.getUntil(truePrimes.max() / 2)
        assertContentEquals(truePrimes, genPrimes)
        assertContentEquals(truePrimes, genPrimes2)
    }
    @Test
    fun leastCommonMultiple() {
        assertEquals(60L, leastCommonMultiple(12, 15))
        assertEquals(12L, leastCommonMultiple(12, 3))
    }
}