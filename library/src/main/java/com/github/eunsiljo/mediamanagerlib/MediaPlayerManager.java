package com.github.eunsiljo.mediamanagerlib;

import android.media.MediaPlayer;

import com.github.eunsiljo.mediamanagerlib.utils.MediaUtils;
import com.github.eunsiljo.mediamanagerlib.utils.log;

import java.io.File;
import java.io.IOException;


/**
 * Created by EunsilJo on 2016. 11. 3..
 */

public class MediaPlayerManager {
    public static String TAG = MediaPlayerManager.class.getSimpleName();

    private MediaPlayer mPlayer = null;

    public static final int PLAYER_STATE_STOP = 0;
    public static final int PLAYER_STATE_START = 1;
    public static final int PLAYER_STATE_PAUSE = 2;

    private int mState = PLAYER_STATE_STOP;

    private String mPath = null;


    public void createMediaPlayer(){
        mPlayer = new MediaPlayer();
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener){
        if(mPlayer == null) return;
        mPlayer.setOnPreparedListener(listener);
    }
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        if(mPlayer == null) return;
        mPlayer.setOnCompletionListener(listener);
    }

    public boolean setupPlaying(String path){
        if(mPlayer == null){
            createMediaPlayer();
        }
        if(!MediaUtils.checkURLFormat(path)){
            File file = new File(path);
            if(!file.exists()){
                return false;
            }
        }
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            log.e(TAG, "MediaPlayer prepare() failed");
            return false;
        } catch (IllegalStateException ie) {
            log.e(TAG, "IllegalStateException - MediaPlayer prepare() failed");
            return false;
        } catch (Exception e){
            log.e(TAG, "Error: " + e.getMessage());
            return false;
        }
        mPath = path;
        return true;
    }

    public void startPlaying(int start) {
        if(mPlayer == null) return;

        mPlayer.seekTo(start);
        mPlayer.start();

        mState = PLAYER_STATE_START;
    }

    public void pausePlaying(){
        if(mPlayer == null) return;

        mPlayer.pause();

        mState = PLAYER_STATE_PAUSE;
    }

    public void stopPlaying() {
        if(mPlayer == null) return;

        mPlayer.stop();
        mPlayer.reset();
        mPath = null;

        mState = PLAYER_STATE_STOP;
    }

    public void setSeekTo(int msec){
        if(mPlayer == null) return;

        mPlayer.seekTo(msec);
    }

    public int getDuration(){
        if(mPlayer == null) return -1;

        return mPlayer.getDuration();
    }

    public int getCurrentPosition(){
        if(mPlayer == null) return -1;

        return mPlayer.getCurrentPosition();
    }

    public boolean isPlaying(){
        if(mPlayer == null) return false;

        return mPlayer.isPlaying();
    }

    public boolean isStopped(){
        return mState == PLAYER_STATE_STOP;
    }

    public boolean checkNowPlay(String play){
        return (mPath != null && mPath.equals(play));
    }

    public void releasePlayer(){
        if(mPlayer == null) return;

        if (isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        mPlayer = null;
    }
}
