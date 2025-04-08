package de.osca.android.map.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.osca.android.essentials.data.client.OSCAHttpClient
import de.osca.android.map.data.MapApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MapModule {
    @Singleton
    @Provides
    fun provideMapApiService(oscaHttpClient: OSCAHttpClient): MapApiService =
        oscaHttpClient.create(MapApiService::class.java)

}
