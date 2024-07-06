package ayush.karn.stocksapp.models

data class GainersAndLosers(
    val last_updated: String,
    val metadata: String,
    val most_actively_traded: List<MostActivelyTraded>,
    val top_gainers: List<TopGainer>,
    val top_losers: List<TopLoser>
)