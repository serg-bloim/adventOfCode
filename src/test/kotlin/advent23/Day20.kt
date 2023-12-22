package advent23

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Day20 {
    internal class Task1 {
        @Test
        fun testSmall() {
            val actual = Solution.solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(32000000, actual)
        }

        @Test
        fun testSmall2() {
            val actual = Solution.solve(load_test(2))
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(11687500, actual)
        }

        @Test
        fun testReal() {
            val actual = Solution.solve(load_prod())
            println("Result: $actual")
            assertEquals(867118762, actual)
        }

    }

    internal class Task2 {
        @Test
        fun testReal() {
            val actual = Solution.solve2(load_prod())
            println("Result: $actual")
            assertEquals(12345, actual)
        }

    }

    object Solution {
        fun solve(txt: String): Any {
            val rx = BlackHole("rx")
            val modules = parseModules(txt, rx)
            val bcst = modules.first { it is Broadcast }
            val btn = Button(bcst)
            val processing = ArrayDeque<Module>()
            val rt = Runtime()
            repeat(1000) {
                processing.addLast(btn)
                while (processing.isNotEmpty()) {
                    val next = processing.removeFirst()
                    val dsts = next.process(rt)
                    processing.addAll(dsts)
                }
            }
            return rt.lo.toLong() * rt.hi
        }

        private fun parseModules(txt: String, rx: BlackHole): List<Module> {
            val modules = txt.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { parseModuleDescr(it) }
                .toList().let { descrs ->
                    val modules = descrs.map {
                        when (it.type) {
                            "" -> Broadcast(it.name)
                            "&" -> Conjunction(it.name)
                            "%" -> FlipFlop(it.name)
                            else -> throw IllegalArgumentException()
                        }
                    }
                    val lookup = modules.associateBy { it.name }
                    for (d in descrs) {
                        val src = lookup[d.name]!!
                        src.dsts = d.dsts.map { lookup[it] ?: rx }
                        for (dst in src.dsts) {
                            // Init Conjunctions
                            if (dst is Conjunction)
                                dst.receive(false, src)
                        }
                    }
                    modules
                }
            return modules
        }

        val moduleRe = Regex("""(%|&)?(\w+) -> ([\w, ]+)""")
        private fun parseModuleDescr(str: String): ModuleDescr {
            val (type, name, dsts) = moduleRe.matchEntire(str)!!.destructured
            return ModuleDescr(name, type, dsts.split(", "))
        }

        fun solve2(txt: String): Any {
            val rx = BlackHole("rx")
            val modules = parseModules(txt, rx)
            val bcst = modules.first { it is Broadcast }
            val btn = Button(bcst)
            val processing = ArrayDeque<Module>()
            val rt = Runtime()
            var i = 0L
            while (true) {
                processing.addLast(btn)
                i++
                while (processing.isNotEmpty()) {
                    val next = processing.removeFirst()
                    val dsts = next.process(rt)
                    if (rx.received > 0 && rx.pulseLo == true) return i
                    processing.addAll(dsts)
                }
            }
        }
    }


    data class ModuleDescr(val name: String, val type: String, val dsts: List<String>)
    class Runtime(var lo: Int = 0, var hi: Int = 0) {
        fun register(pulse: Boolean, n: Int = 1) {
            if (pulse) hi += n
            else lo += n
        }
    }

    abstract class Module(val name: String) {
        var input = ArrayDeque<Pair<Boolean, Module>>()
        lateinit var dsts: List<Module>
        open fun receive(pulse: Boolean, from: Module) {
            input.addLast(Pair(pulse, from))
        }

        open fun process(rt: Runtime): List<Module> {
            val result = calcOutPulse()
            val cnt = dsts.onEach { it.receive(result, this) }.count()
//            println("Module $name sends pulse '${if (result) "HI" else "LO"}' $cnt times to [${dsts.joinToString(", ") { it.name }}]")
            rt.register(result, cnt)
            return dsts
        }

        open fun calcOutPulse() = input.removeFirst().first
    }

    class Button(bcst: Module) : Module("button") {
        init {
            dsts = listOf(bcst)
        }

        override fun calcOutPulse() = false
    }

    class Broadcast(name: String) : Module(name)

    class Conjunction(name: String) : Module(name) {
        val inputs = mutableMapOf<Module, Boolean>()
        override fun receive(pulse: Boolean, from: Module) {
            inputs[from] = pulse
        }

        override fun calcOutPulse(): Boolean {
            return inputs.isEmpty() || inputs.values.any { !it }
        }
    }

    class FlipFlop(name: String) : Module(name) {
        var state = false
        override fun calcOutPulse(): Boolean {
            return state
        }

        override fun process(rt: Runtime): List<Module> {
            return if (super.calcOutPulse()) emptyList()
            else {
                state = !state
                super.process(rt)
            }
        }
    }

    class BlackHole(name: String) : Module("$name") {
        var received = 0
        var pulseHi = false
        var pulseLo = false

        override fun receive(pulse: Boolean, from: Module) {
            if (pulse) pulseHi = true else pulseLo = true
            received++
        }

        override fun process(rt: Runtime) = emptyList<Module>()
    }

    companion object {
        val res_prefix = this::class.java.enclosingClass.simpleName.lowercase()
        fun load_test(prefix: Any = ""): String {
            return Resources().loadString("${res_prefix}_test$prefix.txt")
        }

        fun load_prod(): String {
            return Resources().loadString("${res_prefix}_prod.txt")
        }
    }

    class Tests {
        @Test
        fun test_FlipFlop() {
            val rt = Runtime()
            val flipFlop = FlipFlop("test")
            val rcvr = Broadcast("rcvr")
            flipFlop.dsts = listOf(rcvr)
            flipFlop.receive(true, rcvr)
            assertEquals(0, flipFlop.process(rt).size)

            flipFlop.receive(false, rcvr)
            assertEquals(1, flipFlop.process(rt).size)
            assertTrue(rcvr.calcOutPulse())

            flipFlop.receive(false, rcvr)
            assertEquals(1, flipFlop.process(rt).size)
            assertFalse(rcvr.calcOutPulse())

            flipFlop.receive(false, rcvr)
            flipFlop.receive(true, rcvr)
            assertEquals(1, flipFlop.process(rt).size)
            assertTrue(rcvr.calcOutPulse())
        }

        @Test
        fun test_Conjunction() {
            val rcvr = Broadcast("rcvr")
            val mod = Conjunction("test")
            val inp1 = Broadcast("inp1")
            val inp2 = Broadcast("inp2")
            val inp3 = Broadcast("inp3")
            mod.dsts = listOf(rcvr)
            val rt = Runtime()
            assertEquals(1, mod.process(rt).size)
            assertTrue(rcvr.calcOutPulse())
            val iters = listOf(
                Triple(inp1, true, false),
                Triple(inp2, true, false),
                Triple(inp3, true, false),
                Triple(inp3, false, true),
                Triple(inp2, false, true),
                Triple(inp1, false, true),
                Triple(inp1, true, true),
            )

            for ((inp, pulse, expected) in iters) {
                mod.receive(pulse, inp)
                assertEquals(1, mod.process(rt).size)
                assertEquals(expected, rcvr.calcOutPulse())
            }
        }
    }
}

