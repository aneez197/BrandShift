package com.aneez.brandshift.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aneez.brandshift.core.dispatcher.DefaultDispatcherProvider
import com.aneez.brandshift.core.dispatcher.DispatcherProvider
import com.aneez.brandshift.core.utils.ResourceProvider
import com.aneez.brandshift.core.utils.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that registers global application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(impl: ResourceProviderImpl): ResourceProvider

    companion object {
        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider {
            return DefaultDispatcherProvider()
        }

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("brandshift_preferences") }
            )
        }
    }
}
