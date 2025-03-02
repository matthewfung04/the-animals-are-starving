package com.example.theanimalsarestarving.network

import com.example.theanimalsarestarving.models.Household
import com.example.theanimalsarestarving.models.Pet
import com.example.theanimalsarestarving.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    //router.post("/", createUser);
    @POST("user")
    fun addUser(@Body user: User): Call<User>

    //router.get("/:email", getUser);
    @GET("user/{email}")
    fun getUser(@Path("email") email: String): Call<User>  // Single user response

    @GET("pet/household/{householdId}")
    fun getPets(@Path("householdId") householdId: String): Call<List<Pet>>  // Return a list of pets

    @PATCH("pet/{petId}/feed")
    fun feedPet(
        @Path("petId") petId: String,
        @Body body: Map<String, Boolean> = mapOf("fed" to true)
    ): Call<Pet>

    @POST("household/create")
    suspend fun createHousehold(
        @Body body: Map<String, String>
    ): Response<Household>
}
