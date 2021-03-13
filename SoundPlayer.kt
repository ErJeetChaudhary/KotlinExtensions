import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.SparseIntArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Jitendra on 22/07/19.
 */
@Singleton
class SoundPlayer @Inject constructor(@ApplicationContext context: Context) {

    private val mSoundPoolMap = SparseIntArray()

    private val mSoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()).build()
    } else {
        SoundPool(2, AudioManager.STREAM_MUSIC, 0)
    }

    init {
        mSoundPoolMap.put(Sound.Correct.ordinal, mSoundPool.load(context, R.raw.correct, 1))
        mSoundPoolMap.put(Sound.Wrong.ordinal, mSoundPool.load(context, R.raw.wrong, 1))
        mSoundPoolMap.put(Sound.CLAP.ordinal, mSoundPool.load(context, R.raw.claps, 1))
        mSoundPoolMap.put(Sound.SKIP.ordinal, mSoundPool.load(context, R.raw.skip, 1))
    }

    fun play(sound: Sound) {
        Logger.wtf("SoundPlayer", "playing sound: $sound")
        mSoundPool.play(mSoundPoolMap[sound.ordinal], 1.0f, 1.0f, 0, 0, 1.0f)
    }

    enum class Sound {
        Correct, Wrong, CLAP, SKIP
    }
}
