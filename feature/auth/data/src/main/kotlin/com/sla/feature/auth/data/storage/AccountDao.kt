package com.sla.feature.auth.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sla.feature.auth.data.entity.AccountEntity
import com.sla.feature.auth.domain.entity.AccountSignInTuple
import com.sla.feature.auth.domain.entity.AccountTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
  @Insert
  suspend fun createAccount(account: AccountEntity)

  @Query("SELECT id, password FROM accounts WHERE email = :email")
  suspend fun findByEmail(email: String): AccountSignInTuple?

  @Query("SELECT email, password FROM accounts WHERE id = :id")
  suspend fun findById(id: Long): AccountTuple?

  @Query("SELECT * FROM accounts WHERE id = :id")
  fun account(id: Long): Flow<AccountEntity?>

  @Query("UPDATE accounts SET email = :email WHERE id = :id AND email != :email")
  fun setEmailById(id: Long, email: String): Int

  @Update(AccountEntity::class)
  fun updatePassword(accountSignInTuple: AccountSignInTuple)
}
