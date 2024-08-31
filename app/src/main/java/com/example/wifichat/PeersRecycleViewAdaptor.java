package com.example.wifichat;

import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;

public class PeersRecycleViewAdaptor extends
        RecyclerView.Adapter<PeersRecycleViewAdaptor.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView avatars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            avatars = itemView.findViewById(R.id.imageAvatars);
        }

        @Override
        public void onClick(View view) {
            //只在主线程中更新
            int lastChosenPeerIdx = chosenPeerIndex;
            //chosenPeerIndex = getLayoutPosition();
            chosenPeerIndex = getAdapterPosition();
            //重置之前选择的
            if (0 <= lastChosenPeerIdx && lastChosenPeerIdx < peers.size()) {
                notifyItemChanged(lastChosenPeerIdx);
            }
            //更新现在选择的
            notifyItemChanged(chosenPeerIndex);
        }
    }

    private WeakReference<RecyclerView> recycleView;
    private ArrayList<PeerModel> peers;
    //只在UI线程中读写
    private int chosenPeerIndex = -1;

    public PeersRecycleViewAdaptor(RecyclerView view) {
        recycleView = new WeakReference<>(view);
        peers = new ArrayList<>(10);
    }

    @NonNull
    @Override
    public PeersRecycleViewAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peer,
                parent, false);
        ViewHolder result = new ViewHolder(v);
        v.setOnClickListener(result);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull PeersRecycleViewAdaptor.ViewHolder holder,
                                 int position) {
        PeerModel peerModel = peers.get(position);
        holder.name.setText(peerModel.name);
        holder.avatars.setImageDrawable(peerModel.avatars);
        if (position == chosenPeerIndex) {
            holder.avatars.setBackgroundColor(Color.RED);
        } else {
            holder.avatars.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    //只在主线程中调用
    public int addPeerModel(PeerModel peerModel, boolean scroll) {
        chosenPeerIndex = -1;
        //
        int pos = peers.size();
        peers.add(peerModel);
        notifyItemInserted(pos);
        if (scroll) {
            recycleView.get().scrollToPosition(pos);
        }
        return pos;
    }

    //只在主线程中调用
    public int addAllPeersModel(ArrayList<PeerModel> peersModel, boolean scroll) {
        chosenPeerIndex = -1;
        //
        int posStart = peers.size();
        peers.addAll(peersModel);
        notifyItemRangeInserted(posStart, peersModel.size());
        int pos = peers.size() - 1;
        if (scroll) {
            recycleView.get().scrollToPosition(pos);
        }
        return pos;
    }

    //只在主线程中调用
    public void setAllPeersModel(ArrayList<PeerModel> peersModel) {
        chosenPeerIndex = -1;
        //
        if (peersModel.size() < peers.size()) {
            int count = peers.size() - peersModel.size();
            peers = peersModel;
            notifyItemRangeRemoved(peers.size(), count);
        } else if (peersModel.size() > peers.size()) {
            int start = peers.size();
            int count = peersModel.size() - peers.size();
            peers = peersModel;
            notifyItemRangeInserted(start, count);
        } else {
            peers = peersModel;
        }
        if (!peers.isEmpty()) {
            notifyItemRangeChanged(0, peers.size());
        }
    }

    //只在主线程中调用
    @Nullable
    public PeerModel getChosenPeer() {
      if (0 <= chosenPeerIndex && chosenPeerIndex < peers.size()) {
          return peers.get(chosenPeerIndex);
      }
      return null;
    }
}
