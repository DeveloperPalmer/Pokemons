package com.pokemons.core.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pokemons.core.ui.R

private val Roboto = FontFamily(
  Font(R.font.roboto_regular, FontWeight.Normal),
  Font(R.font.roboto_medium, FontWeight.Medium),
  Font(R.font.roboto_bold, FontWeight.Bold),
)

@Immutable
data class AppTypography internal constructor(
  val h0: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Medium,
    fontSize = 68.sp,
  ),
  val h1: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
  ),
  val h2: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
  ),
  val h3: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
  ),
  val h4: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Medium,
    fontSize = 19.sp,
    lineHeight = 24.sp,
  ),
  val title: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 20.sp,
  ),
  val subtitle: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    lineHeight = 16.sp,
  ),
  val body: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 18.sp,
  ),
  val button: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 18.sp,
  ),
  val caption1: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
  ),
  val caption2: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 16.sp,
  ),
  val caption3: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
  ),
  val caption4: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Bold,
    fontSize = 9.sp,
    lineHeight = 8.sp,
  ),
  val caption5: TextStyle = TextStyle(
    fontFamily = Roboto,
    fontWeight = FontWeight.Normal,
    fontSize = 9.sp,
    lineHeight = 12.sp,
  ),
)

/**
 * Used for fallback only and shouldn't be used generally, use [AppTypography] instead.
 */
val MaterialTypography = Typography()

internal val LocalAppTypography = staticCompositionLocalOf { AppTypography() }
