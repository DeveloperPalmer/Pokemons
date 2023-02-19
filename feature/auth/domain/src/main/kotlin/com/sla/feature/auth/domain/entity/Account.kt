package com.sla.feature.auth.domain.entity

data class Account(
  val id: Long,
  val email: String,
  val createdAt: Long = UNKNOWN_CREATED_AT
) {
  companion object {
    const val UNKNOWN_CREATED_AT = 0L
  }
}
