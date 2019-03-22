package com.example.autoapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.autoapp.R;
import com.example.autoapp.models.Driver;

import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.DriversAdapterViewHolder> {

    private Context context;
    private ArrayList<Driver> driversList;

    public DriversAdapter(Context context, ArrayList<Driver> driversList) {
        this.context = context;
        this.driversList = driversList;
    }

    @NonNull
    @Override
    public DriversAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.driver_adapter, viewGroup, false);
        return new DriversAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriversAdapterViewHolder driversAdapterViewHolder, int i) {
        driversAdapterViewHolder.tv_drive_name.setText(driversList.get(i).getDriverName());
    }

    @Override
    public int getItemCount() {
        return driversList.size();
    }

    class DriversAdapterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_driver;
        ImageView iv_driver;
        TextView tv_drive_name;

        DriversAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_driver = itemView.findViewById(R.id.ll_driver);
            iv_driver = itemView.findViewById(R.id.iv_driver);
            tv_drive_name = itemView.findViewById(R.id.tv_drive_name);
        }
    }
}
