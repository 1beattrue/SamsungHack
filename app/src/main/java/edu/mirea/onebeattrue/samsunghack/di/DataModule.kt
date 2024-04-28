package edu.mirea.onebeattrue.samsunghack.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import edu.mirea.onebeattrue.samsunghack.data.auth.AuthRepositoryImpl
import edu.mirea.onebeattrue.samsunghack.data.realtimedb.RealtimeDbRepositoryImpl
import edu.mirea.onebeattrue.samsunghack.domain.auth.AuthRepository
import edu.mirea.onebeattrue.samsunghack.domain.realtimedb.RealtimeDbRepository

@Module
interface DataModule {
    @ApplicationScope
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @ApplicationScope
    @Binds
    fun bindDbRepository(impl: RealtimeDbRepositoryImpl): RealtimeDbRepository

    companion object {
        @ApplicationScope
        @Provides
        fun provideFirebaseDatabaseReference(): DatabaseReference =
            FirebaseDatabase.getInstance().reference

        @ApplicationScope
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}