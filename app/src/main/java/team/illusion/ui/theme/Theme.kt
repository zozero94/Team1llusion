package team.illusion.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

private val DarkColorPalette = darkColors(
    primary = IllusionColor.DefaultGray,
    primaryVariant = Color.White,
    secondary = IllusionColor.IllusionYellow
)

private val LightColorPalette = lightColors(
    primary = IllusionColor.DefaultGray,
    primaryVariant = Color.White,
    secondary = IllusionColor.IllusionYellow

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun Team1llusionTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val configuration = LocalConfiguration.current
    val isTablet = remember(configuration) { configuration.screenWidthDp > 600 }
    val isVertical = remember(configuration) {
        configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    CompositionLocalProvider(
        LocalUseTablet provides isTablet,
        LocalUseVertical provides isVertical
    ) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

val LocalUseTablet = compositionLocalOf { false }
val LocalUseVertical = compositionLocalOf { true }
