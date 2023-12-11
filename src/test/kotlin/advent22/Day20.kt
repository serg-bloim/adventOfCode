package advent22

import advent23.toCircularLinkedList
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day20 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(3, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Int {
            val llist = txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { it.toInt() }
                .toCircularLinkedList()
            val initOrder = llist.toNodeList()
//        println(llist)
            for (node in initOrder) {
                node.move(node.value)
//            println(llist)
            }
            val zero = llist.nodesAsSequence(false).first { it.value == 0 }
            val res = generateSequence(zero) { it.fwd(1000) }.drop(1).map { it.value }.take(3).sum()

            return res
        }
    }

    internal class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            assertEquals(1623178306, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            println("Result: $actual")
        }

        fun solve(txt: String): Long {
            val a = (Int.MAX_VALUE.toLong() * 2)
            val encKey = 811589153
            val llist = txt.lineSequence()
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { it.toLong() * encKey }
                .toCircularLinkedList()
            val initOrder = llist.toNodeList()
            repeat(10) {
                for (node in initOrder) {
                    node.move(node.value)
                }
            }
            val zero = llist.nodesAsSequence(false).first { it.value == 0L }
            val res = generateSequence(zero) { it.fwd(1000) }.drop(1).map { it.value }.take(3).sum()

            return res
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
}

