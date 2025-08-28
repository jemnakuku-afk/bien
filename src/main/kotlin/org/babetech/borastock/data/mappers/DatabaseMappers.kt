package org.babetech.borastock.data.mappers

import org.babe.sqldelight.data.db.SelectAllStockEntries
import org.babe.sqldelight.data.db.SelectAllStockExits
import org.babe.sqldelight.data.db.SelectAllStockItems
import org.babe.sqldelight.data.db.SelectAllSuppliers
import org.babe.sqldelight.data.db.SelectRecentEntries
import org.babe.sqldelight.data.db.SelectRecentExits
import org.babe.sqldelight.data.db.SelectStockStatistics
import org.babe.sqldelight.data.db.SelectStockEntriesByItemId
import org.babe.sqldelight.data.db.SelectStockExitsByItemId
import org.babetech.borastock.data.models.*

// Mapper for StockItem
fun SelectAllStockItems.toDomainModel(): StockItem {
    return StockItem(
        id = id,
        name = name,
        category = category,
        description = description,
        currentStock = current_stock?.toInt() ?: 0,
        minStock = min_stock.toInt(),
        maxStock = max_stock.toInt(),
        unitPrice = unit_price,
        supplier = Supplier(
            id = supplier_id,
            name = supplier_name,
            category = supplier_category,
            contactPerson = contact_person,
            email = supplier_email,
            phone = supplier_phone,
            address = null, // Not included in the query
            city = null, // Not included in the query
            country = null, // Not included in the query
            rating = supplier_rating?.toFloat() ?: 0f,
            status = SupplierStatus.valueOf(supplier_status ?: "ACTIVE"),
            reliability = SupplierReliability.valueOf(supplier_reliability ?: "AVERAGE"),
            lastOrderDate = null, // Not included in the query
            paymentTerms = null, // Not included in the query
            notes = null, // Not included in the query
            createdAt = created_at,
            updatedAt = updated_at
        ),
        status = StockItemStatus.valueOf(status ?: "ACTIVE"),
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Mapper for StockEntry
fun SelectAllStockEntries.toDomainModel(): StockEntry {
    return StockEntry(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        entryDate = entry_date,
        batchNumber = batch_number,
        expiryDate = expiry_date,
        supplier = supplier_name,
        supplierId = supplier_id,
        status = EntryStatus.valueOf(status ?: "PENDING"),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockEntriesByItemId.toDomainModel(): StockEntry {
    return StockEntry(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        entryDate = entry_date,
        batchNumber = batch_number,
        expiryDate = expiry_date,
        supplier = supplier_name,
        supplierId = supplier_id,
        status = EntryStatus.valueOf(status ?: "PENDING"),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Mapper for StockExit
fun SelectAllStockExits.toDomainModel(): StockExit {
    return StockExit(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        exitDate = exit_date,
        customer = customer,
        orderNumber = order_number,
        deliveryAddress = delivery_address,
        status = ExitStatus.valueOf(status ?: "PENDING"),
        urgency = ExitUrgency.valueOf(urgency ?: "LOW"),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockExitsByItemId.toDomainModel(): StockExit {
    return StockExit(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        exitDate = exit_date,
        customer = customer,
        orderNumber = order_number,
        deliveryAddress = delivery_address,
        status = ExitStatus.valueOf(status ?: "PENDING"),
        urgency = ExitUrgency.valueOf(urgency ?: "LOW"),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Mapper for Supplier
fun SelectAllSuppliers.toDomainModel(): Supplier {
    return Supplier(
        id = id,
        name = name,
        category = category,
        contactPerson = contact_person,
        email = email,
        phone = phone,
        address = address,
        city = city,
        country = country,
        rating = rating?.toFloat() ?: 0f,
        status = SupplierStatus.valueOf(status ?: "ACTIVE"),
        reliability = SupplierReliability.valueOf(reliability ?: "AVERAGE"),
        lastOrderDate = last_order_date,
        paymentTerms = payment_terms,
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Mapper for StockStatistics
fun SelectStockStatistics.toDomainModel(): StockStatistics {
    return StockStatistics(
        totalItems = total_items?.toInt() ?: 0,
        itemsInStock = items_in_stock?.toInt() ?: 0,
        itemsLowStock = items_low_stock?.toInt() ?: 0,
        itemsOutOfStock = items_out_of_stock?.toInt() ?: 0,
        itemsOverstocked = items_overstocked?.toInt() ?: 0,
        totalStockValue = total_stock_value ?: 0.0
    )
}

// Mappers for Recent Movements
fun SelectRecentEntries.toRecentMovement(): RecentMovement {
    return RecentMovement(
        id = id,
        productName = product_name,
        quantity = quantity.toInt(),
        date = entry_date,
        type = MovementType.ENTRY,
        description = "Entrée: ${quantity} x ${product_name} de ${supplier_name}"
    )
}

fun SelectRecentExits.toRecentMovement(): RecentMovement {
    return RecentMovement(
        id = id,
        productName = product_name,
        quantity = quantity.toInt(),
        date = exit_date,
        type = MovementType.EXIT,
        description = "Sortie: ${quantity} x ${product_name} pour ${customer}"
    )
}