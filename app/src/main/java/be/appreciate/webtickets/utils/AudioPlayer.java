package be.appreciate.webtickets.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import be.appreciate.webtickets.R;

/**
 * Created by Inneke De Clippel on 31/03/2016.
 */
public class AudioPlayer
{
    private static SoundPool soundPool;
    private static int soundIdSuccess;
    private static int soundIdError;

    public static void initSoundPool(Context context)
    {
        if(AudioPlayer.soundPool == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .build();

                AudioPlayer.soundPool = new SoundPool.Builder()
                        .setMaxStreams(1)
                        .setAudioAttributes(audioAttributes)
                        .build();
            }
            else
            {
                AudioPlayer.soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
            }

            AudioPlayer.soundIdSuccess = AudioPlayer.soundPool.load(context, R.raw.scan_succes, 1);
            AudioPlayer.soundIdError = AudioPlayer.soundPool.load(context, R.raw.scan_fail, 1);
        }
    }

    public static void playSuccessSound()
    {
        if(AudioPlayer.soundPool != null)
        {
            AudioPlayer.soundPool.play(AudioPlayer.soundIdSuccess, 1, 1, 1, 0, 1);
        }
    }

    public static void playErrorSound()
    {
        if(AudioPlayer.soundPool != null)
        {
            AudioPlayer.soundPool.play(AudioPlayer.soundIdError, 1, 1, 1, 0, 1);
        }
    }
}
