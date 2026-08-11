package com.aistudio.empireofkings.game.data

/**
 * Stable 3D identity registry shared by profile, wardrobe and online previews.
 *
 * Stable IDs and the online protocol are preserved while independently authored
 * source review remains explicit per avatar. All ten active slots now use separately
 * authored Quaternius CC0 GLBs; Android playback and premium visual acceptance
 * remain manual gates.
 */
data class AvatarDefinition(
    val id: String,
    val label: String,
    val gender: Gender,
    val androidModelAsset: String,
    val sourceStatus: SourceStatus,
    val onlineProtocolPreset: String,
    val idleAnimationName: String = "idle"
)

enum class Gender {
    FEMALE,
    MALE,
    /** Protocol slot is male, but visual validation is still pending. */
    MALE_VISUAL_VALIDATION_PENDING
}

enum class SourceStatus {
    VALIDATED_ANDROID_GLB,
    REAL_HUMAN_VARIANT_FROM_BASE_LICENSE_REVIEW,
    /** GLB is present, but an independently authored distinct human source is missing. */
    DISTINCT_HUMAN_SOURCE_PENDING,
    /** Separately authored source with verified redistribution license and static GLB contract checks. */
    LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED,
    CATALOG_ENTRY_PENDING_MODEL
}

object AvatarCatalog {
    const val FALLBACK_MODEL_ASSET = "models/Xbot.glb"
    private val definitions = listOf(
        AvatarDefinition("maya", "Maya", Gender.FEMALE, "models/avatars/maya.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "female-1", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("sofia", "Sofia", Gender.FEMALE, "models/avatars/sofia.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "female-1", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("amara", "Amara", Gender.FEMALE, "models/avatars/amara.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "female-1", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("elena", "Elena", Gender.FEMALE, "models/avatars/elena.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "female-1", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("nadia", "Nadia", Gender.FEMALE, "models/avatars/nadia.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "female-1", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("leo", "Leo", Gender.MALE, "models/avatars/leo.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-1", idleAnimationName = "Human Armature|Idle"),
        AvatarDefinition("mateo", "Mateo", Gender.MALE, "models/avatars/mateo.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-2", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("karim", "Karim", Gender.MALE, "models/avatars/karim.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-1", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("daniel", "Daniel", Gender.MALE, "models/avatars/daniel.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-2", idleAnimationName = "CharacterArmature|Idle"),
        AvatarDefinition("isaac", "Isaac", Gender.MALE, "models/avatars/isaac.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-1", idleAnimationName = "CharacterArmature|Idle"),

        // Existing persisted IDs remain supported for backwards compatibility.
        AvatarDefinition("king_warrior", "Rey Guerrero", Gender.MALE, "models/avatars/leo.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-1", idleAnimationName = "Human Armature|Idle"),
        AvatarDefinition("royal_guard", "Guardia Real", Gender.MALE, "models/avatars/mateo.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "male-2", idleAnimationName = "CharacterArmature|Idle"),
        // Historical UI ID aliases the first female human variant.
        AvatarDefinition("arcane_queen", "Reina Arcana", Gender.FEMALE, "models/avatars/sofia.glb", SourceStatus.LICENSED_HUMAN_SOURCE_ANDROID_CONTRACT_VALIDATED, "female-1", idleAnimationName = "CharacterArmature|Idle")
    )

    private val byId = definitions.associateBy { it.id }

    val catalogEntries: List<AvatarDefinition> = definitions
    val validIds: Set<String> = byId.keys

    fun definitionFor(id: String?): AvatarDefinition = byId[id] ?: byId.getValue("king_warrior")

    fun androidModelAssetFor(id: String?): String = definitionFor(id).androidModelAsset

    fun onlinePresetFor(id: String?): String = definitionFor(id).onlineProtocolPreset
}
