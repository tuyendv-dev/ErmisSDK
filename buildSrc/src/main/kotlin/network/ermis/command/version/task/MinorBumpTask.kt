package network.ermis.command.version.task

import network.ermis.command.version.codechange.parseVersion
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class MinorBumpTask: DefaultTask() {

    @ExperimentalStdlibApi
    @TaskAction
    private fun command() {
        val configurationFile = File(CONFIGURATION_PATH)
        val newInfo = parseVersion(configurationFile, true)

        configurationFile.printWriter().use { printer ->
            newInfo.forEach(printer::println)
        }

        println("Minor version bumped.")
    }
}
