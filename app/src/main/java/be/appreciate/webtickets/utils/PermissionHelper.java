package be.appreciate.webtickets.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class PermissionHelper
{
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    public static String[] getCameraPermission()
    {
        return new String[]{PERMISSION_CAMERA};
    }

    public static boolean hasCameraPermission(Context context)
    {
        return ContextCompat.checkSelfPermission(context, PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean allPermissionsGranted(int[] grantResults)
    {
        if(grantResults != null)
        {
            for(int result : grantResults)
            {
                if(result != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }

        return true;
    }
}
