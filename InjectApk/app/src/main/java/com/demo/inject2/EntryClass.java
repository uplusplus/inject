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
            Log.e(TAG, ">>>>>>>>>>>>>Fake wifi info<<<<<<<<<<<<<<");
            Context context = ContexHunter.getContext();
            WifiLocationManager wifiLocation = new WifiLocationManager(context);
            wifiLocation.setCurrrentWifi("PingAn-LifeAgent", "04:bd:88:ed:cf:a0", "80:71:7a:17:a8:91");
            wifiLocation.setNetworkInfo(ConnectivityManager.TYPE_WIFI, 0, "WIFI", "", "PingAn-LifeAgent");
            String[][] result = {
                    {"04:bd:88:ed:cf:b0","PingAn-LifeAgent","-76"},
                    {"88:25:93:21:35:62","TP-LINK光辉部","-59"},
                    {"04:bd:88:ed:cf:a0","PingAn-LifeAgent","-63"},
                    {"84:d4:7e:56:51:c0","PingAn-LifeAgent","-79"},
                    {"80:89:17:a7:56:97","TP-LINK_5697","-93"},
                    {"84:d4:7e:56:47:00","PingAn-LifeAgent","-63"},
                    {"88:25:93:2b:e4:08","dfmj00","-77"},
                    {"04:bd:88:ee:bd:e0","PingAn-LifeAgent","-73"},
                    {"b0:95:8e:12:02:16","TP-LINK_0216","-85"},
                    {"b2:95:8e:12:02:16","TPGuest_0216","-86"},
                    {"88:25:93:43:b8:44","dfmj02","-93"},
                    {"88:25:93:43:b8:14","dfmj01","-83"},
                    {"84:d4:7e:56:47:10","PingAn-LifeAgent","-78"},
                    {"04:bd:88:ee:bd:f0","PingAn-LifeAgent","-77"},
                    {"b0:95:8e:12:02:18","TP-LINK_5G_0216","-90"},
                    {"78:d3:8d:be:84:74","","-88"},
                    {"8c:be:be:27:eb:dd","Xiaomi_tq","-87"},
                    {"50:bd:5f:67:6f:6a","TP-LINK_6F6A","-89"},
                    {"78:d3:8d:be:84:90","","-84"},
                    {"24:69:68:d4:28:c0","SASA","-91"},
                    {"fc:d7:33:71:2f:0c","cxs","-96"},
            };
            wifiLocation.setScanResults(result);
            Log.e(TAG, ">>>>>>>>>>>>> DONE<<<<<<<<<<<<<<");
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.i(TAG, errors.toString());
        }

        return null;
    }
}
