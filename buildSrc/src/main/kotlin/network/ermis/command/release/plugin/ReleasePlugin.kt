package network.ermis.command.release.plugin

import network.ermis.command.release.task.ReleaseTask
import network.ermis.command.utils.registerExt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

private const val CONFIG_CLOJURE_NAME = "releaseScript"
private const val COMMAND_NAME = "generate-release"

class ReleasePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension: ReleaseCommandExtension =
            project.extensions.create(CONFIG_CLOJURE_NAME, ReleaseCommandExtension::class.java)

        project.tasks.registerExt<ReleaseTask>(COMMAND_NAME) {
            this.config = extension
        }
    }
}
