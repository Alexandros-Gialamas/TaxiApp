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
    suspend fun insertRide(ride: RideEntity){
    }

    @Query("""
            INSERT INTO rides_table (date, customerId, origin, destination, distance, duration, driverId, driverName, value) 
            VALUES (:date, :customerId, :origin, :destination, :distance, :duration, :driverId, :driverName, :value) 
        """)
    suspend fun saveRide(
        date: String?,
        customerId: String,
        origin: String,
        destination: String,
        distance: Double,
        duration: String,
        driverId: Int,
        driverName: String,
        value: Double
    )



    @Query("SELECT * FROM rides_table WHERE customerId = :customerId")
     fun getAllRides(customerId: String): Flow<List<RideEntity>>



    @Query("SELECT * FROM rides_table WHERE driverId = :driverId AND customerId = :customerId")
     fun getLocalRideHistory(customerId: String, driverId: Int?): Flow<List<RideEntity>>


}