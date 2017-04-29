package com.example.billy.rocketbeach;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.dift.ui.SwipeToAction;

public class BeachAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Beach> items;

    BeachAdapter(List<Beach> items) {
        this.items = items;
    }

    private class BeachViewHolder extends SwipeToAction.ViewHolder<Beach> {
        TextView titleView;
        TextView authorView;

        BeachViewHolder(View v) {
            super(v);

            titleView = (TextView) v.findViewById(R.id.title);
            authorView = (TextView) v.findViewById(R.id.author);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View beachView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_beach, parent, false);
        return new BeachViewHolder(beachView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Beach item = items.get(position);
        BeachViewHolder beach = (BeachViewHolder) holder;
        beach.titleView.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
