package com.crlsribeiro.reelcine.di

import com.crlsribeiro.reelcine.data.repository.GroupRepositoryImpl
import com.crlsribeiro.reelcine.data.repository.RecommendationRepositoryImpl
import com.crlsribeiro.reelcine.domain.repository.GroupRepository
import com.crlsribeiro.reelcine.domain.repository.RecommendationRepository
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
