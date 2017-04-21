package com.demo.inject2;

/**
 * Created by uplusplus on 2017/4/9.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class WifiLocationManager {
    private static final  String TAG = "WifiLocationManager";
    private Context mContext;
    private WifiManager wifiManager;
    ConnectivityManager connectivityManager;

    public WifiLocationManager(Context context){
        mContext = context;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static Field sSystemContext_field;
    public static Context getSystemContext() throws Exception {
        Object context = null;
        if (sSystemContext_field == null) {
            Class<?> ActivityThread_class = Class.forName("android.app.ActivityThread");
            sSystemContext_field = ActivityThread_class.getDeclaredField("mSystemContext");
            sSystemContext_field.setAccessible(true);
        }

        context = sSystemContext_field.get(null);

        return (Context) context;
    }

    public List<ScanResult> getWifiList(){
        return wifiManager.getScanResults();
    }

/*
    bssid=08:10:77:f9:2a:1b;freq=2472;level=-44;flags=43;ssid=Netcore_2_4G;
    bssid=94:77:2b:27:e5:0c;freq=2412;level=-69;flags=11;ssid=lady_x;
    bssid=ec:26:ca:a5:f4:65;freq=2462;level=-75;flags=11;ssid=swj1993;
    bssid=ec:26:ca:34:64:90;freq=2462;level=-84;flags=11;ssid=TP-LINK88888;
    bssid=8e:25:93:ac:91:fe;freq=2412;level=-91;flags=11;ssid=dongruitianyou2;

     holder.put: {
     "version":"1.1.0",
     "host":"maps.google.com",
     "address_language":"zh_CN",
     "request_address":true,
     "wifi_towers":[
        {"mac_address":"08:10:77:f9:2a:1b","ssid":"Netcore_2_4G","signal_strength":-42},
        {"mac_address":"94:77:2b:27:e5:0c","ssid":"lady_x","signal_strength":-70},
        {"mac_address":"ec:26:ca:a5:f4:65","ssid":"swj1993","signal_strength":-75},
        {"mac_address":"ec:26:ca:34:64:90","ssid":"TP-LINK88888","signal_strength":-69},
        {"mac_address":"c0:61:18:92:f8:a6","ssid":"TP-LINK_F8A6","signal_strength":-85}]}
* */

    public String getWifiInfo(){
        List<ScanResult> wifiList = getWifiList();
        StringBuilder sb = new StringBuilder();
        sb.append("\n附近WIFI信号:\n");
        for (int i = 0; i < wifiList.size(); i++) {
            JSONObject tower = new JSONObject();
            try {
                tower.put("mac_address", wifiList.get(i).BSSID);
                tower.put("ssid", wifiList.get(i).SSID);
                tower.put("signal_strength", wifiList.get(i).level);
                sb.append(tower.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
/*
    bssid=08:10:77:f9:2a:1b;freq=2472;level=-44;flags=43;ssid=Netcore_2_4G;
    bssid=94:77:2b:27:e5:0c;freq=2412;level=-69;flags=11;ssid=lady_x;
    bssid=ec:26:ca:a5:f4:65;freq=2462;level=-75;flags=11;ssid=swj1993;
    bssid=ec:26:ca:34:64:90;freq=2462;level=-84;flags=11;ssid=TP-LINK88888;
    bssid=8e:25:93:ac:91:fe;freq=2412;level=-91;flags=11;ssid=dongruitianyou2;

     holder.put: {
     "version":"1.1.0",
     "host":"maps.google.com",
     "address_language":"zh_CN",
     "request_address":true,
     "wifi_towers":[
        {"mac_address":"08:10:77:f9:2a:1b","ssid":"Netcore_2_4G","signal_strength":-42},
        {"mac_address":"94:77:2b:27:e5:0c","ssid":"lady_x","signal_strength":-70},
        {"mac_address":"ec:26:ca:a5:f4:65","ssid":"swj1993","signal_strength":-75},
        {"mac_address":"ec:26:ca:34:64:90","ssid":"TP-LINK88888","signal_strength":-69},
        {"mac_address":"c0:61:18:92:f8:a6","ssid":"TP-LINK_F8A6","signal_strength":-85}]}
* */

    public static String toAscii(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    Method set_ScanResults;
    public  boolean setScanResults(String[][] list){
        if(set_ScanResults == null){
            Class<?> imclass = wifiManager.getClass();
            try {
                set_ScanResults = imclass.getMethod("setScanResults",  new Class[]{String[][].class});
            } catch (Exception e) {
                Log.e(TAG,  "Unsupport system.");
                return false;
            }
        }

        try {
            if(set_ScanResults != null)
            {
                for(int i=0; i<list.length; i++){
                    list[i][1] = toAscii( list[i][1] );
                }

                set_ScanResults.invoke(wifiManager, new Object[]{list});
            }
        } catch (Exception e) {
            Log.e(TAG,  "Unsupport system.");
            return false;
        }

        return true;
    }

    public String getCurrentWifi(){
        StringBuilder sb = new StringBuilder();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        sb.append("当前连接wifi：\n"+wifiInfo.toString());
        return sb.toString();
    }

    Method set_current_wifi;
    public  boolean setCurrrentWifi(String ssid, String bssid, String mac){
        if(set_current_wifi == null){
            Class<?> imclass = wifiManager.getClass();
            try {
                set_current_wifi = imclass.getMethod("setConnectionInfo", String.class, String.class, String.class);
            } catch (Exception e) {
                Log.e(TAG,  "Unsupport system.");
                return false;
            }
        }

        try {
            if(set_current_wifi != null)
            {
                String utf_ssid = toAscii(ssid);
                set_current_wifi.invoke(wifiManager, utf_ssid, bssid, mac);
            }
        } catch (Exception e) {
            Log.e(TAG,  "Unsupport system.");
            return false;
        }

        return true;
    }

    Method setActiveNetworkInfo;
    public boolean setNetworkInfo(int type, int subtype, String typeName, String subtypeName, String extraInfo){
        if(setActiveNetworkInfo == null){
            Class<?> imclass = connectivityManager.getClass();
            try {
                setActiveNetworkInfo = imclass.getMethod("setActiveNetworkInfo", int.class, int.class, String.class, String.class, String.class);
            } catch (Exception e) {
                Log.e(TAG,  "Unsupport system.");
                return false;
            }
        }

        if(setActiveNetworkInfo != null)
        {
            try {
                setActiveNetworkInfo.invoke(connectivityManager, type, subtype, typeName, subtypeName, extraInfo);
            } catch (Exception e) {
                Log.e(TAG,  "Unsupport system.");
                return false;
            }
        }

        return true;
    }

    public  boolean isNetworkAvaliable(){
        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
        return !(networkinfo == null || !networkinfo.isAvailable());
    }

    public  boolean isWifiNetwrokType() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isAvailable()) {
            if (info.getTypeName().equalsIgnoreCase("wifi")) {
                return true;
            }
        }
        return false;
    }

    private static String type2String(int type){
        switch (type){
            case        ConnectivityManager.TYPE_MOBILE: return "MOBILE";
            case        ConnectivityManager.TYPE_WIFI: return "WIFI";
            case        ConnectivityManager.TYPE_WIMAX: return "WIMAX";
            case        ConnectivityManager.TYPE_ETHERNET: return "ETHERNET";
            case        ConnectivityManager.TYPE_BLUETOOTH: return "BLUETOOTH";
            default: return "Unkown";
        }
    }

    public String getCurrrentNetworkInfo(){
        StringBuilder sb = new StringBuilder();
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null)
            sb.append("Net Type:" + type2String(info.getType()) + "\n" + info.toString());
        else
            sb.append("Not connected.");
        return  sb.toString();
    }
}