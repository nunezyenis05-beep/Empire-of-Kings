package com.aistudio.empireofkings.game.data

/** Local creator/admin policy surface; persistence/authority remains server-owned. */
object CatalogAdminActions {
    /** Validates a bounded batch before an admin action reaches persistence/server code. */
    fun validateBatch(itemIds: List<String>): Boolean =
        itemIds.size <= InventoryRules.MAX_CATALOG_BATCH &&
            itemIds.all { it.isNotBlank() } &&
            itemIds.distinct().size == itemIds.size

    /** Only an unassigned catalog definition may be assigned to a player slot. */
    fun canAssign(definition: EquipmentDefinition): Boolean =
        definition.availability == CatalogAvailability.ASSIGNABLE

    /** Publishing is a one-way transition from assignable to diamond-sale state. */
    fun canPublishForDiamondSale(definition: EquipmentDefinition): Boolean =
        definition.availability == CatalogAvailability.ASSIGNABLE &&
            !definition.publishedForDiamondSale

    /** Retiring an already-retired definition is not a valid state transition. */
    fun canRetire(definition: EquipmentDefinition): Boolean =
        definition.availability != CatalogAvailability.RETIRED
}
