package com.github.connorwyatt.quoteboard.window

import com.github.connorwyatt.quoteboard.utilities.PriceFormatter
import com.github.connorwyatt.quoteboard.window.models.QuoteboardData
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class QuoteboardTable(priceFormatter: PriceFormatter) : JTable() {
    private var data: QuoteboardData = QuoteboardData()

    private val tableModel =
        object : DefaultTableModel() {
            override fun getColumnCount(): Int = data.enabledQuoteboardColumns.count()

            override fun getRowCount(): Int = data.quoteboardInstruments.count()

            override fun getColumnName(column: Int): String =
                data.enabledQuoteboardColumns[column].displayName

            override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
                val quoteboardInstrument = data.quoteboardInstruments[rowIndex]
                val columnType = data.enabledQuoteboardColumns[columnIndex]

                return quoteboardInstrument.getColumnValue(columnType, priceFormatter) ?: "No data"
            }
        }

    init {
        model = tableModel
    }

    fun update(quoteboardData: QuoteboardData) {
        val previousData = data
        data = quoteboardData

        if (enabledQuoteboardColumnsUpdated(data, previousData)) {
            tableModel.fireTableStructureChanged()
        } else {
            tableModel.fireTableDataChanged()
        }
    }

    private fun enabledQuoteboardColumnsUpdated(
        currentData: QuoteboardData,
        previousData: QuoteboardData,
    ): Boolean =
        !currentData.enabledQuoteboardColumns.containsAll(previousData.enabledQuoteboardColumns) ||
            !previousData.enabledQuoteboardColumns.containsAll(currentData.enabledQuoteboardColumns)
}
