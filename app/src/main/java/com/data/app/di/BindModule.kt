package com.data.app.di

import com.data.app.data.datasource.BaseDataSource
import com.data.app.data.datasourceImpl.BaseDataSourceImpl
import com.data.app.data.repositoryImpl.BaseRepositoryImpl
import com.data.app.domain.repository.BaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

    @Binds
    @Singleton
    abstract fun bindBaseRepository(baseRepositoryImpl: BaseRepositoryImpl):BaseRepository

    @Binds
    @Singleton
    abstract fun provideBaseDataSource(baseDataSourceImpl: BaseDataSourceImpl):BaseDataSource
}