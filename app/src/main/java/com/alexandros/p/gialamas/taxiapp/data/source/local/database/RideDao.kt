package com.alexandros.p.gialamas.taxiapp.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideEntity)

    @Query("SELECT * FROM rides_table WHERE driverId = :driverId OR :driverId = 0")
    fun getRideHistory(driverId: Int): Flow<List<RideEntity>>

}