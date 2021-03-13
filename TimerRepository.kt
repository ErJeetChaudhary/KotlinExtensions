/**
 * Created by Jitendra on 17:32, 20-01-2020
 */
class TimerRepository {

    private val mElapsedTime = MutableLiveData<Long>()

    private var mInitialTime = SystemClock.elapsedRealtime()

    private val mTimer = Timer()

    fun startTimer() {
        mTimer.scheduleAtFixedRate(object : TimerTask() {

            override fun run() {
                val newValue = (SystemClock.elapsedRealtime() - mInitialTime) / 1000L
                mElapsedTime.postValue(newValue)
            }

        }, ONE_SECOND, ONE_SECOND)
    }

    fun getElapsedTime() = mElapsedTime

    fun cancel() {
        mTimer.cancel()
    }

    companion object {

        const val ONE_SECOND = 1000L

        const val TAG = "TimerRepository"

    }

}
