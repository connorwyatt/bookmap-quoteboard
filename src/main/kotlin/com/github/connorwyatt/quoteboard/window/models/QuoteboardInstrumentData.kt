package com.github.connorwyatt.quoteboard.window.models

data class QuoteboardInstrumentData(
    val lastTradedPriceLevel: Double? = null,
    val baselinePriceLevel: Double? = null,
    val bidPriceLevel: Double? = null,
    val askPriceLevel: Double? = null,
) {
    val priceLevelDelta: Double?
        get() {
            if (lastTradedPriceLevel == null || baselinePriceLevel == null) {
                return null
            }

            return lastTradedPriceLevel - baselinePriceLevel
        }
}
