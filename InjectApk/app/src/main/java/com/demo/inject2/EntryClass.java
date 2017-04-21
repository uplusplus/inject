package com.demo.inject2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * @author boyliang
 */
public final class EntryClass {
    public static final String TAG = "INJECT";

    public static Object[] invoke(int i) {

        try {
            Log.i(TAG, ">>>>>>>>>>>>>Fake wifi info<<<<<<<<<<<<<<");
            Context context = ContexHunter.getContext();
            WifiLocationManager wifiLocation = new WifiLocationManager(context);
            wifiLocation.setCurrrentWifi("PingAn-LifeAgent", "04:bd:88:ed:cf:a0", "80:71:7a:17:a8:91");
            wifiLocation.setNetworkInfo(ConnectivityManager.TYPE_WIFI, 0, "WIFI", "", "PingAn-LifeAgent");
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.i(TAG, errors.toString());
        }

        return null;
    }
}
