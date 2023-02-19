package com.sla.feature.auth.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.datastore.preferences.core.longPreferencesKey
import com.sla.feature.app.domain.AppScope
import com.sla.feature.auth.data.entity.AccountEntity
import com.sla.feature.auth.data.storage.AccountDao
import com.sla.feature.auth.domain.AuthSessionRepository
import com.sla.feature.auth.domain.entity.Account
import com.sla.feature.auth.domain.entity.AccountSignInTuple
import com.sla.feature.auth.domain.entity.AuthException
import com.sla.feature.auth.domain.entity.AuthSessionState
import com.sla.feature.auth.domain.entity.SignUp
import com.sla.feature.auth.domain.entity.Passwords
import com.sla.feature.core.data.AppPreferences
import com.sla.feature.core.domain.di.SingleIn
import com.sla.feature.core.domain.mapDistinctNotNullChanges
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AuthSessionDataRepository @Inject constructor(
  private val appPreferences: AppPreferences,
  private val accountDao: AccountDao
) : AuthSessionRepository {

  override fun sessionState(): Flow<AuthSessionState> {
    return accountId().map { if (it != null) AuthSessionState.Active else AuthSessionState.Inactive }
  }

  override suspend fun saveAccountId(id: Long) {
    appPreferences.edit { it[prefAccountId] = id }
  }

  override fun accountId(): Flow<Long?> {
    return appPreferences.data.map { d -> d[prefAccountId] }
  }

  override fun account(): Flow<Account?> {
    return sessionState().transformLatest { session ->
      if (session == AuthSessionState.Active) {
        val accountId = accountId().firstOrNull() ?: return@transformLatest emit(null)
        val account = accountDao.account(accountId).map { it?.toAccount() }
        emitAll(account)
      } else {
        emit(null)
      }
    }
  }

  override fun account(id: Long): Flow<Account> {
    return accountDao.account(id).mapDistinctNotNullChanges { it?.toAccount() }
  }

  override suspend fun findAccountId(email: String, password: String): Long {
    val tuple = accountDao.findByEmail(email) ?: throw AuthException.AccountNotFound
    if (tuple.password != password) throw AuthException.AccountNotFound
    return tuple.id
  }

  override suspend fun createAccount(signUp: SignUp) {
    try {
      val entity = AccountEntity.fromSignUpData(signUp)
      accountDao.createAccount(entity)
    } catch (e: SQLiteConstraintException) {
      throw AuthException.AccountAlreadyExistException
    }
  }

  override suspend fun logout() {
    appPreferences.edit { it.remove(prefAccountId) }
  }

  override suspend fun changeEmail(email: String) {
    val accountId = accountId().firstOrNull() ?: throw AuthException.AccountNotFound
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      throw AuthException.IncorrectEmailException
    }
    try {
      if (accountDao.setEmailById(accountId, email) == 0) {
        throw AuthException.AccountAlreadyExistException
      }
    } catch (e: SQLiteConstraintException) {
      throw AuthException.AccountAlreadyExistException
    }
  }

  override suspend fun changePassword(passwords: Passwords) {
    val accountId = accountId().firstOrNull() ?: throw AuthException.AccountNotFound
    val account = accountDao.findById(accountId) ?: throw AuthException.AccountNotFound

    if (account.password != passwords.oldPass) {
      throw AuthException.IncorrectPasswordException
    }

    passwords.validate()

    accountDao.updatePassword(AccountSignInTuple(accountId, passwords.newPass))
  }
}

private val prefAccountId = longPreferencesKey("pref_account_id")
