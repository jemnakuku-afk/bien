@@ .. @@
 import org.babetech.borastock.ui.screens.screennavigation.AccueilUiState
 import org.babetech.borastock.ui.screens.screennavigation.AccueilViewModel
 import org.jetbrains.compose.resources.painterResource
 import org.koin.compose.viewmodel.koinViewModel
+import org.babetech.borastock.domain.usecase.*

 /**
  * A simple data class to represent a dashboard metric.
@@ -158,6 +159,7 @@
                         is AccueilUiState.Success -> {
                             MainDashboardPane(
                                 statistics = state.statistics,
                                 recentMovements = state.recentMovements,
                                 criticalStockItems = state.criticalStockItems,
                                 showChartButton = !showSupporting,
@@ -175,7 +177,7 @@
                         .width(maxWidth * 0.8f)
                         .fillMaxHeight()) {
                         SupportingChartPane(
                             onBack = {
                                 scope.launch { navigator.navigateBack() }
                             }
                         )
@@ -244,6 +246,7 @@
             item { DashboardMetricsGrid(statistics) }
             item { RecentMovementsList(recentMovements) }
             if (criticalStockItems.isNotEmpty()) {
                 item { CriticalStockSection(criticalStockItems) }
             }
+            item { QuickChartPreview() }
             item { QuickActionsSection() }
             if (showChartButton) {
                 item {
@@ -280,6 +283,49 @@
     }
 }

+// --- Quick Chart Preview Composable ---
+@Composable
+fun QuickChartPreview(graphicViewModel: GraphicViewModel = koinViewModel()) {
+    val uiState by graphicViewModel.uiState.collectAsStateWithLifecycle()
+    
+    Card(
+        modifier = Modifier
+            .fillMaxWidth()
+            .shadow(
+                elevation = 4.dp,
+                shape = RoundedCornerShape(16.dp)
+            ),
+        shape = RoundedCornerShape(16.dp),
+        colors = CardDefaults.cardColors(
+            containerColor = MaterialTheme.colorScheme.surface
+        )
+    ) {
+        Column(
+            modifier = Modifier.padding(20.dp),
+            verticalArrangement = Arrangement.spacedBy(16.dp)
+        ) {
+            Text(
+                "Aperçu des tendances",
+                style = MaterialTheme.typography.titleLarge.copy(
+                    fontWeight = FontWeight.Bold
+                ),
+                color = MaterialTheme.colorScheme.onSurface
+            )
+            
+            Box(
+                modifier = Modifier
+                    .fillMaxWidth()
+                    .height(200.dp)
+            ) {
+                if (uiState.isLoading) {
+                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
+                } else {
+                    MonthlyTrendsChart(uiState.monthlyTrends)
+                }
+            }
+        }
+    }
+}
+
 // --- Supporting Chart Pane Composable ---
 @OptIn(ExperimentalMaterial3AdaptiveApi::class)
 @Composable
@@ -310,14 +356,7 @@
             color = MaterialTheme.colorScheme.onSurface
         )
-        // Add your chart composable here
-        Box(
-            modifier = Modifier
-                .fillMaxWidth()
-                .height(300.dp)
-                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
-            contentAlignment = Alignment.Center
-        ) {
-            Text("Graphiques en construction...", color = MaterialTheme.colorScheme.onSurfaceVariant)
+        Box(modifier = Modifier.fillMaxSize()) {
+            GraphicSwitcherScreen()
         }
     }
 }