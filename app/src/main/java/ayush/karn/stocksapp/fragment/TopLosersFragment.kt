package ayush.karn.stocksapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import ayush.karn.stocksapp.R
import ayush.karn.stocksapp.adapter.GainAndLoseAdapter
import ayush.karn.stocksapp.adapter.GridSpacingItemDecoration
import ayush.karn.stocksapp.api.AlphaAdvantageClient
import ayush.karn.stocksapp.api.AlphaVantageService
import ayush.karn.stocksapp.databinding.FragmentTopLosersBinding
import ayush.karn.stocksapp.models.Data
import ayush.karn.stocksapp.models.TopLoser
import ayush.karn.stocksapp.repository.StocksDataRepository
import ayush.karn.stocksapp.utilities.Resource
import ayush.karn.stocksapp.viewmodel.StocksViewModel
import ayush.karn.stocksapp.viewmodel.StocksViewModelFactory
import com.google.gson.Gson

/**
 * Fragment to display top losers in stock market.
 */
class TopLosersFragment : Fragment(), GainAndLoseAdapter.OnClickListener {

    private var _binding: FragmentTopLosersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StocksViewModel
    private lateinit var loseAdapter: GainAndLoseAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val CACHE_KEY = "last_top_losers"

    private var TAG = "TopLosersFragmentDebug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTopLosersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup shared preferences for caching
        setupSharedPreferences()

        // Setup recycler view for displaying top losers
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
     * Initialize shared preferences for storing cached data.
     */
    private fun setupSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences("stocks_app_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Setup recycler view with adapter and layout manager.
     */
    private fun setupRecyclerView() {
        loseAdapter = GainAndLoseAdapter(emptyList(), this)
        binding.topLosersRecyclerView.apply {
            adapter = loseAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        loseAdapter.isForLoseFragment = true
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        binding.topLosersRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
    }

    /**
     * Fetches top losers data from API and updates cache.
     */
    private fun fetchDataAndUpdateCache() {
        val alphaVantageService = AlphaAdvantageClient.getInstance().create(AlphaVantageService::class.java)
        val factory = StocksViewModelFactory(repository = StocksDataRepository(alphaVantageService))
        viewModel = ViewModelProvider(this, factory)[StocksViewModel::class.java]

        viewModel.fetchGainersAndLosers()  // This method is used for both gainers and losers
        observeLosers()
    }

    /**
     * Observes changes in the top losers data.
     * Updates UI based on the data received.
     */
    private fun observeLosers() {
        viewModel.gainersAndLosersLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progessTopLoser.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    val topLosers = resource.data?.top_losers ?: emptyList()
                    binding.progessTopLoser.visibility = View.GONE
                    updateRecyclerView(topLosers)

                    // Cache the data
                    val json = Gson().toJson(topLosers)
                    sharedPreferences.edit().putString(CACHE_KEY, json).apply()

                    Log.d(TAG, "Losers Success-> $topLosers")
                }
                is Resource.Error -> {
                    val errorMessage = resource.message
                    binding.progessTopLoser.visibility = View.GONE
                    Log.d(TAG, "Losers Error-> $errorMessage")
                }
            }
        }
    }

    /**
     * Loads data from cache and updates UI.
     */
    private fun loadDataFromCache() {
        val cachedDataJson = sharedPreferences.getString(CACHE_KEY, null)
        if (!cachedDataJson.isNullOrEmpty()) {
            val cachedTopLosers = Gson().fromJson(cachedDataJson, Array<TopLoser>::class.java).toList()
            updateRecyclerView(cachedTopLosers)
        }
    }

    /**
     * Updates the recycler view with the provided list of top losers.
     *
     * @param topLosers List of top losers to display.
     */
    private fun updateRecyclerView(topLosers: List<TopLoser>) {
        val dataList = topLosers.map {
            Data(it.ticker, "$${it.price}", "${it.change_percentage}")
        }
        loseAdapter.setData(dataList)
    }

    /**
     * Handles click event on recycler view items.
     *
     * @param position Clicked item position.
     */
    override fun onItemClick(position: Int) {
        val currStock = loseAdapter.getItem(position)
        val bundle = Bundle().apply {
            putString("ticker", currStock.name)
        }
        findNavController().navigate(R.id.action_topLosersFragment_to_companyDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
