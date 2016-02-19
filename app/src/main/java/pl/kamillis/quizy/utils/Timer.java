package pl.kamillis.quizy.utils;

import android.os.CountDownTimer;

public class Timer extends CountDownTimer {

    private TimerListener listener;

    public Timer(TimerListener listener, long secondsInFuture) {
        super(secondsInFuture * 1000, 1000);
        this.listener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        listener.onTimerTick(millisUntilFinished / 1000);
    }

    @Override
    public void onFinish() {
        listener.onTimerFinish();
    }

    public interface TimerListener {
        void onTimerTick(long seconds);
        void onTimerFinish();
    }

}
