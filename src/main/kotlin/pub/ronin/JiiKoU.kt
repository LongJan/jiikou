package pub.ronin

import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class JiiKoU {

    private var target = "";
    private val options = mutableListOf<String>()
    private var executedName = ""

    fun target(targetFile: String): JiiKoU {
        target = targetFile
        return this
    }

    fun addOption(option: String): JiiKoU {
        options.add(option)
        return this
    }

    fun setOptions(vararg newOptions: String): JiiKoU {
        options.addAll(newOptions.toList())
        return this
    }

    fun rename(newName: String): JiiKoU {
        executedName = newName
        return this
    }

    fun makeExecute() {
        validate()
        val optionsStr = if (options.isEmpty()) {
            ""
        } else {
            options.joinToString(" ")
        }
        val targetPath = Paths.get(target)
        val tempFilePath = Paths.get("$target.temp")
        Files.deleteIfExists(tempFilePath)
        Files.move(targetPath, tempFilePath)
        FileOutputStream(targetPath.toFile()).buffered().use { bos ->
            bos.write(
                """#!/usr/bin/env sh
exec java $optionsStr -jar "$0" "$@"

            """.trimMargin().toByteArray(Charset.forName("ASCII"))
            )
            FileInputStream(tempFilePath.toFile()).buffered().use { bis ->
                val byteBuffer = ByteArray(1024)
                while (true) {
                    val readSize = bis.read(byteBuffer)
                    if (readSize == -1) {
                        break
                    }
                    bos.write(byteBuffer, 0, readSize)
                }
            }
            Files.delete(tempFilePath)
        }
        targetPath.toFile().setExecutable(true, false)
        if (executedName.isNotEmpty()) {
            val executedFilePath = targetPath.resolveSibling(executedName)
            Files.copy(targetPath, executedFilePath)
            executedFilePath.toFile().setExecutable(true, false)
        }
    }

    private fun validate() {
        if (target.isEmpty()) {
            throw JiiKoUException("no target!")
        }
    }

}



