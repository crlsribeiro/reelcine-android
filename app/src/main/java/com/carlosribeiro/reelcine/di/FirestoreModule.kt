package com.carlosribeiro.reelcine.di

import com.carlosribeiro.reelcine.data.repository.GroupRepositoryImpl
import com.carlosribeiro.reelcine.data.repository.RecommendationRepositoryImpl
import com.carlosribeiro.reelcine.domain.repository.GroupRepository
import com.carlosribeiro.reelcine.domain.repository.RecommendationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirestoreModule {

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        groupRepositoryImpl: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindRecommendationRepository(
        recommendationRepositoryImpl: RecommendationRepositoryImpl
    ): RecommendationRepository
}
// WatchlistRepository binding added via separate module
