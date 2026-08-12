package com.aistudio.empireofkings.game.data

/** Player-facing inventory and catalog policy. The server contract is intentionally unchanged. */
object InventoryRules {
    const val MAX_PLAYER_SLOTS = 200
    const val INITIAL_ITEMS_PER_CATEGORY = 15
    const val MAX_CATALOG_BATCH = 200
    val selectableCategories = listOf("Mascota", "Atuendo", "Zapato", "Maquillaje", "Peinado", "Arma", "Accesorio")
}

enum class CatalogAvailability { ASSIGNABLE, PUBLISHED_DIAMOND_SALE, RETIRED }
