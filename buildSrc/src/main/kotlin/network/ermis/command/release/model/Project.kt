package network.ermis.command.release.model

data class Project(val sections: MutableList<Section>) : MutableList<Section> by sections
