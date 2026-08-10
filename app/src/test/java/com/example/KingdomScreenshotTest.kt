package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.KingdomStateEntity
import com.example.data.UserAccount
import com.example.ui.screens.KingdomScreen
import com.example.ui.theme.EmpireOfKingsTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class KingdomScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun kingdom_screen_screenshot() {
    val user = UserAccount(
      username = "REY_DE_PRUEBA",
      goldCoins = 50000L,
      coronasDiamonds = 300L
    )
    val state = KingdomStateEntity.createDefaultState()

    composeTestRule.setContent {
      EmpireOfKingsTheme {
        KingdomScreen(
          userAccount = user,
          kingdomState = state,
          onNavigate = {},
          onSaveKingdom = {},
          onResetKingdom = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/kingdom_screen.png")
  }
}
