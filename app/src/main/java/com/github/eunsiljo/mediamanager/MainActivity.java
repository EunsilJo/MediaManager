package com.github.eunsiljo.mediamanager;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.eunsiljo.mediamanager.view.CustomSeekBar;
import com.github.eunsiljo.mediamanagerlib.MediaPlayerManager;
import com.github.eunsiljo.mediamanagerlib.MediaRecorderManager;
import com.github.eunsiljo.mediamanagerlib.SoundPoolManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TextView btnRefresh;
    private ImageView btnRecord;
    private TextView txtRecord;
    private Animation mAnimation;

    private View layoutMediaPlayer;
    private ImageView btnMPPlayPause;
    private CustomSeekBar seekBarMPPlay;
    private TextView txtMPTime;
    private View layoutMPRate;

    private View layoutSoundPool;
    private ImageView btnSPPlayPause;
    private CustomSeekBar seekBarSPPlay;
    private TextView txtSPTime;
    private View layoutSPRate;
    private ImageView btnSPRateUp;
    private ImageView btnSPRateDown;
    private TextView txtSPSpeed;

    private MediaRecorderManager mMediaRecorderManager;
    private MediaPlayerManager mMediaPlayerManager;
    private SoundPoolManager mSoundPoolManager;

    private String mFile;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        initListener();
        initData();
    }

    private void initLayout() {
        btnRefresh = (TextView)findViewById(R.id.btnRefresh);
        btnRecord = (ImageView)findViewById(R.id.btnRecord);
        txtRecord = (TextView)findViewById(R.id.txtRecord);
        mAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.record_anim);

        layoutMediaPlayer = findViewById(R.id.layoutMediaPlayer);
        btnMPPlayPause = (ImageView)layoutMediaPlayer.findViewById(R.id.btnPlayPause);
        seekBarMPPlay = (CustomSeekBar)layoutMediaPlayer.findViewById(R.id.seekBarPlay);
        txtMPTime = (TextView)layoutMediaPlayer.findViewById(R.id.txtTime);
        layoutMPRate = layoutMediaPlayer.findViewById(R.id.layoutRate);
        layoutMPRate.setVisibility(View.GONE);

        layoutSoundPool = findViewById(R.id.layoutSoundPool);
        btnSPPlayPause = (ImageView)layoutSoundPool.findViewById(R.id.btnPlayPause);
        seekBarSPPlay = (CustomSeekBar)layoutSoundPool.findViewById(R.id.seekBarPlay);
        seekBarSPPlay.setThumb(null);
        txtSPTime = (TextView)layoutSoundPool.findViewById(R.id.txtTime);
        layoutSPRate = layoutSoundPool.findViewById(R.id.layoutRate);
        layoutSPRate.setVisibility(View.VISIBLE);
        btnSPRateUp = (ImageView)layoutSoundPool.findViewById(R.id.btnRateUp);
        btnSPRateDown = (ImageView)layoutSoundPool.findViewById(R.id.btnRateDown);
        txtSPSpeed = (TextView)layoutSoundPool.findViewById(R.id.txtSpeed);

        mMediaRecorderManager = new MediaRecorderManager();
        mMediaPlayerManager = new MediaPlayerManager();
        mSoundPoolManager = new SoundPoolManager();
    }

    private void initListener() {
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMediaRecorderManager.isRecording()){
                    setupRecorder();
                }else{
                    onRecord(false);
                    btnRecord.setEnabled(false);

                    if(mFilePath != null) {
                        setupMPPlayer(mFilePath);
                        setupSPPlayer(mFilePath);
                    }
                }
            }
        });

        btnMPPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFilePath == null) return;

                if(mMediaPlayerManager.checkNowPlay(mFilePath)){
                    toggleMPPlayPause();
                }
            }
        });

        btnSPPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFilePath == null) return;

                if(mSoundPoolManager.checkNowPlay(mFilePath)){
                    toggleSPPlayPause();
                }
            }
        });

        seekBarMPPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtMPTime.setText(String.format(getString(R.string.time), formatTime(progress),
                        formatTime(mMediaPlayerManager.getDuration())));
                if(fromUser) {
                    mMediaPlayerManager.setSeekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarSPPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtSPTime.setText(String.format(getString(R.string.time), formatTime(progress),
                        formatTime(mSoundPoolManager.getDuration())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnSPRateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSPRate(true);
            }
        });

        btnSPRateDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSPRate(false);
            }
        });

        mMediaRecorderManager.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what){
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        onRecord(false);
                        Toast.makeText(MainActivity.this, getString(R.string.toast_recorder_info_max_duration),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                        onRecord(false);
                        Toast.makeText(MainActivity.this, getString(R.string.toast_recorder_info_max_file_size),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private MediaPlayer.OnCompletionListener mMPCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            onMPPlay(false);
            seekBarMPPlay.setProgress(0);
        }
    };

    private MediaPlayer.OnCompletionListener mSPCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            onSPPlay(false);
            seekBarSPPlay.setProgress(0);
        }
    };

    private void initData(){
        refresh();
    }

    // =============================================================================
    // MediaRecorderManager
    // =============================================================================

    private void setupRecorder(){
        String file = "/MediaManager" + "_" + System.currentTimeMillis() + ".m4a";
        String path = getDCIMFilePath("/MediaManager", file);

        if(mMediaRecorderManager.setupRecording(path)){
            onRecord(true);
            mFile = file;
            mFilePath = path;
        }else{
            Toast.makeText(MainActivity.this, getString(R.string.toast_recorder_setup_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            mMediaRecorderManager.startRecording();
        } else {
            mMediaRecorderManager.stopRecording();
        }
        updateRecordView(start);
    }

    private void updateRecordView(boolean start) {
        if(start){
            btnRecord.startAnimation(mAnimation);
            txtRecord.setText(getString(R.string.record_stop));
        }else{
            btnRecord.clearAnimation();
            txtRecord.setText(mFile);
        }
        btnRecord.setActivated(start);
    }

    // =============================================================================
    // MediaPlayerManager
    // =============================================================================

    private void setupMPPlayer(String path){
        if(mMediaPlayerManager.setupPlaying(path)){
            mMediaPlayerManager.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    btnMPPlayPause.setActivated(false);
                    seekBarMPPlay.setProgress(0);
                    seekBarMPPlay.setMax(mMediaPlayerManager.getDuration());
                    txtMPTime.setText(String.format(getString(R.string.time), formatTime(0),
                            formatTime(mMediaPlayerManager.getDuration())));

                    mMediaPlayerManager.setOnCompletionListener(mMPCompletionListener);
                }
            });
        }else{
            Toast.makeText(MainActivity.this, getString(R.string.toast_mp_player_setup_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleMPPlayPause() {
        onMPPlay(!btnMPPlayPause.isActivated());
    }

    private void onMPPlay(boolean start) {
        if (start) {
            mMediaPlayerManager.startPlaying(seekBarMPPlay.getProgress());
        } else {
            mMediaPlayerManager.pausePlaying();
        }
        updateMPPlayView(start);
    }

    private void updateMPPlayView(boolean start) {
        if(start){
            mMPHandler.post(mMPProgressChecker);
        }else{
            mMPHandler.removeCallbacks(mMPProgressChecker);
        }
        btnMPPlayPause.setActivated(start);
    }

    private Handler mMPHandler = new Handler();
    private final Runnable mMPProgressChecker = new Runnable() {
        @Override
        public void run() {
            if(mMediaPlayerManager == null) return;

            int pos = mMediaPlayerManager.getCurrentPosition();
            seekBarMPPlay.setProgress(pos);

            mMPHandler.postDelayed(mMPProgressChecker, 1000 - (pos % 1000));
        }
    };

    // =============================================================================
    // SoundPoolManager
    // =============================================================================

    private void setupSPPlayer(String path){
        if(mSoundPoolManager.setupPlaying(getApplicationContext(), path)){
            mSoundPoolManager.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    btnSPPlayPause.setActivated(false);
                    seekBarSPPlay.setProgress(0);
                    seekBarSPPlay.setMax(mSoundPoolManager.getDuration());
                    txtSPTime.setText(String.format(getString(R.string.time), formatTime(0),
                            formatTime(mSoundPoolManager.getDuration())));

                    mSoundPoolManager.setOnCompletionListener(mSPCompletionListener);
                }
            });
        }else{
            Toast.makeText(MainActivity.this, getString(R.string.toast_sp_player_setup_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleSPPlayPause() {
        onSPPlay(!btnSPPlayPause.isActivated());
    }

    private void onSPPlay(boolean start) {
        if (start) {
            mSoundPoolManager.startPlaying();
        } else {
            mSoundPoolManager.pausePlaying();
        }
        updateSPPlayView(start);
    }

    private void onSPRate(boolean up){
        float rate = Math.round(mSoundPoolManager.getRate() * 100f) / 100f;
        if(up){
            if(rate < 2.0f){
                rate = rate + 0.1f;
            }
        }else{
            if(rate > 0.5f){
                rate = rate - 0.1f;
            }
        }
        mSoundPoolManager.setRate(rate);
        updateSPRateView(rate);
    }

    private void updateSPPlayView(boolean start) {
        if(start){
            mSPHandler.post(mSPProgressChecker);
        }else{
            mSPHandler.removeCallbacks(mSPProgressChecker);
        }
        btnSPPlayPause.setActivated(start);
    }

    private void updateSPRateView(float rate) {
        txtSPSpeed.setText(String.format(getString(R.string.speed), rate));
    }

    private Handler mSPHandler = new Handler();
    private final Runnable mSPProgressChecker = new Runnable() {
        @Override
        public void run() {
            if(mSoundPoolManager == null) return;

            int pos = mSoundPoolManager.getCurrentPosition();
            seekBarSPPlay.setProgress(pos);

            mSPHandler.postDelayed(mSPProgressChecker, 1000 - (pos % 1000));
        }
    };

    private void refresh(){
        if(mMediaRecorderManager.isRecording()) {
            mMediaRecorderManager.stopRecording();
        }
        btnRecord.setActivated(false);
        txtRecord.setText(getString(R.string.record_start));

        String time_0 = formatTime(0);

        if(!mMediaPlayerManager.isStopped()) {
            mMediaPlayerManager.stopPlaying();
        }
        btnMPPlayPause.setActivated(false);
        seekBarMPPlay.setProgress(0);
        seekBarMPPlay.setMax(0);
        txtMPTime.setText(String.format(getString(R.string.time), time_0, time_0));

        if(!mSoundPoolManager.isStopped()) {
            mSoundPoolManager.stopPlaying();
        }
        btnSPPlayPause.setActivated(false);
        txtSPTime.setText(String.format(getString(R.string.time), time_0, time_0));
        txtSPSpeed.setText(String.format(getString(R.string.speed), 1.0f));

        mFile = null;
        mFilePath = null;

        btnRecord.setEnabled(true);
    }

    private String formatTime(int msec) {
        int time = msec / 1000;
        int h = time / 3600;
        int m = (time - h * 3600) / 60;
        int s = time - (h * 3600 + m * 60);
        String strTime;
        if (h == 0) {
            strTime = String.format("%02d:%02d", m, s);
        } else {
            strTime = String.format("%d:%02d:%02d", h, m, s);
        }
        return strTime;
    }

    private String getDCIMFilePath(String folder, String file){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        path = path + folder;
        File dir = new File(path);
        dir.mkdirs();
        path = path + file;
        return path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMediaRecorderManager != null){
            mMediaRecorderManager.releaseRecorder();
        }
        if(mMediaPlayerManager != null){
            mMediaPlayerManager.releasePlayer();
        }
        if(mSoundPoolManager != null){
            mSoundPoolManager.releasePlayer();
        }
    }
}
