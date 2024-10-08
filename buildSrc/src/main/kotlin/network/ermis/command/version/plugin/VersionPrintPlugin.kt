package network.ermis.command.version.plugin

import network.ermis.command.utils.registerExt
import network.ermis.command.version.task.VersionPrintTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

private const val CONFIG_CLOJURE_NAME = "versionPrint"
private const val COMMAND_NAME = "version-print"

class VersionPrintPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension: VersionPrintCommandExtension =
            target.extensions.create(CONFIG_CLOJURE_NAME, VersionPrintCommandExtension::class.java)

        target.tasks.registerExt<VersionPrintTask>(COMMAND_NAME) {
            this.config = extension
        }
    }
}
