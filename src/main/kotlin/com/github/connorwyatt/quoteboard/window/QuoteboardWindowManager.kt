package com.github.connorwyatt.quoteboard.window

import com.github.connorwyatt.quoteboard.utilities.PriceFormatter
import io.reactivex.rxjava3.disposables.Disposable
import java.awt.Dimension
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

class QuoteboardWindowManager(
    private val dataObservableFactory: QuoteboardDataObservableFactory,
    private val priceFormatter: PriceFormatter,
) {
    private var frame: JFrame? = null
    private var dataSubscription: Disposable? = null

    fun open() {
        if (frame == null) {
            this.frame = createWindow()
        }

        this.frame?.let {
            it.isVisible = true
            it.toFront()
        }
    }

    private fun createWindow() =
        JFrame("Quoteboard").apply {
            minimumSize = Dimension(400, 200)
            defaultCloseOperation = JFrame.HIDE_ON_CLOSE

            val quoteboardTable = QuoteboardTable(priceFormatter)
            val scrollPane = JScrollPane(quoteboardTable)
            add(scrollPane)

            // TODO: Ensure data subscription gets disposed properly
            dataSubscription =
                dataObservableFactory.dataObservable
                    .throttleLast(fpsToMilliseconds(30), TimeUnit.MILLISECONDS)
                    .subscribe { SwingUtilities.invokeLater { quoteboardTable.update(it) } }
        }

    private fun fpsToMilliseconds(frames: Int): Long = TimeUnit.SECONDS.toMillis(1) / frames

    fun dispose() {
        dataSubscription?.dispose()
        frame?.dispose()
    }
}
