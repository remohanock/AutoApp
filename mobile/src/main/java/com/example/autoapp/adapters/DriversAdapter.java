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

import com.bumptech.glide.Glide;
import com.example.autoapp.R;
import com.example.autoapp.helpers.CircleTransform;
import com.example.autoapp.models.Driver;
import com.example.autoapp.models.DriverList;

import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.DriversAdapterViewHolder> {

    private Context context;
    private DriverList driversList;

    public DriversAdapter(Context context, DriverList driversList) {
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

        driversAdapterViewHolder.tv_drive_name.setText(driversList.getDriverArrayList().get(i).getDriverName());
        Glide.with(context)
                .load(driversList.getDriverArrayList().get(i).getDriverPhoto())
                .transform(new CircleTransform())
                .error(context.getDrawable(R.drawable.driver))
                .into(driversAdapterViewHolder.iv_driver);
    }

    @Override
    public int getItemCount() {
        return driversList.getDriversCount();
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
