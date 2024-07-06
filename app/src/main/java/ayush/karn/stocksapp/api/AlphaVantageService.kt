package ayush.karn.stocksapp.api

import ayush.karn.stocksapp.models.CompanyDetails
import ayush.karn.stocksapp.models.GainersAndLosers
import ayush.karn.stocksapp.models.TickerSearch
import ayush.karn.stocksapp.utilities.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageService {

    @GET("query")
    suspend fun getGainersAndLosers(
        @Query("function") function: String = "TOP_GAINERS_LOSERS",
        @Query("apikey") apiKey: String = Constants.API_KEY
    ): Response<GainersAndLosers>

    @GET("query")
    suspend fun getCompanyDetails(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = Constants.API_KEY
    ): Response<CompanyDetails>

    @GET("query")
    suspend fun getTickerSearch(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("apikey") apiKey: String = Constants.API_KEY
    ): Response<TickerSearch>
}
