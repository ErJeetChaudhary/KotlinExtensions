val gson: Gson = GsonBuilder().setLenient().create()

@SuppressLint("ClickableViewAccessibility")
inline fun View.doOnTouch(crossinline action: (view: View?, event: MotionEvent?) -> Boolean) {
    this.setOnTouchListener { v, event -> action(v, event) }
}

data class ViewBinder<T>(val item: T?, val position: Int)

@Suppress("DEPRECATION")
inline fun <reified T : Any> View.doOnTouch() {
    this.doOnTouch { view, _ ->
        val shadow = View.DragShadowBuilder(view)
        val item = view?.tag as T?
        val itemJson = gson.toJson(item)
        val clipDataItem = ClipData.Item(itemJson)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val clipData = ClipData(itemJson, mimeTypes, clipDataItem)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view?.startDragAndDrop(clipData, shadow, null, 0)
        } else {
            view?.startDrag(clipData, shadow, null, 0)
        }
        false
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Any> View.doOnTouchViewBinder(position: Int) {
    this.doOnTouch { view, _ ->
        val shadow = View.DragShadowBuilder(view)
        val item = ViewBinder(view?.tag as T?, position)
        val itemJson = gson.toJson(item)
        val clipDataItem = ClipData.Item(itemJson)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val clipData = ClipData(itemJson, mimeTypes, clipDataItem)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view?.startDragAndDrop(clipData, shadow, null, 0)
        } else {
            view?.startDrag(clipData, shadow, null, 0)
        }
        false
    }
}

inline fun <reified T : Any> View.doOnDrag(drawable: Drawable?, crossinline action: (current: T?, selected: T) -> Unit) {
    this.setOnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED, DragEvent.ACTION_DRAG_EXITED -> {
                if (drawable != null) {
                    view.background = drawable
                }
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DROP -> {
                val clipData = event.clipDescription?.label?.toString()
                clipData?.let {
                    val item = gson.fromJson<T>(clipData)
                    val currentItem = view.tag as T?
                    view.tag = item
                    action(currentItem, item)
                    view.invalidate()
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                if (drawable != null) {
                    view.background = drawable
                }
                true
            }
            else -> false
        }
    }
}


inline fun <reified T : Any> View.doOnDragViewBinder(drawable: Drawable?, crossinline action: (current: T?, selected: T?, layoutId: Int?) -> Unit) {
    this.setOnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED, DragEvent.ACTION_DRAG_EXITED -> {
                view.background = drawable
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DROP -> {
                val clipData = event.clipDescription?.label?.toString()
                clipData?.let {
                    val item = gson.fromJson<ViewBinder<T>>(clipData)
                    val currentItem = view.tag as T?
                    view.tag = item.item
                    action(currentItem, item.item, item.position)
                    view.invalidate()
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.background = drawable
                true
            }
            else -> false
        }
    }
}
