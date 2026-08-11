package com.aistudio.empireofkings.game.data

/**
 * 3D equipment registry kept separate from Room inventory rows.
 *
 * Names and stable IDs can already flow through the existing wardrobe/economy
 * screens, while every model path remains explicit until a real, licensed GLB
 * is supplied. This prevents the avatar GLB from being mislabeled as clothing.
 */
data class EquipmentDefinition(
    val id: String,
    val slot: String,
    val label: String,
    val modelAsset: String? = null,
    val status: EquipmentAssetStatus = EquipmentAssetStatus.MODEL_PENDING,
    val note: String = "Se necesita GLB separado unido al rig humano."
)

enum class EquipmentAssetStatus {
    MODEL_PENDING,
    MODEL_READY_LICENSE_REVIEW
}

object EquipmentCatalog {
    private val entries = listOf(
        EquipmentDefinition("outfit_royal", "Atuendo", "Atuendo del Trono"),
        EquipmentDefinition("outfit_night", "Atuendo", "Guardia Nocturna"),
        EquipmentDefinition("armor_azur", "Armadura", "Coraza de Fuego Azur"),
        EquipmentDefinition("cape_crown", "Capa", "Capa de la Corona"),
        EquipmentDefinition("cape_shadow", "Capa", "Capa de Sombra"),
        EquipmentDefinition("crown_iron", "Corona", "Diadema de Hierro"),
        EquipmentDefinition("amulet_sun", "Accesorio", "Amuleto Solar"),
        EquipmentDefinition("wpn_corona_king", "Arma", "Corona del Rey"),
        EquipmentDefinition("wpn_juicio_dorado", "Arma", "Juicio Dorado"),
        EquipmentDefinition("wpn_escopeta_abismo", "Arma", "Escopeta del Abismo"),
        EquipmentDefinition("wpn_tormenta_cristal", "Arma", "Tormenta de Cristal"),
        EquipmentDefinition("wpn_ojo_leon", "Arma", "Ojo de León"),
        EquipmentDefinition("wpn_baston_flama_azul", "Arma", "Bastón Flama Azul"),
        EquipmentDefinition("wpn_espada_mística", "Arma", "Mandoble Imperial de León")
        EquipmentDefinition("m_tops_01", "Pulóver/top", "Pulóver Imperial"),
        EquipmentDefinition("m_tops_02", "Pulóver/top", "Pulóver Místico"),
        EquipmentDefinition("m_tops_03", "Pulóver/top", "Pulóver Arcano"),
        EquipmentDefinition("m_tops_04", "Pulóver/top", "Pulóver Celestial"),
        EquipmentDefinition("m_tops_05", "Pulóver/top", "Pulóver Eclipse"),
        EquipmentDefinition("m_tops_06", "Pulóver/top", "Pulóver Sombra"),
        EquipmentDefinition("m_tops_07", "Pulóver/top", "Pulóver Fuego"),
        EquipmentDefinition("m_tops_08", "Pulóver/top", "Pulóver Hielo"),
        EquipmentDefinition("m_tops_09", "Pulóver/top", "Pulóver Tormenta"),
        EquipmentDefinition("m_tops_10", "Pulóver/top", "Pulóver Obsidiana"),
        EquipmentDefinition("m_tops_11", "Pulóver/top", "Top Dragón Carmesí"),
        EquipmentDefinition("m_tops_12", "Pulóver/top", "Top Dragón de Hielo"),
        EquipmentDefinition("m_tops_13", "Pulóver/top", "Top Guardián Imperial"),
        EquipmentDefinition("m_tops_14", "Pulóver/top", "Top Príncipe Arcano"),
        EquipmentDefinition("m_tops_15", "Pulóver/top", "Top Caballero Celestial"),
        EquipmentDefinition("m_tops_16", "Pulóver/top", "Top Eclipse Real"),
        EquipmentDefinition("m_tops_17", "Pulóver/top", "Top Sombra Nocturna"),
        EquipmentDefinition("m_tops_18", "Pulóver/top", "Top Fuego del Trono"),
        EquipmentDefinition("m_tops_19", "Pulóver/top", "Top Tormenta Regia"),
        EquipmentDefinition("m_tops_20", "Pulóver/top", "Top Obsidiana"),
        EquipmentDefinition("m_pants_01", "Pantalón", "Pantalón Imperial"),
        EquipmentDefinition("m_pants_02", "Pantalón", "Pantalón Místico"),
        EquipmentDefinition("m_pants_03", "Pantalón", "Pantalón Arcano"),
        EquipmentDefinition("m_pants_04", "Pantalón", "Pantalón Celestial"),
        EquipmentDefinition("m_pants_05", "Pantalón", "Pantalón Eclipse"),
        EquipmentDefinition("m_pants_06", "Pantalón", "Pantalón Sombra"),
        EquipmentDefinition("m_pants_07", "Pantalón", "Pantalón Fuego"),
        EquipmentDefinition("m_pants_08", "Pantalón", "Pantalón Hielo"),
        EquipmentDefinition("m_pants_09", "Pantalón", "Pantalón Tormenta"),
        EquipmentDefinition("m_pants_10", "Pantalón", "Pantalón Obsidiana"),
        EquipmentDefinition("m_pants_11", "Pantalón", "Pantalón Ripped Carmesí"),
        EquipmentDefinition("m_pants_12", "Pantalón", "Pantalón Ripped de Hielo"),
        EquipmentDefinition("m_pants_13", "Pantalón", "Pantalón Dragón Imperial"),
        EquipmentDefinition("m_pants_14", "Pantalón", "Pantalón Arcano Nocturno"),
        EquipmentDefinition("m_pants_15", "Pantalón", "Pantalón Celestial Real"),
        EquipmentDefinition("m_pants_16", "Pantalón", "Pantalón Eclipse Negro"),
        EquipmentDefinition("m_pants_17", "Pantalón", "Pantalón Sombra del Trono"),
        EquipmentDefinition("m_pants_18", "Pantalón", "Pantalón Fuego Imperial"),
        EquipmentDefinition("m_pants_19", "Pantalón", "Pantalón Tormenta Azul"),
        EquipmentDefinition("m_pants_20", "Pantalón", "Pantalón Obsidiana Real"),
        EquipmentDefinition("m_shorts_01", "Short", "Short Imperial"),
        EquipmentDefinition("m_shorts_02", "Short", "Short Místico"),
        EquipmentDefinition("m_shorts_03", "Short", "Short Arcano"),
        EquipmentDefinition("m_shorts_04", "Short", "Short Celestial"),
        EquipmentDefinition("m_shorts_05", "Short", "Short Eclipse"),
        EquipmentDefinition("m_shorts_06", "Short", "Short Sombra"),
        EquipmentDefinition("m_shorts_07", "Short", "Short Fuego"),
        EquipmentDefinition("m_shorts_08", "Short", "Short Hielo"),
        EquipmentDefinition("m_shorts_09", "Short", "Short Tormenta"),
        EquipmentDefinition("m_shorts_10", "Short", "Short Obsidiana"),
        EquipmentDefinition("m_shorts_11", "Short", "Short Ripped Carmesí"),
        EquipmentDefinition("m_shorts_12", "Short", "Short Ripped de Hielo"),
        EquipmentDefinition("m_shorts_13", "Short", "Short Dragón"),
        EquipmentDefinition("m_shorts_14", "Short", "Short Eclipse Real"),
        EquipmentDefinition("m_shorts_15", "Short", "Short del Guardián"),
        EquipmentDefinition("m_culottes_01", "Culotte", "Culotte Imperial"),
        EquipmentDefinition("m_culottes_02", "Culotte", "Culotte Místico"),
        EquipmentDefinition("m_culottes_03", "Culotte", "Culotte Arcano"),
        EquipmentDefinition("m_culottes_04", "Culotte", "Culotte Celestial"),
        EquipmentDefinition("m_culottes_05", "Culotte", "Culotte Eclipse"),
        EquipmentDefinition("m_culottes_06", "Culotte", "Culotte Sombra"),
        EquipmentDefinition("m_culottes_07", "Culotte", "Culotte Fuego"),
        EquipmentDefinition("m_culottes_08", "Culotte", "Culotte Hielo"),
        EquipmentDefinition("m_culottes_09", "Culotte", "Culotte Tormenta"),
        EquipmentDefinition("m_culottes_10", "Culotte", "Culotte Obsidiana"),
        EquipmentDefinition("m_sets_01", "Conjunto", "Conjunto Imperial"),
        EquipmentDefinition("m_sets_02", "Conjunto", "Conjunto Místico"),
        EquipmentDefinition("m_sets_03", "Conjunto", "Conjunto Arcano"),
        EquipmentDefinition("m_sets_04", "Conjunto", "Conjunto Celestial"),
        EquipmentDefinition("m_sets_05", "Conjunto", "Conjunto Eclipse"),
        EquipmentDefinition("m_sets_06", "Conjunto", "Conjunto Sombra"),
        EquipmentDefinition("m_sets_07", "Conjunto", "Conjunto Fuego"),
        EquipmentDefinition("m_sets_08", "Conjunto", "Conjunto Hielo"),
        EquipmentDefinition("m_sets_09", "Conjunto", "Conjunto Tormenta"),
        EquipmentDefinition("m_sets_10", "Conjunto", "Conjunto Obsidiana"),
        EquipmentDefinition("m_armor_01", "Armadura", "Armadura Imperial"),
        EquipmentDefinition("m_armor_02", "Armadura", "Armadura Mística"),
        EquipmentDefinition("m_armor_03", "Armadura", "Armadura Arcana"),
        EquipmentDefinition("m_armor_04", "Armadura", "Armadura Celestial"),
        EquipmentDefinition("m_armor_05", "Armadura", "Armadura Eclipse"),
        EquipmentDefinition("m_armor_06", "Armadura", "Armadura Sombra"),
        EquipmentDefinition("m_armor_07", "Armadura", "Armadura Fuego"),
        EquipmentDefinition("m_armor_08", "Armadura", "Armadura Hielo"),
        EquipmentDefinition("m_armor_09", "Armadura", "Armadura Tormenta"),
        EquipmentDefinition("m_armor_10", "Armadura", "Armadura Obsidiana"),
        EquipmentDefinition("m_capes_01", "Capa", "Capa Imperial"),
        EquipmentDefinition("m_capes_02", "Capa", "Capa Arcana"),
        EquipmentDefinition("m_capes_03", "Capa", "Capa Celestial"),
        EquipmentDefinition("m_capes_04", "Capa", "Capa Eclipse"),
        EquipmentDefinition("m_capes_05", "Capa", "Capa Obsidiana"),
        EquipmentDefinition("m_accessories_01", "Accesorio", "Brazalete Dragón"),
        EquipmentDefinition("m_accessories_02", "Accesorio", "Cinturón Imperial"),
        EquipmentDefinition("m_accessories_03", "Accesorio", "Guantes Arcanos"),
        EquipmentDefinition("m_accessories_04", "Accesorio", "Hombreras Celestiales"),
        EquipmentDefinition("m_accessories_05", "Accesorio", "Faja de Eclipse"),
        EquipmentDefinition("m_accessories_06", "Accesorio", "Collar de Sombra"),
        EquipmentDefinition("m_accessories_07", "Accesorio", "Broche de Fuego"),
        EquipmentDefinition("m_accessories_08", "Accesorio", "Bufanda de Hielo"),
        EquipmentDefinition("m_accessories_09", "Accesorio", "Lentes de Tormenta"),
        EquipmentDefinition("m_accessories_10", "Accesorio", "Medallón Obsidiana"),
    )

    private val byId = entries.associateBy { it.id }
    val all: List<EquipmentDefinition> = entries

    fun definitionFor(id: String): EquipmentDefinition? = byId[id]

    fun statusLabel(id: String): String = when (byId[id]?.status) {
        EquipmentAssetStatus.MODEL_READY_LICENSE_REVIEW -> "EQUIPO 3D EN REVISIÓN"
        EquipmentAssetStatus.MODEL_PENDING, null -> "EQUIPO 3D PENDIENTE"
    }
}
