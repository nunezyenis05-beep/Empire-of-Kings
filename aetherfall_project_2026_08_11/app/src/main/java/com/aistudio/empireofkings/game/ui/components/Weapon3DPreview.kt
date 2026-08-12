package com.aistudio.empireofkings.game.ui.components

import androidx.compose.foundation.background
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

@Composable
fun Weapon3DPreview(
    assetPath: String,
    modifier: Modifier = Modifier,
    allowCameraGestures: Boolean = true
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val modelInstance = rememberModelInstance(modelLoader, assetPath)
    val cameraManipulator = rememberCameraManipulator(
        orbitHomePosition = Position(x = 0f, y = 0.15f, z = 5.2f),
        targetPosition = Position(x = 0.15f, y = 0.05f, z = 0f)
    )

    Box(
        modifier = modifier.background(Color(0xFF07050A)),
        contentAlignment = Alignment.Center
    ) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
            engine = engine,
            modelLoader = modelLoader,
            cameraManipulator = cameraManipulator.takeIf { allowCameraGestures },
            mainLightNode = rememberMainLightNode(engine) {
                intensity = 120_000.0f
            }
        ) {
            modelInstance?.let {
                ModelNode(
                    modelInstance = it,
                    scaleToUnits = 3.0f,
                    autoAnimate = false
                )
            }
        }

        if (modelInstance == null) {
            Text(
                text = "CARGANDO ARMA 3D…",
                color = Color(0xFFFFB13B),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
