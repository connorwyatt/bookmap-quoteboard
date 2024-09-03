package com.github.connorwyatt.quoteboard

import com.github.connorwyatt.quoteboard.configuration.Configuration
import com.github.connorwyatt.quoteboard.models.QuoteboardColumn
import com.github.connorwyatt.quoteboard.models.QuoteboardColumnType
import com.github.connorwyatt.quoteboard.models.SubscribedInstrument
import com.github.connorwyatt.quoteboard.models.Trade
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class StrategyState {
    private val isStrategyEnabledSubject: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    private val subscribedInstrumentsSubject: BehaviorSubject<List<SubscribedInstrument>> =
        BehaviorSubject.createDefault(emptyList())

    private val quoteboardColumnsSubject: BehaviorSubject<List<QuoteboardColumn>> =
        BehaviorSubject.createDefault(
            enumValues<QuoteboardColumnType>().map {
                QuoteboardColumn(it, Configuration.defaultEnabledQuoteboardColumns.contains(it))
            }
        )

    private val tradesSubject: BehaviorSubject<Trade> = BehaviorSubject.create()

    var isStrategyEnabled: Boolean
        get() = isStrategyEnabledSubject.value == true
        set(value) = isStrategyEnabledSubject.onNext(value)

    val subscribedInstrumentsObservable: Observable<List<SubscribedInstrument>> =
        subscribedInstrumentsSubject
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .unsubscribeOn(Schedulers.computation())

    val quoteboardColumnsObservable: Observable<List<QuoteboardColumn>> =
        quoteboardColumnsSubject
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .unsubscribeOn(Schedulers.computation())

    val tradesObservable: Observable<Trade> =
        tradesSubject
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .unsubscribeOn(Schedulers.computation())

    fun addInstrument(subscribedInstrument: SubscribedInstrument) =
        updateSubscribedInstruments { previousSubscribedInstruments ->
            previousSubscribedInstruments.plus(subscribedInstrument).sortedBy { it.alias }
        }

    fun removeInstrument(alias: String) =
        updateSubscribedInstruments { previousSubscribedInstruments ->
            previousSubscribedInstruments.filterNot { it.alias == alias }
        }

    fun setInstrumentIsEnabled(alias: String, isEnabled: Boolean) =
        updateSubscribedInstruments { previousSubscribedInstruments ->
            previousSubscribedInstruments.map { subscribedInstrument ->
                if (subscribedInstrument.alias == alias)
                    subscribedInstrument.copy(isEnabled = isEnabled)
                else subscribedInstrument
            }
        }

    fun updateInstrumentOrder(alias: String, order: Int) =
        updateSubscribedInstruments { previousSubscribedInstruments ->
            previousSubscribedInstruments.map { subscribedInstrument ->
                if (subscribedInstrument.alias == alias) subscribedInstrument.copy(order = order)
                else subscribedInstrument
            }
        }

    fun tradeOccurred(trade: Trade) = tradesSubject.onNext(trade)

    fun setQuoteboardColumnIsEnabled(type: QuoteboardColumnType, isEnabled: Boolean) {
        updateQuoteboardColumns { previousQuoteboardColumns ->
            previousQuoteboardColumns.map { quoteboardColumn ->
                if (quoteboardColumn.type == type) quoteboardColumn.copy(isEnabled = isEnabled)
                else quoteboardColumn
            }
        }
    }

    private fun updateSubscribedInstruments(
        transformer: (List<SubscribedInstrument>) -> List<SubscribedInstrument>
    ) {
        val previousSubscribedInstruments = subscribedInstrumentsSubject.value ?: emptyList()

        val newSubscribedInstruments = transformer(previousSubscribedInstruments)

        if (newSubscribedInstruments == previousSubscribedInstruments) {
            return
        }

        subscribedInstrumentsSubject.onNext(newSubscribedInstruments)
    }

    private fun updateQuoteboardColumns(
        transformer: (List<QuoteboardColumn>) -> List<QuoteboardColumn>
    ) {
        val previousQuoteboardColumns = quoteboardColumnsSubject.value ?: emptyList()

        val newQuoteboardColumns = transformer(previousQuoteboardColumns)

        if (newQuoteboardColumns == previousQuoteboardColumns) {
            return
        }

        quoteboardColumnsSubject.onNext(newQuoteboardColumns)
    }
}
