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
    )

    private val byId = entries.associateBy { it.id }
    val all: List<EquipmentDefinition> = entries

    fun definitionFor(id: String): EquipmentDefinition? = byId[id]

    fun statusLabel(id: String): String = when (byId[id]?.status) {
        EquipmentAssetStatus.MODEL_READY_LICENSE_REVIEW -> "EQUIPO 3D EN REVISIÓN"
        EquipmentAssetStatus.MODEL_PENDING, null -> "EQUIPO 3D PENDIENTE"
    }
}
