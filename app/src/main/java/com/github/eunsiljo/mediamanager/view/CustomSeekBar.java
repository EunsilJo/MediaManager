package com.github.eunsiljo.mediamanager.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

/**
 * Created by EunsilJo on 2016. 11. 3..
 */

public class CustomSeekBar extends SeekBar implements CustomProgress{

    public CustomSeekBar(Context context) {
        super(context);
        setPadding(0,0,0,0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0,0,0,0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(0,0,0,0);
    }

    @Override
    public void setProgressWithAnimation(int progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();

        if(progress == getMax()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mListener != null) {
                        mListener.onProgressFinish(CustomSeekBar.this);
                    }
                }
            }, duration);
        }
    }

    private OnProgressFinishListener mListener;
    @Override
    public void setOnProgressFinish(OnProgressFinishListener listener) {
        mListener = listener;
    }

}
