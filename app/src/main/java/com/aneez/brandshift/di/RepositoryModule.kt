package com.aneez.brandshift.di

import com.aneez.brandshift.data.repository.IdentityRepositoryImpl
import com.aneez.brandshift.domain.repository.IdentityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding domain repositories to their data-layer implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIdentityRepository(impl: IdentityRepositoryImpl): IdentityRepository
}
