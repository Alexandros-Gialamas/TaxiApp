package com.alexandros.p.gialamas.taxiapp.di

import com.alexandros.p.gialamas.taxiapp.data.source.remote.KtorHelper
import com.alexandros.p.gialamas.taxiapp.data.source.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.data.source.remote.api.RideServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRideService(rideServiceImpl: RideServiceImpl): RideService {
        return rideServiceImpl
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return KtorHelper.getHttpClient()
    }


}