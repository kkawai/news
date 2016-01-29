package com.initech.news.view.drag;

import android.content.Context;
import android.media.AudioManager;

import com.initech.news.NewsApplication;
import com.initech.news.util.MLog;

/**
 * Created by kevin on 11/25/2015.
 */
public class DragAudioManager {

   private static final String TAG = "DragAudioManager";
   private static DragAudioManager sInstance;
   private AudioManager mAudioManager;
   private int mCurrentVol = -1;
   private int mMasterVol;
   private boolean mIsSelfAdjusting;

   private DragAudioManager() {
   }

   public static DragAudioManager getInstance() {
      if (sInstance == null) {
         sInstance = new DragAudioManager();
         sInstance.mAudioManager = (AudioManager) NewsApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
      }
      return sInstance;
   }

   /**
    * Allows changing of music streaming volume per some given alpha value.
    *
    * @param alpha
    */
   public void adjustVolume(final float alpha) {
      mIsSelfAdjusting = true;
      if (mCurrentVol == -1) {
         mCurrentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
      }
      if (mCurrentVol != mMasterVol)
         mCurrentVol = mMasterVol;
      final int tempVol = ((AudioManager) NewsApplication.getInstance().getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC);
      MLog.i(TAG, "current volume: " + tempVol + " mCurrentVol: " + mCurrentVol + " " + (int) (mCurrentVol * alpha) + " alpha: " + alpha + " " +
            "master: " + mMasterVol);
      if (tempVol != 0 && mCurrentVol == 0) {
         mCurrentVol = tempVol;  //weird bug fix; where volume goes to zero
      }
      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (mCurrentVol * alpha), 0);
      if (alpha == 1.0f)
         mIsSelfAdjusting = false;
   }

   /**
    * For some reason, updating the master volume rocker doesn't update
    * subsequent calls to getStreamVolume(). So, this method allows
    * us to keep track of the master volume and sync with mini-player
    * horizontal swipes.
    *
    * @param newVolume
    */
   public void updateMasterVolume(final int newVolume) {
      if (mIsSelfAdjusting) {
         MLog.i(TAG, "mIsSelfAdjusting dont update master vol");
         return;
      }
      mMasterVol = newVolume;
      MLog.i(TAG, "new master vol: " + newVolume);
   }

   public void setLastVolume() {
      if (mCurrentVol == -1)
         return;
      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVol, 0);
      MLog.i(TAG, "setLastVolume " + mCurrentVol);
      mCurrentVol = -1;
      mIsSelfAdjusting = false;
   }

}
