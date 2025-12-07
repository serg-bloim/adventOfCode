package utils

import org.junit.jupiter.api.Test
import java.io.PrintStream
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals


val dbg = PrintStream(System.getProperty("user.dir") + "/dbg.txt")
val result = PrintStream(System.getProperty("user.dir") + "/result.txt")

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
    return LongRange(max(first, other.first), min(last, other.last))
}

fun IntRange.intersectRange(other: IntRange): IntRange {
    return IntRange(max(first, other.first), min(last, other.last))
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

fun <T> Iterable<T>.repeatForever(n: Int = Int.MAX_VALUE) = sequence {
    for (i in 1..n) {
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

fun <K, V : Any> Map<K, V>.intersect(other: Map<K, V>, mergeOp: (V, V) -> V): Map<K, V> {
    return buildMap {
        val first = this@intersect
        for ((k, v) in other) {
            first[k]?.also { put(k, mergeOp(it, v)) }
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

fun greatestCommonDivisor(a: Long, b: Long): Long {
    val factorsA = primeFactors(a)
    var rest = b
    var gcd = 1L
    val factors = buildMap {
        for ((f, cnt) in factorsA) {
            for (i in 0..<cnt) {
                if (rest % f == 0L) {
                    this.compute(f) { k, v -> (v ?: 0) + 1 }
                    gcd *= f
                    rest /= f
                }
            }
        }
    }
    Primes.factorCache[gcd] = factors
    return gcd
}

fun <T> List<T>.permutations(n: Int): Sequence<Sequence<T>> = sequence {
    val radix = size
    val max = radix.toLong().pow(n)
    for (i in 0..<max) {
        yield(sequence {
            var rest = i
            for (i in 0..<n) {
                val index = rest % radix
                yield(this@permutations[index.toInt()])
                rest /= radix
            }
        })
    }
}

fun <E> List<E>.permutations2() = this.permutations2 { a, b -> Pair(a, b) }
fun <E, R> List<E>.permutations2(transform: (E, E) -> R): Sequence<R> = sequence {
    val list = this@permutations2
    for (i in list.indices) {
        for (j in i + 1..<list.size) {
            yield(transform(list[i], list[j]))
        }
    }
}

fun assertEquals(expected: Any?, actual: Any?, message: String? = null) {
    val expected = if (expected is Int) expected.toLong() else expected
    val actual = if (actual is Int) actual.toLong() else actual
    assertEquals(expected, actual, message)
}

fun <T> List<T>.skipStart(n: Int) = subList(n, size)

enum class Direction {
    North,
    West,
    South,
    East, ;

    fun reversed() = when (this) {
        North -> South
        West -> East
        South -> North
        East -> West
    }

    fun next() = when (this) {
        North -> East
        West -> North
        South -> West
        East -> South
    }

    fun right() = next()
    fun left() = next().reversed()
}

data class Coords(val x: Int, val y: Int)

fun Coords.move(dir: Direction, n: Int = 1) = this + when (dir) {
    Direction.North -> Coords(0, n)
    Direction.South -> Coords(0, -n)
    Direction.West -> Coords(-n, 0)
    Direction.East -> Coords(n, 0)
}

operator fun Coords.plus(other: Coords) = Coords(x + other.x, y + other.y)

fun Coords.withinBox(width: Int, height: Int): Boolean {
    return x >= 0 && x < width && y >= 0 && y < height
}

fun <T> Coords.withinBox(field: Field<T>): Boolean {
    return x >= 0 && x < field.width && y >= 0 && y < field.height
}


data class LongCoords(val x: Long, val y: Long)

fun LongCoords.move(dir: Direction, n: Long = 1) = when (dir) {
    Direction.North -> copy(y = y + n)
    Direction.West -> copy(x = x - n)
    Direction.South -> copy(y = y - n)
    Direction.East -> copy(x = x + n)
}

fun LongRange.size(): Long = last - start + 1

fun IntRange.size(): Int = last - start + 1

fun <T> Sequence<T>.append(elem: T) = this + sequenceOf(elem)
fun min(a: Long, b: Long, c: Long) = min(a, min(b, c))

fun <T> Sequence<T>.withoutIndex(n: Int) = this.filterIndexed { index, _ -> index != n }

fun Coords.neighbors(xMax: Int = Int.MAX_VALUE - 1, yMax: Int = Int.MAX_VALUE - 1) =
    Direction.entries.asSequence()
        .map { move(it) }
        .filter { it.withinBox(xMax + 1, yMax + 1) }

operator fun <E> List<E>.component6() = this[5]

fun ceilingDiv(a: Int, b: Int) = (a + b - 1) / b

class Field<T>(val data: List<MutableList<T>>) {
    constructor(width: Int, height: Int, initOp: (Coords) -> T) : this(
        (0..<height).map { y ->
            val row = ArrayList<T>(width)
            for (x in 0..<width) {
                row.add(initOp(Coords(x, y)))
            }
            row
        }.toList()
    )

    val width = data[0].size
    val height = data.size
    operator fun contains(coords: Coords) = coords.withinBox(width, height)
    operator fun get(coords: Coords) = data[coords.y][coords.x]
    operator fun set(coords: Coords, v: T) {
        data[coords.y][coords.x] = v
    }

    fun getOrElse(coords: Coords, otherwise: T) =
        if (coords in this) this[coords] else otherwise

    fun getOrNull(coords: Coords) = if (coords in this) this[coords] else null

    fun forEachIndexed(op: (Coords, T) -> Unit) =
        data.forEachIndexed { y, row -> row.forEachIndexed { x, v -> op(Coords(x, y), v) } }

    fun findCoords(op: (T) -> Boolean): Coords? {
        data.forEachIndexed { y, row -> row.forEachIndexed { x, v -> if (op(v)) return Coords(x, y) } }
        return null
    }

    fun forEach(op: (T) -> Unit) = forEachIndexed { _, v -> op(v) }

    fun <V> mapIndexed(op: (Coords, T) -> V) =
        Field(data.mapIndexed { y, row -> row.mapIndexed { x, v -> op(Coords(x, y), v) }.toMutableList() })

    fun <V> map(op: (T) -> V) = mapIndexed { _, v -> op(v) }

    fun withBorder(borderWidth: Int, borderValue: T): Field<T> {
        val fieldWidth = this.width
        val border = borderValue.repeatAsSequence(fieldWidth + 2 * borderWidth).toMutableList()
        val oldData = data
        val newData = sequence {
            yield(border)
            for (row in oldData) {
                val newRow = ArrayList<T>()
                newRow.addAll(borderValue.repeatAsSequence(borderWidth))
                newRow.addAll(row)
                newRow.addAll(borderValue.repeatAsSequence(borderWidth))
                yield(newRow)
            }
            yield(border.toMutableList())
        }.toList()
        return Field(newData)
    }

    fun copy() = map { it }

    fun toString(cellToString: (T) -> String) =
        data.joinToString("\n") { it.joinToString("", transform = cellToString) }

    override fun toString() = toString { it.toString() }
}

fun <T> T.repeatAsSequence(n: Int): Sequence<T> = generateSequence { this }.take(n)

fun Coords.floodFillVisit(
    xMax: Int = Int.MAX_VALUE - 1,
    yMax: Int = Int.MAX_VALUE - 1,
    canTravel: (Coords, Coords) -> Boolean
) = sequence {
    val coastLine = mutableSetOf(this@floodFillVisit)
    val visited = mutableSetOf<Coords>()
    while (coastLine.isNotEmpty()) {
        val nextVisited = coastLine.first()
        coastLine.remove(nextVisited)
        yield(nextVisited)
        visited.add(nextVisited)
        nextVisited.neighbors(xMax, yMax)
            .filter { neighbor -> canTravel(nextVisited, neighbor) && neighbor !in visited }
            .forEach { neighbor -> coastLine.add(neighbor) }
    }
}

fun assertEquals(expected: Any, actual: Any) {
    val expected = if (expected is Int) expected.toLong() else expected
    val actual = if (actual is Int) actual.toLong() else actual
    assertEquals(expected, actual)
}