package network.ermis.command.changelog.plugin

import network.ermis.command.changelog.task.ChangelogAddModelSectionTask
import network.ermis.command.utils.registerExt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

private const val CONFIG_CLOJURE_NAME = "changelogAddModelSection"
private const val COMMAND_NAME = "changelog-add-model-section"

class ChangelogAddModelSectionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension: ChangelogAddModelSectionCommandExtension =
            project.extensions.create(CONFIG_CLOJURE_NAME, ChangelogAddModelSectionCommandExtension::class.java)

        project.tasks.registerExt<ChangelogAddModelSectionTask>(COMMAND_NAME) {
            this.config = extension
        }
    }
}
