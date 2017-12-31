package com.github.eunsiljo.mediamanagerlib;

import android.media.MediaRecorder;
import android.util.Log;

import com.github.eunsiljo.mediamanagerlib.utils.log;

import java.io.IOException;


/**
 * Created by EunsilJo on 2016. 11. 3..
 */

public class MediaRecorderManager {
    public static String TAG = MediaRecorderManager.class.getSimpleName();

    private static final long MAX_SIZE = 3000000;

    private MediaRecorder mRecorder = null;

    private final int RECORDER_START = 0;
    private final int RECORDER_STOP = 1;

    private int mState = RECORDER_STOP;

    private String mPath = null;


    public void createMediaRecorder(){
        mRecorder = new MediaRecorder();
    }

    public void setOnInfoListener(MediaRecorder.OnInfoListener listener) {
        if(mRecorder == null) return;
        mRecorder.setOnInfoListener(listener);
    }

    public boolean setupRecording(String path){
        return setupRecording(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4,
                MediaRecorder.AudioEncoder.AAC,
                path,
                MAX_SIZE,
                0);
    }

    public boolean setupRecording(int audioSource, int outputFormat, int audioEncoder,
                                  String path, long maxSize, int maxDuration){
        if(mRecorder == null){
            createMediaRecorder();
        }
        try {
            mRecorder.setAudioSource(audioSource);
            mRecorder.setOutputFormat(outputFormat);
            mRecorder.setAudioEncoder(audioEncoder);
            mRecorder.setOutputFile(path);
            mRecorder.setMaxFileSize(maxSize);
            mRecorder.setMaxDuration(maxDuration);
            mRecorder.prepare();
        } catch (IOException e) {
            log.e(TAG, "MediaRecorder prepare() failed");
            return false;
        } catch (IllegalStateException ie){
            log.e(TAG, "IllegalStateException - MediaRecorder prepare() failed");
            return false;
        } catch (Exception e){
            log.e(TAG, "Error: " + e.getMessage());
            return false;
        }
        mPath = path;
        return true;
    }

    public void startRecording() {
        if(mRecorder == null) return;

        mRecorder.start();

        mState = RECORDER_START;
    }

    public void stopRecording() {
        if(mRecorder == null) return;

        mRecorder.stop();
        mRecorder.reset();

        mState = RECORDER_STOP;
    }

    public boolean isRecording(){
        return mState == RECORDER_START;
    }

    public void releaseRecorder(){
        if(mRecorder == null) return;

        if (isRecording()) {
            mRecorder.stop();
        }
        mRecorder.release();
        mRecorder = null;
    }
}
