package com.alexandros.p.gialamas.taxiapp.di

import android.content.Context
import androidx.room.Room
import com.alexandros.p.gialamas.taxiapp.data.source.local.database.TaxiAppDatabase
import com.alexandros.p.gialamas.taxiapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{

    @Provides
    @Singleton
    fun provideTaxiAppDatabase(@ApplicationContext context: Context): TaxiAppDatabase {
        return Room.databaseBuilder(
            context = context,
            TaxiAppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideRideDao(taxiAppDatabase: TaxiAppDatabase) = taxiAppDatabase.rideDao()

}