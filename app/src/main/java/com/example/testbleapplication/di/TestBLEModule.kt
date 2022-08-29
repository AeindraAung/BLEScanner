package com.example.testbleapplication.di

import com.example.testbleapplication.data.BLEDataSource
import com.example.testbleapplication.data.BLEDataSourceImpl
import com.example.testbleapplication.domain.TestBLERepository
import com.example.testbleapplication.domain.TestBLERepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestBLEModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: TestBLERepositoryImpl): TestBLERepository

    @Binds
    @Singleton
    abstract fun bindBLEDataSource(impl: BLEDataSourceImpl): BLEDataSource
}