package com.pokemons.feature.bottombar.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pokemons.core.ui.LocalScreenConfiguration
import com.pokemons.core.ui.theme.AppTheme
import com.pokemons.feature.core.domain.entity.ScreenRotation

val LocalBottomBarPadding = staticCompositionLocalOf<PaddingValues> {
  error("No BottomBarPadding provided")
}

val LocalBottomBarController = staticCompositionLocalOf<BottomBarController> {
  error("No BottomBarController provided")
}

@Composable
fun MainBottomAppBar(
  controller: BottomBarController,
  modifier: Modifier = Modifier,
) {
  val state by controller.state().collectAsState(BottomBarState())
  val isVisible by controller.isVisible().collectAsState(false)
  val rotation = LocalScreenConfiguration.current.rotation
  AnimatedVisibility(
    modifier = modifier,
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    val tabs = BottomBarSection.values().map { section ->
      Tab(
        id = section.id,
        iconResId = section.iconResId,
        titleResId = section.titleResId,
        isActive = section.isActive(state),
        badge = state.notifications[section]?.toBadge()
      )
    }
    when (rotation) {
      ScreenRotation.PortraitUp,
      ScreenRotation.PortraitDown -> {
        Box(
          modifier = Modifier
            .background(color = AppTheme.colors.white)
            .navigationBarsPadding(),
        ) {
          HorizontalBottomAppBar(
            tabs = tabs,
            onTabClick = { id -> controller.reportSectionClick(id.section) }
          )
        }
      }
      ScreenRotation.LandscapeRight -> {
        VerticalBottomAppBar(
          modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 8.dp, end = 16.dp, bottom = 16.dp),
          tabs = tabs.reversed(),
          onTabClick = { id -> controller.reportSectionClick(id.section) }
        )
      }
      ScreenRotation.LandscapeLeft -> {
        VerticalBottomAppBar(
          modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 8.dp, start = 16.dp, bottom = 16.dp),
          tabs = tabs.reversed(),
          onTabClick = { id -> controller.reportSectionClick(id.section) }
        )
      }
    }
  }
}

private fun Notification.toBadge(): Badge {
  return when (this) {
    is Notification.Alert -> Badge.Alert
    is Notification.Count -> Badge.Count(this.count)
  }
}

private fun BottomBarSection.isActive(state: BottomBarState): Boolean =
  this == state.selectedSection

private val Tab.Id.section: BottomBarSection
  get() {
    return when (this.value) {
      BottomBarSection.Pokemons.name -> BottomBarSection.Pokemons
      else -> error("can't find section for id: ${this.value}")
    }
  }

private val BottomBarSection.id: Tab.Id
  get() = Tab.Id(this.name)

private val BottomBarSection.iconResId: Int
  get() {
    return when (this) {
      BottomBarSection.Pokemons -> R.drawable.ic_list_24
    }
  }

private val BottomBarSection.titleResId: Int
  get() {
    return when (this) {
      BottomBarSection.Pokemons -> R.string.pokemons
    }
  }

@Composable
fun BadgedBox(
  icon: @Composable () -> Unit,
  badge: Badge?,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    icon()
    if (badge != null) {
      Badge(badge = badge)
    }
  }
}

