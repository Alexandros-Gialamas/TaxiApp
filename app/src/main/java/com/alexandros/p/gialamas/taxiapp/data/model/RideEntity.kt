package com.alexandros.p.gialamas.taxiapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexandros.p.gialamas.taxiapp.util.Constants

@Entity(tableName = Constants.RIDES_TABLE_NAME)
data class RideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String? = null,
    val origin: String,
    val destination: String,
    val distance: Double,
    val duration: String,
    val driverId: Int,
    val driverName: String,
    val value: Double
)


