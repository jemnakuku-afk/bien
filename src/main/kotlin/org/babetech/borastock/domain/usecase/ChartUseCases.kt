package org.babetech.borastock.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.babetech.borastock.data.models.*
import org.babetech.borastock.data.repository.StockRepository

// Chart Data Models
data class ChartDataPoint(
    val label: String,
    val value: Double,
    val date: String? = null
)

data class StockEvolutionData(
    val entries: List<ChartDataPoint>,
    val exits: List<ChartDataPoint>,
    val stockLevels: List<ChartDataPoint>
)

data class CategoryDistribution(
    val category: String,
    val count: Int,
    val value: Double,
    val percentage: Float
)

data class SupplierPerformance(
    val supplierName: String,
    val totalOrders: Int,
    val totalValue: Double,
    val reliability: SupplierReliability
)

// Use Cases for Chart Data
class GetStockEvolutionUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<StockEvolutionData> {
        return combine(
            repository.getAllStockEntries(),
            repository.getAllStockExits(),
            repository.getAllStockItems()
        ) { entries, exits, items ->
            val entryPoints = entries.groupBy { it.entryDate.take(10) } // Group by date
                .map { (date, dayEntries) ->
                    ChartDataPoint(
                        label = date,
                        value = dayEntries.sumOf { it.totalValue },
                        date = date
                    )
                }.sortedBy { it.date }

            val exitPoints = exits.groupBy { it.exitDate.take(10) }
                .map { (date, dayExits) ->
                    ChartDataPoint(
                        label = date,
                        value = dayExits.sumOf { it.totalValue },
                        date = date
                    )
                }.sortedBy { it.date }

            val stockLevelPoints = items.map { item ->
                ChartDataPoint(
                    label = item.name,
                    value = item.currentStock.toDouble()
                )
            }

            StockEvolutionData(
                entries = entryPoints,
                exits = exitPoints,
                stockLevels = stockLevelPoints
            )
        }
    }
}

class GetCategoryDistributionUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<CategoryDistribution>> {
        return repository.getAllStockItems().map { items ->
            val totalValue = items.sumOf { it.totalValue }
            items.groupBy { it.category }
                .map { (category, categoryItems) ->
                    val categoryValue = categoryItems.sumOf { it.totalValue }
                    CategoryDistribution(
                        category = category,
                        count = categoryItems.size,
                        value = categoryValue,
                        percentage = if (totalValue > 0) (categoryValue / totalValue * 100).toFloat() else 0f
                    )
                }
                .sortedByDescending { it.value }
        }
    }
}

class GetSupplierPerformanceUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<SupplierPerformance>> {
        return combine(
            repository.getAllSuppliers(),
            repository.getAllStockEntries()
        ) { suppliers, entries ->
            suppliers.map { supplier ->
                val supplierEntries = entries.filter { it.supplierId == supplier.id }
                SupplierPerformance(
                    supplierName = supplier.name,
                    totalOrders = supplierEntries.size,
                    totalValue = supplierEntries.sumOf { it.totalValue },
                    reliability = supplier.reliability
                )
            }.sortedByDescending { it.totalValue }
        }
    }
}

class GetLowStockAlertsUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<ChartDataPoint>> {
        return repository.getAllStockItems().map { items ->
            items.filter { it.stockStatus == StockStatus.LOW_STOCK || it.stockStatus == StockStatus.OUT_OF_STOCK }
                .map { item ->
                    ChartDataPoint(
                        label = item.name,
                        value = item.currentStock.toDouble()
                    )
                }
                .sortedBy { it.value }
        }
    }
}

class GetMonthlyTrendsUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<ChartDataPoint>> {
        return combine(
            repository.getAllStockEntries(),
            repository.getAllStockExits()
        ) { entries, exits ->
            val monthlyData = mutableMapOf<String, Double>()
            
            // Process entries
            entries.forEach { entry ->
                val month = entry.entryDate.take(7) // YYYY-MM
                monthlyData[month] = (monthlyData[month] ?: 0.0) + entry.totalValue
            }
            
            // Subtract exits
            exits.forEach { exit ->
                val month = exit.exitDate.take(7) // YYYY-MM
                monthlyData[month] = (monthlyData[month] ?: 0.0) - exit.totalValue
            }
            
            monthlyData.map { (month, value) ->
                ChartDataPoint(
                    label = month,
                    value = value,
                    date = month
                )
            }.sortedBy { it.date }
        }
    }
}