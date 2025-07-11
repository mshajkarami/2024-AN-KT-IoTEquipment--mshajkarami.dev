package com.irisaco.iotequipment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAccount(): AccountEntity?

    @Query("UPDATE accounts SET isActive = 0")
    suspend fun deactivateAllAccounts()

    @Query("UPDATE accounts SET isActive = 1 WHERE username = :username")
    suspend fun activateAccount(username: String)

    @Query("DELETE FROM accounts WHERE username = :username")
    suspend fun deleteAccount(username: String)

    @Query("SELECT * FROM accounts")
    suspend fun getAllAccounts(): List<AccountEntity>
}



