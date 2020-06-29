package com.meung.hangmangame;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    static private SoundManager instance;
    private static SoundPool mSoundPool;
    private static HashMap<Integer, Integer> mSoundPoolMap;
    private static AudioManager mAudioManager;
    private static Context mContext;

    private SoundManager(){}

    static synchronized public SoundManager getInstance(){
        if(instance == null) instance = new SoundManager();
        return instance;
    }
    public static void initSounds(Context context){
        mContext = context;
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    public static void addSound(int index, int SoundID){
        mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
    }
    public static void loadSounds(){
        mSoundPoolMap.put(1, mSoundPool.load(mContext, R.raw.click, 1));
    }
    public static void playSound(int index, float speed){
        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed);
    }
    public static void stopSound(int index){
        mSoundPool.stop(mSoundPoolMap.get(index));
    }
    public static void cleanup(){
        mSoundPool.release();
        mSoundPool = null;
        mSoundPoolMap.clear();
        mAudioManager.unloadSoundEffects();
        instance = null;
    }
}
