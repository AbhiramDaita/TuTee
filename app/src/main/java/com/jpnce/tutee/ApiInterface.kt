package com.jpnce.tutee


import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @PUT("/v4/spreadsheets/{id}/values/Sheet1!{row}?valueInputOption=USER_ENTERED&key=AIzaSyCTeUin_e2F4IkbTqZ1QF1Z7APRENDDBR8")

    fun updateNotes(@Body body: JsonObject, @Path(value="row",encoded = true) row:String, @Header("Authorization") Authorization: String,@Path(value="id",encoded = true)id:String) : Call<JsonObject>
}