@Composable
private fun Badge(
  badge: Badge,
  modifier: Modifier = Modifier
) {
  val minSize = when (badge) {
    is Badge.Alert -> 10.dp
    is Badge.Count -> 12.dp
  }
  Box(
    modifier = modifier
      .offset(x = 13.dp)
      .defaultMinSize(minWidth = minSize, minHeight = minSize)
      .background(
        color = AppTheme.colors.white,
        shape = RoundedCornerShape(40.dp)
      )
      .padding(horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    if (badge is Badge.Count) {
      Text(
        text = if (badge.count > 99) "99+" else badge.count.toString(),
        color = AppTheme.colors.white,
        style = AppTheme.typography.caption4
      )
    }
  }
}

sealed class Badge {
  object Alert : Badge()
  data class Count(val count: Int) : Badge()
}

@Composable
fun HorizontalDivider(
  modifier: Modifier = Modifier,
  color: Color = AppTheme.colors.white,
  thickness: Dp = 1.dp,
  startIndent: Dp = 0.dp,
  endIndent: Dp = 0.dp,
) {
  val indentMod = if (startIndent.value != 0f || endIndent.value != 0f) {
    Modifier.padding(start = startIndent, end = endIndent)
  } else {
    Modifier
  }
  Box(
    modifier.then(indentMod)
      .fillMaxWidth()
      .height(thickness)
      .background(color = color)
  )
}

@Composable
fun VerticalDivider(
  modifier: Modifier = Modifier,
  color: Color = AppTheme.colors.white,
  thickness: Dp = 1.dp,
  topIndent: Dp = 0.dp,
  bottomIndent: Dp = 0.dp,
) {
  val indentMod = if (topIndent.value != 0f || bottomIndent.value != 0f) {
    Modifier.padding(top = topIndent, bottom = bottomIndent)
  } else {
    Modifier
  }
  Box(
    modifier.then(indentMod)
      .fillMaxHeight()
      .height(1.dp)
      .width(thickness)
      .background(color = color)
  )
}

@Composable
fun HorizontalBottomAppBar(
  tabs: List<Tab>,
  onTabClick: (Tab.Id) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(AppTheme.colors.white),
    verticalAlignment = Alignment.CenterVertically
  ) {
    tabs.forEach { tab ->
      val interactionSource = remember { MutableInteractionSource() }
      BottomBarTab(
        modifier = Modifier
          .heightIn(min = 56.dp)
          .weight(1f)
          .clickable(
            interactionSource = interactionSource,
            indication = rememberRipple(bounded = true, radius = 36.dp)
          ) { onTabClick(tab.id) },
        iconResId = tab.iconResId,
        titleResId = tab.titleResId,
        isActive = tab.isActive,
        badge = tab.badge
      )
    }
  }
}

@Composable
fun VerticalBottomAppBar(
  tabs: List<Tab>,
  onTabClick: (Tab.Id) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .background(
        color = AppTheme.colors.white,
        shape = RoundedCornerShape(20.dp),
      )
      .clip(RoundedCornerShape(20.dp)),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    tabs.forEach { tab ->
      val interactionSource = remember { MutableInteractionSource() }
      BottomBarTab(
        modifier = Modifier
          .clickable(
            interactionSource = interactionSource,
            indication = rememberRipple(bounded = true, radius = 36.dp)
          ) { onTabClick(tab.id) }
          .padding(8.dp)
          .weight(1f),
        iconResId = tab.iconResId,
        titleResId = tab.titleResId,
        isActive = tab.isActive,
        badge = tab.badge
      )
    }
  }
}

@Composable
private fun BottomBarTab(
  @DrawableRes
  iconResId: Int,
  titleResId: Int,
  isActive: Boolean,
  modifier: Modifier = Modifier,
  badge: Badge? = null
) {
  val iconTint by animateColorAsState(
    targetValue = if (isActive) {
      AppTheme.colors.white
    } else {
      AppTheme.colors.white
    },
    animationSpec = tween(150)
  )
  val titleColor by animateColorAsState(
    targetValue = if (isActive) {
      AppTheme.colors.textPrimary
    } else {
      AppTheme.colors.white
    },
    animationSpec = tween(150)
  )
  Column(
    modifier = modifier.defaultMinSize(56.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    if (badge != null) {
      BadgedBox(
        icon = {
          Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "bottom bar icon",
            tint = iconTint
          )
        },
        badge = badge
      )
    } else {
      Icon(
        painter = painterResource(id = iconResId),
        contentDescription = "bottom bar icon",
        tint = iconTint
      )
    }

    Text(
      text = stringResource(id = titleResId),
      style = AppTheme.typography.caption1,
      color = titleColor
    )
  }
}

@Immutable
data class Tab(
  val id: Id,
  @DrawableRes
  val iconResId: Int,
  val titleResId: Int,
  val isActive: Boolean,
  val badge: Badge? = null
) {
  @JvmInline
  value class Id(val value: Any)
}



