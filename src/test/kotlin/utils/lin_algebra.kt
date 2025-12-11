package utils

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.abs

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

    for (j in coefs[0].indices) {
        // Find any row below j that has non-zero value at j and swap into j pos
//        logger.info { "Step: $j\n" + toString(coefs, consts) }
        val nonNullRowInd = coefs.withIndex()
            .firstOrNull() { (index, row) -> index >= j && row[j] != 0 && rowDivisible(index, row[j]) }
            ?.index
        if (nonNullRowInd == null) {
            val check = coefs.withIndex().all { (index, row) -> index < j || row[j] == 0 }
            assert(check)
        }

        if (nonNullRowInd != null) {
            if (!(coefs[nonNullRowInd][j] == 1)) {
                1 + 1
            }
            swapRows(j, nonNullRowInd)
            if (coefs[j][j] != 1) {
                //normalize row
                val div = coefs[j][j]
                divRow(j, div)
            }
            // make sure all following rows have 0 in j column by subtracting j row from it
            for (k in (j + 1) until coefs.size) {
                val times = coefs[k][j]
                subtractRow(k, j, times)
            }
        } else {
            1 + 1
        }
    }
    for (j in coefs[0].indices.reversed()) {
        for (k in 0 until j) {
            val times = coefs[k][j]
            subtractRow(k, j, times)
        }
    }
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