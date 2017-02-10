package com.mnipshagen.planning_machine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nipsh on 02/02/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter <SearchAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        return new ViewHolder(null);
    }

    @Override
    public void onBindViewHolder (SearchAdapter.ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
