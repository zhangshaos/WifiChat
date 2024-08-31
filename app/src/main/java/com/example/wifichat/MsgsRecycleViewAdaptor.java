package com.example.wifichat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MsgsRecycleViewAdaptor extends
        RecyclerView.Adapter<MsgsRecycleViewAdaptor.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, msg;
        ImageView avatars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName2);
            msg = itemView.findViewById(R.id.textMsg2);
            avatars = itemView.findViewById(R.id.imageAvatars2);
        }
    }

    WeakReference<RecyclerView> recycleView;
    ArrayList<MsgModel> messages;

    public MsgsRecycleViewAdaptor(RecyclerView view) {
        recycleView = new WeakReference<>(view);
        messages = new ArrayList<>(10);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MsgModel m = messages.get(position);
        holder.name.setText(m.name);
        holder.msg.setText(m.msg);
        holder.avatars.setImageDrawable(m.avatars);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}
