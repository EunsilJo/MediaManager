MediaManager
===================
[![](https://jitpack.io/v/EunsilJo/MediaManager.svg)](https://jitpack.io/#EunsilJo/MediaManager) [![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)

Android Library that help you to **record and play simple audio resources** easily.

## How to import
Add it in your root build.gradle at the end of repositories:
```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the dependency
```java
dependencies {
	compile 'com.github.EunsilJo:MediaManager:1.0.2'
}
```

## How to use
#### AndroidManifest.xml
Need to include these permissions in your AndroidManifest.xml file.
```java
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```
#### Activity.java
Create constructors and call methods.
```java
mMediaRecorderManager = new MediaRecorderManager();
mMediaPlayerManager = new MediaPlayerManager();
mSoundPoolManager = new SoundPoolManager();
```
Release resources associated with objects.
```java
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
```

### MediaRecorderManager
<img src="https://github.com/EunsilJo/MediaManager/blob/master/screenshots/1.png?raw=true" height="400"/> <img src="https://github.com/EunsilJo/MediaManager/blob/master/screenshots/2.png?raw=true" height="400"/>

The recording control is based on the MediaRecorder. (https://developer.android.com/reference/android/media/MediaRecorder.html)

#### Setup
```java
public boolean setupRecording(String path){
    return setupRecording(MediaRecorder.AudioSource.MIC,
            MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.AudioEncoder.AAC,
            path,
            MAX_SIZE,
            0);
}
```
```java
public boolean setupRecording(int audioSource, int outputFormat, int audioEncoder, String path, long maxSize, int maxDuration)
```
* *int audioSource* : the audio source to use
* *int outputFormat* : the output format to use
* *int audioEncoder* : the audio encoder to use
* *String path* : the output path to use
* *long maxSize* : the maximum filesize in bytes (if zero or negative, disables the limit)
* *int maxDuration* : the maximum duration in ms (if zero or negative, disables the duration limit)

#### Start
```java
public void startRecording()
```
#### Stop
```java
public void stopRecording()
```
##### ++
public boolean isRecording()

public void releaseRecorder()

#### Listener
```java
public void setOnInfoListener(MediaRecorder.OnInfoListener listener)
```
```java
new MediaRecorder.OnInfoListener() {
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        switch (what){
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
            ...
            break;
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
            ...
            break;
        }
    }
});
```

### MediaPlayerManager
<img src="https://github.com/EunsilJo/MediaManager/blob/master/screenshots/3.png?raw=true" height="400"/>

The playing control is based on the MediaPlayer for **longer sound files or streams**. (https://developer.android.com/reference/android/media/MediaPlayer.html)

The MediaPlayer lacks *setRate.

#### Setup
```java
public boolean setupPlaying(String path)
```
* *String path* : the path of the file, or the http/rtsp URL of the stream you want to play

#### Start
```java
public void startPlaying(int start)
```
* *int start* : the offset in milliseconds from the start to seek to
```java
public boolean checkNowPlay(String play)
```
* *String play* : the path of the file

##### ++
public void setSeekTo(int msec)

public int getDuration()

public int getCurrentPosition()


#### Pause / Stop
```java
public void pausePlaying()
```
You should call setupPlaying() to play resources after stopPlaying() is called.
```java
public void stopPlaying()
```

##### ++
public boolean isPlaying()

public boolean isStopped()

public void releasePlayer()


#### Listener
```java
public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener)
```
```java
public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener)
```

### SoundPool
<img src="https://github.com/EunsilJo/MediaManager/blob/master/screenshots/4.png?raw=true" height="400"/>

The playing control is based on the SoundPool for **short clips** (about five minutes). (https://developer.android.com/reference/android/media/SoundPool.html)

The SoundPool lacks *setSeekTo, *getCurrentPosition, *getDuration, *OnCompletionListener.

#### Setup
```java
public boolean setupPlaying(Context context, String path)
```
* *String path* : the path to the audio file

#### Start
```java
public void startPlaying()
```
```java
public boolean checkNowPlay(String play)
```
* *String play* : the path of the file
```java
public void setRate(float rate)
```
* *float rate* : playback rate (1.0 = normal playback, range 0.5 to 2.0)

##### ++
public float getRate()

##### ++made
public int getDuration()

public int getCurrentPosition()

#### Pause / Stop
```java
public void pausePlaying()
```
You should call setupPlaying() to play resources after stopPlaying() is called.
```java
public void stopPlaying()
```

##### ++
public boolean isPlaying()

public boolean isStopped()

public void releasePlayer()


#### Listener
```java
public void setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener listener)
```
##### ++made
```java
public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener)
```

### +
Please check the demo app to see examples.