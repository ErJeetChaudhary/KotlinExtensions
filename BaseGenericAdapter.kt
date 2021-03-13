import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Jitendra on 15:17, 24-01-2020
 */
abstract class BaseGenericAdapter<T, VH : RecyclerView.ViewHolder>(val mContext: Context) : RecyclerView.Adapter<VH>() {

    private var items = mutableListOf<T>()

    protected val inflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        getViewBinder(DataBindingUtil.inflate(inflater, viewType, parent, false), viewType)

    override fun onBindViewHolder(holder: VH, position: Int) {
        (holder as Binder<T>).bind(item = items[position])
    }

    override fun getItemViewType(position: Int) = getLayoutId(position, items[position])

    fun getItem(position: Int): T = items[position]

    private fun isValidPosition(position: Int): Boolean = position >= 0 && position < items.size

    fun remove(position: Int): Boolean = isValidPosition(position) && items.removeAt(position) != null

    fun add(item: T) {
        items.add(item)
        notifyItemInserted(itemCount - 1)
    }

    fun set(position: Int, item: T) {
        try {
            items[position] = item
            notifyItemChanged(position)
        } catch (e: Exception) {
        }
    }

    fun remove(item: T): Boolean = items.remove(item)

    fun getItems(): MutableList<T> = items

    open fun setItems(items: MutableList<T>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    fun clear() {
        try {
            items.clear()
            notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }

    fun hasItems() = items.isNotEmpty()

    override fun getItemCount() = items.size

    protected abstract fun getLayoutId(position: Int, item: T): Int

    abstract fun getViewBinder(binding: ViewDataBinding, viewType: Int): VH

    abstract class Binder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }
}
