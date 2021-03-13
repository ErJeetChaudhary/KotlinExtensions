import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter
import org.jsoup.Jsoup

/**
 * Created by Jitendra on 10:26, 29-01-2020
 */
abstract class BaseGenericPagerAdapter<T>(val context: Context) : PagerAdapter() {

    private val mItems: MutableList<T> = mutableListOf()

    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val any = (getViewBinder(DataBindingUtil.inflate(mInflater, getLayoutId(), container, false), position) as Binder<T>)
                .bind(position, getItem(position))
        container.addView(any as View)
        return any
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == any
    }

    override fun getCount(): Int = mItems.size

    fun getItems(): MutableList<T> = mItems

    fun add(item: T) {
        mItems.add(item)
        notifyDataSetChanged()
    }

    open fun addAll(items: List<T>) {
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    open fun setItems(items: List<T>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T = mItems[position]

    protected abstract fun getLayoutId(): Int

    abstract fun getViewBinder(binding: ViewDataBinding, position: Int): Any

    abstract class Binder<T> {

        var position: Int = 0

        fun bind(position: Int, item: T): Any {
            this.position = position
            return bind(item)
        }

        abstract fun bind(item: T): Any

        fun parse(text: String?): String? {
            return Jsoup.parse(text).text()
        }

    }
}
