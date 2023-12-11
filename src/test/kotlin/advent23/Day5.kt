package advent23

import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

class Day5 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(35L, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val lines = txt.lineSequence().iterator()
            val seeds = lines.asSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .first()
                .let {
                    it.removePrefix("seeds: ")
                        .split(' ')
                        .map { it.toLong() }
                }.toList()
            val mappings = lines.asSequence().map { it.trim() }
                .dropWhile { it.isEmpty() }
                .chunked { it.isEmpty() }
                .map { block ->
                    val header = block.first().removeSuffix(" map:")
                    val ranges = block.map { parseRange(it) }.toList()
                    Mapping(header, ranges)
                }.toList()
            val lowerLocation = seeds.map { seed ->
                mappings.fold(seed) { v, mapping ->
                    mapping.transform(v)
                }
            }.min()
            return lowerLocation
        }

    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(46L, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val lines = txt.lineSequence().iterator()
            val seeds = lines.asSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .first()
                .let {
                    it.removePrefix("seeds: ")
                        .split(' ')
                        .map { it.toLong() }
                        .chunked(2)
                        .map { (a, n) -> LongRange(a, a + n - 1) }
                }

            val mappings = lines.asSequence().map { it.trim() }
                .dropWhile { it.isEmpty() }
                .chunked { it.isEmpty() }
                .map { block ->
                    val header = block.first().removeSuffix(" map:")
                    val ranges = block.map { parseRange(it) }.toList()
                    Mapping(header, ranges)
                }
            // Merge all maps into a single one for easier calculations
            val mergedMapping = mappings.reduce { m1, m2 -> m1.merge(m2) }

            // Build a list of seeds that may be a minimum.
            // They are the boundaries of ranges and gaps between them
            val candidates = seeds.asSequence().flatMap { seeds ->
                val start = seeds.first
                val end = seeds.last
                mergedMapping.ranges.asSequence()
                    .dropWhile { it.srcEnd() < start }
                    .takeWhile { it.src <= end }
                    .flatMap {
                        sequenceOf(
                            it.src - 1,         // Check
                            it.src,
                            it.srcEnd(),
                            it.srcEnd() + 1
                        )
                    }
                    .filter { it in start..end }
            }
            return candidates.map { mergedMapping.transform(it) }.min()
        }
    }

    data class MappingRange(val dst: Long, val src: Long, val n: Long) {

        val srcRange = LongRange(src, srcEnd())
        val dstRange = LongRange(dst, dstEnd())
        operator fun contains(v: Long): Boolean {
            return v >= src && v < src + n
        }

        fun transform(v: Long): Long {
            return v + delta()
        }

        fun reverse(v: Long): Long {
            return v - delta()
        }

        fun delta() = dst - src
        fun srcEnd() = src + n - 1
        fun dstEnd() = dst + n - 1
        fun subrange(delta: Long, n: Long) = MappingRange(dst + delta, src + delta, n)
        override fun toString(): String {
            return "[${this.src}-${this.srcEnd()}] -> [${this.dst}-${this.dstEnd()}]"
        }

        fun subrangeSrc(start: Long, end: Long) = subrange(start - src, end - start + 1)

        fun subrangeDst(dstStart: Long, dstEnd: Long) = subrange(dstStart - dst, dstEnd - dstStart + 1)
        fun subrangeSrc(range: LongRange) = this.subrangeSrc(range.first, range.last)
        fun intersectSrc(start: Long, end: Long) = subrangeSrc(max(src, start), min(srcEnd(), end))
        fun intersectSrc(srcRange: LongRange) = intersectSrc(srcRange.first, srcRange.last)
    }

    class Mapping(val name: String, val ranges: List<MappingRange>) {
        fun transform(v: Long): Long {
            for (r in ranges) {
                if (v in r)
                    return r.transform(v)
            }
            return v
        }

        fun merge(other: Mapping): Mapping {
            if (ranges.isEmpty()) return other
            if (other.ranges.isEmpty()) return this
            val prevRangesList = ranges.sortedBy { it.src }
            val nextRangesList = other.ranges.sortedBy { it.src }
            val ranges = sequence {
                val pRanges = prevRangesList.iterator().memorizing()
                pRanges.next()
                var gapStart = Long.MIN_VALUE
                for (currentRange in prevRangesList) {
                    val gapEnd = currentRange.src - 1
                    // Process the gap before the range
                    val gapIntersections = nextRangesList.asSequence()
                        .dropWhile { it.srcEnd() < gapStart }
                        .takeWhile { it.src <= gapEnd }
                        .map { it.intersectSrc(gapStart, gapEnd) }
                    yieldAll(gapIntersections)
                    // Process the range itself
                    val rangeIntersections = nextRangesList.asSequence()
                        .dropWhile { it.srcEnd() < currentRange.dst }
                        .takeWhile { it.src <= currentRange.dstEnd() }
                        .map { it.intersectSrc(currentRange.dstRange) }
                    var pos = currentRange.dst
                    for (nRange in rangeIntersections) {
                        if (nRange.src > pos) {
                            yield(currentRange.subrangeDst(pos, nRange.src - 1))
                        }
                        yield(MappingRange(nRange.dst, currentRange.reverse(nRange.src), nRange.n))
                        pos = nRange.srcEnd() + 1
                    }
                    if (pos <= currentRange.dstEnd())
                        yield(currentRange.subrangeDst(pos, currentRange.dstEnd()))
                    gapStart = currentRange.srcEnd() + 1
                }
                // Process the gap to the end of Long space
                val gapIntersections = nextRangesList.asSequence()
                    .dropWhile { it.srcEnd() < gapStart }
                    .map { it.intersectSrc(gapStart, Long.MAX_VALUE) }
                yieldAll(gapIntersections)
            }
            return Mapping("", ranges.sortedBy { it.src }.toList())
        }

        override fun toString(): String {
            return ranges.toString()
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

        val rangeRe = Regex("""(\d+) (\d+) (\d+)""")
        fun parseRange(str: String): MappingRange {
            val (dst, src, n) = rangeRe.matchEntire(str)!!.destructured
            return MappingRange(dst.toLong(), src.toLong(), n.toLong())
        }
    }
}

