package network.ermis.command.changelog.plugin

import network.ermis.command.changelog.task.ChangelogReleaseSectionTask
import network.ermis.command.utils.registerExt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

private const val CONFIG_CLOJURE_NAME = "changelogReleaseSection"
private const val COMMAND_NAME = "changelog-release-section"

class ChangelogReleaseSectionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension: ChangelogReleaseSectionCommandExtension =
            project.extensions.create(CONFIG_CLOJURE_NAME, ChangelogReleaseSectionCommandExtension::class.java)

        project.tasks.registerExt<ChangelogReleaseSectionTask>(COMMAND_NAME) {
            this.config = extension
        }
    }
}
