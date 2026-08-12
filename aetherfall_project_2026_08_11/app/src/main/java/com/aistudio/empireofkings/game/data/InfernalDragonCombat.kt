package com.aistudio.empireofkings.game.data

/** Combat definition for the pilot weapon. Kept separate so the gallery asset and combat stats share one ID. */
data object InfernalDragonCombat {
    const val WEAPON_ID = "wpn_dragon_infernal"
    const val DISPLAY_NAME = "Rifle del Dragón Infernal"
    const val MODEL_ASSET = "models/weapons/rifle_dragon_infernal.gltf"
    const val DAMAGE = 99
    const val FIRE_RATE = 82
    const val RANGE = 86
    const val ACCURACY = 91
    const val PROJECTILE_SPEED = 26f
    const val MUZZLE_FLASH_MS = 120L
    const val IMPACT_RADIUS = 0.35f
}
