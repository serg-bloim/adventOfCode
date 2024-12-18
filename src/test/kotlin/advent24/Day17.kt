package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.dbg
import utils.pow
import utils.result
import kotlin.test.assertEquals

private const val ADV = 0
private const val BXL = 1
private const val BST = 2
private const val JNZ = 3
private const val BXC = 4
private const val OUT = 5
private const val BDV = 6
private const val CDV = 7
private const val REG_A = 0
private const val REG_B = 1
private const val REG_C = 2

private val opNames = mapOf(
    ADV to "ADV",
    BXL to "BXL",
    BST to "BST",
    JNZ to "JNZ",
    BXC to "BXC",
    OUT to "OUT",
    BDV to "BDV",
    CDV to "CDV"
)

class Day17 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals("4,6,3,5,6,3,5,2,1,0", actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals("6,1,6,4,2,4,7,3,5", actual)
            println("Result: $actual")
        }
    }

    @Nested
    inner class Task2 {

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(202975183645226, actual)
        }

        fun solve(txt: String): Any {
            val txt = load_prod()
            val (mem, prog) = parseInput(txt)
            val progList = prog.toList()
            val regAVariants = findRecursively(prog, progList)
            return regAVariants.min()
        }
    }

    private fun findRecursively(prog: IntArray, expectedResult: List<Int>): List<Long> {
        if (expectedResult.isEmpty()) return listOf(0)
        val regAVariants = findRecursively(prog, expectedResult.subList(1, expectedResult.size))
        val regASolutions = mutableListOf<Long>()
        for (regA in regAVariants) {
            for (rem in 0..7) {
                val candidate = regA * 8 + rem
                val mem = LongArray(3) { 0 }
                mem[0] = candidate
                val res = solve(mem, prog).toList()
                if (res == expectedResult)
                    regASolutions.add(candidate)
            }
        }
        result.println("For $expectedResult regA is $regASolutions")
        return regASolutions
    }

    fun solve(txt: String): Any {
        val (mem, prog) = parseInput(txt)
        return solve(mem, prog).joinToString(",")
    }

    fun solve(mem: LongArray, prog: IntArray): List<Int> {
        val res = eval(prog, mem).toList()
        dbg.println()
        dbg.println()
        dbg.println("RegA: ${mem[0]}\nRegB: ${mem[1]}\nRegC: ${mem[2]}\n")
        dbg.println(res.joinToString(","))
        return res
    }

    fun eval(prog: IntArray, mem: LongArray) = sequence {
        var ip = 0
        while (true) {
            dbg.print("[" + mem.joinToString { it.toString().padStart(10) + " / " + it % 8 } + "]")
            dbg.print("  IP: ${ip.toString().padStart(3)}")
            if (ip < 0 || ip >= prog.size) {
                return@sequence
            }
            val op = prog[ip]
            val operand = prog[ip + 1].toLong()
            dbg.print(" OP: ${opNames[op]} Operand = $operand")
            if (operand < 7) dbg.print(" Combo: ${getCombo(operand, mem).toString().padEnd(10)}")
            when (op) {
                ADV -> divOp(operand, mem, REG_A)
                BDV -> divOp(operand, mem, REG_B)
                CDV -> divOp(operand, mem, REG_C)
                BST -> setReg(mem, REG_B, getCombo(operand, mem) % 8)
                BXC -> setReg(mem, REG_B, mem[REG_B].xor(mem[REG_C]))
                JNZ -> if (mem[REG_A] != 0L) {
                    ip = operand.toInt() - 2
                }

                BXL -> {
                    val regB = mem[REG_B]
                    val res = regB.xor(operand)
                    setReg(mem, REG_B, res)
                }

                OUT -> {
                    val combo = getCombo(operand, mem)
                    val out = combo % 8
                    dbg.print(out)
                    yield(out.toInt())
                }

                else -> throw IllegalArgumentException("Unknown op '$op'")
            }
            dbg.println()
            ip += 2
        }
    }

    private fun setReg(mem: LongArray, reg: Int, res: Long) {
        val regName = when (reg) {
            0 -> "A"
            1 -> "B"
            else -> "C"
        }
        dbg.print("Write to reg$regName: ${mem[reg]} -> $res")
        mem[reg] = res
    }

    private fun divOp(operand: Long, mem: LongArray, reg: Int) {
        val combo = getCombo(operand, mem).toInt()
        val regA = mem[REG_A]
        val res = regA / 2.pow(combo)
        setReg(mem, reg, res)
    }

    private fun getCombo(operand: Long, mem: LongArray): Long {
        return when (operand) {
            in 0..3 -> operand
            in 4..6 -> mem[operand.toInt() - 4]
            else -> throw IllegalArgumentException("Combo operand '$operand'")
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

        fun getVal(inp: String) = inp.substringAfter(':').trim()

        fun parseInput(txt: String): Pair<LongArray, IntArray> {
            val lines = txt.lineSequence()
                .filterNot { it.isEmpty() }
                .iterator()
            val regA = getVal(lines.next()).toLong()
            val regB = getVal(lines.next()).toLong()
            val regC = getVal(lines.next()).toLong()
            val prog = getVal(lines.next()).split(',').map { it.toInt() }.toIntArray()
            return Pair(longArrayOf(regA, regB, regC), prog)
        }
    }
}