package com.sla.feature.auth.domain

import com.sla.feature.auth.domain.entity.Account
import com.sla.feature.auth.domain.entity.AuthSessionState
import com.sla.feature.auth.domain.entity.SignUp
import com.sla.feature.auth.domain.entity.Passwords
import kotlinx.coroutines.flow.Flow

interface AuthSessionRepository {

  fun account(): Flow<Account?>

  fun sessionState(): Flow<AuthSessionState>

  suspend fun saveAccountId(id: Long)

  fun accountId(): Flow<Long?>

  fun account(id: Long): Flow<Account>

  suspend fun findAccountId(email: String, password: String): Long

  suspend fun createAccount(signUp: SignUp)
  suspend fun logout()
  suspend fun changeEmail(email: String)
  suspend fun changePassword(passwords: Passwords)
}
