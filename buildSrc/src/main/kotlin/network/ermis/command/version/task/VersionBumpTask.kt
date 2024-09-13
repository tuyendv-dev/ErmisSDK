package network.ermis.command.version.task

import network.ermis.command.utils.hasBreakingChange
import network.ermis.command.version.codechange.parseVersion
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

private const val CHANGELOG_PATH = "CHANGELOG.md"

open class VersionBumpTask: DefaultTask() {

    @ExperimentalStdlibApi
    @TaskAction
    private fun command() {
        val dependencyFile = File(CHANGELOG_PATH)
        val configurationFile = File(CONFIGURATION_PATH)

        val hasBreakingChange = hasBreakingChange(dependencyFile)
        val newInfo = parseVersion(configurationFile, hasBreakingChange)

        configurationFile.printWriter().use { printer ->
            newInfo.forEach(printer::println)
        }

        println("Breaking change: $hasBreakingChange")
        println("Version bumped.")
    }
}
