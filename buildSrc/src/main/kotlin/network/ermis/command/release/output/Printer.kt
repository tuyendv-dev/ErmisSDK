package network.ermis.command.release.output

import network.ermis.command.release.model.Document
import network.ermis.command.release.model.Section

interface Printer {
    fun printline(text: String)
}

fun Document.print(printer: Printer) {
    flatten().printline(printer)
}

fun List<Section>.printline(printer: Printer) {
    forEach { section ->
        section.forEach(printer::printline)

        if (section.size > 1) {
            printer.printline("")
        }
    }
}
