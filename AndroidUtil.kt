object AndroidUtil {
    val isPOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    val isOOrLater = isPOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    val isNougatMR1OrLater = isOOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    val isNougatOrLater = isNougatMR1OrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    val isMarshMallowOrLater = isNougatOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val isLolliPopOrLater = isMarshMallowOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    val isKitKatOrLater = isLolliPopOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    val isJellyBeanMR2OrLater = isKitKatOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
    fun uriToFile(uri: Uri): File {
        return File(uri.path?.replaceFirst("file://".toRegex(), ""))
    }
    /**
     * Quickly converts path to URIs, which are mandatory in libVLC.
     *
     * @param path The path to be converted.
     * @return A URI representation of path
     */
    fun pathToUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }
    fun locationToUri(location: String): Uri {
        val uri = Uri.parse(location)
        requireNotNull(uri.scheme) { "location has no scheme" }
        return uri
    }
    fun fileToUri(file: File): Uri {
        return Uri.fromFile(file)
    }
}
