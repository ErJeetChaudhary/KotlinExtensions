import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * Created by Jitendra on 17:07, 24-01-2020
 */
abstract class BaseGenericSpinnerAdapter<T>(val mContext: Context) : BaseAdapter() {

    private var mItems: List<T> = arrayListOf()

    private val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        return if (view == null) {
            val binding: ViewDataBinding = DataBindingUtil.inflate(mInflater, getLayoutId(), parent, false)
            view = getViewBinder(binding, getItem(position))
            view.tag = binding
            view
        } else {
            getViewBinder(view.tag as ViewDataBinding, getItem(position))
        }
    }

    override fun getItem(position: Int): T {
        return mItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mItems.size
    }

    fun setItems(items: List<T>) {
        mItems = items
        notifyDataSetChanged()
    }

    fun getItems(): List<T> {
        return mItems
    }

    protected abstract fun getLayoutId(): Int

    abstract fun getViewBinder(binding: ViewDataBinding, item: T): View

    abstract class Binder<T> {

        var position: Int = 0

        fun bind(position: Int, item: T) {
            this.position = position
            bind(item)
        }

        abstract fun bind(item: T): View
    }
}
