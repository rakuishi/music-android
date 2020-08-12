package com.rakuishi.music.di

import android.content.Context
import com.rakuishi.music.data.MusicPlayer
import com.rakuishi.music.data.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideMediaRepository(@ApplicationContext context: Context) = MusicRepository(context)

    @Singleton
    @Provides
    fun provideMusicPlayer(@ApplicationContext context: Context) = MusicPlayer(context)
}