package com.example.testbleapplication.di

import android.content.Context
import com.example.testbleapplication.service.BLEDeviceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Installs AppModule in the generate SingletonComponent.
class AppModule {
    // @Singleton providers are only called once per SingletonComponent instance.
    @Provides
    @Singleton
    fun provideBLEManager(@ApplicationContext context: Context): BLEDeviceManager {
        return BLEDeviceManager(context = context)
    }
}