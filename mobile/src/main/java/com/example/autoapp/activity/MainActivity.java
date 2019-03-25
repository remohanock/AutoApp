package com.example.autoapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autoapp.R;
import com.example.autoapp.adapters.AppsAdapter;
import com.example.autoapp.controller.ObjectsController;
import com.example.autoapp.helpers.CircleTransform;
import com.example.autoapp.helpers.ItemClickSupport;
import com.example.autoapp.models.Apps;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_apps;
    private ImageView iv_album_art;
    private FrameLayout fl_app_detail;
    private TextView tv_in_progress;

    String driverImage = "";
    private ObjectsController objectsController;
    private int selectedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        objectsController = new ObjectsController();
        getExtras();
        bindControls();
        setAppsListItems();
        setMediaPlayer();
    }

    /***
     * Set the media player view and functionality
     */
    private void setMediaPlayer() {
        Glide.with(this).load("https://i.imgur.com/jAwu0Xk.png").error(android.R.drawable.ic_menu_gallery).into(iv_album_art);
    }

    /***
     * Set the list of apps in the top Intelligent bar
     */
    private void setAppsListItems() {

        String[] appsNames = {"Contact", "Calendar", "Camera", "Weather", "News"};
        for (int i = 0; i < appsNames.length; i++) {
            objectsController.setApps(new Apps(i, appsNames[i]));
        }
        final AppsAdapter appsAdapter = new AppsAdapter(this, objectsController.getAppsList());
        rv_apps.setAdapter(appsAdapter);
        ItemClickSupport.addTo(rv_apps).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                tv_in_progress.setText(objectsController.getApps(position).getAppName() + " is in progress");
                if (fl_app_detail.getVisibility() == View.GONE) {
                    fl_app_detail.setVisibility(View.VISIBLE);
                } else {
                    if (selectedPosition == position)   //for checking if same item is opened or not
                        fl_app_detail.setVisibility(View.GONE);
                }
                selectedPosition = position;
            }
        });
    }

    /***
     * Bind the view elements
     */
    private void bindControls() {
        rv_apps = findViewById(R.id.rv_apps);
        iv_album_art = findViewById(R.id.iv_album_art);
        fl_app_detail = findViewById(R.id.fl_app_detail);
        tv_in_progress = findViewById(R.id.tv_in_progress);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this)
                .load(driverImage)
                .transform(new CircleTransform())
                .error(getDrawable(R.drawable.driver))
                .into(iv_profile);
        rv_apps.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    /***
     * get the parameters sent from the previous activity.
     */
    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get("DriverName") != null) {
                Toast.makeText(this, Objects.requireNonNull(bundle.get("DriverName")).toString(), Toast.LENGTH_SHORT).show();
            }
            if (bundle.get("DriverPhoto") != null) {
                driverImage = Objects.requireNonNull(bundle.get("DriverPhoto")).toString();
            }
        }
    }
}
