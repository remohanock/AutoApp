package com.example.autoapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autoapp.R;
import com.example.autoapp.adapters.AppsAdapter;
import com.example.autoapp.controller.ObjectsController;
import com.example.autoapp.helpers.CircleTransform;
import com.example.autoapp.helpers.ItemClickSupport;
import com.example.autoapp.models.Apps;

import java.text.MessageFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_apps;
    private ImageView iv_album_art;
    private FrameLayout fl_app_detail;
    private TextView tv_in_progress;
    private ScrollView scroll_main;
    private ViewGroup viewGroup;
    private View mp_notch;
    private View mp_notch_close;
    private ConstraintLayout cl_media_list;
    private ImageView iv_map;

    String driverImage = "";
    private ObjectsController objectsController;
    private int selectedPosition = -1;
    private boolean appsBarExpanded, mediaBarExpanded;
    private Animation animShow, animHide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        objectsController = new ObjectsController();
        getExtras();            //get the parameters from the previous activity
        bindControls();         //bind all the UI elements
        initAnimation();        //initialize the animations
        setAppsListItems();     //set the apps list in the top section of the screen
        setMediaPlayer();       //initialize the media player functionality
        initializeMap();        //setup the map and its functionality
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMap() {
        //to collapse all expanded views when map is touched.
        iv_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                collapseAllViews();
                return false;
            }
        });
    }

    /***
     * Initialize the animations
     */
    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);
    }

    /***
     * Set the media player view and functionality
     */
    private void setMediaPlayer() {
        Glide.with(this).load("https://i.imgur.com/jAwu0Xk.png").error(android.R.drawable.ic_menu_gallery).into(iv_album_art);
        iv_album_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mp_notch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp_notch.setVisibility(View.GONE);
                cl_media_list.setVisibility(View.VISIBLE);
                cl_media_list.startAnimation(animShow);
                mediaBarExpanded = true;
            }
        });

        mp_notch_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseAllViews();
            }
        });
    }

    /***
     * Collapse all the expanded views like apps bar and media player bar based on their conditions.
     */
    private void collapseAllViews() {
        if (mediaBarExpanded) {
            cl_media_list.startAnimation(animHide);
            cl_media_list.setVisibility(View.GONE);
            mediaBarExpanded = false;
            animHide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mp_notch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if (appsBarExpanded) {
            TransitionManager.beginDelayedTransition(viewGroup);        //for transition animation when item is clicked
            fl_app_detail.setVisibility(View.GONE);
            appsBarExpanded = false;
            selectedPosition = -1;
        }
    }

    /***
     * Set the list of apps in the top Intelligent bar
     */
    private void setAppsListItems() {
        String[] appsNames = {"Contact", "Calendar", "Camera", "Weather", "News"};
        for (int i = 0; i < appsNames.length; i++) {
            objectsController.setApps(new Apps(i, appsNames[i]));
        }
        AppsAdapter appsAdapter = new AppsAdapter(objectsController.getAppsList());
        rv_apps.setAdapter(appsAdapter);
        ItemClickSupport.addTo(rv_apps).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                tv_in_progress.setText(MessageFormat.format("{0} is in progress", objectsController.getApps(position).getAppName()));
                TransitionManager.beginDelayedTransition(viewGroup);        //for transition animation when item is clicked
                if (fl_app_detail.getVisibility() == View.GONE) {
                    fl_app_detail.setVisibility(View.VISIBLE);
                    appsBarExpanded = true;
                } else {
                    if (selectedPosition == position) {  //for checking if same item is opened or not
                        fl_app_detail.setVisibility(View.GONE);
                        appsBarExpanded = false;
                    }
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
        tv_in_progress = findViewById(R.id.tv_in_progress);
        mp_notch = findViewById(R.id.mp_notch);
        mp_notch_close = findViewById(R.id.mp_notch_close);
        cl_media_list = findViewById(R.id.cl_media_list);
        iv_map = findViewById(R.id.iv_map);
        viewGroup = findViewById(R.id.ll_apps_detailed_view);
        fl_app_detail = viewGroup.findViewById(R.id.fl_app_detail);
//        scroll_main = viewGroup.findViewById(R.id.scroll_main);
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
