package com.github.connorwyatt.quoteboard.configuration

import com.github.connorwyatt.quoteboard.models.QuoteboardColumnType

object Configuration {
    val defaultEnabledQuoteboardColumns: Set<QuoteboardColumnType> =
        setOf(
            QuoteboardColumnType.INSTRUMENT_SYMBOL,
            QuoteboardColumnType.LAST_TRADED_PRICE,
            QuoteboardColumnType.DELTA,
        )
}
