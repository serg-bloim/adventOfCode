package advent24

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.dbg
import utils.repeatAsSequence
import utils.result
import java.util.LinkedList
import kotlin.test.assertEquals

class Day9 {
    @Nested
    inner class Task1 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            assertEquals(1928L, actual)
            result.println("Result: $actual")
            println("Result: $actual")
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            assertEquals(6401092019345, actual)
            println("Result: $actual")
        }

        fun solve(txt: String): Any {
            val format1 = parseInput(txt)
            val files = format1.chunked(2)
                .mapIndexed { index, pair -> Triple(index, pair.first(), pair.getOrNull(1) ?: 0) }
                .toList()
            val blocks = compactIdBlocks(files)
            return blocks.mapIndexed { i, id -> (i * id).toLong() }.sum()
        }
    }

    private fun compactIdBlocks(files: List<Triple<Int, Int, Int>>) = sequence {
        val fwdItr = files.iterator()
        val bwdItr = files.asReversed().asSequence()
            .flatMap { (id, fileSize, freeSpace) -> generateSequence { id }.take(fileSize) }
            .iterator()
        var lastBwdFileId = Int.MAX_VALUE
        while (fwdItr.hasNext()) {
            val (id, fileSize, freeSpace) = fwdItr.next()
            if (id < lastBwdFileId) {
                yieldAll(generateSequence { id }.take(fileSize))
            } else {
                if (id == lastBwdFileId) {
                    yieldAll(bwdItr.asSequence().takeWhile { it == id })
                }
                return@sequence
            }
            if (freeSpace > 0) {
                val filesFromEnd = bwdItr.asSequence()
                    .take(freeSpace) // take maximum blocks as we have free space
                for (bwdFileId in filesFromEnd) {
                    // If the bwd file has same or lower ID than fwd file, it means we finished
                    if (bwdFileId == id) return@sequence
                    else yield(bwdFileId)
                    lastBwdFileId = bwdFileId
                }
            }
        }
    }

    data class FileDescriptor(
        val id: Int,
        val fileSize: Int,
        var freeSpace: Int,
        var moved: Boolean = false,
        val additionalFileRefs: LinkedList<FileDescriptor> = LinkedList()
    )

    @Nested
    inner class Task2 {
        @Test
        fun testSmall() {
            val actual = solve(load_test())
            println("Result: $actual")
            result.println("Result: $actual")
            assertEquals(2858L, actual)
        }

        @Test
        fun testReal() {
            val actual = solve(load_prod())
            result.println("Result: $actual")
            println("Result: $actual")
            assertEquals(55555555, actual)
        }

        fun solve(txt: String): Any {
            val format1 = parseInput(txt)
            val files = format1.chunked(2)
                .mapIndexed { index, pair -> FileDescriptor(index, pair.first(), pair.getOrNull(1) ?: 0) }
                .toList()
            for (fd in files.asReversed()) {
                for (insertAfter in files) {
                    if (insertAfter.id >= fd.id) break
                    if (insertAfter.freeSpace >= fd.fileSize) {
                        insertAfter.additionalFileRefs.add(fd)
                        insertAfter.freeSpace -= fd.fileSize
                        fd.moved = true
                        break
                    }
                }
            }

            val blocks = files.asSequence().flatMap {
                sequence {
                    val id = if (it.moved) 0 else it.id
                    yieldAll(id.repeatAsSequence(it.fileSize))
                    for (af in it.additionalFileRefs) {
                        yieldAll(af.id.repeatAsSequence(af.fileSize))
                    }
                    yieldAll(0.repeatAsSequence(it.freeSpace))
                }
            }
            return blocks.mapIndexed { i, id -> (i * id).toLong() }.sum()
        }
    }

    private fun buildReverseFileLookup(files: List<Triple<Int, Int, Int>>) = buildMap<Int, MutableList<Int>> {
        for ((id, fileSize, _) in files.asReversed()) {
            computeIfAbsent(fileSize) { mutableListOf() }.add(id)
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

        fun parseInput(txt: String): Sequence<Int> {
            val data = txt.asSequence().map { it.digitToInt() }
            return data
        }
    }
}