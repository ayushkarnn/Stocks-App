package ayush.karn.stocksapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import ayush.karn.stocksapp.R
import ayush.karn.stocksapp.adapter.GainAndLoseAdapter
import ayush.karn.stocksapp.adapter.GridSpacingItemDecoration
import ayush.karn.stocksapp.api.AlphaAdvantageClient
import ayush.karn.stocksapp.api.AlphaVantageService
import ayush.karn.stocksapp.databinding.FragmentTopGainersBinding
import ayush.karn.stocksapp.models.Data
import ayush.karn.stocksapp.models.TopGainer
import ayush.karn.stocksapp.repository.StocksDataRepository
import ayush.karn.stocksapp.utilities.Resource
import ayush.karn.stocksapp.viewmodel.StocksViewModel
import ayush.karn.stocksapp.viewmodel.StocksViewModelFactory
import com.google.gson.Gson

/**
 * Fragment to display top gainers in the stock market.
 */
class TopGainersFragment : Fragment(), GainAndLoseAdapter.OnClickListener {

    private var _binding: FragmentTopGainersBinding? = null
    private val binding get() = _binding!!

    private lateinit var gainAdapter: GainAndLoseAdapter
    private lateinit var viewModel: StocksViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val CACHE_KEY = "last_top_gainers"
    private var TAG = "TopGainersFragmentDebug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopGainersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize shared preferences for caching
        setupSharedPreferences()

        // Setup RecyclerView for displaying top gainers
        setupRecyclerView()

        // Fetch data and update cache only if it's the first time the fragment is created
        if (savedInstanceState == null) {
            fetchDataAndUpdateCache()
        }
    }

    override fun onResume() {
        super.onResume()

        // Load data from cache and update UI
        loadDataFromCache()
    }

    /**
     * Initializes SharedPreferences for storing cached data.
     */
    private fun setupSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences("stocks_app_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Sets up RecyclerView with adapter and layout manager.
     */
    private fun setupRecyclerView() {
        gainAdapter = GainAndLoseAdapter(emptyList(), this)
        binding.topGainerRecyclerView.apply {
            adapter = gainAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        binding.topGainerRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
    }

    /**
     * Fetches top gainers data from the API and updates the cache.
     */
    private fun fetchDataAndUpdateCache() {
        val alphaVantageService = AlphaAdvantageClient.getInstance().create(AlphaVantageService::class.java)
        val factory = StocksViewModelFactory(repository = StocksDataRepository(alphaVantageService))
        viewModel = ViewModelProvider(this, factory)[StocksViewModel::class.java]

        viewModel.fetchGainersAndLosers()
        observeGainers()
    }

    /**
     * Observes changes in the top gainers data.
     * Updates UI based on the received data.
     */
    private fun observeGainers() {
        viewModel.gainersAndLosersLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progessHome.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    val topGainers = resource.data?.top_gainers ?: emptyList()
                    binding.progessHome.visibility = View.GONE
                    updateRecyclerView(topGainers)

                    // Cache the data
                    val json = Gson().toJson(topGainers)
                    sharedPreferences.edit().putString(CACHE_KEY, json).apply()

                    Log.d(TAG, "Gainers Success -> $topGainers")
                }
                is Resource.Error -> {
                    val errorMessage = resource.message
                    binding.progessHome.visibility = View.GONE
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Gainers Error -> $errorMessage")
                }
            }
        }
    }

    /**
     * Loads data from cache and updates the UI.
     */
    private fun loadDataFromCache() {
        val cachedDataJson = sharedPreferences.getString(CACHE_KEY, null)
        if (!cachedDataJson.isNullOrEmpty()) {
            val cachedTopGainers = Gson().fromJson(cachedDataJson, Array<TopGainer>::class.java).toList()
            updateRecyclerView(cachedTopGainers)
        }
    }

    /**
     * Updates the RecyclerView with the provided list of top gainers.
     *
     * @param topGainers List of top gainers to display.
     */
    private fun updateRecyclerView(topGainers: List<TopGainer>) {
        val dataList = topGainers.map {
            Data(it.ticker, "$${it.price}", "+${it.change_percentage}")
        }
        gainAdapter.setData(dataList)
    }

    /**
     * Handles click events on RecyclerView items.
     *
     * @param position Clicked item position.
     */
    override fun onItemClick(position: Int) {
        val currStock = gainAdapter.getItem(position)
        val bundle = Bundle().apply {
            putString("ticker", currStock.name)
        }
        findNavController().navigate(R.id.action_topGainerFragment_to_companyDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
