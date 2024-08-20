package com.example.xftp.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.io.Serializable

@Entity(tableName = "connection_database")
data class ConnectionModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val timestamp: Long = System.currentTimeMillis()
): Serializable

@Dao
interface ConnectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: ConnectionModel): Long
    @Query("SELECT * from connection_database ORDER BY timestamp DESC")
    suspend fun getAllConnections(): List<ConnectionModel>
    @Delete
    suspend fun deleteConnection(connection: ConnectionModel)
}