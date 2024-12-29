package com.alexandros.p.gialamas.taxiapp.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity

@Database(entities = [RideEntity::class], version = 1, exportSchema = false)
abstract class TaxiAppDatabase: RoomDatabase() {
    abstract fun rideDao(): RideDao
}
