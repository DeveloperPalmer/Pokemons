package com.sla.feature.pokemons.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.sla.core.ui.R
import com.sla.core.ui.theme.AppTheme
import com.sla.core.uikit.ButtonAppearance
import com.sla.core.uikit.ButtonSize
import com.sla.core.uikit.PrimaryButton
import com.sla.core.uikit.TimePickerTab
import com.sla.core.uikit.surface
import com.sla.feature.pokemons.domain.entity.Pokemon

@Composable
internal fun PokemonItem(
  pokemon: Pokemon,
  onClick: () -> Unit,
  isFavorites: Boolean,
  onFavoritesClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(12.dp)
  Box(
    modifier = modifier
      .border(
        width = 1.dp,
        color = AppTheme.colors.greyLight,
        shape = shape
      )
      .surface(
        elevation = 1.dp,
        backgroundColor = AppTheme.colors.white,
        shape = shape,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      AsyncImage(
        modifier = Modifier.size(124.dp),
        model = ImageRequest.Builder(LocalContext.current)
          .data(pokemon.avatar)
          .decoderFactory(SvgDecoder.Factory())
          .build(),
        contentDescription = null
      )
      Text(
        text = pokemon.name,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.body
      )
    }
    FavoritesButton(
      modifier = Modifier.align(Alignment.TopEnd),
      isActive = isFavorites,
      onClick = onFavoritesClick
    )
  }
}

@Composable
internal fun FavoritesButton(
  isActive: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  IconButton(
    modifier = modifier.padding(top = 4.dp, end = 4.dp),
    onClick = onClick
  ) {
    Icon(
      painter = painterResource(if (isActive) R.drawable.ic_star_full_24 else R.drawable.ic_star_empty_24),
      tint = if (isActive) AppTheme.colors.primary else AppTheme.colors.textPrimary,
      contentDescription = null
    )
  }
}

@Composable
internal fun PokemonTabs(
  currentPage: Int,
  onPageChanges: (Int) -> Unit
) {
  TabRow(
    selectedTabIndex = currentPage,
    backgroundColor = Color.Transparent,
    indicator = { tabPositions ->
      TabRowDefaults.Indicator(
        modifier = Modifier.tabIndicatorOffset(tabPositions[currentPage]),
        height = 2.dp,
        color = AppTheme.colors.primary
      )
    }
  ) {
    TimePickerTab(
      selected = currentPage == 0,
      onClick = { onPageChanges(0) },
      title = stringResource(id = R.string.all_cards_tab),
    )
    TimePickerTab(
      selected = currentPage == 1,
      onClick = { onPageChanges(1) },
      title = stringResource(id = R.string.favorites_tab),
    )
  }
}

@Composable
internal fun TopAppBar(
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
  isAuthorized: Boolean = false,
  onSignIn: () -> Unit,
) {
  Box(
    modifier = modifier.fillMaxWidth(),
  ) {
    if (title != null) {
      Text(
        modifier = Modifier.align(Alignment.Center),
        text = title,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.title
      )
    }
    if (isAuthorized) {
      ProfileButton(
        modifier = Modifier.align(Alignment.CenterEnd),
        onClick = onProfileClick
      )
    } else {
      SignInButton(
        modifier = Modifier.align(Alignment.CenterEnd),
        onClick = onSignIn
      )
    }
  }
}

@Composable
private fun SignInButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  PrimaryButton(
    modifier = modifier,
    onClick = onClick,
    text = stringResource(id = R.string.sign_in),
    size = ButtonSize.Small,
    appearance = ButtonAppearance.Accent
  )
}

@Composable
private fun ProfileButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    modifier = modifier,
    onClick = onClick
  ) {
    Icon(
      painter = painterResource(id = R.drawable.ic_user_24),
      tint = AppTheme.colors.textPrimary,
      contentDescription = null,
    )
  }
}
