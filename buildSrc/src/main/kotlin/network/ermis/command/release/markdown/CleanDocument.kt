package network.ermis.command.release.markdown

import network.ermis.command.release.extensions.toDocument
import network.ermis.command.utils.isStartOfProject
import network.ermis.command.release.model.Document
import network.ermis.command.release.model.Project
import network.ermis.command.release.model.Section

internal fun Document.clean(): Document =
    removeEmptyProjects()
        .map { project -> project.removeEmptySections() }
        .toDocument()

internal fun Document.removeEmptyProjects(): List<Project> =
    filter { project ->
        project.flatten().any { line ->
            line.startsWith("-")
        }
    }

internal fun List<Section>.removeEmptySections(): List<Section> =
    filter { section ->
        section.any { line ->
            line.startsWith("-") || isStartOfProject(line)
        }
    }
