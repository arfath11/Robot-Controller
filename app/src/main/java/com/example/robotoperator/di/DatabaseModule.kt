package com.example.robotoperator.di

import android.content.Context
import com.example.robotoperator.data.db.AppDatabase
import com.example.robotoperator.data.db.PointVertexDao
import com.example.robotoperator.data.repository.PointCloudRepositoryImpl
import com.example.robotoperator.domain.repository.PointCloudRepository
import com.example.robotoperator.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun providePointVertexDao(database: AppDatabase): PointVertexDao {
        return database.pointVertexDao()
    }
    
    @Provides
    @Singleton
    fun providePointCloudRepository(pointVertexDao: PointVertexDao): PointCloudRepository {
        return PointCloudRepositoryImpl(pointVertexDao)
    }
    
    @Provides
    fun provideSavePointCloudUseCase(repository: PointCloudRepository): SavePointCloudUseCase {
        return SavePointCloudUseCase(repository)
    }
    
    @Provides
    fun provideGetPointCloudUseCase(repository: PointCloudRepository): GetPointCloudUseCase {
        return GetPointCloudUseCase(repository)
    }
    
    @Provides
    fun provideGetAllAnnotationTypesUseCase(repository: PointCloudRepository): GetAllAnnotationTypesUseCase {
        return GetAllAnnotationTypesUseCase(repository)
    }
    
    @Provides
    fun provideDeletePointCloudUseCase(repository: PointCloudRepository): DeletePointCloudUseCase {
        return DeletePointCloudUseCase(repository)
    }
    
    @Provides
    fun provideDeleteAllPointCloudsUseCase(repository: PointCloudRepository): DeleteAllPointCloudsUseCase {
        return DeleteAllPointCloudsUseCase(repository)
    }
} 