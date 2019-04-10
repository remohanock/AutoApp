package com.example.autoapp.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.autoapp.R;
import com.example.autoapp.models.AppsList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsAdapterViewHolder> {

    private AppsList appsList;
    private int selectedPosition = -1;

    /***
     *
     * @param appsList the Applist object
     */
    public AppsAdapter(AppsList appsList) {
        this.appsList = appsList;
    }

    @NonNull
    @Override
    public AppsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.apps_adapter, viewGroup, false);
        return new AppsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsAdapterViewHolder appsAdapterViewHolder, int i) {
        appsAdapterViewHolder.tv_app_name.setText(appsList.getAppsArrayList().get(i).getAppName());
        if (selectedPosition == i) {
            appsAdapterViewHolder.tv_app_name.setSelected(true);
            appsAdapterViewHolder.tv_app_name.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            appsAdapterViewHolder.tv_app_name.setSelected(false);
            appsAdapterViewHolder.tv_app_name.setTextColor(Color.parseColor("#195C7C"));
        }
    }

    @Override
    public int getItemCount() {
        return appsList.getAppsCount();
    }

    class AppsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_app_name;

        AppsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
        }
    }

    public void setSelected(int position) {
        selectedPosition = position;
    }
}
