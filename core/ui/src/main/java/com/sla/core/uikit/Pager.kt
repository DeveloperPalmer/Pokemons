@file:OptIn(ExperimentalPagerApi::class)

package com.sla.core.uikit

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@Composable
fun OnboardingPager(
  pagerState: PagerState,
  count: Int,
  currentPage: Int,
  modifier: Modifier = Modifier,
  enableAutoScroll: Boolean = false,
  loopedAutoScroll: Boolean = true,
  autoScrollDelayMs: Long = 4000,
  autoScrollDurationMs: Int = 600,
  onPageChange: (pageIndex: Int) -> Unit,
  page: @Composable PagerScope.(pageIndex: Int) -> Unit
) {
  BoxWithConstraints(modifier = modifier) {
    var autoScrollJob: Job? = null
    LaunchedEffect(currentPage) {
      if (pagerState.currentPage != currentPage) pagerState.animateScrollToPage(currentPage)
    }
    LaunchedEffect(pagerState.currentPage) {
      if (pagerState.currentPage == count - 1 && !loopedAutoScroll) {
        autoScrollJob?.cancel()
      }
      snapshotFlow { pagerState.currentPage }.collect(onPageChange)
    }
    var maxWidth by remember { mutableStateOf(constraints.maxWidth) }
    SideEffect {
      maxWidth = constraints.maxWidth
    }

    LaunchedEffect(Unit) {
      if (enableAutoScroll) {
        autoScrollJob = launch {
          while (true) {
            yield()
            delay(autoScrollDelayMs)
            pagerState.animateScrollBy(
              value = if (pagerState.currentPage == pagerState.pageCount - 1) {
                ((pagerState.pageCount - 1) * maxWidth.toFloat()).unaryMinus()
              } else {
                maxWidth.toFloat()
              },
              animationSpec = tween(durationMillis = autoScrollDurationMs)
            )
          }
        }
      }
    }
    LaunchedEffect(Unit) {
      pagerState.interactionSource.interactions.take(1).collect {
        autoScrollJob?.cancel()
      }
    }
    HorizontalPager(
      count = count,
      state = pagerState,
    ) { index ->
      page(index)
    }
  }
}
