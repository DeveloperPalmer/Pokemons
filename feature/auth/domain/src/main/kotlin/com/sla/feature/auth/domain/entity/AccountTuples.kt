package com.sla.feature.auth.domain.entity

data class AccountSignInTuple(
  val id: Long,
  val password: String
)

data class AccountTuple(
  val email: Long,
  val password: String
)
