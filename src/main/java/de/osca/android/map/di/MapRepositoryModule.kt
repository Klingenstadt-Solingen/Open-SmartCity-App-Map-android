package de.osca.android.map.di


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.osca.android.map.data.MapRepositoryImpl
import de.osca.android.map.domain.boundary.MapRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class MapRepositoryModule {
    @Binds
    abstract fun provideMapRepository(repositoryImpl: MapRepositoryImpl): MapRepository
}