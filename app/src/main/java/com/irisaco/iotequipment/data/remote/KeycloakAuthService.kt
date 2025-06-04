package com.irisaco.iotequipment.data.remote

import com.irisaco.iotequipment.data.model.KeycloakTokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface KeycloakAuthService {

    @FormUrlEncoded
    @POST("realms/dev/protocol/openid-connect/token")
    suspend fun login(
        @Field("client_id") clientId: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password",
        @Field("client_secret") clientSecret: String
    ): Response<KeycloakTokenResponse>

    // need to fix !!!!!!!!
    @FormUrlEncoded
    @POST("realms/{realm}/protocol/openid-connect/token")
    suspend fun refreshToken(
        @Path("realm") realm: String = "dev",
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = "your_client_id",
        @Field("client_secret") clientSecret: String = "your_client_secret"
    ): Response<KeycloakTokenResponse>


}