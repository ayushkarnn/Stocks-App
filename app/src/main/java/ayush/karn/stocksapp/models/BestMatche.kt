package ayush.karn.stocksapp.models

import com.google.gson.annotations.SerializedName

data class BestMatche(
    @SerializedName("1. symbol")
    val _symbol: String,
    @SerializedName("2. name")
    val _name: String,
    @SerializedName("3. type")
    val _type: String,
    @SerializedName("4. region")
    val _region: String,
    @SerializedName("5. marketOpen")
    val _marketOpen: String,
    @SerializedName("6. marketClose")
    val _marketClose: String,
    @SerializedName("7. timezone")
    val _timezone: String,
    @SerializedName("8. currency")
    val _currency: String,
    @SerializedName("9. matchScore")
    val _matchScore: String
)