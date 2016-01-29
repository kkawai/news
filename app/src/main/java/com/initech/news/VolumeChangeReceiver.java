package com.initech.news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.initech.news.view.drag.DragAudioManager;

/**
 * Created by kevin on 11/25/2015.
 */
public class VolumeChangeReceiver extends BroadcastReceiver {

   @Override
   public void onReceive(final Context context, final Intent intent) {
      DragAudioManager.getInstance().updateMasterVolume(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC));
   }
}
