package pe.com.gianbravo.blockedcontacts.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import gianca.mostradorropa.friendssApp.touchHelper.ItemTouchHelperAdapter
import kotlinx.android.synthetic.main.item_number.view.*
import pe.com.gianbravo.blockedcontacts.R

/**
 * @author Giancarlo Bravo Anlas
 *
 */


class RvNumberListAdapter(
    private var context: Context?,
    private var preparationTypes: ArrayList<Unit>?,
    private val onItemSelectedListener: OnItemSelected? = null,
    private val onItemRemoveListener: OnItemRemove? = null
) :
    RecyclerView.Adapter<RvNumberListAdapter.DataClientViewHolder>(),
    Filterable , ItemTouchHelperAdapter {

    interface OnItemSelected {
        fun onItemSelected(
            item: String?,
            buttonView: CompoundButton?,
            isChecked: Boolean
        )
    }
    interface OnItemRemove {
        fun onItemRemove(
            item: String?,
            position: Int
        )
    }

    private var layoutInflater: LayoutInflater =
        context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var listItems: ArrayList<String>
    private var listItemsFiltered: ArrayList<String>
    private var listSelected: ArrayList<String> = arrayListOf()

    init {
        listItemsFiltered = ArrayList()
        listItems = ArrayList()
    }

    fun loadData(listClient: ArrayList<String>?) {
        listClient?.let {
            this.listItems = it
            filter.filter("")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataClientViewHolder {
        val view: View = layoutInflater.inflate(R.layout.item_number, parent, false)
        return DataClientViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return listItemsFiltered.size
    }

    override fun onBindViewHolder(
        holder: DataClientViewHolder,
        position: Int
    ) {
        val currentItem = listItemsFiltered[position]
        holder.tvNumber.text = currentItem

        holder.layout.setOnClickListener {
           // onItemSelectedListener?.onItemSelected(currentItem, null, false)
            onItemRemoveListener?.onItemRemove(currentItem, position)
        }
    }

    fun getAnswers(): ArrayList<String> {
        return listItemsFiltered
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterString = constraint.toString().toLowerCase()
                val results = FilterResults()
                val list: ArrayList<String> = listItems
                val count = list.size
                val nlist: ArrayList<String> = ArrayList<String>(count)

                list.forEach { service ->
                    if (service.toLowerCase().contains(filterString)) {
                        nlist.add(service)
                    }
                }
                results.values = nlist
                results.count = nlist.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listItemsFiltered = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }

    class DataClientViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val tvNumber = view.tvNumber
        val layout = view.layout
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }

    override fun onItemDismiss(position: Int) {
        onItemRemoveListener?.onItemRemove(listItemsFiltered[position] ,position )
    }
    
    fun removeItem(position:Int){
        listItems.removeAt(listItems.indexOf(listItemsFiltered[position]))
        listItemsFiltered.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listItemsFiltered.size)
    }

}


