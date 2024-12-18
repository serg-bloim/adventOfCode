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

        fun solve(txt: String): Any {
            val (mem, prog) = parseInput(txt)

            val res = eval(prog, mem).joinToString(",")
            dbg.println()
            dbg.println()
            dbg.println("RegA: ${mem[0]}\nRegB: ${mem[1]}\nRegC: ${mem[2]}\n")
            dbg.println(res)
            return res
        }

    }

    fun eval(prog: IntArray, mem: IntArray) = sequence {
        var ip = 0
        while (true) {
            dbg.print("[" + mem.joinToString { it.toString().padStart(10) } + "]")
            dbg.print("  IP: ${ip.toString().padStart(3)}")
            if (ip < 0 || ip >= prog.size) {
                return@sequence
            }
            val op = prog[ip]
            val operand = prog[ip + 1]
            dbg.print(" OP: $op Operand = $operand")
            if (operand < 7) dbg.print(" Combo: ${getCombo(operand, mem)}")
            dbg.println()
            when (op) {
                ADV -> {
                    divOp(operand, mem, REG_A)
                }

                BXL -> {
                    val regB = mem[REG_B]
                    val res = regB.xor(operand)
                    mem[REG_B] = res
                }

                BST -> {
                    val res = getCombo(operand, mem) % 8
                    mem[REG_B] = res
                }

                JNZ -> {
                    if (mem[REG_A] != 0) {
                        ip = operand - 2
                    }
                }

                BXC -> {
                    val regB = mem[REG_B]
                    val regC = mem[REG_C]
                    val res = regB.xor(regC)
                    mem[REG_B] = res
                }

                OUT -> {
                    val combo = getCombo(operand, mem)
                    yield(combo % 8)
                }

                BDV -> {
                    divOp(operand, mem, REG_B)
                }

                CDV -> {
                    divOp(operand, mem, REG_C)
                }

                else -> throw IllegalArgumentException("Unknown op '$op'")
            }
            ip += 2
        }
    }

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(5555555, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Any {
            return 11111111
        }
    }

    private fun divOp(operand: Int, mem: IntArray, reg: Int) {
        val combo = getCombo(operand, mem)
        val regA = mem[REG_A]
        val res = regA / 2.pow(combo)
        mem[reg] = res
    }

    private fun getCombo(operand: Int, mem: IntArray): Int {
        return when (operand) {
            in 0..3 -> operand
            in 4..6 -> mem[operand - 4]
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
        fun parseInput(txt: String): Pair<IntArray, IntArray> {
            val lines = txt.lineSequence()
                .filterNot { it.isEmpty() }
                .iterator()
            val regA = getVal(lines.next()).toInt()
            val regB = getVal(lines.next()).toInt()
            val regC = getVal(lines.next()).toInt()
            val prog = getVal(lines.next()).split(',').map { it.toInt() }.toIntArray()
            return Pair(intArrayOf(regA, regB, regC), prog)
        }
    }
}