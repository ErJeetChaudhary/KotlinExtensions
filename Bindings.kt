object Bindings {

    @JvmStatic
    @BindingAdapter("app:backgroundId")
    fun loadDrawable(view: View, resId: Int) {
        view.setBackgroundResource(resId)
    }
  
     @JvmStatic
    @BindingAdapter("app:colorId")
    fun setTextColor(textView: TextView, colorId: Int) {
        textView.setTextColorRes(colorId)
    }

    @JvmStatic
    @BindingAdapter("app:parseColor")
    fun setTextColor(textView: TextView, color: String?) {
        if (!TextUtils.isEmpty(color)) {
            textView.setTextColor(Color.parseColor(color))
        }
    }

    @JvmStatic
    @BindingAdapter("app:textColorRes")
    fun setTextColorRes(materialButton: MaterialButton, @ColorRes colorRes: Int) {
        materialButton.apply { setTextColor(context.getColorCompat(colorRes)) }
    }

    @JvmStatic
    @BindingAdapter("app:viewBackgroundColor")
    fun setViewBackgroundColor(view: View, @ColorRes colorRes: Int = android.R.color.white) {
        view.apply { setBackgroundColor(context.getColorCompat(colorRes)) }
    }

    @JvmStatic
    @BindingAdapter("app:cardBackgroundColor")
    fun setCardBackgroundColor(view: CardView, @ColorRes colorRes: Int = android.R.color.white) {
        view.apply { setCardBackgroundColor(context.getColorCompat(colorRes)) }
    }

    @JvmStatic
    @BindingAdapter("app:cardBackgroundColorString")
    fun setCardBackgroundColor(view: CardView, color: String) {
        view.apply { setCardBackgroundColor(Color.parseColor(color)) }
    }

    @JvmStatic
    @BindingAdapter("app:buttonBackgroundTintRes")
    fun buttonBackgroundTintRes(button: AppCompatButton, @ColorRes colorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.backgroundTintList = ContextCompat.getColorStateList(button.context, colorRes)
        } else {
            button.setBackgroundColor(ContextCompat.getColor(button.context, colorRes))
        }
    }

    @JvmStatic
    @BindingAdapter("app:buttonTextColorRes")
    fun buttonTextColorRes(button: AppCompatButton, @ColorRes colorRes: Int) {
        button.apply { setTextColor(context.getColorCompat(colorRes)) }
    }
  
    @JvmStatic
    @BindingAdapter("app:loadResizedImage")
    fun loadResizedImage(imageView: ImageView, path: String?) {
        Glide.with(imageView.context).asBitmap().load(path).dontTransform()
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    resource?.density = imageView.context.resources.displayMetrics.densityDpi
                    return false
                }
            }).into(imageView)
    }  
}
