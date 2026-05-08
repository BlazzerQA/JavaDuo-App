package com.javadu.di

import android.content.Context
import androidx.room.Room
import com.javadu.data.database.AppDatabase
import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.dao.UserDao
import com.javadu.data.database.dao.UserProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "javaduo_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideLessonDao(database: AppDatabase): LessonDao = database.lessonDao()

    @Provides
    fun provideQuestionDao(database: AppDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao = database.userProgressDao()
}
