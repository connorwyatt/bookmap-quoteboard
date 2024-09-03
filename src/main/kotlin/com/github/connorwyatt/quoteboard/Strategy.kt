package com.github.connorwyatt.quoteboard

import com.github.connorwyatt.quoteboard.models.SubscribedInstrument
import com.github.connorwyatt.quoteboard.models.Trade
import com.github.connorwyatt.quoteboard.strategypanels.StrategyPanelsFactory
import com.github.connorwyatt.quoteboard.utilities.PriceFormatter
import com.github.connorwyatt.quoteboard.window.QuoteboardDataObservableFactory
import com.github.connorwyatt.quoteboard.window.QuoteboardWindowManager
import velox.api.layer1.Layer1ApiAdminAdapter
import velox.api.layer1.Layer1ApiDataAdapter
import velox.api.layer1.Layer1ApiFinishable
import velox.api.layer1.Layer1ApiInstrumentAdapter
import velox.api.layer1.Layer1ApiInstrumentSpecificEnabledStateProvider
import velox.api.layer1.Layer1ApiProvider
import velox.api.layer1.Layer1CustomPanelsGetter
import velox.api.layer1.annotations.Layer1ApiVersion
import velox.api.layer1.annotations.Layer1ApiVersionValue
import velox.api.layer1.annotations.Layer1Attachable
import velox.api.layer1.annotations.Layer1StrategyName
import velox.api.layer1.common.ListenableHelper
import velox.api.layer1.data.InstrumentInfo
import velox.api.layer1.data.TradeInfo

@Layer1Attachable
@Layer1StrategyName("Quoteboard")
@Layer1ApiVersion(Layer1ApiVersionValue.VERSION2)
class Strategy(apiProvider: Layer1ApiProvider) :
    Layer1ApiAdminAdapter,
    Layer1ApiInstrumentAdapter,
    Layer1ApiDataAdapter,
    Layer1ApiInstrumentSpecificEnabledStateProvider,
    Layer1CustomPanelsGetter,
    Layer1ApiFinishable {
    private val priceFormatter = PriceFormatter(apiProvider)
    private val strategyState = StrategyState()
    private val quoteboardDataObservableFactory = QuoteboardDataObservableFactory(strategyState)
    private val windowManager =
        QuoteboardWindowManager(quoteboardDataObservableFactory, priceFormatter)
    private val strategyPanelsFactory = StrategyPanelsFactory(strategyState, windowManager)

    init {
        ListenableHelper.addListeners(apiProvider, this)
    }

    override fun onInstrumentAdded(alias: String, instrumentInfo: InstrumentInfo) =
        strategyState.addInstrument(SubscribedInstrument(alias, instrumentInfo))

    override fun onInstrumentRemoved(alias: String) = strategyState.removeInstrument(alias)

    override fun onTrade(alias: String, priceLevel: Double, size: Int, tradeInfo: TradeInfo) {
        strategyState.tradeOccurred(Trade(alias, priceLevel, size, tradeInfo))
    }

    override fun onStrategyCheckboxEnabled(alias: String, isEnabled: Boolean) {
        strategyState.isStrategyEnabled = isEnabled
    }

    override fun isStrategyEnabled(alias: String): Boolean = strategyState.isStrategyEnabled

    override fun getCustomGuiFor(alias: String, strategyName: String) =
        strategyPanelsFactory.createStrategyPanels()

    override fun finish() {
        windowManager.dispose()
    }
}
