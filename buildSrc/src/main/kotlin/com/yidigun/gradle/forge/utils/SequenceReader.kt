package com.yidigun.gradle.forge.utils

import java.io.Reader
import java.io.StringReader

class SequenceReader(private vararg val readers: Reader) : Reader() {

    companion object {
        fun wrap(origin: Reader, header: String, footer: String): SequenceReader {
            return SequenceReader(
                StringReader(header),
                origin,
                StringReader(footer)
            )
        }
        fun wrap(origin: Reader, header: Reader, footer: Reader): SequenceReader {
            return SequenceReader(header, origin, footer)
        }
    }

    private var currentIndex = 0

    override fun read(cbuf: CharArray?, off: Int, len: Int): Int {
        if (currentIndex >= readers.size) return -1

        val charsRead = readers[currentIndex].read(cbuf, off, len)
        if (charsRead < 0) {
            currentIndex++
            return read(cbuf, off, len)
        }
        return charsRead
    }

    override fun close() {
        readers.forEach { it.close() }
    }
}
