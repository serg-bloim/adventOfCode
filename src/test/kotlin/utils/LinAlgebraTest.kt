package utils

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LinAlgebraTest {
    @Test
    fun test_simple_1() {
        /**
         * x = 1
         * y = 2
         * z = 3
         * 1x  1y  3z = 12
         * 3x  1y  1z = 8
         * 1x  3y  1z = 10
         *
         */
        val coefs = arrayOf(
            arrayOf(1, 1, 3),
            arrayOf(3, 1, 1),
            arrayOf(1, 3, 1)
        )
        val consts = arrayOf(12, 8, 10)
        solveLinearEqSystem(coefs, consts)
        println(toString(coefs, consts))
    }

    @Test
    fun test_simple_2() {
        val txt = """
            1 1 3 |12
            3 1 1 |8
            1 3 1 |10
        """.trimIndent()
        val (coefs, consts) = parseLinEqSystem(txt)
        solveLinearEqSystem(coefs, consts)
        println(toString(coefs, consts))
        val expected = """
            1 0 0 | 1
            0 1 0 | 2
            0 0 1 | 3
        """.trimIndent()
        assertEqual(expected, toString(coefs, consts))
    }

    @Test
    fun test_simple_3() {
        val txt = """
            0 1 1 1 1 1 0 1|56
            1 1 1 1 0 1 0 0|47
            1 0 1 1 1 1 0 0|33
            0 0 0 0 0 1 1 1|28
            1 0 0 1 1 1 0 1|34
            0 0 1 0 0 1 1 0|27
            1 1 1 1 1 0 0 1|55
            0 0 0 1 1 0 1 0|4
            1 0 1 1 1 0 0 0|24
        """.trimIndent()
        val (coefs, consts) = parseLinEqSystem(txt)
        solveLinearEqSystem(coefs, consts)
        println(toString(coefs, consts))
        val expected = """
            1 0 0 0 0 0 0 0 | 8
            0 1 0 0 0 0 0 0 | 15
            0 0 1 0 0 0 0 0 | 15
            0 0 0 1 0 0 0 0 | 0
            0 0 0 0 1 0 0 0 | 1
            0 0 0 0 0 1 0 0 | 9
            0 0 0 0 0 0 1 0 | 3
            0 0 0 0 0 0 0 1 | 16
            0 0 0 0 0 0 0 0 | 0
        """.trimIndent()
        assertEqual(expected, toString(coefs, consts))
    }

    @Test
    fun test_simple_4() {
        val txt = """
            1 0 1 1 0 | 7
            0 0 0 1 1 | 5
            1 1 0 1 1 | 12
            1 1 0 0 1 | 7
            1 0 1 0 1 | 2
        """.trimIndent()
        val (coefs, consts) = parseLinEqSystem(txt)
        solveLinearEqSystem(coefs, consts)
        println(toString(coefs, consts))
        val expected = """
             1  0  1  0  0 | 2
             0  1 -1  0  0 | 5
             0  0  0  1  0 | 5
             0  0  0  0  1 | 0
             0  0  0  0  0 | 0
        """.trimIndent()
        assertEqual(expected, toString(coefs, consts))
    }

    @Test
    fun test_simple_5() {
        val txt = """
            1 1 0 0 0 0 | 13
            0 1 0 1 1 0 | 23
            1 0 1 1 0 1 | 40
            0 0 1 0 1 1 | 24
            1 0 1 0 0 0 | 16
        """.trimIndent()
        val (coefs, consts) = parseLinEqSystem(txt)
        solveLinearEqSystem(coefs, consts)
        println(toString(coefs, consts))
        val expected = """
                 1  0  0  0 -1  0 | 3
                 0  1  0  0  1  0 | 10
                 0  0  1  0  1  0 | 13
                 0  0  0  1  0  0 | 13
                 0  0  0  0  0  1 | 11
        """.trimIndent()
        assertEqual(expected, toString(coefs, consts))
    }

    @Test
    fun testSolveLinEqSys() {
        /**
         * x = 1
         * y = 2
         * z = 3
         * 1x  1y  3z = 12
         * 3x  1y  1z = 8
         * 1x  3y  1z = 10
         *
         */
        val coefs = arrayOf(
            arrayOf(1, 1, 3),
            arrayOf(3, 1, 1),
            arrayOf(1, 3, 1)
        )
        val consts = arrayOf(12, 8, 10)
        solveLinearEqSystem(coefs, consts)
        println(coefs)
        println(consts)
    }
    @Test
    fun test_fractionCompareTo(){
        assertTrue { Fraction(5, 2) > 2 }
        assertTrue { Fraction(5, 2) < 3 }
        assertTrue { Fraction(-5, 2) < -1 }
    }
    fun assertEqual(expectedLinEqSys: String, actualLinEqSys: String) {
        fun transform(txt: String): String {
            val (coefs, consts) = parseLinEqSystem(txt)
            return "\n" + toString(coefs, consts) + "\n"
        }
        assertEquals(transform(expectedLinEqSys), transform(actualLinEqSys))
    }

}