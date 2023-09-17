package com.example.minesweeper;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class Timer {
    private boolean mActive;
    private int mClock;
    private TextView mClockView;
    private String mTime;

    // Constructor
    public Timer(TextView view)
    {
        mActive = false;
        mClockView = view;
        runTimer();

    }

    public void onClickStart()
    {
        mActive = true;
    }

    public void onClickEnd()
    {
        mActive = false;
    }

    public boolean getStatus()
    {
        return mActive;
    }

    public String getTime() { return mTime; }

    private void runTimer()
    {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                String time = String.format("%03d", mClock);
                mTime = time;
                mClockView.setText(time);

                if (mActive) {
                    mClock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }



}
