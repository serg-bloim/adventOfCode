package advent23

import org.junit.jupiter.api.Test
import java.io.PrintStream
import kotlin.math.max
import kotlin.test.assertEquals

fun <T> Sequence<T>.chunked(predicate: (T) -> Boolean): Sequence<Sequence<T>> {
    val seq = this
    return sequence {
        val iter = seq.iterator()
        while (iter.hasNext()) {
            yield(sequence {
                for (elem in iter) {
                    if (predicate(elem)) break
                    yield(elem)
                }
            }.toList().asSequence())
        }
    }
}

fun <T> Sequence<T>.toCircularLinkedList(): CircularLinkedList<T> {
    val nodes = this.map { CircularLinkedNode(it) }
        .toList()
    val list = CircularLinkedList(nodes)
    nodes.asSequence().zipWithNext { a, b ->
        a.next = b
        b.prev = a
    }.forEach { }
    val fst = nodes.first()
    val lst = nodes.last()
    fst.prev = lst
    lst.next = fst
    return list
}

class CircularLinkedNode<T>(val value: T) {
    var next: CircularLinkedNode<T> = this
    var prev: CircularLinkedNode<T> = this
    lateinit var list: CircularLinkedList<T>
    fun fwd(n: Int): CircularLinkedNode<T> = generateSequence(this) { it.next }.drop(n).first()
    fun bwd(n: Int): CircularLinkedNode<T> = generateSequence(this) { it.prev }.drop(n).first()
    fun move(n: Long) {
        val size = list.size - 1
        val relativeMoveFwd = ((n % size + size) % size).toInt()
        if (relativeMoveFwd == 0) return
        val insertionPoint =
            if (relativeMoveFwd < size / 2) {
                fwd(relativeMoveFwd)
            } else {
                bwd(list.size - relativeMoveFwd)
            }
        removeSelf()
        insertionPoint.insertAfter(this)
    }

    fun move(n: Int) = move(n.toLong())

    private fun insertAfter(node: CircularLinkedNode<T>) {
        node.next = next
        node.prev = this
        next.prev = node
        next = node
        list.size++
    }

    private fun removeSelf() {
        prev.next = next
        next.prev = prev
        list.size--
    }
}

class CircularLinkedList<T>(nodes: List<CircularLinkedNode<T>>) {
    fun nodesAsSequence(repeat: Boolean = true) =
        generateSequence(first) { it.next }.let { if (repeat) it else it.take(size) }

    fun toNodeList() = nodesAsSequence(false).toList()

    var size = nodes.size
    val first = nodes.first()

    init {
        for (n in nodes) {
            n.list = this
        }
    }

    override fun toString(): String = "[" + nodesAsSequence(false).joinToString(",") { it.value.toString() } + "]"
}

class CirculatLinkedListTest {
    @Test
    fun testCircular1() {
        val test = "0 1 2 3 4 5 6 7 8"
        val llist = test.split(' ').asSequence().map { it.toInt() }.toCircularLinkedList()
        val a = llist.first.fwd(1)
        assertEquals(5, a.fwd(4).value)
        assertEquals(5, a.bwd(9 - 4).value)
        a.move(4)
        assertEquals("0 2 3 4 5 1 6 7 8", llist.toNodeList().joinToString(" ") { it.value.toString() })
        val b = llist.first.fwd(1)
        b.move(6)
        assertEquals("0 3 4 5 1 6 7 2 8", llist.toNodeList().joinToString(" ") { it.value.toString() })

    }

    @Test
    fun testPow() {
        assertEquals(1, 5.pow(0))
        assertEquals(5, 5.pow(1))
        assertEquals(125, 5.pow(3))
    }

    @Test
    fun testRangeIntersect() {
        assertEquals(LongRange(5, 10), LongRange(1, 10).intersectRange(LongRange(5, 20)))
        assertEquals(LongRange.EMPTY, LongRange(1, 10).intersectRange(LongRange(11, 20)))
        assertEquals(LongRange(10, 10), LongRange(1, 10).intersectRange(LongRange(10, 20)))
    }
}

fun Int.pow(v: Int) = generateSequence(1) { it * this }.drop(v).first()

fun Long.pow(v: Int) = generateSequence(1L) { it * this }.drop(v).first()

fun LongRange.intersectRange(other: LongRange): LongRange {
    return LongRange(kotlin.math.max(start, other.start), kotlin.math.min(endInclusive, other.endInclusive))
}

class MemorizingIterator<T>(val iterator: Iterator<T>) : Iterator<T> {
    private var current: T? = null
    override fun hasNext() = iterator.hasNext()

    override fun next() = iterator.next().also { current = it }
//    override fun next() = (if (iterator.hasNext()) iterator.next() else null).let { val ret = peek!!; peek = it; ret }

    fun current(): T = current!!
    fun tryNext(): T? = if (hasNext()) next() else null
}

fun <T> Iterator<T>.memorizing(): MemorizingIterator<T> {
    return MemorizingIterator(this)
}

fun <T> Iterable<T>.repeatForever() = sequence {
    while (true) {
        yieldAll(this@repeatForever)
    }
}

object Primes {
    val factorCache: MutableMap<Long, Map<Long, Int>> = mutableMapOf()
    private val primes = mutableListOf(2L)
    fun getUntil(max: Long): List<Long> {
        for (n in primes.last() + 1..max) {
            if (!primes.any { n % it == 0L }) {
                primes += n
            }
        }
        val pos = primes.binarySearch(max).let {
            if (it >= 0) it + 1
            else -it - 1
        }
        return primes.subList(0, pos)
    }
}

fun <K, V : Any> Map<K, V>.merge(other: Map<K, V>, mergeOp: (V, V) -> V): Map<K, V> {
    return buildMap {
        putAll(this@merge)
        for ((k, v) in other) {
            merge(k, v, mergeOp)
        }
    }
}

fun primeFactors(num: Long): Map<Long, Int> {
    Primes.factorCache[num]?.let { return it }
    return Primes.getUntil(num).asSequence()
        .map { prime ->
            val factorMultiplications =
                generateSequence(num) { if (it % prime == 0L) it / prime else null }.count() - 1
            Pair(prime, factorMultiplications)
        }
        .filter { it.second > 0 }
        .associate { it }
        .also { Primes.factorCache[num] = it }
}

fun defactorize(factors: Map<Long, Int>) = factors.asSequence()
    .map { it.key.pow(it.value) }
    .reduceOrNull { a, b -> a * b }
    ?: 1

fun leastCommonMultiple(a: Long, b: Long): Long {
    val factorsA = primeFactors(a)
    val factorsB = primeFactors(b)
    val res = factorsA.merge(factorsB, ::max)
    val lcm = defactorize(res)
    Primes.factorCache[lcm] = res
    return lcm
}

fun <E> List<E>.permutations2(): Sequence<Pair<E, E>> {
    return sequence {
        var rest = this@permutations2
        while (rest.size > 1) {
            val first = rest.first()
            rest = rest.subList(1, rest.size)
            yieldAll(rest.asSequence().map { Pair(first, it) })
        }
    }
}


val dbg = PrintStream(System.getProperty("user.dir") + "/src/test/resources/advent23/dbg.txt")
val result = PrintStream(System.getProperty("user.dir") + "/src/test/resources/advent23/result.txt")

