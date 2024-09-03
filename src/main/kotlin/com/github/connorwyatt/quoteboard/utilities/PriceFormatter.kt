package com.github.connorwyatt.quoteboard.utilities

import com.github.connorwyatt.quoteboard.models.SubscribedInstrument
import velox.api.layer1.Layer1ApiProvider

class PriceFormatter(private val apiProvider: Layer1ApiProvider) {
    fun formatPrice(subscribedInstrument: SubscribedInstrument, priceLevel: Double): String {
        return apiProvider.formatPrice(
            subscribedInstrument.alias,
            priceLevel * subscribedInstrument.instrumentInfo.pips,
        )
    }

    fun formatDelta(subscribedInstrument: SubscribedInstrument, priceLevel: Double): String {
        val sign =
            when {
                priceLevel > 0 -> "+"
                else -> ""
            }

        val formattedPrice = formatPrice(subscribedInstrument, priceLevel)

        return "$sign$formattedPrice"
    }
}
