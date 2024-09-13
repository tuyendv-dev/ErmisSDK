package network.ermis.command.changelog.task

import network.ermis.command.changelog.plugin.ChangelogReleaseSectionCommandExtension
import network.ermis.command.release.markdown.parseReleaseSectionInChangelog
import network.ermis.command.release.output.InMemoryPrinter
import network.ermis.command.utils.filterOldReleases
import network.ermis.command.utils.getCurrentVersion
import network.ermis.command.utils.parseChangelogFile
import network.ermis.command.version.task.CONFIGURATION_PATH
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task called to update an specified changelog. It takes most recent section and attributed a version to it.
 * Use this after the release is done.
 */
open class ChangelogReleaseSectionTask : DefaultTask() {

    @Input
    lateinit var config: ChangelogReleaseSectionCommandExtension

    @TaskAction
    private fun command() {
        val changeLogFile = File(config.changelogPath)
        val configurationFile = File(CONFIGURATION_PATH)

        println("changelogPath: $changeLogFile")

        val releaseDocument = parseChangelogFile(changeLogFile)
        val oldReleases = filterOldReleases(changeLogFile)
        val inMemoryPrinter = InMemoryPrinter()

        parseReleaseSectionInChangelog(
            inMemoryPrinter,
            releaseDocument,
            oldReleases,
            getCurrentVersion(configurationFile)
        )

        changeLogFile.printWriter().use { printer ->
            inMemoryPrinter.lines().forEach(printer::println)
        }

        println("changelog updated")
    }
}
