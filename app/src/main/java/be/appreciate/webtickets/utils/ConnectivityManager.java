package be.appreciate.webtickets.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class ConnectivityManager
{
    private static List<ConnectivityListener> listeners;

    public static void addListener(ConnectivityListener listener)
    {
        if(listener == null)
        {
            return;
        }

        Context context = ConnectivityManager.getContext(listener);

        if(context == null)
        {
            return;
        }

        if(ConnectivityManager.listeners == null)
        {
            ConnectivityManager.listeners = new ArrayList<>();
        }

        if(!ConnectivityManager.listeners.contains(listener))
        {
            ConnectivityManager.listeners.add(listener);

            if(ConnectivityManager.listeners.size() == 1)
            {
                IntentFilter filter = new IntentFilter();
                filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
                filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                context.registerReceiver(ConnectivityManager.connectionBroadcastReceiver, filter);
            }

            ConnectivityManager.notifyListener(listener, ConnectivityManager.isConnected(context));
        }
    }

    public static void removeListener(ConnectivityListener listener)
    {
        if(listener == null)
        {
            return;
        }

        if(ConnectivityManager.listeners != null && ConnectivityManager.listeners.contains(listener))
        {
            ConnectivityManager.listeners.remove(listener);

            if(ConnectivityManager.listeners.size() == 0)
            {
                Context context = ConnectivityManager.getContext(listener);

                if(context != null)
                {
                    context.unregisterReceiver(ConnectivityManager.connectionBroadcastReceiver);
                }
            }
        }
    }

    private static void notifyListeners(Context context)
    {
        boolean connected = ConnectivityManager.isConnected(context);

        if(ConnectivityManager.listeners != null)
        {
            for(ConnectivityListener listener : ConnectivityManager.listeners)
            {
                ConnectivityManager.notifyListener(listener, connected);
            }
        }
    }

    private static void notifyListener(ConnectivityListener listener, boolean connected)
    {
        if(connected)
        {
            listener.onConnected();
        }
        else
        {
            listener.onDisconnected();
        }
    }

    private static boolean isConnected(Context context)
    {
        if(context != null)
        {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm != null && cm.getActiveNetworkInfo() != null;
        }
        else
        {
            return false;
        }
    }

    private static Context getContext(ConnectivityListener listener)
    {
        if(listener instanceof Context)
        {
            return (Context) listener;
        }
        else if(listener instanceof Fragment)
        {
            Context context = ((Fragment) listener).getContext();
            if(context != null)
            {
                return context;
            }
        }

        return null;
    }

    private static BroadcastReceiver connectionBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ConnectivityManager.notifyListeners(context);
        }
    };

    public interface ConnectivityListener
    {
        void onConnected();
        void onDisconnected();
    }
}
