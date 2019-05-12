package pub.ronin

/*
 * Example
 * -name app
 * exec "./test.jar"
 * -options "-Xms1G -cp /foo/demo.jar"
 * -help
 * -version
 */
fun main(args: Array<String>) {
    val jiiKoUCli = JiiKoUCli()
    if (args.isEmpty()) {
        jiiKoUCli.help()
        return
    }
    try {
        when (args.first()) {
            "-help" -> jiiKoUCli.help()
            "-version" -> jiiKoUCli.version()
            "-x" -> jiiKoUCli.action(args)
            else -> jiiKoUCli.help()
        }
    } catch (e: Exception) {
        e.printStackTrace(System.err)
    }
}


fun Array<String>.findOption(option: String): String {
    val index = indexOf(option)
    return if (index != -1 && (index + 1) < size) {
        get(index + 1)
    } else {
        ""
    }
}

class JiiKoUCli {
    fun action(args: Array<String>) {
        val jiiKoU = JiiKoU()
        val targetFile = args.findOption("-x")
        if (targetFile.isEmpty()) {
            throw JiiKoUException("error arguments")
        }
        jiiKoU.target(targetFile)
        val name = args.findOption("-name")
        if (name.isNotEmpty()) {
            jiiKoU.rename(name)
        }
        val options = args.findOption("-options")
        if (options.isNotEmpty()) {
            jiiKoU.addOption(options)
        }
        jiiKoU.makeExecute()
        println("jiikou execute success")
    }

    fun version() {
        println("jiikou version: 0.0.1")
    }

    fun help() {

    }
}