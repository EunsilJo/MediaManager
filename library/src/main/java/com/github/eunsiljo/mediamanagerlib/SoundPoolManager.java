package com.github.eunsiljo.mediamanagerlib;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;

import com.github.eunsiljo.mediamanagerlib.utils.MediaUtils;

import java.io.File;


/**
 * Created by EunsilJo on 2017. 7. 5..
 */

public class SoundPoolManager {
    public static String TAG = SoundPoolManager.class.getSimpleName();

    private SoundPool mSoundPool = null;
    private Context mContext;

    public static final int PLAYER_STATE_STOP = 0;
    public static final int PLAYER_STATE_START = 1;
    public static final int PLAYER_STATE_PAUSE = 2;

    private int mState = PLAYER_STATE_STOP;

    private String mPath = null;
    private long mDuration = 0;
    private int mSoundId = 0;
    private int mStreamId = 0;
    private float mPlayingRate = 1.0f;


    public void createMediaPlayer(Context context){
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mContext = context;
    }

    private MediaPlayer.OnCompletionListener mCompletionListener;
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;
    }
    public void setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener listener){
        if(mSoundPool == null) return;
        mSoundPool.setOnLoadCompleteListener(listener);
    }

    public boolean setupPlaying(Context context, String path){
        if(mSoundPool == null){
            createMediaPlayer(context);
        }
        if(!MediaUtils.checkURLFormat(path)){
            File file = new File(path);
            if(!file.exists()){
                return false;
            }
        }
        long duration = getDuration(path);
        int soundId = mSoundPool.load(path, 1);
        if(duration == 0 || soundId == 0){
            return false;
        }
        mSoundId = soundId;
        mDuration = duration;
        mPath = path;
        mTimeSinceStart = 0;
        return true;
    }

    public void startPlaying() {
        if(mSoundPool == null) return;

        if(mDuration == 0 || mSoundId == 0){
            return;
        }

        if(mTimeSinceStart >= mDuration) {
            mTimeSinceStart = 0;
        }

        mStartTime = System.currentTimeMillis();

        if(mTimeSinceStart == 0) {
            int streamId = mSoundPool.play(mSoundId, 1f, 1f, 1, 0, mPlayingRate);
            if (streamId == 0) {
                return;
            }
            mStreamId = streamId;

            mCheckTime = mStartTime;
            mTimeSincePause = 0;
            mCurrentPosition = 0;
        }else{
            mSoundPool.resume(mStreamId);

            mTimeSincePause += mStartTime - mPauseTime;
        }

        long timeDuration = (long)((mDuration - mTimeSinceStart) / mPlayingRate);
        if(mHandler != null) {
            mHandler.postDelayed(mCompletionChecker, timeDuration);
        }

        mState = PLAYER_STATE_START;
    }

    public void pausePlaying(){
        if(mSoundPool == null) return;

        if(mStreamId == 0){
            return;
        }

        mPauseTime = System.currentTimeMillis();

        mSoundPool.pause(mStreamId);

        mTimeSinceStart += (mPauseTime - mStartTime) * mPlayingRate;

        if(mHandler != null){
            mHandler.removeCallbacks(mCompletionChecker);
        }

        mState = PLAYER_STATE_PAUSE;
    }

    public void stopPlaying() {
        if(mSoundPool == null) return;

        if(mStreamId == 0){
            return;
        }

        mSoundPool.stop(mStreamId);

        mStartTime = 0;
        mPauseTime = 0;
        mTimeSinceStart = 0;
        mCheckTime = 0;
        mTimeSincePause = 0;
        mCurrentPosition = 0;
        if(mHandler != null){
            mHandler.removeCallbacks(mCompletionChecker);
        }

        mDuration = 0;
        mSoundId = 0;
        mStreamId = 0;
        mPath = null;

        mState = PLAYER_STATE_STOP;
    }

    private long mStartTime = 0;
    private long mPauseTime = 0;
    private long mTimeSinceStart = 0;
    private long mCheckTime = 0;
    private long mTimeSincePause = 0;
    private long mCurrentPosition = 0;

    private Handler mHandler = new Handler();
    private Runnable mCompletionChecker = new Runnable(){
        @Override
        public void run(){
            if(mCompletionListener != null){
                mCompletionListener.onCompletion(null);
            }
        }
    };

    public void setRate(float rate){
        boolean isPlaying = isPlaying();
        if(isPlaying){
            pausePlaying();
        }
        mPlayingRate = rate;
        if(mSoundPool != null && mStreamId != 0){
            mSoundPool.setRate(mStreamId, mPlayingRate);
        }
        if(isPlaying){
            startPlaying();
        }
    }

    public int getCurrentPosition(){
        if(isPlaying()) {
            long check = System.currentTimeMillis() - mTimeSincePause;
            mCurrentPosition += (check - mCheckTime) * mPlayingRate;
            mCheckTime = check;
        }
        return (int)mCurrentPosition;
    }

    private long getDuration(String path){
        MediaPlayer player = MediaPlayer.create(mContext, Uri.fromFile(new File(path)));
        int duration = 0;
        if(player != null){
            duration = player.getDuration();
        }
        return duration;
    }

    public int getDuration(){
        return (int)mDuration;
    }

    public float getRate(){
        return mPlayingRate;
    }

    public boolean isPlaying(){
        return mState == PLAYER_STATE_START;
    }

    public boolean isStopped(){
        return mState == PLAYER_STATE_STOP;
    }

    public boolean checkNowPlay(String play){
        return (mPath != null && mPath.equals(play));
    }

    public void releasePlayer(){
        if(mSoundPool == null) return;

        if (isPlaying()) {
            stopPlaying();
        }
        mSoundPool.release();
        mSoundPool = null;
    }
}