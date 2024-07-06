package ayush.karn.stocksapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import ayush.karn.stocksapp.activities.MainActivity
import ayush.karn.stocksapp.api.AlphaAdvantageClient
import ayush.karn.stocksapp.api.AlphaVantageService
import ayush.karn.stocksapp.databinding.FragmentCompanyDetailsBinding
import ayush.karn.stocksapp.models.CompanyDetails
import ayush.karn.stocksapp.repository.StocksDataRepository
import ayush.karn.stocksapp.utilities.Resource
import ayush.karn.stocksapp.viewmodel.StocksViewModel
import ayush.karn.stocksapp.viewmodel.StocksViewModelFactory

/**
 * Fragment to display detailed information about a company.
 */
class CompanyDetailsFragment : Fragment() {

    private var _binding: FragmentCompanyDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: CompanyDetailsFragmentArgs by navArgs()
    private lateinit var viewModel: StocksViewModel
    private lateinit var searchAdapter: ArrayAdapter<String>
    private lateinit var mainActivity: MainActivity
    private var selectedSymbol: String? = null
    private var itemSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        initializeViewModel()

        // Setup UI components
        setupUI()

        // Observe company details LiveData
        observeCompanyDetails()

        // Observe ticker search results LiveData
        observeTickerSearchResults()

