package com.aistudio.empireofkings.game.ui.components

import androidx.compose.foundation.background
import com.aistudio.empireofkings.game.data.AvatarCatalog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.sceneview.Scene
import io.github.sceneview.SurfaceType
import io.github.sceneview.math.Position
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

/**
 * Native Filament preview for the selected catalog avatar.
 *
 * The model is deliberately kept behind this small surface so the existing
 * Compose screens, navigation and data model do not depend on SceneView. The
 * each catalog entry carries its own source status; this renderer does not turn
 * a pending file into a production identity. SceneView plays the catalogued
 * neutral clip and keeps touch orbit/zoom enabled for the larger previews.
 */
@Composable
fun HumanAvatar3D(
    preset: String,
    modifier: Modifier = Modifier,
    showLoadingLabel: Boolean = true,
    allowCameraGestures: Boolean = false
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val definition = AvatarCatalog.definitionFor(preset)
    val modelInstance = rememberModelInstance(modelLoader, definition.androidModelAsset)
    val rememberedCameraManipulator = rememberCameraManipulator(
        orbitHomePosition = Position(x = 0f, y = 0.75f, z = 2.7f),
        targetPosition = Position(x = 0f, y = 0.8f, z = 0f)
    )
    val cameraManipulator = rememberedCameraManipulator.takeIf { allowCameraGestures }

    Box(
        modifier = modifier.background(avatarAccent(preset)),
        contentAlignment = Alignment.Center
    ) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
            engine = engine,
            modelLoader = modelLoader,
            cameraManipulator = cameraManipulator,
            mainLightNode = rememberMainLightNode(engine) {
                intensity = 100_000.0f
            }
        ) {
            modelInstance?.let { instance ->
                // SceneView starts the first embedded animation when autoAnimate
                // is enabled. This keeps the renderer compatible with the current
                // SceneView API while the catalog continues to document each
                // asset's neutral idle clip.
                ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 2.0f,
                    autoAnimate = true
                )
            }
        }
        if (modelInstance == null && showLoadingLabel) {
            Text(
                text = "CARGANDO AVATAR 3D",
                color = Color(0xFFD9B45A),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun avatarAccent(preset: String): Color = when (preset) {
    "arcane_queen" -> Color(0xFF24153E)
    "royal_guard" -> Color(0xFF122A3B)
    else -> Color(0xFF21182F)
}
