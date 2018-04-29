package com.example.jasim.plateup.Check;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jasim on 20.01.2018.
 */

public class Connection {
    Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    public Connection(Context context) {
        this.context = context;
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
    }

    public boolean getConnected() {
        return connected;
    }

}
