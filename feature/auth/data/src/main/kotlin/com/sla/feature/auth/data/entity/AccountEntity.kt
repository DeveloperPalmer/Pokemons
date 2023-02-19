package com.sla.feature.auth.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sla.feature.auth.domain.entity.Account
import com.sla.feature.auth.domain.entity.SignUp

@Entity(
  tableName = "accounts",
  indices = [
    Index("email", unique = true)
  ]
)
data class AccountEntity(
  // See NOTE_AUTO_GENERATE_PRIMARY_KEY
  @PrimaryKey(autoGenerate = true)
  val id: Long,
  @ColumnInfo(collate = ColumnInfo.NOCASE)
  val email: String,
  val password: String,
  @ColumnInfo(name = "created_at")
  val createdAt: Long
) {

  fun toAccount(): Account = Account(
    id = id,
    email = email,
    createdAt = createdAt
  )

  companion object {
    fun fromSignUpData(signUp: SignUp): AccountEntity = AccountEntity(
      // See NOTE_AUTO_GENERATE_PRIMARY_KEY
      id = 0L,
      email = signUp.email,
      password = signUp.password,
      createdAt = System.currentTimeMillis()
    )
  }
}

// NOTE_AUTO_GENERATE_PRIMARY_KEY
// Если в качестве идентификатора аккаунта поставить "0" ,
// библиотека "room" сгенерирует ключ автоматически