        // Set up reference to MainActivity and adjust bottom navigation visibility
        activity?.let { act ->
            if (act is MainActivity) {
                mainActivity = act
            } else {
                throw IllegalStateException("Activity must be MainActivity")
            }
        }
        mainActivity.setBottomNavigationVisibility(false)
    }

    /**
     * Initializes ViewModel using ViewModelProvider and ViewModelFactory.
     */
    private fun initializeViewModel() {
        val alphaVantageService = AlphaAdvantageClient.getInstance().create(AlphaVantageService::class.java)
        val factory = StocksViewModelFactory(StocksDataRepository(alphaVantageService))
        viewModel = ViewModelProvider(this, factory).get(StocksViewModel::class.java)
    }

    /**
     * Sets up the UI components including text watcher for ticker search.
     */
    private fun setupUI() {
        // Fetch company details for the provided ticker
        val ticker = args.ticker
        viewModel.fetchCompanyDetails(ticker)

        // Initialize ArrayAdapter for ticker search dropdown
        searchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.companySpinner.setAdapter(searchAdapter)
        binding.companySpinner.visibility = View.GONE // Initially set to GONE

        // TextWatcher for live search as user types
        binding.tickerSearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                itemSelected = false
            }

            override fun afterTextChanged(s: Editable?) {
                val keywords = s.toString().trim()
                if (keywords.isNotEmpty()) {
                    viewModel.searchTicker(keywords)
                } else {
                    binding.companySpinner.dismissDropDown()
                    binding.companySpinner.visibility = View.GONE // Hide when text is empty
                }
            }
        })

        // Show dropdown when spinner gains focus
        binding.companySpinner.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && !binding.companySpinner.isPopupShowing) {
                binding.companySpinner.showDropDown()
            }
        }

        // Handle item selection in spinner
        binding.companySpinner.setOnItemClickListener { parent, view, position, id ->
            val selectedCompany = parent.getItemAtPosition(position) as? String
            selectedCompany?.let {
                val symbol = it.substringAfterLast("(").substringBeforeLast(")")
                selectedSymbol = symbol.trim()
                viewModel.fetchCompanyDetails(selectedSymbol!!)
                itemSelected = true
            }
        }
    }

    /**
     * Observes LiveData for ticker search results.
     * Updates UI based on search results.
     */
    private fun observeTickerSearchResults() {
        viewModel.tickerSearchLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progessDetails.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    resource.data?.let { data ->
                        val searchResults = data.bestMatches.map { "${it._name} (${it._symbol})" }
                        if (searchResults.isNotEmpty() && !itemSelected) {
                            searchAdapter.clear()
                            searchAdapter.addAll(searchResults)
                            searchAdapter.notifyDataSetChanged()
                            binding.companySpinner.post {
                                binding.companySpinner.visibility = View.VISIBLE // Show when there are results
                                binding.companySpinner.showDropDown()
                            }
                        } else {
                            binding.companySpinner.dismissDropDown()
                            binding.companySpinner.visibility = View.GONE // Hide when no results
                        }
                    }
                    binding.progessDetails.visibility = View.GONE
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()
                    binding.progessDetails.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Observes LiveData for company details.
     * Updates UI based on received company details.
     */
    private fun observeCompanyDetails() {
        viewModel.companyOverviewLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progessDetails.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    resource.data?.let { data ->
                        updateUIWithData(data)
                    }
                    binding.progessDetails.visibility = View.GONE
                }
                is Resource.Error -> {
                    showErrorState(resource.message)
                    binding.progessDetails.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Updates UI with company details data.
     *
     * @param data Company details object containing relevant information.
     */
    private fun updateUIWithData(data: CompanyDetails) {
        with(binding) {
            companyDescriptionTv.text = data.description ?: ""
            companyNameTv.text = data.name ?: ""
            AssetTypeTv.text = "${data.symbol ?: ""} ${data.assetType ?: ""}"
            aboutCompaNyTv.text = "About ${data.name ?: ""}"
            exChangeTv.text = data.exchange ?: ""
            fiftyTwoWeekHighTv.text = data.fiftyTwoWeekHigh ?: ""
            fiftyTwoWeekLowTv.text = data.fiftyTwoWeekLow ?: ""
            profitMarginTv.text = data.profitMargin ?: ""
            PeRatiotV.text = data.peRatio ?: ""
            betAtV.text = data.beta ?: ""
            dividendYieldTv.text = data.dividendYield ?: ""
            companYindustryTv.text = "Industry: ${data.industry ?: ""}"
            companYsectorTv.text = "Sector: ${data.sector ?: ""}"
        }

        // Show or hide detailed company information based on completeness
        if (isCompanyDetailsComplete(data)) {
            showCompanyDetails()
        } else {
            hideCompanyDetails()
        }


        Log.d("CompanyDetailsFragment", "Company details fetched successfully")
    }

    /**
     * Checks if all required company details are present.
     *
     * @param data Company details object to check.
     * @return True if all required details are present, false otherwise.
     */
    private fun isCompanyDetailsComplete(data: CompanyDetails): Boolean {
        return !data.description.isNullOrEmpty() &&
                !data.name.isNullOrEmpty() &&
                !data.symbol.isNullOrEmpty() &&
                !data.assetType.isNullOrEmpty() &&
                !data.exchange.isNullOrEmpty() &&
                !data.fiftyTwoWeekHigh.isNullOrEmpty() &&
                !data.fiftyTwoWeekLow.isNullOrEmpty() &&
                !data.profitMargin.isNullOrEmpty() &&
                !data.peRatio.isNullOrEmpty() &&
                !data.beta.isNullOrEmpty() &&
                !data.dividendYield.isNullOrEmpty() &&
                !data.industry.isNullOrEmpty() &&
                !data.sector.isNullOrEmpty()
    }

    /**
     * Show detailed company information UI components.
     */
    private fun showCompanyDetails() {
        with(binding) {
            detailsLinear1.visibility = View.VISIBLE
            mygraph.visibility = View.VISIBLE
            allOverViewLinear.visibility = View.VISIBLE
        }
    }

    /**
     * Hide detailed company information UI components.
     */
    private fun hideCompanyDetails() {
        with(binding) {
            detailsLinear1.visibility = View.GONE
            mygraph.visibility = View.GONE
            allOverViewLinear.visibility = View.GONE
        }
    }

    /**
     * Handles error state by showing an error message.
     *
     * @param errorMessage Error message to display.
     */
    private fun showErrorState(errorMessage: String?) {
        Log.e("CompanyDetailsFragment", "Error fetching company details: $errorMessage")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        // Restore bottom navigation visibility when fragment is detached
        mainActivity.setBottomNavigationVisibility(true)
    }
}
