package ayush.karn.stocksapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayush.karn.stocksapp.models.CompanyDetails
import ayush.karn.stocksapp.models.GainersAndLosers
import ayush.karn.stocksapp.models.TickerSearch
import ayush.karn.stocksapp.repository.StocksDataRepository
import ayush.karn.stocksapp.utilities.Resource
import kotlinx.coroutines.launch

/**
 * ViewModel class for handling data related to stocks app.
 *
 * @property repository The repository that provides access to data operations.
 */
class StocksViewModel(
    private val repository: StocksDataRepository
) : ViewModel() {

    /**
     * LiveData for observing gainers and losers data.
     */
    val gainersAndLosersLiveData: LiveData<Resource<GainersAndLosers>>
        get() = repository.gainersAndLosersLiveData

    /**
     * LiveData for observing company details data.
     */
    val companyOverviewLiveData: LiveData<Resource<CompanyDetails>>
        get() = repository.companyOverviewLiveData

    /**
     * LiveData for observing ticker search results.
     */
    val tickerSearchLiveData: LiveData<Resource<TickerSearch>>
        get() = repository.tickerSearchLiveData

    /**
     * Initiates fetching of gainers and losers data.
     * Uses viewModelScope to launch a coroutine to handle the repository operation.
     */
    fun fetchGainersAndLosers() {
        viewModelScope.launch {
            repository.fetchGainersAndLosers()
        }
    }

    /**
     * Initiates fetching of company details for a specific symbol.
     * Uses viewModelScope to launch a coroutine to handle the repository operation.
     *
     * @param symbol The symbol (ticker) of the company for which details are to be fetched.
     */
    fun fetchCompanyDetails(symbol: String) {
        viewModelScope.launch {
            repository.fetchCompanyDetails(symbol)
        }
    }

    /**
     * Initiates a ticker search based on provided keywords.
     * Uses viewModelScope to launch a coroutine to handle the repository operation.
     *
     * @param keywords Keywords to search for tickers.
     */
    fun searchTicker(keywords: String) {
        viewModelScope.launch {
            repository.searchTicker(keywords)
        }
    }
}
