package com.github.connorwyatt.quoteboard.models

import velox.api.layer1.data.InstrumentInfo

data class SubscribedInstrument(
    val alias: String,
    val instrumentInfo: InstrumentInfo,
    val isEnabled: Boolean = false,
    val order: Int = 0,
)
