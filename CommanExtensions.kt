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

inline fun CollapsingToolbarLayout.hideTitleOnExpand(appBarLayout: AppBarLayout, title: String, crossinline action: (offset: Int) -> Unit = { _ -> }) {
    appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {

        var isShow = true

        var scrollRange = -1

        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            action(verticalOffset)
            if (scrollRange == -1) {
                scrollRange = appBarLayout?.totalScrollRange ?: 0
            }

            if (scrollRange + verticalOffset == 0) {
                this@hideTitleOnExpand.title = title
                isShow = true
            } else if (isShow) {
                this@hideTitleOnExpand.title = " " // careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        }
    })
}

internal fun EditText.textWatcher(action: (s: String?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action(s?.toString())
        }
    })
}

internal fun ViewPager.onPageChanged(action: (position: Int) -> Unit) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            action(position)
        }
    })
}

internal fun ViewPager2.onPageChanged(action: (position: Int) -> Unit) {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            action(position)
        }

    })
}

internal fun RadioGroup.onCheckChanged(action: (group: RadioGroup?, checkedId: Int) -> Unit) {
    this.setOnCheckedChangeListener { group, checkedId -> action(group, checkedId) }
}

internal fun NestedLinearRadioGroup.onCheckChanged(action: (group: NestedRadioGroupManager?, checkedId: Int) -> Unit) {
    this.setOnCheckedChangeListener { group, checkedId -> action(group, checkedId) }
}

internal fun ScrollView.isVisible(view: View): Boolean {
    val scrollBounds = Rect()
    this.getDrawingRect(scrollBounds)
    val top = view.y
    val bottom = top + view.height
    return scrollBounds.top < top && scrollBounds.bottom > bottom // fully visible
}

inline fun View.doOnTouch(crossinline action: (view: View?, event: MotionEvent?) -> Boolean) {
    this.setOnTouchListener { v, event -> action(v, event) }
}

inline fun View.startAnimation(
    @AnimRes anim: Int,
    crossinline onAnimationStart: (animation: Animation?) -> Unit = { },
    crossinline onAnimationRepeat: (animation: Animation?) -> Unit = { },
    crossinline onAnimationEnd: (animation: Animation?) -> Unit = { }
) {
    val animation = AnimationUtils.loadAnimation(this.context, anim)
    animation.onAnimationStart(onAnimationStart)
    animation.onAnimationEnd(onAnimationEnd)
    animation.onAnimationRepeat(onAnimationRepeat)
    this.startAnimation(animation)
}

inline fun Animation.onAnimationRepeat(crossinline action: (animation: Animation?) -> Unit) = addOnAnimationListener(onAnimationRepeat = action)

inline fun Animation.onAnimationEnd(crossinline action: (animation: Animation?) -> Unit) = addOnAnimationListener(onAnimationEnd = action)

inline fun Animation.onAnimationStart(crossinline action: (animation: Animation?) -> Unit) = addOnAnimationListener(onAnimationStart = action)

inline fun Animation.addOnAnimationListener(
    crossinline onAnimationRepeat: (animation: Animation?) -> Unit = { },
    crossinline onAnimationEnd: (animation: Animation?) -> Unit = { },
    crossinline onAnimationStart: (animation: Animation?) -> Unit = { }
): Animation.AnimationListener {

    val listener = object : Animation.AnimationListener {

        override fun onAnimationRepeat(animation: Animation?) {
            onAnimationRepeat(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            onAnimationStart(animation)
        }
    }

    setAnimationListener(listener)
    return listener
}

internal fun Activity.doOnInternet(action: () -> Unit = { }) {
    if (this.hasInternet()) {
        action()
    } else {
        Snackbar.make(
            this.findViewById(android.R.id.content),
            "Error while connecting to server. Please check internet connection.",
            Snackbar.LENGTH_LONG
        ).setAction("OK") {

        }.setActionTextColor(ContextCompat.getColor(this, R.color.green_whts)).show()
    }
}

internal fun Fragment.doOnInternet(action: () -> Unit = { }) {
    this.activity?.let {
        if (it.hasInternet()) {
            action()
        } else {
            Snackbar.make(
                it.findViewById(android.R.id.content),
                "Error while connecting to server. Please check internet connection.",
                Snackbar.LENGTH_LONG
            )
                .setAction("OK") {}
                .setActionTextColor(ContextCompat.getColor(it, R.color.green_whts))
                .show()
        }
    }
}

internal fun Long.formatDateTime(format: String): String = SimpleDateFormat(format, Locale.getDefault()).format(Timestamp(this))

inline fun <reified T> Gson.fromJson(value: String?): T {
    return this.fromJson(value, T::class.java)
}

inline fun <reified T> Gson.toJsonString(value: T?): String? {
    return this.toJson(value, T::class.java)
}

inline fun View.doOnScrollChanged(crossinline action: (view: View) -> Unit) {
    this.viewTreeObserver.addOnScrollChangedListener {
        action(this)
    }
}

inline fun Activity.takeScreenshot(crossinline result: (bitmap: Bitmap) -> Unit) {
    this.window?.let { window ->
        val view = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(
                    window, Rect(
                        locationOfViewInWindow[0], locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width, locationOfViewInWindow[1] + view.height
                    ), bitmap, { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            result(bitmap)
                        } else {
                            view.draw(Canvas(bitmap))
                            result(bitmap)
                        }
                    }, Handler()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                view.draw(Canvas(bitmap))
                result(bitmap)
            }
        } else {
            view.draw(Canvas(bitmap))
            result(bitmap)
        }
    }
}

internal fun Activity.openCamera(requestCode: Int, output: File) {
    if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
        this.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this@openCamera, "${packageName}.provider", output))
        }, requestCode)
    } else {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, requestCode, Manifest.permission.CAMERA)
                .setRationale(R.string.rationale_camera)
                .setPositiveButtonText(R.string.ok)
                .setNegativeButtonText(R.string.cancel)
                .build()
        )
    }
}

internal fun Activity.hasSoftKeys(): Boolean {
    val d: Display = windowManager.defaultDisplay
    val realDisplayMetrics = DisplayMetrics()
    d.getRealMetrics(realDisplayMetrics)
    val realHeight = realDisplayMetrics.heightPixels
    val realWidth = realDisplayMetrics.widthPixels
    val displayMetrics = DisplayMetrics()
    d.getMetrics(displayMetrics)
    val displayHeight = displayMetrics.heightPixels
    val displayWidth = displayMetrics.widthPixels
    return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
}
