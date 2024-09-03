package com.github.connorwyatt.quoteboard.models

enum class QuoteboardColumnType {
    INSTRUMENT_SYMBOL {
        override val displayName: String
            get() = "Symbol"
    },
    INSTRUMENT_ALIAS {
        override val displayName: String
            get() = "Alias"
    },
    INSTRUMENT_NAME {
        override val displayName: String
            get() = "Name"
    },
    EXCHANGE {
        override val displayName: String
            get() = "Exchange"
    },
    LAST_TRADED_PRICE {
        override val displayName: String
            get() = "Price"
    },
    DELTA {
        override val displayName: String
            get() = "Δ"
    },
    TICK_DELTA {
        override val displayName: String
            get() = "Tick Δ"
    };

    abstract val displayName: String
}
