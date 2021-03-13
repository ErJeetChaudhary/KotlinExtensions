/**
 * Created by Jitendra on 13:19, 20-08-2020
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal fun Activity.setGradientStatusBar() {
    window.apply {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = getColorCompat(android.R.color.transparent)
        navigationBarColor = getColorCompat(android.R.color.transparent)
        setBackgroundDrawable(getDrawableCompat(R.drawable.theme_grad_rect))
    }
}

internal fun Activity.setStatusBarColor(@ColorRes color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = ContextCompat.getColor(this@setStatusBarColor, color)
        }
    }
}

internal fun Activity.setStatusBarColor(color: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.parseColor(color)
        }
    }
}

internal fun Activity.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.parseColor("#fefefe") // optional
    }
}

internal fun Activity.clearLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = window.decorView.systemUiVisibility // get current flag
        flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // use XOR here for remove LIGHT_STATUS_BAR from flags
        window.decorView.systemUiVisibility = flags
        window.statusBarColor = Color.GREEN // optional
    }
}

inline fun <reified T : Any> newIntent(context: Context) = Intent(context, T::class.java)

inline fun <reified T : Any> Activity.launchActivity(requestCode: Int = -1, options: Bundle? = null, finish: Boolean? = false, noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }

    if (finish == true) {
        this.finish()
    }
}

inline fun <reified T : Any> Context.launchActivity(options: Bundle? = null, finish: Boolean? = false, noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }

    if (finish == true) {
        (this as Activity).finish()
    }
}

