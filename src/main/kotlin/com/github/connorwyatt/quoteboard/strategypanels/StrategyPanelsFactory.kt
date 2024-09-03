package com.github.connorwyatt.quoteboard.strategypanels

import com.github.connorwyatt.quoteboard.StrategyState
import com.github.connorwyatt.quoteboard.extensions.intersperse
import com.github.connorwyatt.quoteboard.models.QuoteboardColumn
import com.github.connorwyatt.quoteboard.models.SubscribedInstrument
import com.github.connorwyatt.quoteboard.swing.addAncestorListeners
import com.github.connorwyatt.quoteboard.window.QuoteboardWindowManager
import com.github.connorwyatt.quoteboard.withPrevious
import io.reactivex.rxjava3.disposables.Disposable
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import javax.swing.SwingUtilities
import velox.gui.StrategyPanel

class StrategyPanelsFactory(
    private val strategyState: StrategyState,
    private val windowManager: QuoteboardWindowManager,
) {
    fun createStrategyPanels(): Array<StrategyPanel> {
        return if (strategyState.isStrategyEnabled) {
            createEnabledStrategyPanels()
        } else {
            createDisabledStrategyPanels()
        }
    }

    private fun createEnabledStrategyPanels() =
        arrayOf(createMainPanel(), createColumnsPanel(), createInstrumentsPanel())

    private fun createMainPanel(): StrategyPanel =
        StrategyPanel("Quoteboard").apply {
            JButton("Launch quoteboard window")
                .apply { addActionListener { SwingUtilities.invokeLater { windowManager.open() } } }
                .also(::add)
        }

    private fun createColumnsPanel(): StrategyPanel =
        StrategyPanel("Columns").apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            var quoteboardColumnsSubscription: Disposable? = null
            addAncestorListeners(
                {
                    quoteboardColumnsSubscription =
                        strategyState.quoteboardColumnsObservable.withPrevious().subscribe {
                            (quoteboardColumns, previousQuoteboardColumns) ->
                            println("subscription received onNext")
                            val hasChanged =
                                previousQuoteboardColumns
                                    ?.let { quoteboardColumns.zip(it) }
                                    ?.any { it.first != it.second } ?: true

                            if (hasChanged) {
                                SwingUtilities.invokeLater {
                                    removeAll()

                                    createColumnRows(quoteboardColumns)
                                        .intersperse { Box.createVerticalStrut(10) }
                                        .forEach(::add)

                                    validate()
                                }
                            }
                        }
                },
                {
                    quoteboardColumnsSubscription?.dispose()
                    quoteboardColumnsSubscription = null
                },
            )
        }

    private fun createColumnRows(quoteboardColumns: List<QuoteboardColumn>): List<Box?> =
        quoteboardColumns.map { quoteboardColumn ->
            Box.createHorizontalBox().apply {
                JCheckBox(quoteboardColumn.type.displayName, quoteboardColumn.isEnabled)
                    .apply {
                        addChangeListener {
                            strategyState.setQuoteboardColumnIsEnabled(
                                quoteboardColumn.type,
                                isSelected,
                            )
                        }
                    }
                    .also(::add)

                add(Box.createHorizontalGlue())
            }
        }

    private fun createInstrumentsPanel(): StrategyPanel =
        StrategyPanel("Instruments").apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            var subscribedInstrumentsSubscription: Disposable? = null
            addAncestorListeners(
                {
                    var previousSubscribedInstruments: List<SubscribedInstrument>? = null
                    subscribedInstrumentsSubscription =
                        strategyState.subscribedInstrumentsObservable.subscribe {
                            subscribedInstruments ->
                            if (
                                subscribedInstruments.count() !=
                                    previousSubscribedInstruments?.count()
                            ) {
                                SwingUtilities.invokeLater {
                                    removeAll()

                                    createInstrumentRows(subscribedInstruments)
                                        .intersperse { Box.createVerticalStrut(10) }
                                        .forEach(::add)

                                    validate()
                                }
                            }
                            previousSubscribedInstruments = subscribedInstruments
                        }
                },
                {
                    subscribedInstrumentsSubscription?.dispose()
                    subscribedInstrumentsSubscription = null
                },
            )
        }

    private fun createInstrumentRows(
        subscribedInstruments: List<SubscribedInstrument>
    ): List<Box?> =
        subscribedInstruments.map { subscribedInstrument ->
            Box.createHorizontalBox().apply {
                JCheckBox(subscribedInstrument.alias, subscribedInstrument.isEnabled)
                    .apply {
                        addChangeListener {
                            strategyState.setInstrumentIsEnabled(
                                subscribedInstrument.alias,
                                isSelected,
                            )
                        }
                    }
                    .also(::add)

                add(Box.createHorizontalGlue())

                add(JLabel("Order: "))

                JSpinner(SpinnerNumberModel(subscribedInstrument.order, 0, 1000, 1))
                    .apply {
                        val width = 80
                        size = Dimension(width, size.height)
                        maximumSize = Dimension(width, maximumSize.height)
                        minimumSize = Dimension(width, minimumSize.height)
                        preferredSize = Dimension(width, preferredSize.height)

                        addChangeListener {
                            strategyState.updateInstrumentOrder(
                                subscribedInstrument.alias,
                                value as Int,
                            )
                        }
                    }
                    .also(::add)
            }
        }

    private fun createDisabledStrategyPanels(): Array<StrategyPanel> {
        val mainPanel = StrategyPanel("Quoteboard").apply { add(JLabel("Add-on is disabled.")) }

        return arrayOf(mainPanel)
    }
}
