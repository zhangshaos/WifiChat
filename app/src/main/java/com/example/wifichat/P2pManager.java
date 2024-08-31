package com.example.wifichat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class P2pManager {

    public interface P2pApiResultAction {
        void onP2pPeersChanged(WifiP2pDeviceList devices);

        void onP2pStateChanged(boolean enabled);

        void onP2pConnectionChangedAction(WifiP2pInfo p2pInfo,
                                          NetworkInfo networkInfo, WifiP2pGroup p2pGroup);

        void onP2pThisDeviceChangedAction(WifiP2pDevice device);
    }

    private WifiManager wifiManager;
    private LocationManager locationManager;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private P2pApiResultReceiver apiResultReceiver;

    private Activity attachedActivity;

    public void setP2pApiResultAction(Activity activity, P2pApiResultAction action) {
        assert apiResultReceiver != null;
        apiResultReceiver.setP2pApiResultAction(action);
        attachedActivity = activity;
    }

    public void onInit() {
        Context context = MyApp.getAppContext();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);
        apiResultReceiver = new P2pApiResultReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        context.registerReceiver(apiResultReceiver, intentFilter);
    }

    public void onDestroy() {
        Context context = MyApp.getAppContext();
        context.unregisterReceiver(apiResultReceiver);
    }

    class MyActionListener implements WifiP2pManager.ActionListener {
        String name;

        MyActionListener(String name) {
            this.name = name;
        }

        @Override
        public void onSuccess() {
            Context context = MyApp.getAppContext();
            Toast.makeText(context, String.format("%s: Success", name),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int i) {
            Context context = MyApp.getAppContext();
            Toast.makeText(context, String.format("%s: Failure(%d)", name, i),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestPermission() {
        assert attachedActivity != null;
        //检测硬件是否打开
        boolean result = wifiManager.isWifiEnabled() &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!result) {
            Toast.makeText(attachedActivity, "请打开Wi-Fi和GPS功能",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        //检测权限
        final String[] permissions = new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };
        result = ActivityCompat.checkSelfPermission(
                attachedActivity, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        if (!result) {
            ActivityCompat.requestPermissions(attachedActivity, permissions, 0);
            return false;
        }
        return true;
    }

    //@SuppressLint("MissingPermission")
    public void startDiscoverPeers() {
        if (checkAndRequestPermission()) {
            manager.discoverPeers(channel, new MyActionListener("startDiscoverPeers"));
        }
    }

    public void stopDiscoverPeers() {
        manager.stopPeerDiscovery(channel, new MyActionListener("stopDiscoverPeers"));
    }

    //@SuppressLint("MissingPermission")
    public void createP2pGroup() {
        if (checkAndRequestPermission()) {
            manager.createGroup(channel, new MyActionListener("createP2pGroup"));
        }
    }

    public void removeP2pGroup() {
        manager.removeGroup(channel, new MyActionListener("removeP2pGroup"));
    }

    //@SuppressLint("MissingPermission")
    public void connect(WifiP2pConfig config) {
        if (checkAndRequestPermission()) {
            manager.connect(channel, config, new MyActionListener("connect"));
        }
    }

    public void cancelConnect() {
        manager.cancelConnect(channel, new MyActionListener("cancelConnect"));
    }
}
