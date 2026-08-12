package com.aistudio.empireofkings.game.data

data class Weapon3DDefinition(
    val id: String,
    val name: String,
    val category: String,
    val rarity: String,
    val level: Int,
    val modelAsset: String,
    val damage: Int,
    val fireRate: Int,
    val range: Int,
    val accuracy: Int,
    val description: String
)

object Weapon3DCatalog {
    val pilotInfernalDragon = Weapon3DDefinition(
        id = "wpn_dragon_infernal",
        name = "Rifle del Dragón Infernal",
        category = "Rifle mágico",
        rarity = "Mítico",
        level = 15,
        modelAsset = "models/weapons/rifle_dragon_infernal.gltf",
        damage = 99,
        fireRate = 82,
        range = 86,
        accuracy = 91,
        description = "Arma 3D piloto reconstruida desde la referencia aprobada: cuerpo blindado, alas, cabeza dracónica, espinas y núcleo de magma."
    )
}
