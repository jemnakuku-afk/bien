package org.babetech.borastock.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.babetech.borastock.domain.usecase.*

data class GraphicUiState(
    val stockEvolution: StockEvolutionData = StockEvolutionData(emptyList(), emptyList(), emptyList()),
    val categoryDistribution: List<CategoryDistribution> = emptyList(),
    val supplierPerformance: List<SupplierPerformance> = emptyList(),
    val lowStockAlerts: List<ChartDataPoint> = emptyList(),
    val monthlyTrends: List<ChartDataPoint> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class GraphicViewModel(
    private val getStockEvolutionUseCase: GetStockEvolutionUseCase,
    private val getCategoryDistributionUseCase: GetCategoryDistributionUseCase,
    private val getSupplierPerformanceUseCase: GetSupplierPerformanceUseCase,
    private val getLowStockAlertsUseCase: GetLowStockAlertsUseCase,
    private val getMonthlyTrendsUseCase: GetMonthlyTrendsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GraphicUiState())
    val uiState: StateFlow<GraphicUiState> = _uiState.asStateFlow()

    init {
        loadChartData()
    }

    private fun loadChartData() {
        viewModelScope.launch {
            try {
                combine(
                    getStockEvolutionUseCase(),
                    getCategoryDistributionUseCase(),
                    getSupplierPerformanceUseCase(),
                    getLowStockAlertsUseCase(),
                    getMonthlyTrendsUseCase()
                ) { stockEvolution, categoryDist, supplierPerf, lowStock, monthlyTrends ->
                    GraphicUiState(
                        stockEvolution = stockEvolution,
                        categoryDistribution = categoryDist,
                        supplierPerformance = supplierPerf,
                        lowStockAlerts = lowStock,
                        monthlyTrends = monthlyTrends,
                        isLoading = false,
                        error = null
                    )
                }.catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Erreur lors du chargement des données: ${exception.message}"
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erreur lors du chargement des données: ${e.message}"
                )
            }
        }
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadChartData()
    }
}