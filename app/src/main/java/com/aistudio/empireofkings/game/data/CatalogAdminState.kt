package com.aistudio.empireofkings.game.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local Room projection of creator/admin catalog transitions. Server authority is unchanged. */
@Entity(tableName = "catalog_admin_state")
data class CatalogAdminState(
    @PrimaryKey val itemId: String,
    val availability: String = CatalogAvailability.ASSIGNABLE.name,
    val publishedForDiamondSale: Boolean = false
)

data class CatalogAdminEntry(
    val definition: EquipmentDefinition,
    val availability: CatalogAvailability,
    val publishedForDiamondSale: Boolean,
    val assignedLocally: Boolean
)
