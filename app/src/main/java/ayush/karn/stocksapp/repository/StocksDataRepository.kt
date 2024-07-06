package ayush.karn.stocksapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ayush.karn.stocksapp.api.AlphaVantageService
import ayush.karn.stocksapp.models.CompanyDetails
import ayush.karn.stocksapp.models.GainersAndLosers
import ayush.karn.stocksapp.models.TickerSearch
import ayush.karn.stocksapp.utilities.Resource

/**
 * Repository class responsible for fetching data from the API and exposing it via LiveData.
 *
 * @property alphaVantageService An instance of AlphaVantageService used for API calls.
 */
class StocksDataRepository(
    private val alphaVantageService: AlphaVantageService
) {
    // LiveData instances to hold different types of data
    private val _gainersAndLosersLiveData = MutableLiveData<Resource<GainersAndLosers>>()
    private val _companyOverviewLiveData = MutableLiveData<Resource<CompanyDetails>>()
    private val _tickerSearchLiveData = MutableLiveData<Resource<TickerSearch>>()

    // Public LiveData accessors for observing data changes
    val gainersAndLosersLiveData: LiveData<Resource<GainersAndLosers>>
        get() = _gainersAndLosersLiveData

    val companyOverviewLiveData: LiveData<Resource<CompanyDetails>>
        get() = _companyOverviewLiveData

    val tickerSearchLiveData: LiveData<Resource<TickerSearch>>
        get() = _tickerSearchLiveData

    /**
     * Fetches gainers and losers data from the API.
     * Updates _gainersAndLosersLiveData with Resource.Loading, Resource.Success, or Resource.Error based on API response.
     */
    suspend fun fetchGainersAndLosers() {
        _gainersAndLosersLiveData.postValue(Resource.Loading)
        try {
            val response = alphaVantageService.getGainersAndLosers()
            if (response.isSuccessful) {
                _gainersAndLosersLiveData.postValue(Resource.Success(response.body()))
            } else {
                _gainersAndLosersLiveData.postValue(Resource.Error("Failed to fetch data: ${response.code()}"))
            }
        } catch (e: Exception) {
            _gainersAndLosersLiveData.postValue(Resource.Error("Network error: ${e.message}"))
        }
    }

    /**
     * Fetches company details for a given symbol from the API.
     * Updates _companyOverviewLiveData with Resource.Loading, Resource.Success, or Resource.Error based on API response.
     *
     * @param symbol The symbol (ticker) of the company to fetch details for.
     */
    suspend fun fetchCompanyDetails(symbol: String) {
        _companyOverviewLiveData.postValue(Resource.Loading)
        try {
            val response = alphaVantageService.getCompanyDetails(symbol = symbol)
            if (response.isSuccessful) {
                _companyOverviewLiveData.postValue(Resource.Success(response.body()))
            } else {
                _companyOverviewLiveData.postValue(Resource.Error("Failed to fetch data: ${response.code()}"))
            }
        } catch (e: Exception) {
            _companyOverviewLiveData.postValue(Resource.Error("Network error: ${e.message}"))
        }
    }

    /**
     * Performs a ticker search based on keywords.
     * Updates _tickerSearchLiveData with Resource.Loading, Resource.Success, or Resource.Error based on API response.
     *
     * @param keywords Keywords to search for tickers.
     */
    suspend fun searchTicker(keywords: String) {
        _tickerSearchLiveData.postValue(Resource.Loading)
        try {
            val response = alphaVantageService.getTickerSearch(keywords = keywords)
            if (response.isSuccessful) {
                _tickerSearchLiveData.postValue(Resource.Success(response.body()))
            } else {
                _tickerSearchLiveData.postValue(Resource.Error("Failed to fetch data: ${response.code()}"))
            }
        } catch (e: Exception) {
            _tickerSearchLiveData.postValue(Resource.Error("Network error: ${e.message}"))
        }
    }
}
