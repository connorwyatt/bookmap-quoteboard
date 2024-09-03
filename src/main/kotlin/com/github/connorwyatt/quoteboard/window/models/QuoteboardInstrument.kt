package com.github.connorwyatt.quoteboard.window.models

import com.github.connorwyatt.quoteboard.models.QuoteboardColumnType
import com.github.connorwyatt.quoteboard.models.SubscribedInstrument
import com.github.connorwyatt.quoteboard.utilities.PriceFormatter
import kotlin.math.roundToLong

data class QuoteboardInstrument(
    val subscribedInstrument: SubscribedInstrument,
    val quoteboardInstrumentData: QuoteboardInstrumentData,
) {
    fun getColumnValue(
        quoteboardColumnType: QuoteboardColumnType,
        priceFormatter: PriceFormatter,
    ): Any? =
        // TODO: Convert levels to prices
        when (quoteboardColumnType) {
            QuoteboardColumnType.INSTRUMENT_SYMBOL -> subscribedInstrument.instrumentInfo.symbol
            QuoteboardColumnType.INSTRUMENT_ALIAS -> subscribedInstrument.alias
            QuoteboardColumnType.INSTRUMENT_NAME -> subscribedInstrument.instrumentInfo.fullName
            QuoteboardColumnType.EXCHANGE -> subscribedInstrument.instrumentInfo.exchange
            QuoteboardColumnType.LAST_TRADED_PRICE ->
                quoteboardInstrumentData.lastTradedPriceLevel?.let {
                    priceFormatter.formatPrice(subscribedInstrument, it)
                }
            QuoteboardColumnType.DELTA ->
                quoteboardInstrumentData.priceLevelDelta?.let {
                    priceFormatter.formatDelta(subscribedInstrument, it)
                }
            QuoteboardColumnType.TICK_DELTA ->
                quoteboardInstrumentData.priceLevelDelta?.roundToLong()
        }
}
