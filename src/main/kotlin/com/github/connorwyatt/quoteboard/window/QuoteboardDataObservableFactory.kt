package com.github.connorwyatt.quoteboard.window

import com.github.connorwyatt.quoteboard.StrategyState
import com.github.connorwyatt.quoteboard.window.models.DataEvent
import com.github.connorwyatt.quoteboard.window.models.QuoteboardData
import com.github.connorwyatt.quoteboard.window.models.QuoteboardInstrument
import com.github.connorwyatt.quoteboard.window.models.QuoteboardInstrumentData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class QuoteboardDataObservableFactory(private val strategyState: StrategyState) {
    val dataEventsObservable: Observable<DataEvent> =
        {
            val subscribedInstrumentsDataEvents =
                strategyState.subscribedInstrumentsObservable.map {
                    DataEvent.SubscribedInstrumentsUpdated(it)
                }
            val quoteboardColumnsDataEvents =
                strategyState.quoteboardColumnsObservable.map {
                    DataEvent.QuoteboardColumnsUpdated(it)
                }
            val tradeDataEvents = strategyState.tradesObservable.map { DataEvent.TradeOccurred(it) }

            Observable.merge(
                    subscribedInstrumentsDataEvents,
                    quoteboardColumnsDataEvents,
                    tradeDataEvents,
                )
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.computation())
        }()

    val dataObservable: Observable<QuoteboardData> =
        dataEventsObservable
            .scan(QuoteboardData()) { quoteboardData, dataEvent ->
                when (dataEvent) {
                    is DataEvent.SubscribedInstrumentsUpdated -> {
                        updateQuoteboardInstruments(dataEvent, quoteboardData)
                    }
                    is DataEvent.QuoteboardColumnsUpdated -> {
                        updateQuoteboardColumns(dataEvent, quoteboardData)
                    }
                    is DataEvent.TradeOccurred -> {
                        updateQuoteboardInstrumentData(dataEvent, quoteboardData)
                    }
                    else -> {
                        quoteboardData
                    }
                }
            }
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .unsubscribeOn(Schedulers.computation())

    private fun updateQuoteboardInstruments(
        dataEvent: DataEvent.SubscribedInstrumentsUpdated,
        quoteboardData: QuoteboardData,
    ): QuoteboardData {
        val newQuoteboardInstruments =
            dataEvent.subscribedInstruments.map { subscribedInstrument ->
                val existingInstrument =
                    quoteboardData.quoteboardInstruments.singleOrNull {
                        it.subscribedInstrument.alias == subscribedInstrument.alias
                    }
                existingInstrument?.copy(subscribedInstrument = subscribedInstrument)
                    ?: QuoteboardInstrument(subscribedInstrument, QuoteboardInstrumentData())
            }

        return quoteboardData.copy(
            quoteboardInstruments =
                newQuoteboardInstruments
                    .filter { it.subscribedInstrument.isEnabled }
                    .sortedByDescending { it.subscribedInstrument.order }
        )
    }

    private fun updateQuoteboardColumns(
        dataEvent: DataEvent.QuoteboardColumnsUpdated,
        quoteboardData: QuoteboardData,
    ): QuoteboardData {
        val enabledQuoteboardColumns = dataEvent.quoteboardColumns.filter { it.isEnabled }

        return quoteboardData.copy(
            enabledQuoteboardColumns = enabledQuoteboardColumns.map { it.type }
        )
    }

    private fun updateQuoteboardInstrumentData(
        dataEvent: DataEvent.TradeOccurred,
        quoteboardData: QuoteboardData,
    ): QuoteboardData {
        val trade = dataEvent.trade

        return quoteboardData.copy(
            quoteboardInstruments =
                quoteboardData.quoteboardInstruments.map { quoteboardInstrument ->
                    if (trade.alias != quoteboardInstrument.subscribedInstrument.alias) {
                        return@map quoteboardInstrument
                    }

                    quoteboardInstrument.copy(
                        quoteboardInstrumentData =
                            quoteboardInstrument.quoteboardInstrumentData.copy(
                                lastTradedPriceLevel = trade.priceLevel,
                                baselinePriceLevel =
                                    quoteboardInstrument.quoteboardInstrumentData.baselinePriceLevel
                                        ?: trade.priceLevel,
                            )
                    )
                }
        )
    }
}
