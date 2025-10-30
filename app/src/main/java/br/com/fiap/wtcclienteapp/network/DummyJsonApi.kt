package br.com.fiap.wtcclienteapp.network

import retrofit2.http.GET
import retrofit2.http.Query

data class DummyUser(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val age: Int?,
    val gender: String?,
    val ssn: String?
)

data class DummyUsersResponse(val users: List<DummyUser>)

interface DummyJsonApi {
    @GET("users")
    suspend fun listUsers(): DummyUsersResponse

    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): DummyUsersResponse
}


