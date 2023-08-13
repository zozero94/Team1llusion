package team.illusion.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import team.illusion.data.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    fun provideSerialization(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }


    @Provides
    fun providerFirebaseDatabase(): FirebaseDatabase = Firebase.database

    @Provides
    fun provideFirebaseAuth() :FirebaseAuth = FirebaseAuth.getInstance()

}
