package com.alexandros.p.gialamas.taxiapp.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.util.Constants

@Database(entities = [RideEntity::class], version = 1, exportSchema = false)
abstract class TaxiAppDatabase: RoomDatabase(){
    abstract fun rideDao(): RideDao

    companion object {
        @Volatile
        private var INSTANCE: TaxiAppDatabase? = null

        fun getDatabase(context: Context): TaxiAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                context.applicationContext,
                TaxiAppDatabase::class.java,
                Constants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}