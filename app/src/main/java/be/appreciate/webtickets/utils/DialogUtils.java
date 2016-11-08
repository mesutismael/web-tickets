package be.appreciate.webtickets.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import be.appreciate.webtickets.R;

/**
 * Created by Inneke De Clippel on 25/03/2016.
 */
public class DialogUtils
{
    public static boolean canShowDialog(Fragment fragment)
    {
        return DialogUtils.canShowDialog(fragment.getContext());
    }

    public static boolean canShowDialog(Context context)
    {
        FragmentActivity activity = DialogUtils.getActivity(context);
        boolean finishing = activity == null || activity.isFinishing();
        boolean destroyed = activity == null || activity.getSupportFragmentManager() == null || activity.getSupportFragmentManager().isDestroyed();

        return !finishing && !destroyed;
    }

    private static FragmentActivity getActivity(Context context)
    {
        // Copied from http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/support/v7/app/MediaRouteButton.java#MediaRouteButton.getActivity%28%29

        while (context != null && context instanceof ContextWrapper)
        {
            if (context instanceof FragmentActivity)
            {
                return (FragmentActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        return null;
    }

    public static MaterialDialog makeErrorDialog(Context context, int messageRes)
    {
        return new MaterialDialog.Builder(context)
                .content(R.string.dialog_error)
                .content(messageRes)
                .positiveText(R.string.dialog_positive)
                .build();
    }

    public static MaterialDialog makeProgressDialog(Context context, int messageRes)
    {
        return new MaterialDialog.Builder(context)
                .content(messageRes)
                .progress(true, 0)
                .cancelable(false)
                .build();
    }

    public static void dismissDialog(MaterialDialog dialog)
    {
        if(dialog != null && DialogUtils.canShowDialog(dialog.getContext()))
        {
            dialog.dismiss();
        }
    }
}
