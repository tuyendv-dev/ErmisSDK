package network.ermis.command.version.plugin

import network.ermis.command.utils.registerExt
import network.ermis.command.version.task.MinorBumpTask
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val COMMAND_NAME = "minor-bump"

class MinorBumpPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.registerExt<MinorBumpTask>(COMMAND_NAME) {}
    }
}
