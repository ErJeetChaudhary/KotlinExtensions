/**
 * Created by Jitendra on 13:54, 31-01-2020
 */
class CountDownRepository(time: Long, interval: Long) : CountDownTimer(time, interval) {

    val countDown = MutableLiveData<Event<CountDown>>()

    override fun onFinish() {
        countDown.postValue(Event(CountDown(finished = true, currentMillis = 0L)))
    }

    override fun onTick(millisUntilFinished: Long) {
        countDown.postValue(Event(CountDown(finished = false, currentMillis = millisUntilFinished)))
    }

    open class CountDown(val finished: Boolean, val currentMillis: Long)

}
