@@ .. @@
 import org.babetech.borastock.domain.usecase.UpdateStockItemUseCase
 import org.babetech.borastock.domain.usecase.UpdateSupplierUseCase
 import org.babetech.borastock.ui.screens.auth.viewmodel.LoginViewModel
 import org.babetech.borastock.ui.screens.screennavigation.AccueilViewModel
 import org.babetech.borastock.ui.screens.screennavigation.Entries.EntriesViewModel
 import org.babetech.borastock.ui.screens.screennavigation.StockViewModel
 import org.babetech.borastock.ui.screens.screennavigation.exits.ExitsViewModel
 import org.babetech.borastock.ui.screens.screennavigation.suppliers.SuppliersViewModel
 import org.babetech.borastock.ui.screens.setup.viewmodel.CompanySetupViewModel
+import org.babetech.borastock.ui.screens.dashboard.GraphicViewModel
 import org.koin.core.module.dsl.viewModelOf
 import org.koin.dsl.module

 val appModule = module {

     // Dispatcher
   single { IODispatcher }

     // DataStore multiplateforme
     single<DataStore<Preferences>> { provideDataStore() }

     // Repositories
     single<BoraStockRepository> { BoraStockRepositoryImpl(datastore = get(), datastoreUser = get()) }
     single<StockRepository> { StockRepositoryImpl(dispatcher = get()) }

     // SQLDelight
    single { provideDriver() }
    single { AppDatabase(driver = get()) }

     // Auth Manager Google
     single<GoogleAuthManager> { KMAuthGoogle.googleAuthManager }

     // Use Cases – User
     factory { GetCurrentUserUseCase(repository = get<BoraStockRepository>()) }
     factory { SetCurrentUserUseCase(repository = get<BoraStockRepository>()) }

     // Use Cases – Stock Items
     factory { GetAllStockItemsUseCase(repository = get<StockRepository>()) }
     factory { GetStockItemByIdUseCase(repository = get<StockRepository>()) }
     factory { CreateStockItemUseCase(repository = get<StockRepository>()) }
     factory { UpdateStockItemUseCase(repository = get<StockRepository>()) }
     factory { DeleteStockItemUseCase(repository = get<StockRepository>()) }

     // Use Cases – Stock Entries
     factory { GetAllStockEntriesUseCase(repository = get<StockRepository>()) }
     factory { GetStockEntriesByItemIdUseCase(repository = get<StockRepository>()) }
     factory { AddStockEntryUseCase(repository = get<StockRepository>()) }
     factory { UpdateStockEntryUseCase(repository = get<StockRepository>()) }
     factory { DeleteStockEntryUseCase(repository = get<StockRepository>()) }

     // Use Cases – Stock Exits
     factory { GetAllStockExitsUseCase(repository = get<StockRepository>()) }
     factory { GetStockExitsByItemIdUseCase(repository = get<StockRepository>()) }
     factory { AddStockExitUseCase(repository = get<StockRepository>()) }
     factory { UpdateStockExitUseCase(repository = get<StockRepository>()) }
     factory { DeleteStockExitUseCase(repository = get<StockRepository>()) }

     // Use Cases – Suppliers
     factory { GetAllSuppliersUseCase(repository = get<StockRepository>()) }
     factory { GetSupplierByIdUseCase(repository = get<StockRepository>()) }
     factory { CreateSupplierUseCase(repository = get<StockRepository>()) }
     factory { UpdateSupplierUseCase(repository = get<StockRepository>()) }
     factory { DeleteSupplierUseCase(repository = get<StockRepository>()) }


     factory { GetSupplierStatisticsUseCase(repository = get<StockRepository>()) }

     // Use Cases – Statistics
     factory { GetStockStatisticsUseCase(repository = get<StockRepository>()) }
     factory { GetRecentMovementsUseCase(repository = get<StockRepository>()) }

+    // Use Cases – Charts
+    factory { GetStockEvolutionUseCase(repository = get<StockRepository>()) }
+    factory { GetCategoryDistributionUseCase(repository = get<StockRepository>()) }
+    factory { GetSupplierPerformanceUseCase(repository = get<StockRepository>()) }
+    factory { GetLowStockAlertsUseCase(repository = get<StockRepository>()) }
+    factory { GetMonthlyTrendsUseCase(repository = get<StockRepository>()) }
+
     // ViewModels
     viewModelOf(::LoginViewModel)
     viewModelOf(::CompanySetupViewModel)
     viewModelOf(::EntriesViewModel)
     viewModelOf(::ExitsViewModel)
     viewModelOf(::StockViewModel)
     viewModelOf(::AccueilViewModel)
+    viewModelOf(::GraphicViewModel)



     viewModelOf(::SuppliersViewModel)

     factory { GetStockMovementsUseCase(get()) }
-    factory { GetStockStatisticsUseCase(get()) }
     factory { GetSuppliersDistributionUseCase(get()) }



 }