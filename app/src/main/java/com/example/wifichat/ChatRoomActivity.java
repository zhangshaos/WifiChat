package com.example.wifichat;

import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {
    private static final String TAG = ChatRoomActivity.class.getName();

    P2pManager p2pManager;
    boolean p2pEnabled = false;
    private Drawable defaultAvatars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        defaultAvatars = ResourcesCompat.getDrawable(getResources(),
                R.drawable.baseline_3p_24, null);
        //
        setupP2pApiResultAction();
    }

    protected void setupP2pApiResultAction() {
        p2pManager = MyApp.getP2pManger();
        p2pManager.setP2pApiResultAction(this, new P2pManager.P2pApiResultAction() {
            @Override
            public void onP2pPeersChanged(WifiP2pDeviceList devices) {
                Log.d(TAG, String.format("onP2pPeersChanged: %s", devices.toString()));
                ArrayList<PeerModel> peers = new ArrayList<>();
                for (WifiP2pDevice device : devices.getDeviceList()) {
                    PeerModel m = new PeerModel();
                    m.name = String.format("%s(%s)", device.deviceName,
                            device.isGroupOwner() ? "Server" : "Client");
                    m.avatars = defaultAvatars;
                    m.device = device;
                    peers.add(m);
                }
                //peersViewAdaptor.setAllPeersModel(peers);
            }

            @Override
            public void onP2pStateChanged(boolean enabled) {
                Log.d(TAG, String.format("onP2pStateChanged: %b", enabled));
                p2pEnabled = enabled;
            }

            @Override
            public void onP2pConnectionChangedAction(WifiP2pInfo p2pInfo,
                                                     NetworkInfo networkInfo,
                                                     WifiP2pGroup p2pGroup) {
                Log.d(TAG, String.format("onP2pConnectionChangedAction: %s", p2pInfo.toString()));
            }

            @Override
            public void onP2pThisDeviceChangedAction(WifiP2pDevice device) {
                Log.d(TAG, String.format("onP2pThisDeviceChangedAction: %s", device.toString()));
            }
        });
    }
}