package com.github.connorwyatt.quoteboard.models

import velox.api.layer1.data.TradeInfo

data class Trade(
    val alias: String,
    val priceLevel: Double,
    val quantity: Int,
    val tradeInfo: TradeInfo,
)
