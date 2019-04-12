package com.example.autoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.autoapp.R;
import com.example.autoapp.models.InstalledApps;

import java.util.ArrayList;
import java.util.Collections;

public class InstalledAppsAdapter extends RecyclerView.Adapter<InstalledAppsAdapter.InstalledAppsAdapterViewHolder> implements Filterable {

    private ArrayList<InstalledApps> installedAppsList;
    private ArrayList<InstalledApps> installedAppsListFiltered;
    private Context context;

    /***
     *
     * @param installedAppsList the Applist object
     */
    public InstalledAppsAdapter(Context context, ArrayList<InstalledApps> installedAppsList) {
        this.context = context;
        this.installedAppsList = installedAppsList;
        this.installedAppsListFiltered = installedAppsList;
        Collections.sort(installedAppsListFiltered, (o1, o2) -> o1.getInstalledAppName().compareTo(o2.getInstalledAppName()));
    }

    @NonNull
    @Override
    public InstalledAppsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.installed_apps_adapter, viewGroup, false);
        return new InstalledAppsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledAppsAdapterViewHolder installedAppsAdapterViewHolder, int i) {
        installedAppsAdapterViewHolder.tv_app_name.setText(installedAppsListFiltered.get(i).getInstalledAppName());
        installedAppsAdapterViewHolder.tv_package_name.setText(installedAppsListFiltered.get(i).getAppPackageName());
        if (installedAppsListFiltered.get(i).getDrawable() != null) {
            installedAppsAdapterViewHolder.iv_installed_apps.setImageDrawable(installedAppsListFiltered.get(i).getDrawable());
        } else {
            installedAppsAdapterViewHolder.iv_installed_apps.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.mipmap.ic_launcher, null));
        }
        installedAppsAdapterViewHolder.cl_installed_apps.setOnClickListener(v -> {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(installedAppsListFiltered.get(i).getAppPackageName());
            if (launchIntent != null) {
                context.startActivity(launchIntent);//null pointer check in case package name was not found
            }
        });

    }

    @Override
    public int getItemCount() {
        return installedAppsListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    installedAppsListFiltered = installedAppsList;
                } else {
                    ArrayList<InstalledApps> appsFiltered = new ArrayList<>();
                    for (InstalledApps apps : installedAppsList) {
                        if (apps.getInstalledAppName().toLowerCase().contains(charString.toLowerCase()) ||
                                apps.getAppPackageName().toLowerCase().contains(charString.toLowerCase())) {
                            appsFiltered.add(apps);
                        }
                    }
                    installedAppsListFiltered = appsFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = installedAppsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                installedAppsListFiltered = (ArrayList<InstalledApps>) results.values;
                Collections.sort(installedAppsListFiltered, (o1, o2) -> o1.getInstalledAppName().compareTo(o2.getInstalledAppName()));
                notifyDataSetChanged();
            }
        };
    }


    class InstalledAppsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_app_name, tv_package_name;
        private ImageView iv_installed_apps;
        private ConstraintLayout cl_installed_apps;

        InstalledAppsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            tv_package_name = itemView.findViewById(R.id.tv_package_name);
            iv_installed_apps = itemView.findViewById(R.id.iv_installed_apps);
            cl_installed_apps = itemView.findViewById(R.id.cl_installed_apps);
        }
    }
}
