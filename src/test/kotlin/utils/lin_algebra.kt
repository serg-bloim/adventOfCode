package utils

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.abs
import kotlin.math.min

data class Fraction(var num: Int, var denom: Int) : Comparable<Fraction> {
    override fun compareTo(other: Fraction) = toDouble().compareTo(other.toDouble())

    override fun toString(): String {
        if (isWhole()) return num.toString()
        return "$num/$denom"
    }
}

fun Int.toFraction() = Fraction(this, 1)
fun solveLinearEqSystem(coefs: Array<Array<Int>>, consts: Array<Int>) {
    val fcoefs = coefs.map { it.map { it.toFraction() }.toTypedArray() }.toTypedArray()
    val fconsts = consts.map { it.toFraction() }.toTypedArray()
    solveLinearEqSystem(fcoefs, fconsts)
    for (i in coefs.indices) {
        for (j in coefs[0].indices) {
            coefs[i][j] = fcoefs[i][j].toInt()
        }
    }
    for (i in consts.indices) {
        consts[i] = fconsts[i].toInt()
    }
}

fun Fraction.toInt() = num / denom

fun Fraction.toDouble() = num.toDouble() / denom

fun solveLinearEqSystem(coefs: Array<Array<Fraction>>, consts: Array<Fraction>) {
    val logger = KotlinLogging.logger("solveLinearEqSystem")
    fun <T> Array<T>.swap(i: Int, j: Int) {
        if (i == j) return
        val tmp = get(i)
        set(i, get(j))
        set(j, tmp)
    }

    fun swapRows(i: Int, j: Int) {
        coefs.swap(i, j)
        consts.swap(i, j)
    }

    fun subtractRow(subtrFrom: Int, subtrWhat: Int, n: Fraction) {
        val n = n.copy()
        for (ind in coefs[0].indices) {
            coefs[subtrFrom][ind].minusAssign(coefs[subtrWhat][ind] * n)
        }
        consts[subtrFrom].minusAssign(consts[subtrWhat] * n)
    }

    fun divRow(rowInd: Int, div: Fraction) {
        val div = div.copy()
        for (ind in coefs[0].indices) {
//            assert(coefs[rowInd][ind] % div == 0)
            coefs[rowInd][ind] /= div
        }
//        assert(consts[rowInd] % div == 0)
        consts[rowInd] /= div
    }

    var diagOffset = 0
    val minDimension = min(coefs.size, coefs[0].size)
    for (j in 0 until minDimension) {
        while (j + diagOffset in coefs[0].indices) {
            val hasNonNullRow = coefs
                .withIndex()
                .any { (index, row) -> index >= j && row[j + diagOffset].isNotZero() }
            if (hasNonNullRow) {
                break
            } else {
                diagOffset++
            }
        }
        val pivotColumn = j + diagOffset
        if (pivotColumn !in coefs[0].indices) break
        val nonNullRowInd = coefs.asSequence().withIndex()
            .filter { (index, row) -> index >= j && row[pivotColumn].isNotZero() }
            .map { it.index }
            .first()

        swapRows(j, nonNullRowInd)

        val div = coefs[j][pivotColumn]
        divRow(j, div)
        // zeroing out all other rows in column j
        for (k in coefs.indices) {
            if (k != j) {
                val times = coefs[k][pivotColumn]
                subtractRow(k, j, times)
            }
        }
    }
}

operator fun Fraction.divAssign(other: Fraction) {
    num *= other.denom
    denom *= other.num
    normalize()
}

operator fun Fraction.times(other: Fraction) = Fraction(num * other.num, denom * other.denom).also { it.normalize() }

fun Fraction.isZero() = num == 0
fun Fraction.isNotZero() = !isZero()

operator fun Fraction.plusAssign(other: Int) {
    num += other * denom
    normalize()
}

operator fun Fraction.minusAssign(other: Fraction) {
    if (denom == other.denom) {
        num -= other.num
    } else {
        num = (num * other.denom) - (other.num * denom)
        denom *= other.denom
        normalize()
    }
    if (num == 0) denom = 1
}

operator fun Fraction.times(other: Int): Fraction = Fraction(num * other, denom)

operator fun Fraction.plus(other: Fraction): Fraction = Fraction((num * other.denom) + (other.num * denom), denom * other.denom).also { it.normalize() }

operator fun Fraction.minus(other: Fraction): Fraction = Fraction((num * other.denom) - (other.num * denom), denom * other.denom).also { it.normalize() }

operator fun Fraction.compareTo(n: Int) = when {
    isWhole() && n == num -> 0
    toInt() >= n -> 1
    else -> -1
}

fun Fraction.isWhole(): Boolean {
    if (denom != 1) normalize()
    return denom == 1
}

fun Fraction.normalize() {
    if (denom == 1) return
    if (num == 0) denom = 1
    else {
        val gcf = getGCF(abs(num), abs(denom)) * if (denom < 0) -1 else 1
        num /= gcf
        denom /= gcf
    }
}

fun getGCF(a: Int, b: Int) = greatestCommonDivisor(a.toLong(), b.toLong()).toInt()

fun parseLinEqSystem(txt: String): Pair<Array<Array<Int>>, Array<Int>> =
    txt.trim().lines().map { line ->
        val (coefsStr, constStr) = line.split("|")
        val coefs = coefsStr.trim().split("\\s+".toRegex()).map { it.trim().toInt() }.toTypedArray()
        val const = constStr.trim().toInt()
        Pair(coefs, const)
    }.unzip()
        .let { (cfs, csts) -> Pair(cfs.toTypedArray(), csts.toTypedArray()) }

fun toString(coefs: Array<Array<String>>, consts: Array<String>, indent: Boolean = true, constsSeparator: String = "|"): String {
    val padding = if (indent) {
        coefs.maxOf { it.maxOf { it.length } }
    } else 0
    val txt = coefs.zip(consts) { row, cnst ->
        val rowStr = row.joinToString(" ") { it.padStart(padding) }
        "$rowStr $constsSeparator $cnst"
    }
        .joinToString("\n")
    return txt
}

fun toString(coefs: Array<Array<Int>>, consts: Array<Int>, indent: Boolean = true, constsSeparator: String = "|"): String {
    return toString(coefs.map { row -> row.map { it.toString() }.toTypedArray() }.toTypedArray(), consts.map { it.toString() }.toTypedArray(), indent, constsSeparator)
}

fun toString(coefs: Array<Array<Fraction>>, consts: Array<Fraction>, indent: Boolean = true, constsSeparator: String = "|"): String {
    return toString(coefs.map { row -> row.map { it.toString() }.toTypedArray() }.toTypedArray(), consts.map { it.toString() }.toTypedArray(), indent, constsSeparator)
}