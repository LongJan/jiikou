package pub.ronin

import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class JiiKoUTest {

    private val resourcesPath: Path

    init {
        val rootPath = javaClass.getResource("/").path
        resourcesPath = if (rootPath.endsWith("build/classes/kotlin/test/")) {
            Paths.get(rootPath).parent.parent.resolveSibling("resources/test")
        } else {
            Paths.get(rootPath).resolveSibling("resources")
        }
    }

    @Before
    fun setup() {
        Files.deleteIfExists(resourcesPath.resolve("test/test.jar"))
        Files.copy(resourcesPath.resolve("test/backup/test.jar"), resourcesPath.resolve("test/test.jar"))
    }

    @Test
    fun testMakeExecute() {
        JiiKoU().target(resourcesPath.resolve("test/test.jar").toString()).makeExecute()
        assertTrue(
            ProcessBuilder("sh", resourcesPath.resolve("test/test.jar").toString())
                .start().inputStream.read() != -1
        )
    }

    @Test
    fun testRenameMakeExecute() {
        JiiKoU().target(resourcesPath.resolve("test/test.jar").toString()).rename("test").makeExecute()
        assertTrue(
            ProcessBuilder("sh", resourcesPath.resolve("test/test").toString())
                .start().inputStream.read() != -1
        )
    }

    @After
    fun teardown() {
        resourcesPath.resolve("test").toFile().listFiles().filter { it.isFile }.forEach { it.delete() }
    }

}