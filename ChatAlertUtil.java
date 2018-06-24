package com.caihang.ylyim.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.caihang.ylyim.R;

public class ChatAlertUtil {
    public static void alert(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert);
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
        }
    }
}
