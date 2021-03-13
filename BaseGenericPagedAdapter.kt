import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Jitendra on 15:17, 24-01-2020
 */
abstract class BaseGenericPagedAdapter<T, VH : RecyclerView.ViewHolder>(val mContext: Context, diffCallback: DiffUtil.ItemCallback<T>) : PagedListAdapter<T, VH>(diffCallback) {

    private val inflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = getViewBinder(DataBindingUtil.inflate(inflater, viewType, parent, false), viewType)

    override fun onBindViewHolder(holder: VH, position: Int) {
        (holder as Binder<T>).bind(item = getItem(position))
    }

    override fun getItemViewType(position: Int) = getLayoutId(position, getItem(position))

    override fun getItemId(position: Int) = position.toLong()

    fun getCurrentItem(position: Int): T? = getItem(position)

    protected abstract fun getLayoutId(position: Int, item: T?): Int

    abstract fun getViewBinder(binding: ViewDataBinding, viewType: Int): VH

    abstract class Binder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T?)
    }
}
