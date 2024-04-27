package edu.mirea.onebeattrue.samsunghack.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import edu.mirea.onebeattrue.samsunghack.data.AuthRepositoryImpl
import edu.mirea.onebeattrue.samsunghack.domain.auth.AuthRepository

@Module
interface DataModule {
    @ApplicationScope
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @ApplicationScope
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}