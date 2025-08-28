@@ .. @@
 package org.babetech.borastock.ui.screens.dashboard


 import androidx.compose.foundation.layout.Arrangement
 import androidx.compose.foundation.layout.Box
-import androidx.compose.foundation.gestures.TransformableState
-import androidx.compose.foundation.gestures.detectTransformGestures
-import androidx.compose.foundation.gestures.transformable
-import androidx.compose.ui.graphics.graphicsLayer
-import androidx.compose.ui.input.pointer.pointerInput

 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Spacer
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.layout.size
 import androidx.compose.foundation.layout.width
 import androidx.compose.foundation.lazy.LazyRow
 import androidx.compose.foundation.lazy.items
-import androidx.compose.material.Text
+import androidx.compose.foundation.rememberScrollState
+import androidx.compose.foundation.verticalScroll
 import androidx.compose.material3.*
 import androidx.compose.runtime.Composable
+import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.runtime.setValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.graphics.StrokeCap
 import androidx.compose.ui.graphics.painter.Painter
 import androidx.compose.ui.text.TextStyle
 import androidx.compose.ui.text.font.FontFamily
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
+import androidx.lifecycle.compose.collectAsStateWithLifecycle

 import borastock.composeapp.generated.resources.Res
 import borastock.composeapp.generated.resources.analytics
 import borastock.composeapp.generated.resources.barchart
 import borastock.composeapp.generated.resources.donutlarge
 import borastock.composeapp.generated.resources.piechart
 import com.aay.compose.barChart.BarChart
 import com.aay.compose.barChart.model.BarParameters
 import com.aay.compose.baseComponents.model.GridOrientation
 import com.aay.compose.donutChart.DonutChart
 import com.aay.compose.donutChart.PieChart
 import com.aay.compose.donutChart.model.PieChartData
 import com.aay.compose.lineChart.LineChart
 import com.aay.compose.lineChart.model.LineParameters
 import com.aay.compose.lineChart.model.LineType
 import com.aay.compose.radarChart.RadarChart
 import com.aay.compose.radarChart.model.NetLinesStyle
 import com.aay.compose.radarChart.model.Polygon
 import com.aay.compose.radarChart.model.PolygonStyle
+import org.babetech.borastock.domain.usecase.*

 import org.jetbrains.compose.resources.painterResource
+import org.koin.compose.viewmodel.koinViewModel



 data class ChartType(
     val key: String,
     val title: String,
     val icon: Painter,
     val description: String
 )


 @Composable
