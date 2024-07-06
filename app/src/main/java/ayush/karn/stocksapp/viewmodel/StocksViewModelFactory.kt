package ayush.karn.stocksapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ayush.karn.stocksapp.api.AlphaVantageService
import ayush.karn.stocksapp.repository.StocksDataRepository

class StocksViewModelFactory(
    private val repository: StocksDataRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StocksViewModel::class.java)) {
            return StocksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
