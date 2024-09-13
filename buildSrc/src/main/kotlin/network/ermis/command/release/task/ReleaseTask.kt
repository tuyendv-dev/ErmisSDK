package network.ermis.command.release.task

import network.ermis.command.release.output.FilePrinter
import network.ermis.command.release.output.print
import network.ermis.command.release.plugin.ReleaseCommandExtension
import network.ermis.command.utils.parseChangelogFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ReleaseTask : DefaultTask() {

    @Input
    lateinit var config: ReleaseCommandExtension

    @TaskAction
    private fun command() {
        val changeLogFile = File(config.changelogPath)
        println("changelogPath: $changeLogFile")

        val releaseDocument = parseChangelogFile(changeLogFile)
        val outputFile = File(project.rootDir, "build/tmp/CHANGELOG_PARSED.md").also { it.parentFile.mkdirs() }
        FilePrinter(outputFile).use { printer -> releaseDocument.print(printer) }

        println("CHANGELOG_PARSED.md generated")
    }
}
