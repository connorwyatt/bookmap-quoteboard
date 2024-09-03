package com.github.connorwyatt.quoteboard.swing

import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

abstract class AncestorAdapter : AncestorListener {
    override fun ancestorAdded(event: AncestorEvent) {}

    override fun ancestorRemoved(event: AncestorEvent) {}

    override fun ancestorMoved(event: AncestorEvent) {}
}
