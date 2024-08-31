package com.example.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

public class P2pApiResultReceiver extends BroadcastReceiver {

    private P2pManager.P2pApiResultAction apiResultAction;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (apiResultAction != null) {
                apiResultAction.onP2pStateChanged(
                        state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            WifiP2pDeviceList deviceList = intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
            if (apiResultAction != null) {
                apiResultAction.onP2pPeersChanged(deviceList);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            WifiP2pInfo p2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pGroup p2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            if (apiResultAction != null) {
                apiResultAction.onP2pConnectionChangedAction(p2pInfo, networkInfo, p2pGroup);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            if (apiResultAction != null) {
                apiResultAction.onP2pThisDeviceChangedAction(device);
            }

        }
    }

    public void setP2pApiResultAction(P2pManager.P2pApiResultAction apiResultAction) {
        this.apiResultAction = apiResultAction;
    }
}