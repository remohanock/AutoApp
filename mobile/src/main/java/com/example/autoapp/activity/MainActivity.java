package com.example.autoapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autoapp.R;
import com.example.autoapp.adapters.AppsAdapter;
import com.example.autoapp.helpers.CircleTransform;
import com.example.autoapp.models.Apps;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_apps;
    private ArrayList<Apps> appsArrayList;
    private ImageView iv_profile;
    String driverImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getExtras();
        bindControls();
        setAppsListItems();
    }

    private void setAppsListItems() {
        appsArrayList = new ArrayList<>();
        String[] appsNames = {"Contact", "Calendar", "Camera", "Weather", "News"};
        for (int i = 0; i < appsNames.length; i++) {
            appsArrayList.add(new Apps(i, appsNames[i]));
        }
        AppsAdapter appsAdapter = new AppsAdapter(this, appsArrayList);
        rv_apps.setAdapter(appsAdapter);
    }

    private void bindControls() {
        rv_apps = findViewById(R.id.rv_apps);
        iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this)
                .load(driverImage)
                .transform(new CircleTransform())
                .error(getDrawable(R.drawable.driver))
                .into(iv_profile);
        rv_apps.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get("DriverName") != null) {
                Toast.makeText(this, Objects.requireNonNull(bundle.get("DriverName")).toString(), Toast.LENGTH_SHORT).show();
            }
            if(bundle.get("DriverPhoto") != null){
                driverImage = Objects.requireNonNull(bundle.get("DriverPhoto")).toString();
            }
        }
    }
}
