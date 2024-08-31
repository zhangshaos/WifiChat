package com.example.wifichat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private P2pManager p2pManager;
    private boolean p2pEnabled = false;
    private RecyclerView peersView;
    private PeersRecycleViewAdaptor peersViewAdaptor;
    private Drawable defaultAvatars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        peersView = findViewById(R.id.list_peers);
        peersViewAdaptor = new PeersRecycleViewAdaptor(peersView);
        peersView.setLayoutManager(new GridLayoutManager(this, 3));
        peersView.setAdapter(peersViewAdaptor);
        defaultAvatars = ResourcesCompat.getDrawable(getResources(),
                R.drawable.baseline_3p_24, null);
        //
        setupP2pApiResultAction();
        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        //
        setupBtnScan();
        setupBtnCreateGroup();
        setupBtnJoin();
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
                peersViewAdaptor.setAllPeersModel(peers);
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

    protected void setupBtnScan() {
        Button button = findViewById(R.id.btn_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2pManager.startDiscoverPeers();
            }
        });
    }

    protected void setupBtnCreateGroup() {
        Button button = findViewById(R.id.btn_create_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 判断当前设备是否是GroupOwner
                p2pManager.createP2pGroup();
            }
        });
    }

    protected void setupBtnJoin() {
        Button button = findViewById(R.id.btn_join);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeerModel peer = peersViewAdaptor.getChosenPeer();
                if (peer == null || !peer.device.isGroupOwner()) {
                    Toast.makeText(LoginActivity.this,
                            "选择一个Server对等设备，并加入其网络",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //todo 连接该设备，如果成功则进入聊天室
                Intent intent = new Intent(LoginActivity.this,
                        ChatRoomActivity.class);
                startActivity(intent);
            }
        });
    }
}