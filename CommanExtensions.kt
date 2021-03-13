inline fun <reified T> Gson.fromJson(value: String?): T {
    return this.fromJson(value, T::class.java)
}

val Any.TAG: String
    get() = javaClass.simpleName

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @return A float value to represent px equivalent to dp depending on device density
 */
internal fun Float.convertDpToPixel(): Float {
    return this * (TUK.instance.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * A value in px (pixels) unit. Which we need to convert into db
 * @return A float value to represent dp equivalent to px value
 */
internal fun Float.convertPixelsToDp(): Float {
    return this / (TUK.instance.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

internal fun Fragment.getFont(@FontRes id: Int): Typeface {
    return ResourcesCompat.getFont(requireContext(), id)!!
}

internal fun Context.getFont(@FontRes id: Int): Typeface {
    return ResourcesCompat.getFont(this, id)!!
}

internal fun <T> Flow<T>.flowIO(): Flow<T> {
    return flowOn(Dispatchers.IO)
}

inline fun <reified T> Moshi.convert(value: String): T? {
    return adapter(T::class.java).fromJson(value)
}

internal fun String?.fromHtml(): String {
    return when {
        this == null -> {
            // return an empty spannable if the html is null
            SpannableString("").toString()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
        }
        else -> {
            Html.fromHtml(this).toString()
        }
    }
}

private fun clearCache() {
    GlobalScope.launch(Dispatchers.IO) {
        Logger.wtf(TAG, "cache isMainThread: ${Looper.getMainLooper() == Looper.myLooper()}")
        try {
            val iterator = mCache.urls()
            while (iterator.hasNext()) {
                iterator.next()
                iterator.remove()
            }
        } catch (e: Exception) {
            Logger.wtf(TAG, "exception while clear cache $e")
        }
    }
}

internal fun Spinner.setEntries(@ArrayRes arrayId: Int) {
    adapter = ArrayAdapter.createFromResource(context, arrayId, android.R.layout.simple_spinner_item).apply {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }
}

internal fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}
