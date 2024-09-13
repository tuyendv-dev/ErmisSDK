package network.ermis.command.release.model

import network.ermis.command.release.model.Project

data class Document(val projects: MutableList<Project>): MutableList<Project> by projects
