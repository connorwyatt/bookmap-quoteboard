package com.github.connorwyatt.quoteboard.window.models

import com.github.connorwyatt.quoteboard.models.QuoteboardColumnType

data class QuoteboardData(
    val quoteboardInstruments: List<QuoteboardInstrument> = emptyList(),
    val enabledQuoteboardColumns: List<QuoteboardColumnType> = emptyList(),
)
