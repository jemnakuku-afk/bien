@@ .. @@
 import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
 import androidx.compose.runtime.*
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.draw.shadow
 import androidx.compose.ui.graphics.Brush
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.text.style.TextAlign
 import androidx.compose.ui.unit.dp
 import borastock.composeapp.generated.resources.*
 import kotlinx.coroutines.launch
 import org.babetech.borastock.data.models.ChartPeriod
 import org.babetech.borastock.data.models.StatisticCard
 import org.babetech.borastock.data.models.TopProduct
+import org.babetech.borastock.ui.screens.dashboard.GraphicSwitcherScreen
+import org.babetech.borastock.ui.screens.dashboard.GraphicViewModel
 import org.jetbrains.compose.resources.painterResource
+import org.koin.compose.viewmodel.koinViewModel


 @OptIn(ExperimentalMaterial3AdaptiveApi::class)
 @Composable
-fun StatistiqueScreen() {
+fun StatistiqueScreen(graphicViewModel: GraphicViewModel = koinViewModel()) {
     val navigator = rememberSupportingPaneScaffoldNavigator()
     val scope = rememberCoroutineScope()

     var selectedPeriod by remember { mutableStateOf("7j") }
     var selectedChart by remember { mutableStateOf("Ventes") }

     // Données d'exemple
     val statisticCards = remember {
         listOf(
             StatisticCard(
                 title = "Chiffre d'affaires",
                 value = "€45,230",
                 change = "+12.5%",
                 isPositive = true,
                 icon = Res.drawable.euro,
                 color = Color(0xFF22c55e)
             ),
             StatisticCard(
                 title = "Commandes",
                 value = "1,247",
                 change = "+8.2%",
                 isPositive = true,
                 icon =Res.drawable.shoppingcart,
                 color = Color(0xFF3b82f6)
             ),
             StatisticCard(
                 title = "Produits vendus",
                 value = "3,891",
                 change = "+15.3%",
                 isPositive = true,
                 icon =Res.drawable.inventory,
                 color = Color(0xFFf59e0b)
             ),
             StatisticCard(
                 title = "Clients actifs",
                 value = "892",
                 change = "-2.1%",
                 isPositive = false,
                 icon = Res.drawable.person,
                 color = Color(0xFFef4444)
             )
         )
     }

     val periods = listOf(
         ChartPeriod("7 jours", "7j"),
         ChartPeriod("30 jours", "30j"),
         ChartPeriod("3 mois", "3m"),
         ChartPeriod("1 an", "1a")
     )

     val topProducts = remember {
         listOf(
             TopProduct("iPhone 15 Pro", "Électronique", 45, 53955.0),
             TopProduct("Samsung Galaxy S24", "Électronique", 32, 28800.0),
             TopProduct("MacBook Air M3", "Informatique", 18, 23400.0),
             TopProduct("AirPods Pro", "Accessoires", 67, 16750.0),
             TopProduct("Dell XPS 13", "Informatique", 12, 12000.0)
         )
     }

     SupportingPaneScaffold(
         value = navigator.scaffoldValue,
         directive = navigator.scaffoldDirective,
         mainPane = {
             AnimatedPane {
                 LazyColumn(
                     modifier = Modifier
                         .fillMaxSize()
                         .background(
                             brush = Brush.verticalGradient(
                                 colors = listOf(
                                     MaterialTheme.colorScheme.surface,
                                     MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                                 )
                             )
                         ),
                     contentPadding = PaddingValues(16.dp),
                     verticalArrangement = Arrangement.spacedBy(16.dp)
                 ) {
                     // En-tête
                     item {
                         StatisticsHeader()
                     }

                     // Cartes de statistiques
                     item {
                         StatisticsCardsGrid(statisticCards)
                     }

                     // Sélecteur de période
                     item {
                         PeriodSelector(
                             periods = periods,
                             selectedPeriod = selectedPeriod,
                             onPeriodSelected = { selectedPeriod = it }
                         )
                     }

-                    // Bouton pour afficher les graphiques détaillés
+                    // Aperçu graphique intégré
                     item {
-                        Button(
-                            onClick = {
-                                scope.launch {
-                                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
-                                }
-                            },
+                        Card(
                             modifier = Modifier
                                 .fillMaxWidth()
-                                .height(48.dp),
-                            shape = RoundedCornerShape(12.dp),
-                            colors = ButtonDefaults.buttonColors(
-                                containerColor = MaterialTheme.colorScheme.primaryContainer
-                            )
+                                .height(300.dp),
+                            shape = RoundedCornerShape(16.dp)
                         ) {
-                            Row(
-                                horizontalArrangement = Arrangement.spacedBy(8.dp),
-                                verticalAlignment = Alignment.CenterVertically
+                            Column(
+                                modifier = Modifier.padding(16.dp),
+                                verticalArrangement = Arrangement.spacedBy(12.dp)
                             ) {
-                                Icon(
-                                    painterResource(Res.drawable.analytics),
-                                    contentDescription = null,
-                                    modifier = Modifier.size(18.dp)
+                                Row(
+                                    modifier = Modifier.fillMaxWidth(),
+                                    horizontalArrangement = Arrangement.SpaceBetween,
+                                    verticalAlignment = Alignment.CenterVertically
+                                ) {
+                                    Text(
+                                        "Aperçu graphique",
+                                        style = MaterialTheme.typography.titleMedium.copy(
+                                            fontWeight = FontWeight.Bold
+                                        )
+                                    )
+                                    TextButton(
+                                        onClick = {
+                                            scope.launch {
+                                                navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
+                                            }
+                                        }
+                                    ) {
+                                        Text("Voir plus")
+                                    }
+                                }
+                                Box(
+                                    modifier = Modifier
+                                        .fillMaxWidth()
+                                        .weight(1f)
+                                ) {
+                                    // Mini chart preview
+                                    GraphicSwitcherScreen(graphicViewModel)
+                                }
+                            }
+                        }
+                    }
+
+                    // Bouton pour afficher les graphiques détaillés
+                    item {
+                        Button(
+                            onClick = {
+                                scope.launch {
+                                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
+                                }
+                            },
+                            modifier = Modifier
+                                .fillMaxWidth()
+                                .height(48.dp),
+                            shape = RoundedCornerShape(12.dp),
+                            colors = ButtonDefaults.buttonColors(
+                                containerColor = MaterialTheme.colorScheme.primaryContainer
+                            )
+                        ) {
+                            Row(
+                                horizontalArrangement = Arrangement.spacedBy(8.dp),
+                                verticalAlignment = Alignment.CenterVertically
+                            ) {
+                                Icon(
+                                    painterResource(Res.drawable.analytics),
+                                    contentDescription = null,
+                                    modifier = Modifier.size(18.dp)
                                 )
                                 Text(
-                                    "Voir les graphiques détaillés",
+                                    "Analyse détaillée",
                                     style = MaterialTheme.typography.labelLarge.copy(
                                         fontWeight = FontWeight.Medium
                                     )
                                 )
                             }
                         }
                     }

                     // Top produits
                     item {
                         TopProductsSection(topProducts)
                     }
                 }
             }
         },
         supportingPane = {
             AnimatedPane {
-                DetailedAnalyticsPane(
-                    selectedChart = selectedChart,
-                    onChartSelected = { selectedChart = it },
-                    selectedPeriod = selectedPeriod,
+                GraphiquesDetailsScreen(
+                    graphicViewModel = graphicViewModel,
                     onBackClick = {
                         scope.launch {
                             navigator.navigateBack()
                         }
                     },
                     showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded
                 )
             }
         }
     )
 }

+@OptIn(ExperimentalMaterial3Api::class)
+@Composable
+private fun GraphiquesDetailsScreen(
+    graphicViewModel: GraphicViewModel,
+    onBackClick: () -> Unit,
+    showBackButton: Boolean
+) {
+    Scaffold(
+        topBar = {
+            TopAppBar(
+                title = { Text("Graphiques détaillés") },
+                navigationIcon = {
+                    if (showBackButton) {
+                        IconButton(onClick = onBackClick) {
+                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
+                        }
+                    }
+                },
+                actions = {
+                    IconButton(onClick = { graphicViewModel.refreshData() }) {
+                        Icon(
+                            painter = painterResource(Res.drawable.analytics),
+                            contentDescription = "Actualiser"
+                        )
+                    }
+                }
+            )
+        }
+    ) { paddingValues ->
+        Box(
+            modifier = Modifier
+                .fillMaxSize()
+                .padding(paddingValues)
+        ) {
+            GraphicSwitcherScreen(graphicViewModel)
+        }
+    }
+}
+
 @Composable
 private fun StatisticsHeader() {
     Card(
         modifier = Modifier
             .fillMaxWidth()
             .shadow(
                 elevation = 4.dp,
                 shape = RoundedCornerShape(16.dp)
             ),
         shape = RoundedCornerShape(16.dp),
         colors = CardDefaults.cardColors(
             containerColor = MaterialTheme.colorScheme.surface
         )
     ) {
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .background(
                     brush = Brush.horizontalGradient(
                         colors = listOf(
                             MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                             MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                         )
                     ),
                     shape = RoundedCornerShape(16.dp)
                 )
                 .padding(20.dp)
         ) {
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.spacedBy(12.dp)
             ) {
                 Box(
                     modifier = Modifier
                         .size(48.dp)
                         .clip(RoundedCornerShape(12.dp))
                         .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                     contentAlignment = Alignment.Center
                 ) {
                     Icon(
                         painter = painterResource(Res.drawable.analytics),
                         contentDescription = null,
                         tint = MaterialTheme.colorScheme.primary,
                         modifier = Modifier.size(24.dp)
                     )
                 }
                 Column {
                     Text(
                         text = "Statistiques & Analytics",
                         style = MaterialTheme.typography.headlineSmall.copy(
                             fontWeight = FontWeight.Bold
                         ),
                         color = MaterialTheme.colorScheme.onSurface
                     )
                     Text(
                         text = "Analyse des performances de votre business",
                         style = MaterialTheme.typography.bodyMedium,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                 }
             }
         }
     }
 }

@@ .. @@
         )
     }
 }

-@OptIn(ExperimentalMaterial3Api::class)
-@Composable
-private fun DetailedAnalyticsPane(
-    selectedChart: String,
-    onChartSelected: (String) -> Unit,
-    selectedPeriod: String,
-    onBackClick: () -> Unit,
-    showBackButton: Boolean
-) {
-    val scrollState = rememberScrollState()
-    val chartTypes = listOf("Ventes", "Revenus", "Commandes", "Clients")
-
-    Scaffold(
-        topBar = {
-            TopAppBar(
-                title = { Text("Analytics détaillées") },
-                navigationIcon = {
-                    if (showBackButton) {
-                        IconButton(onClick = onBackClick) {
-                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
-                        }
-                    }
-                }
-            )
-        }
-    ) { paddingValues ->
-        Column(
-            modifier = Modifier
-                .fillMaxSize()
-                .padding(paddingValues)
-                .padding(16.dp)
-                .verticalScroll(scrollState),
-            verticalArrangement = Arrangement.spacedBy(16.dp)
-        ) {
-            // Sélecteur de type de graphique
-            Card(
-                modifier = Modifier.fillMaxWidth(),
-                shape = RoundedCornerShape(12.dp)
-            ) {
-                Column(
-                    modifier = Modifier.padding(16.dp),
-                    verticalArrangement = Arrangement.spacedBy(12.dp)
-                ) {
-                    Text(
-                        text = "Type d'analyse",
-                        style = MaterialTheme.typography.titleMedium.copy(
-                            fontWeight = FontWeight.Bold
-                        )
-                    )
-
-                    LazyRow(
-                        horizontalArrangement = Arrangement.spacedBy(8.dp)
-                    ) {
-                        items(chartTypes) { type ->
-                            FilterChip(
-                                onClick = { onChartSelected(type) },
-                                label = { Text(type) },
-                                selected = selectedChart == type,
-                                colors = FilterChipDefaults.filterChipColors(
-                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
-                                    selectedLabelColor = MaterialTheme.colorScheme.primary
-                                )
-                            )
-                        }
-                    }
-                }
-            }
-
-            // Zone pour les graphiques (placeholder)
-            Card(
-                modifier = Modifier
-                    .fillMaxWidth()
-                    .height(300.dp),
-                shape = RoundedCornerShape(16.dp)
-            ) {
-                Box(
-                    modifier = Modifier.fillMaxSize(),
-                    contentAlignment = Alignment.Center
-                ) {
-                    Column(
-                        horizontalAlignment = Alignment.CenterHorizontally,
-                        verticalArrangement = Arrangement.spacedBy(8.dp)
-                    ) {
-                        Icon(
-                            painter = painterResource(Res.drawable.analytics),
-                            contentDescription = null,
-                            modifier = Modifier.size(48.dp),
-                            tint = MaterialTheme.colorScheme.primary
-                        )
-                        Text(
-                            text = "Graphique $selectedChart",
-                            style = MaterialTheme.typography.titleMedium,
-                            textAlign = TextAlign.Center
-                        )
-                        Text(
-                            text = "Période: $selectedPeriod",
-                            style = MaterialTheme.typography.bodyMedium,
-                            color = MaterialTheme.colorScheme.onSurfaceVariant,
-                            textAlign = TextAlign.Center
-                        )
-                    }
-                }
-            }
-
-            // Métriques détaillées
-            Card(
-                modifier = Modifier.fillMaxWidth(),
-                shape = RoundedCornerShape(16.dp)
-            ) {
-                Column(
-                    modifier = Modifier.padding(16.dp),
-                    verticalArrangement = Arrangement.spacedBy(12.dp)
-                ) {
-                    Text(
-                        text = "Métriques détaillées",
-                        style = MaterialTheme.typography.titleMedium.copy(
-                            fontWeight = FontWeight.Bold
-                        )
-                    )
-
-                    repeat(4) { index ->
-                        Row(
-                            modifier = Modifier.fillMaxWidth(),
-                            horizontalArrangement = Arrangement.SpaceBetween
-                        ) {
-                            Text(
-                                text = "Métrique ${index + 1}",
-                                style = MaterialTheme.typography.bodyMedium
-                            )
-                            Text(
-                                text = "${(index + 1) * 1234}",
-                                style = MaterialTheme.typography.bodyMedium.copy(
-                                    fontWeight = FontWeight.Bold
-                                ),
-                                color = MaterialTheme.colorScheme.primary
-                            )
-                        }
-                        if (index < 3) {
-                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
-                        }
-                    }
-                }
-            }
-        }
-    }
-}