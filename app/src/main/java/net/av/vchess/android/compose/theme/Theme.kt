package net.av.vchess.android.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Color(0xff333333),
    onBackground = Color(0xFF9c00ff),
    primary = Color(0xFF555555),
    onPrimary = Color(0xFFffffff),
    primaryContainer = Color(0xFF5500aa),
    onPrimaryContainer = Color(0xFFDDDDDD),
    secondaryContainer = Color(0xff666666),
    onSecondaryContainer = Color(0xffdddddd),
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    background = Color(0xffffffff),
    onBackground = Color(0xffaf00ff),
    primaryContainer = Color(0xFF5500aa),
    onPrimaryContainer = Color(0xFFDDDDDD),
    secondaryContainer = Color(0xffbbbbbb),
    onSecondaryContainer = Color(0xff000000),
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun VChessTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//     Dynamic color is available on Android 12+
//    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}