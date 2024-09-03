package com.github.connorwyatt.quoteboard.window.models

import com.github.connorwyatt.quoteboard.models.QuoteboardColumn
import com.github.connorwyatt.quoteboard.models.SubscribedInstrument
import com.github.connorwyatt.quoteboard.models.Trade

interface DataEvent {
    data class SubscribedInstrumentsUpdated(val subscribedInstruments: List<SubscribedInstrument>) :
        DataEvent

    data class QuoteboardColumnsUpdated(val quoteboardColumns: List<QuoteboardColumn>) : DataEvent

    data class TradeOccurred(val trade: Trade) : DataEvent
}
