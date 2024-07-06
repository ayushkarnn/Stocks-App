package ayush.karn.stocksapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ayush.karn.stocksapp.R
import ayush.karn.stocksapp.models.Data

class GainAndLoseAdapter(
    private var listOfItems: List<Data>,
    private val clickListener: OnClickListener
) : RecyclerView.Adapter<GainAndLoseAdapter.GainViewHolder>() {

    private lateinit var context: Context
    var isForLoseFragment: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GainViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_layout, parent, false)
        return GainViewHolder(view)
    }

    inner class GainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.company_name)
        val stockPrice: TextView = itemView.findViewById(R.id.stock_price)
        val priceChange: TextView = itemView.findViewById(R.id.price_change)
    }


    override fun getItemCount(): Int {
        return listOfItems.size
    }

    fun getItem(position: Int): Data {
        return if (position in listOfItems.indices) {
            listOfItems[position]
        } else {
            throw IndexOutOfBoundsException("Position $position is out of bounds for the list of size ${listOfItems.size}")
        }
    }

    override fun onBindViewHolder(holder: GainViewHolder, position: Int) {
        val currItem = getItem(position)
        holder.companyName.text = currItem.name
        holder.stockPrice.text = currItem.price
        holder.priceChange.text = currItem.change

        if (isForLoseFragment) {
            holder.priceChange.setTextColor(context.getColor(R.color.red))
            holder.priceChange.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_lose, 0)
        } else {
            holder.priceChange.setTextColor(context.getColor(R.color.green))
            holder.priceChange.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_gain, 0)
        }
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }
    }

    fun setData(newData: List<Data>) {
        listOfItems = newData
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }


}

