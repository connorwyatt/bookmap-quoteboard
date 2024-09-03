package com.github.connorwyatt.quoteboard.swing

import javax.swing.JComponent
import javax.swing.event.AncestorEvent

fun JComponent.addAncestorListeners(
    ancestorAddedListener: (AncestorEvent) -> Unit,
    ancestorRemovedListener: (AncestorEvent) -> Unit,
) {
    addAncestorListener(
        object : AncestorAdapter() {
            override fun ancestorAdded(event: AncestorEvent) {
                ancestorAddedListener(event)
            }

            override fun ancestorRemoved(event: AncestorEvent) {
                ancestorRemovedListener(event)
            }
        }
    )
}
