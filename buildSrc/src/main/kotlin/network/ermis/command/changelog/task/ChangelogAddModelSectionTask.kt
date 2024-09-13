package network.ermis.command.changelog.task

import network.ermis.command.changelog.plugin.ChangelogAddModelSectionCommandExtension
import network.ermis.command.changelog.plugin.ChangelogReleaseSectionCommandExtension
import network.ermis.command.release.markdown.addModelSection
import network.ermis.command.release.output.InMemoryPrinter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ChangelogAddModelSectionTask : DefaultTask() {

    @Input
    lateinit var config: ChangelogAddModelSectionCommandExtension

    @TaskAction
    private fun command() {
        val changeLogFile = File(config.changelogPath)
        val inMemoryPrinter = InMemoryPrinter()

        println("changelogPath: $changeLogFile")

        addModelSection(
            inMemoryPrinter,
            File(config.changelogModel),
            changeLogFile.readLines()
        )

        changeLogFile.printWriter().use { printer ->
            inMemoryPrinter.lines().forEach(printer::println)
        }

        println("model section added in changelog")
    }
}
