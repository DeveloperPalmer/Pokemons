package com.sla.feature.profile.domain.mapper

import com.sla.feature.auth.domain.entity.Account
import com.sla.feature.profile.domain.entity.Profile

fun Account.toProfile(): Profile {
  return Profile(email = email)
}