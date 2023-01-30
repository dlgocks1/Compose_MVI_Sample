package com.example.compose_mvvm_sample.data.di

import com.example.compose_mvvm_sample.BuildConfig
import com.example.compose_mvvm_sample.data.api.GithubService
import com.example.compose_mvvm_sample.data.datasource.GithubDataSource
import com.example.compose_mvvm_sample.data.repository.GithubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun providesOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        val headerInterceptor = Interceptor {
            val request = it.request()
                .newBuilder()
                .build()
            return@Interceptor it.proceed(request)
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun providesGithubService(retrofit: Retrofit): GithubService =
        retrofit.create(GithubService::class.java)

    @Singleton
    @Provides
    fun providesGithubDataSource(githubService: GithubService): GithubDataSource =
        GithubDataSource(githubService)

    @Singleton
    @Provides
    fun providesGithubRepository(githubDataSource: GithubDataSource): GithubRepository =
        GithubRepository(githubDataSource)
}