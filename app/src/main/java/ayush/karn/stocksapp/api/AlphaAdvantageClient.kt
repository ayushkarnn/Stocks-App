package ayush.karn.stocksapp.api

import ayush.karn.stocksapp.utilities.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AlphaAdvantageClient {

    fun getInstance() :Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}