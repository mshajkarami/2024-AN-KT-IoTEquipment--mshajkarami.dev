package com.irisaco.iotequipment.di.module

import android.content.Context
import com.irisaco.iotequipment.utils.UnsafeConnectionBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    fun provideAuthorizationService(
        @ApplicationContext context: Context
    ): AuthorizationService {
        return AuthorizationService(
            context,
            AppAuthConfiguration.Builder()
                .setConnectionBuilder(UnsafeConnectionBuilder)
                .build()
        )
    }
}