-fun GraphicSwitcherScreen() {
+fun GraphicSwitcherScreen(viewModel: GraphicViewModel = koinViewModel()) {
+    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
     var selectedChart by remember { mutableStateOf("Line") }

     val chartTypes = listOf(
-        ChartType("Line", "Courbes", painterResource(Res.drawable.analytics), "Évolution temporelle"),
-        ChartType("Bar", "Barres", painterResource(Res.drawable.barchart), "Comparaisons"),
-        ChartType("Pie", "Secteurs", painterResource(Res.drawable.piechart), "Répartitions"),
-        ChartType("Donut", "Anneau", painterResource(Res.drawable.donutlarge), "Proportions"),
-        ChartType("Radar", "Radar", painterResource(Res.drawable.analytics), "Multi-critères")
+        ChartType("Evolution", "Évolution", painterResource(Res.drawable.analytics), "Stock dans le temps"),
+        ChartType("Categories", "Catégories", painterResource(Res.drawable.piechart), "Répartition par catégorie"),
+        ChartType("Suppliers", "Fournisseurs", painterResource(Res.drawable.barchart), "Performance fournisseurs"),
+        ChartType("Alerts", "Alertes", painterResource(Res.drawable.donutlarge), "Stock critique"),
+        ChartType("Trends", "Tendances", painterResource(Res.drawable.analytics), "Tendances mensuelles")
     )

-    Column(modifier = Modifier.fillMaxSize()) {
+    Column(
+        modifier = Modifier
+            .fillMaxSize()
+            .verticalScroll(rememberScrollState())
+    ) {
+        if (uiState.isLoading) {
+            Box(
+                modifier = Modifier
+                    .fillMaxWidth()
+                    .height(200.dp),
+                contentAlignment = Alignment.Center
+            ) {
+                CircularProgressIndicator()
+            }
+        }
+
+        if (uiState.error != null) {
+            Card(
+                modifier = Modifier
+                    .fillMaxWidth()
+                    .padding(16.dp),
+                colors = CardDefaults.cardColors(
+                    containerColor = MaterialTheme.colorScheme.errorContainer
+                )
+            ) {
+                Column(
+                    modifier = Modifier.padding(16.dp),
+                    horizontalAlignment = Alignment.CenterHorizontally
+                ) {
+                    Text(
+                        text = "Erreur",
+                        style = MaterialTheme.typography.titleMedium,
+                        color = MaterialTheme.colorScheme.onErrorContainer
+                    )
+                    Text(
+                        text = uiState.error,
+                        style = MaterialTheme.typography.bodyMedium,
+                        color = MaterialTheme.colorScheme.onErrorContainer
+                    )
+                    Button(
+                        onClick = { viewModel.refreshData() },
+                        modifier = Modifier.padding(top = 8.dp)
+                    ) {
+                        Text("Réessayer")
+                    }
+                }
+            }
+        }
+
         // --- Row avec les options de graphique ---
         LazyRow(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(8.dp),
             horizontalArrangement = Arrangement.spacedBy(8.dp)
         ) {
             items(chartTypes) { chartType ->
                 ElevatedCard(
                     onClick = { selectedChart = chartType.key },
                     modifier = Modifier.width(140.dp),
                     colors = CardDefaults.elevatedCardColors(
                         containerColor = if (selectedChart == chartType.key) MaterialTheme.colorScheme.primaryContainer else Color.White
                     )
                 ) {
                     Column(
                         modifier = Modifier
                             .padding(12.dp),
                         horizontalAlignment = Alignment.CenterHorizontally
                     ) {
                         Icon(
                             painter = chartType.icon,
                             contentDescription = chartType.title,
                             tint = if (selectedChart == chartType.key) MaterialTheme.colorScheme.primary else Color.Gray,
                             modifier = Modifier.size(32.dp)
                         )
                         Spacer(Modifier.height(8.dp))
                         Text(
                             chartType.title,
                             style = MaterialTheme.typography.titleSmall,
                             color = if (selectedChart == chartType.key) MaterialTheme.colorScheme.primary else Color.Black
                         )
                         Text(
                             chartType.description,
                             style = MaterialTheme.typography.bodySmall,
                             color = Color.DarkGray,
                             maxLines = 2
                         )
                     }
                 }
             }
         }

         // --- Affichage du graphique sélectionné ---
         Box(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(8.dp)
         ) {
-            when (selectedChart) {
-                "Line" -> GraphicScreen()
-                "Bar" -> BarChartSample()
-                "Pie" -> PieChartSample()
-                "Donut" -> DonutChartSample()
-                "Radar" -> RadarChartSample()
+            if (!uiState.isLoading && uiState.error == null) {
+                when (selectedChart) {
+                    "Evolution" -> StockEvolutionChart(uiState.stockEvolution)
+                    "Categories" -> CategoryDistributionChart(uiState.categoryDistribution)
+                    "Suppliers" -> SupplierPerformanceChart(uiState.supplierPerformance)
+                    "Alerts" -> LowStockAlertsChart(uiState.lowStockAlerts)
+                    "Trends" -> MonthlyTrendsChart(uiState.monthlyTrends)
+                }
             }
         }
     }
 }

+@Composable
+fun StockEvolutionChart(data: StockEvolutionData) {
+    if (data.entries.isEmpty() && data.exits.isEmpty()) {
+        EmptyChartPlaceholder("Aucune donnée d'évolution disponible")
+        return
+    }
+
+    val lineParameters = mutableListOf<LineParameters>()
+    
+    if (data.entries.isNotEmpty()) {
+        lineParameters.add(
+            LineParameters(
+                label = "Entrées",
+                data = data.entries.map { it.value },
+                lineColor = Color(0xFF22c55e),
+                lineType = LineType.CURVED_LINE,
+                lineShadow = true
+            )
+        )
+    }
+    
+    if (data.exits.isNotEmpty()) {
+        lineParameters.add(
+            LineParameters(
+                label = "Sorties",
+                data = data.exits.map { it.value },
+                lineColor = Color(0xFFef4444),
+                lineType = LineType.CURVED_LINE,
+                lineShadow = true
+            )
+        )
+    }

+    LineChart(
+        modifier = Modifier.fillMaxSize(),
+        linesParameters = lineParameters,
+        isGrid = true,
+        gridColor = Color.Gray.copy(alpha = 0.3f),
+        xAxisData = data.entries.map { it.label }.ifEmpty { data.exits.map { it.label } },
+        animateChart = true,
+        showGridWithSpacer = true,
+        yAxisStyle = TextStyle(
+            fontSize = 12.sp,
+            color = Color.Gray,
+        ),
+        xAxisStyle = TextStyle(
+            fontSize = 12.sp,
+            color = Color.Gray,
+            fontWeight = FontWeight.W400
+        ),
+        yAxisRange = 10,
+        oneLineChart = false,
+        gridOrientation = GridOrientation.VERTICAL
+    )
+}

+@Composable
+fun CategoryDistributionChart(data: List<CategoryDistribution>) {
+    if (data.isEmpty()) {
+        EmptyChartPlaceholder("Aucune donnée de catégorie disponible")
+        return
+    }

+    val pieChartData = data.map { category ->
+        PieChartData(
+            partName = category.category,
+            data = category.value,
+            color = when (category.category.lowercase()) {
+                "électronique" -> Color(0xFF3b82f6)
+                "informatique" -> Color(0xFF22c55e)
+                "accessoires" -> Color(0xFFf59e0b)
+                "mobilier" -> Color(0xFF8b5cf6)
+                else -> Color(0xFF6b7280)
+            }
+        )
+    }

+    PieChart(
+        modifier = Modifier.fillMaxSize(),
+        pieChartData = pieChartData,
+        ratioLineColor = Color.LightGray,
+        textRatioStyle = TextStyle(color = Color.Gray, fontSize = 12.sp)
+    )
+}

+@Composable
+fun SupplierPerformanceChart(data: List<SupplierPerformance>) {
+    if (data.isEmpty()) {
+        EmptyChartPlaceholder("Aucune donnée de fournisseur disponible")
+        return
+    }

+    val barParameters = listOf(
+        BarParameters(
+            dataName = "Valeur totale",
+            data = data.map { it.totalValue },
+            barColor = Color(0xFF3b82f6)
+        )
+    )

+    BarChart(
+        chartParameters = barParameters,
+        gridColor = Color.Gray.copy(alpha = 0.3f),
+        xAxisData = data.map { it.supplierName.take(10) }, // Truncate long names
+        isShowGrid = true,
+        animateChart = true,
+        showGridWithSpacer = true,
+        yAxisStyle = TextStyle(
+            fontSize = 12.sp,
+            color = Color.Gray,
+        ),
+        xAxisStyle = TextStyle(
+            fontSize = 10.sp,
+            color = Color.Gray,
+            fontWeight = FontWeight.W400
+        ),
+        yAxisRange = 10,
+        barWidth = 20.dp
+    )
+}

+@Composable
+fun LowStockAlertsChart(data: List<ChartDataPoint>) {
+    if (data.isEmpty()) {
+        EmptyChartPlaceholder("Aucune alerte de stock critique")
+        return
+    }

+    val pieChartData = data.mapIndexed { index, item ->
+        PieChartData(
+            partName = item.label,
+            data = if (item.value == 0.0) 1.0 else item.value, // Avoid zero values
+            color = when {
+                item.value == 0.0 -> Color(0xFFef4444) // Out of stock - red
+                item.value <= 5 -> Color(0xFFf59e0b) // Very low - orange
+                else -> Color(0xFF22c55e) // Low but available - green
+            }
+        )
+    }

+    DonutChart(
+        modifier = Modifier.fillMaxSize(),
+        pieChartData = pieChartData,
+        centerTitle = "Alertes",
+        centerTitleStyle = TextStyle(
+            color = Color(0xFF071952),
+            fontSize = 16.sp,
+            fontWeight = FontWeight.Bold
+        ),
+        outerCircularColor = Color.LightGray,
+        innerCircularColor = Color.Gray,
+        ratioLineColor = Color.LightGray
+    )
+}

+@Composable
+fun MonthlyTrendsChart(data: List<ChartDataPoint>) {
+    if (data.isEmpty()) {
+        EmptyChartPlaceholder("Aucune donnée de tendance disponible")
+        return
+    }

+    val lineParameters = listOf(
+        LineParameters(
+            label = "Tendance mensuelle",
+            data = data.map { it.value },
+            lineColor = Color(0xFF8b5cf6),
+            lineType = LineType.CURVED_LINE,
+            lineShadow = true
+        )
+    )

+    LineChart(
+        modifier = Modifier.fillMaxSize(),
+        linesParameters = lineParameters,
+        isGrid = true,
+        gridColor = Color.Gray.copy(alpha = 0.3f),
+        xAxisData = data.map { it.label },
+        animateChart = true,
+        showGridWithSpacer = true,
+        yAxisStyle = TextStyle(
+            fontSize = 12.sp,
+            color = Color.Gray,
+        ),
+        xAxisStyle = TextStyle(
+            fontSize = 12.sp,
+            color = Color.Gray,
+            fontWeight = FontWeight.W400
+        ),
+        yAxisRange = 10,
+        oneLineChart = true,
+        gridOrientation = GridOrientation.VERTICAL
+    )
+}

+@Composable
+fun EmptyChartPlaceholder(message: String) {
+    Box(
+        modifier = Modifier
+            .fillMaxSize()
+            .padding(32.dp),
+        contentAlignment = Alignment.Center
+    ) {
+        Column(
+            horizontalAlignment = Alignment.CenterHorizontally,
+            verticalArrangement = Arrangement.spacedBy(16.dp)
+        ) {
+            Icon(
+                painter = painterResource(Res.drawable.analytics),
+                contentDescription = null,
+                modifier = Modifier.size(64.dp),
+                tint = Color.Gray
+            )
+            Text(
+                text = message,
+                style = MaterialTheme.typography.bodyLarge,
+                color = Color.Gray
+            )
+        }
+    }
+}

 @Composable
-fun GraphicScreen() {
-
-    val testLineParameters: List<LineParameters> = listOf(
-        LineParameters(
-            label = "revenue",
-            data = listOf(70.0, 00.0, 50.33, 40.0, 100.500, 50.0),
-            lineColor = Color.Gray,
-            lineType = LineType.CURVED_LINE,
-            lineShadow = true,
-        ),
-        LineParameters(
-            label = "Earnings",
-            data = listOf(60.0, 80.6, 40.33, 86.232, 88.0, 90.0),
-            lineColor = Color(0xFFFF7F50),
-            lineType = LineType.DEFAULT_LINE,
-            lineShadow = true
-        ),
-        LineParameters(
-            label = "Earnings",
-            data = listOf(1.0, 40.0, 11.33, 55.23, 1.0, 100.0),
-            lineColor = Color(0xFF81BE88),
-            lineType = LineType.CURVED_LINE,
-            lineShadow = false,
-        )
-    )
-
-    Box(Modifier) {
-        LineChart(
-            modifier = Modifier.fillMaxSize(),
-            linesParameters = testLineParameters,
-            isGrid = true,
-            gridColor = Color.Blue,
-            xAxisData = listOf("2015", "2016", "2017", "2018", "2019", "2020"),
-            animateChart = true,
-            showGridWithSpacer = true,
-            yAxisStyle = TextStyle(
-                fontSize = 14.sp,
-                color = Color.Gray,
-            ),
-            xAxisStyle = TextStyle(
-                fontSize = 14.sp,
-                color = Color.Gray,
-                fontWeight = FontWeight.W400
-            ),
-            yAxisRange = 14,
-            oneLineChart = false,
-            gridOrientation = GridOrientation.VERTICAL
-        )
-    }
+fun GraphicScreen(viewModel: GraphicViewModel = koinViewModel()) {
+    GraphicSwitcherScreen(viewModel)
 }

-
-
-@Composable
-fun BarChartSample() {
-
-    val testBarParameters: List<BarParameters> = listOf(
-        BarParameters(
-            dataName = "Completed",
-            data = listOf(0.6, 10.6, 80.0, 50.6, 44.0, 100.6, 10.0),
-            barColor = Color(0xFF6C3428)
-        ),
-        BarParameters(
-            dataName = "Completed",
-            data = listOf(50.0, 30.6, 77.0, 69.6, 50.0, 30.6, 80.0),
-            barColor = Color(0xFFBA704F),
-        ),
-        BarParameters(
-            dataName = "Completed",
-            data = listOf(100.0, 99.6, 60.0, 80.6, 10.0, 100.6, 55.99),
-            barColor = Color(0xFFDFA878),
-        ),
-    )
-
-    Box(Modifier.fillMaxSize()) {
-        BarChart(
-            chartParameters = testBarParameters,
-            gridColor = Color.DarkGray,
-            xAxisData = listOf("2016", "2017", "2018", "2019", "2020", "2021", "2022"),
-            isShowGrid = true,
-            animateChart = true,
-            showGridWithSpacer = true,
-            yAxisStyle = TextStyle(
-                fontSize = 14.sp,
-                color = Color.DarkGray,
-            ),
-            xAxisStyle = TextStyle(
-                fontSize = 14.sp,
-                color = Color.DarkGray,
-                fontWeight = FontWeight.W400
-            ),
-            yAxisRange = 15,
-            barWidth = 20.dp
-        )
-    }
-}
-
-
-@Composable
-fun PieChartSample() {
-
-    val testPieChartData: List<PieChartData> = listOf(
-        PieChartData(
-            partName = "part A",
-            data = 500.0,
-            color = Color(0xFF22A699),
-        ),
-        PieChartData(
-            partName = "Part B",
-            data = 700.0,
-            color = Color(0xFFF2BE22),
-        ),
-        PieChartData(
-            partName = "Part C",
-            data = 500.0,
-            color = Color(0xFFF29727),
-        ),
-        PieChartData(
-            partName = "Part D",
-            data = 100.0,
-            color = Color(0xFFF24C3D),
-        ),
-    )
-
-    PieChart(
-        modifier = Modifier.fillMaxSize(),
-        pieChartData = testPieChartData,
-        ratioLineColor = Color.LightGray,
-        textRatioStyle = TextStyle(color = Color.Gray),
-    )
-}
-
-
-
-@Composable
-fun DonutChartSample() {
-
-    val testPieChartData: List<PieChartData> = listOf(
-        PieChartData(
-            partName = "part A",
-            data = 500.0,
-            color = Color(0xFF0B666A),
-        ),
-        PieChartData(
-            partName = "Part B",
-            data = 700.0,
-            color = Color(0xFF35A29F),
-        ),
-        PieChartData(
-            partName = "Part C",
-            data = 500.0,
-            color = Color(0xFF97FEED),
-        ),
-        PieChartData(
-            partName = "Part D",
-            data = 100.0,
-            color = Color(0xFF071952),
-        ),
-    )
-
-    DonutChart(
-        modifier = Modifier.fillMaxSize(),
-        pieChartData = testPieChartData,
-        centerTitle = "Orders",
-        centerTitleStyle = TextStyle(color = Color(0xFF071952)),
-        outerCircularColor = Color.LightGray,
-        innerCircularColor = Color.Gray,
-        ratioLineColor = Color.LightGray,
-    )
-}
-
-
-@Composable
-fun RadarChartSample() {
-    val radarLabels =
-        listOf(
-            "Party A",
-            "Party A",
-            "Party A",
-            "Part A",
-            "Party A",
-            "Party A",
-            "Party A",
-            "Party A",
-            "Party A"
-        )
-    val values2 = listOf(120.0, 160.0, 110.0, 112.0, 200.0, 120.0, 145.0, 101.0, 200.0)
-    val values = listOf(180.0, 180.0, 165.0, 135.0, 120.0, 150.0, 140.0, 190.0, 200.0)
-    val labelsStyle = TextStyle(
-        color = Color.Black,
-        fontFamily = FontFamily.Serif,
-        fontWeight = FontWeight.Medium,
-        fontSize = 10.sp
-    )
-
-    val scalarValuesStyle = TextStyle(
-        color = Color.Black,
-        fontFamily = FontFamily.Serif,
-        fontWeight = FontWeight.Medium,
-        fontSize = 10.sp
-    )
-
-    RadarChart(
-        modifier = Modifier.fillMaxSize(),
-        radarLabels = radarLabels,
-        labelsStyle = labelsStyle,
-        netLinesStyle = NetLinesStyle(
-            netLineColor = Color(0x90ffD3CFD3),
-            netLinesStrokeWidth = 2f,
-            netLinesStrokeCap = StrokeCap.Butt
-        ),
-        scalarSteps = 2,
-        scalarValue = 200.0,
-        scalarValuesStyle = scalarValuesStyle,
-        polygons = listOf(
-            Polygon(
-                values = values,
-                unit = "$",
-                style = PolygonStyle(
-                    fillColor = Color(0xffc2ff86),
-                    fillColorAlpha = 0.5f,
-                    borderColor = Color(0xffe6ffd6),
-                    borderColorAlpha = 0.5f,
-                    borderStrokeWidth = 2f,
-                    borderStrokeCap = StrokeCap.Butt,
-                )
-            ),
-            Polygon(
-                values = values2,
-                unit = "$",
-                style = PolygonStyle(
-                    fillColor = Color(0xffFFDBDE),
-                    fillColorAlpha = 0.5f,
-                    borderColor = Color(0xffFF8B99),
-                    borderColorAlpha = 0.5f,
-                    borderStrokeWidth = 2f,
-                    borderStrokeCap = StrokeCap.Butt
-                )
-            )
-        )
-    )
-}