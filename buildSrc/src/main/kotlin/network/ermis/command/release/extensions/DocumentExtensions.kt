package network.ermis.command.release.extensions

import network.ermis.command.release.model.Document
import network.ermis.command.release.model.Project
import network.ermis.command.release.model.Section

fun List<List<Section>>.toDocument(): Document =
    this.map { sections ->
        Project(sections.toMutableList())
    }.let { projects ->
        Document(projects.toMutableList())
    }
