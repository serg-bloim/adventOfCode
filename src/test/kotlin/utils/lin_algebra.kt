package utils

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.abs
import kotlin.math.min

fun solveLinearEqSystem(coefs: Array<Array<Int>>, consts: Array<Int>) {
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

    fun subtractRow(subtrFrom: Int, subtrWhat: Int, n: Int) {
        for (ind in coefs[0].indices) {
            coefs[subtrFrom][ind] -= coefs[subtrWhat][ind] * n
        }
        consts[subtrFrom] -= consts[subtrWhat] * n
    }

    fun divRow(rowInd: Int, div: Int) {
        for (ind in coefs[0].indices) {
            assert(coefs[rowInd][ind] % div == 0)
            coefs[rowInd][ind] /= div
        }
        assert(consts[rowInd] % div == 0)
        consts[rowInd] /= div
    }

    fun rowDivisible(rowInd: Int, n: Int) =
        if (abs(n) == 1) true
        else (consts[rowInd] % n == 0) && coefs[rowInd].all { it % n == 0 }

    var diagOffset = 0
    val minDimension = min(coefs.size, coefs[0].size)
    for (j in 0 until minDimension) {
        while (j + diagOffset in coefs[0].indices) {
            // Find any row below j that has non-zero value at j and swap into j pos
//        logger.info { "Step: $j\n" + toString(coefs, consts) }
            val hasNonNullRow = coefs
                .withIndex()
                .any { (index, row) -> index >= j && row[j + diagOffset] != 0 }
            if (hasNonNullRow) {
                break
            } else {
                diagOffset++
            }
        }
        val pivotColumn = j + diagOffset
        if (pivotColumn !in coefs[0].indices) break
        val nonNullRowInd = coefs.asSequence().withIndex()
            .filter { (index, row) -> index >= j && row[pivotColumn] != 0 }
            .filter { (index, row) -> rowDivisible(index, row[pivotColumn]) } // Need it only cause we don't have fractions
            .map { it.index }
            .first()

        swapRows(j, nonNullRowInd)
        if (coefs[j][j] != 1) {
            //normalize row
            val div = coefs[j][pivotColumn]
            divRow(j, div)
        }
        // zeroing out all other rows in column j
        for (k in coefs.indices) {
            if (k != j) {
                val times = coefs[k][pivotColumn]
                subtractRow(k, j, times)
            }
        }
    }
//    for (j in coefs[0].indices.reversed()) {
//        for (k in 0 until j) {
//            val times = coefs[k][j]
//            subtractRow(k, j, times)
//        }
//    }
}

fun parseLinEqSystem(txt: String): Pair<Array<Array<Int>>, Array<Int>> =
    txt.trim().lines().map { line ->
        val (coefsStr, constStr) = line.split("|")
        val coefs = coefsStr.trim().split("\\s+".toRegex()).map { it.trim().toInt() }.toTypedArray()
        val const = constStr.trim().toInt()
        Pair(coefs, const)
    }.unzip()
        .let { (cfs, csts) -> Pair(cfs.toTypedArray(), csts.toTypedArray()) }

fun toString(coefs: Array<Array<Int>>, consts: Array<Int>, indent: Boolean = true, constsSeparator: String = "|"): String {
    val padding = if (indent) {
        coefs.maxOf { it.maxOf { it.toString().length } }
    } else 0
    val txt = coefs.zip(consts) { row, cnst ->
        val rowStr = row.joinToString(" ") { it.toString().padStart(padding) }
        "$rowStr $constsSeparator $cnst"
    }
        .joinToString("\n")
    return txt
}