package network.ermis.command.unittest.task

import network.ermis.command.unittest.filter.selectedUnitTestCommand
import network.ermis.command.unittest.plugin.UnitTestsCommandExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class UnitTestsTask : DefaultTask() {

    @Input
    lateinit var config: UnitTestsCommandExtension

    @ExperimentalStdlibApi
    @TaskAction
    private fun command() {
        val modules = project.subprojects.map { project -> project.name }

        val command = modules.selectedUnitTestCommand(project)
        File(project.rootDir, config.outputPath)
            .also { it.parentFile.mkdirs() }
            .writeText(command)

        println("Command generated: $command")
        println("Command written in: ${config.outputPath}")
    }
}
