package git.shin.animevsub.utils

import java.util.zip.Inflater

fun inflateRaw(data: ByteArray): ByteArray {
    val inflater = Inflater(true) // true = raw (no zlib header)
    inflater.setInput(data)

    val buffer = ByteArray(1024)
    val output = mutableListOf<Byte>()

    while (!inflater.finished()) {
        val count = inflater.inflate(buffer)
        for (i in 0 until count) {
            output.add(buffer[i])
        }
    }

    inflater.end()
    return output.toByteArray()
}
