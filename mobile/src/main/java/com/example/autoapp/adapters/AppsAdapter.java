package com.example.autoapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.autoapp.R;
import com.example.autoapp.models.Apps;

import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsAdapterViewHolder> {

    ArrayList<Apps> appsArrayList;

    public AppsAdapter(Context context, ArrayList<Apps> appsArrayList) {
        this.appsArrayList = appsArrayList;
    }

    @NonNull
    @Override
    public AppsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.apps_adapter, viewGroup, false);
        return new AppsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppsAdapterViewHolder appsAdapterViewHolder, int i) {
        appsAdapterViewHolder.tv_app_name.setText(appsArrayList.get(i).getAppName());
    }

    @Override
    public int getItemCount() {
        return appsArrayList.size();
    }

    class AppsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_app_name;
        AppsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
        }
    }
}
