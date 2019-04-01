package com.example.autoapp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autoapp.R;
import com.example.autoapp.adapters.AppsAdapter;
import com.example.autoapp.controller.FanDirectionButtonsController;
import com.example.autoapp.controller.FanSpeedBarController;
import com.example.autoapp.controller.HvacPanelController;
import com.example.autoapp.controller.ObjectsController;
import com.example.autoapp.controller.SeatWarmerController;
import com.example.autoapp.controller.TemperatureController;
import com.example.autoapp.customclass.FanDirectionButtons;
import com.example.autoapp.customclass.FanSpeedBar;
import com.example.autoapp.customclass.HvacPanelRow;
import com.example.autoapp.customclass.SeatWarmerButton;
import com.example.autoapp.customclass.TemperatureBarOverlay;
import com.example.autoapp.customclass.ToggleButton;
import com.example.autoapp.helpers.CircleTransform;
import com.example.autoapp.helpers.ItemClickSupport;
import com.example.autoapp.helpers.MusicLibrary;
import com.example.autoapp.models.Apps;
import com.example.autoapp.services.MyMusicService;

import java.text.MessageFormat;
import java.util.List;
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
    private TextView tvSongName, tvArtistName;
    private ImageView iv_PlayPause, iv_Previous, iv_Next;

    private ToggleButton mRecycleAirButton;
    private ToggleButton mAcButton;
    private FanSpeedBar mFanSpeedBar;
    private FanSpeedBarController mFanSpeedBarController;
    private TemperatureController mTemperatureController;
    ImageView mAutoButton;

    private float mTopPanelMaxAlpha = 1.0f;
    private static final float DISABLED_BUTTON_ALPHA = 0.20f;
    private static final float ENABLED_BUTTON_ALPHA = 1.0f;


    String driverImage = "";
    private ObjectsController objectsController;
    private int selectedPosition = -1;
    private boolean appsBarExpanded, mediaBarExpanded;
    private Animation animShow, animHide;
    private MediaMetadataCompat mCurrentMetadata;
    private PlaybackStateCompat mCurrentState;

    private MediaBrowserCompat mMediaBrowser;
    private ImageView iv_Volume;

    private WindowManager mWindowManager;
    private View mPanel;
    private View mContainer;
    private int mPanelCollapsedHeight;
    private int mPanelFullExpandedHeight;
    private int mScreenBottom;
    private int mScreenWidth;
    private int mTemperatureSideMargin;
    private int mTemperatureOverlayWidth;
    private int mTemperatureOverlayHeight;
    private int mTemperatureBarCollapsedHeight;
    private HvacPanelController mHvacPanelController;
    private ViewGroup mDriverTemperatureBarTouchOverlay;
    private ViewGroup mPassengerTemperatureBarTouchOverlay;
    private TemperatureBarOverlay mDriverTemperatureBar;
    private TemperatureBarOverlay mPassengerTemperatureBar;

    private static final String TAG = "HvacUiService";
    private boolean mAutoMode;
    private Drawable mAutoOnDrawable;
    private Drawable mAutoOffDrawable;
    private ImageView iv_Favourite;


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
        //trialFunctions();
        setFunctionalities();
    }

    private void setFunctionalities() {
        Resources res = getResources();
        mAutoOnDrawable = res.getDrawable(R.drawable.ic_auto_on);
        mAutoOffDrawable = res.getDrawable(R.drawable.ic_auto_off);
        mRecycleAirButton = findViewById(R.id.recycle_air_button);
        mRecycleAirButton.setToggleIcons(res.getDrawable(R.drawable.ic_recycle_air_on),
                res.getDrawable(R.drawable.ic_recycle_air_off));
        mAcButton = findViewById(R.id.ac_button);
        mAcButton.setToggleIcons(res.getDrawable(R.drawable.ic_ac_on),
                res.getDrawable(R.drawable.ic_ac_off));
        mFanSpeedBar = findViewById(R.id.fan_speed_bar);
        mFanSpeedBarController = new FanSpeedBarController(mFanSpeedBar);
        FanDirectionButtons mFanDirectionButtons = findViewById(R.id.fan_direction_buttons);
        FanDirectionButtonsController mFanDirectionButtonsController
                = new FanDirectionButtonsController(mFanDirectionButtons);
        HvacPanelRow mPanelBottomRow = findViewById(R.id.bottom_row);
        ToggleButton mFrontDefrosterButton = mPanelBottomRow.findViewById(R.id.front_defroster);
        ToggleButton mRearDefrosterButton = mPanelBottomRow.findViewById(R.id.rear_defroster);
        mFrontDefrosterButton.setToggleIcons(res.getDrawable(R.drawable.ic_front_defroster_on),
                res.getDrawable(R.drawable.ic_front_defroster_off));
        mRearDefrosterButton.setToggleIcons(res.getDrawable(R.drawable.ic_rear_defroster_on),
                res.getDrawable(R.drawable.ic_rear_defroster_off));
        SeatWarmerButton mDriverSeatWarmer = findViewById(R.id.left_seat_heater);
        SeatWarmerButton mPassengerSeatWarmer = findViewById(R.id.right_seat_heater);
        SeatWarmerController mSeatWarmerController = new SeatWarmerController(mPassengerSeatWarmer,
                mDriverSeatWarmer);
        mAutoButton = findViewById(R.id.auto_button);
        mAutoButton.setOnClickListener(mAutoButtonClickListener);
    }

    private View.OnClickListener mAutoButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutoMode = !mAutoMode;
//            mHvacController.setAutoMode(mAutoMode);
            setAutoMode(mAutoMode);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void setAutoMode(boolean isOn) {
        HvacPanelRow linearLayout5 = findViewById(R.id.linearLayout5);
        if (isOn) {
            linearLayout5.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mAutoMode = true;
            linearLayout5.disablePanel(true);
            mTopPanelMaxAlpha = DISABLED_BUTTON_ALPHA;
            mAutoButton.setImageDrawable(mAutoOnDrawable);
        } else {
            linearLayout5.disablePanel(false);
            mTopPanelMaxAlpha = ENABLED_BUTTON_ALPHA;
            mAutoButton.setImageDrawable(mAutoOffDrawable);
        }
        View mHvacFanControlBackground = findViewById(R.id.fan_control_bg);
//        mHvacFanControlBackground.setAlpha(mTopPanelMaxAlpha);
        linearLayout5.setAlpha(mTopPanelMaxAlpha);
    }

    @Override
    public void onStart() {
        super.onStart();

        mMediaBrowser =
                new MediaBrowserCompat(
                        this,
                        new ComponentName(this, MyMusicService.class),
                        mConnectionCallback,
                        null);
        mMediaBrowser.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            if (mCurrentMetadata != null) {
                mMediaBrowser.unsubscribe(mCurrentMetadata.getDescription().getMediaId());
            }
            mMediaBrowser.disconnect();
        }
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        mCurrentState = state;
        if (state == null
                || state.getState() == PlaybackStateCompat.STATE_PAUSED
                || state.getState() == PlaybackStateCompat.STATE_STOPPED) {
            iv_PlayPause.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_black_36dp));
        } else {
            iv_PlayPause.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_pause_black_36dp));
        }

    }

    private void updateMetadata(MediaMetadataCompat metadata) {
        mCurrentMetadata = metadata;
        tvSongName.setText(metadata == null ? "" : metadata.getDescription().getTitle());
        tvArtistName.setText(metadata == null ? "" : metadata.getDescription().getSubtitle());
        iv_album_art.setImageBitmap(
                metadata == null
                        ? null
                        : MusicLibrary.getAlbumBitmap(
                        this, metadata.getDescription().getMediaId()));

        iv_Favourite.setImageDrawable(
                ContextCompat.getDrawable(this,
                        metadata == null
                                ? R.drawable.ic_favorite_border_white_24dp
                                : MusicLibrary.getFavouriteBitmap(metadata.getDescription().getMediaId())));

    }


    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mSubscriptionCallback);
                    try {
                        MediaControllerCompat mediaController =
                                new MediaControllerCompat(
                                        MainActivity.this, mMediaBrowser.getSessionToken());
                        updatePlaybackState(mediaController.getPlaybackState());
                        updateMetadata(mediaController.getMetadata());
                        mediaController.registerCallback(mMediaControllerCallback);
                        MediaControllerCompat.setMediaController(
                                MainActivity.this, mediaController);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

    // Receives callbacks from the MediaController and updates the UI state,
    // i.e.: Which is the current item, whether it's playing or paused, etc.
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    updateMetadata(metadata);

                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    updatePlaybackState(state);

                }

                @Override
                public void onSessionDestroyed() {
                    updatePlaybackState(null);

                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(
                        @NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    onMediaLoaded(children);
                }
            };

    /**
     * Called once all media items have finished loading and initially sets the first media item from list
     *
     * @param media
     */
    private void onMediaLoaded(List<MediaBrowserCompat.MediaItem> media) {

        if (mCurrentMetadata == null) {
            mCurrentMetadata =
                    MusicLibrary.getMetadata(
                            MainActivity.this,
                            MusicLibrary.getMediaItems().get(0).getMediaId());
            updateMetadata(mCurrentMetadata);
            updatePlaybackState(MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState());

        }
    }

    private void trialFunctions() {
        Resources res = getResources();
        mPanelCollapsedHeight = res.getDimensionPixelSize(R.dimen.car_hvac_panel_collapsed_height);
        mPanelFullExpandedHeight
                = res.getDimensionPixelSize(R.dimen.car_hvac_panel_full_expanded_height);
        mTemperatureSideMargin = res.getDimensionPixelSize(R.dimen.temperature_side_margin);
        mTemperatureOverlayWidth = res.getDimensionPixelSize(R.dimen.temperature_bar_width_expanded);
        mTemperatureOverlayHeight
                = res.getDimensionPixelSize(R.dimen.car_hvac_panel_full_expanded_height);
        mTemperatureBarCollapsedHeight
                = res.getDimensionPixelSize(R.dimen.temperature_bar_collapsed_height);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(metrics);
        mScreenBottom = metrics.heightPixels - getStatusBarHeight();
        mScreenWidth = metrics.widthPixels;
        WindowManager.LayoutParams params = getWindow().getAttributes();/*new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);*/
        params.packageName = this.getPackageName();
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = mScreenWidth;
        params.height = mScreenBottom;
        disableAnimations(params);
        mContainer = inflater.inflate(R.layout.hvac_panel, null);
        mContainer.setLayoutParams(params);
        // The top padding should be calculated on the screen height and the height of the
        // expanded hvac panel. The space defined by the padding is meant to be clickable for
        // dismissing the hvac panel.
        int topPadding = mScreenBottom - mPanelFullExpandedHeight;
        mContainer.setPadding(0, topPadding, 0, 0);
        mContainer.setFocusable(false);
        mContainer.setClickable(false);
        mContainer.setFocusableInTouchMode(false);
        mPanel = mContainer.findViewById(R.id.hvac_center_panel);
        mPanel.getLayoutParams().height = mPanelCollapsedHeight;
//        mWindowManager.addView(mContainer, params);
        createTemperatureBars(inflater);
        mHvacPanelController = new HvacPanelController(this /* context */, findViewById(R.id.fl_main),
                mDriverTemperatureBar, mPassengerTemperatureBar,
                mDriverTemperatureBarTouchOverlay, mPassengerTemperatureBarTouchOverlay);
//        Intent bindIntent = new Intent(this /* context */, HvacController.class);
//        if (!bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
//            Log.e("TAG", "Failed to connect to HvacController.");
//        }
    }

    private WindowManager.LayoutParams createClickableOverlayLayoutParam() {
        return getWindow().getAttributes();/*new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);*/
    }

    private TemperatureBarOverlay createTemperatureBarOverlay(LayoutInflater inflater,
                                                              int gravity) {
        TemperatureBarOverlay button = (TemperatureBarOverlay) inflater
                .inflate(R.layout.hvac_temperature_bar_overlay, null);
        WindowManager.LayoutParams params = createClickableOverlayLayoutParam();
        params.gravity = gravity;
        params.x = mTemperatureSideMargin;
        params.y = mScreenBottom - mTemperatureOverlayHeight;
        params.width = mTemperatureOverlayWidth;
        params.height = mTemperatureOverlayHeight;
        disableAnimations(params);
        button.setLayoutParams(params);
        mWindowManager.addView(button, params);
        return button;
    }

    /**
     * Creates a touchable overlay in the dimensions of a collapsed {@link TemperatureBarOverlay}.
     *
     * @return a {@link ViewGroup} that was added to the {@link WindowManager}
     */
    private ViewGroup addTemperatureTouchOverlay(int gravity) {
        WindowManager.LayoutParams params = createClickableOverlayLayoutParam();
        params.gravity = gravity;
        params.x = mTemperatureSideMargin;
        params.y = mScreenBottom - mTemperatureBarCollapsedHeight;
        params.width = mTemperatureOverlayWidth;
        params.height = mTemperatureBarCollapsedHeight;
        ViewGroup overlay = new LinearLayout(this /* context */);
        overlay.setLayoutParams(params);
        mWindowManager.addView(overlay, params);
        return overlay;
    }

    private void createTemperatureBars(LayoutInflater inflater) {
        mDriverTemperatureBar
                = createTemperatureBarOverlay(inflater, Gravity.TOP | Gravity.START);
        mPassengerTemperatureBar
                = createTemperatureBarOverlay(inflater, Gravity.TOP | Gravity.END);
        // Create a transparent overlay that is the size of the collapsed temperature bar.
        // It will receive touch events and trigger the expand/collapse of the panel. This is
        // necessary since changing the height of the temperature bar overlay dynamically, causes
        // a jank when WindowManager updates the view with a new height. This hack allows us
        // to maintain the temperature bar overlay at constant (expanded) height and just
        // update whether or not it is touchable/clickable.
        mDriverTemperatureBarTouchOverlay
                = addTemperatureTouchOverlay(Gravity.TOP | Gravity.START);
        mPassengerTemperatureBarTouchOverlay
                = addTemperatureTouchOverlay(Gravity.TOP | Gravity.END);
    }

    /**
     * Disables animations when window manager updates a child view.
     */
    private void disableAnimations(WindowManager.LayoutParams params) {
        try {
            int currentFlags = (Integer) params.getClass().getField("privateFlags").get(params);
            params.getClass().getField("privateFlags").set(params, currentFlags | 0x00000040);
        } catch (Exception e) {
            Log.e(TAG, "Error disabling animation");
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
        iv_Volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showVolumeControl();
            }
        });

        iv_Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleFavourite(mCurrentMetadata.getDescription().getMediaId());
            }
        });
    }

    /**
     * Toggles favourite on or off.
     * @param mediaId
     */
    public void toggleFavourite(String mediaId){

        iv_Favourite.setImageDrawable(
                ContextCompat.getDrawable(this,MusicLibrary.toggleFavourite(mediaId)));
    }
    /**
     * Shows seekbar for volume in a dialog
     */
    PopupWindow popupWindow;
    public void showVolumeControl(){

        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
            return;
        }
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog = layoutInflater.inflate(R.layout.volume_dialog,null);

        popupWindow = new PopupWindow(dialog, 100, 350);

        //display the popup window with volume seekbar
        popupWindow.showAsDropDown(iv_Volume,50,-450 );

        SeekBar seekbarVolume = dialog.findViewById(R.id.volume_seekbar);
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        seekbarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekbarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        //Declare handler and runnable to Hide popup after some seconds in onStopTrackingTouch
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        };

        handler.postDelayed(runnable,5000);

        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


                handler.postDelayed(runnable,3000);

            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                handler.removeCallbacks(runnable);
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
        iv_PlayPause = findViewById(R.id.iv_playpause);
        iv_PlayPause.setEnabled(true);
        iv_Volume = findViewById(R.id.iv_volume);
        iv_Favourite = findViewById(R.id.iv_favourite);
        iv_Next = findViewById(R.id.iv_next);
        iv_Previous = findViewById(R.id.iv_prev);
        iv_PlayPause.setOnClickListener(mPlaybackButtonListener);
        iv_Next.setOnClickListener(mPlaybackButtonListener);
        iv_Previous.setOnClickListener(mPlaybackButtonListener);

        tvSongName = findViewById(R.id.tv_songname);
        tvArtistName = findViewById(R.id.tv_artistname);
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


    /**
     * Click listener for the play,next and previous buttons
     */
    private final View.OnClickListener mPlaybackButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.iv_playpause:
                            togglePauseOrPlay();
                            break;
                        case R.id.iv_next:
                            MediaControllerCompat.getMediaController(MainActivity.this)
                                    .getTransportControls()
                                    .skipToNext();

                            break;
                        case R.id.iv_prev:
                            MediaControllerCompat.getMediaController(MainActivity.this)
                                    .getTransportControls()
                                    .skipToPrevious();
                            break;
                    }

                }
            };

    /**
     * Plays or Pauses the media file based on its current state
     */
    public void togglePauseOrPlay() {

        final int state =
                mCurrentState == null
                        ? PlaybackStateCompat.STATE_NONE
                        : mCurrentState.getState();
        if (state == PlaybackState.STATE_PAUSED
                || state == PlaybackState.STATE_STOPPED
                || state == PlaybackState.STATE_NONE) {

            if (mCurrentMetadata == null) {
                mCurrentMetadata =
                        MusicLibrary.getMetadata(
                                MainActivity.this,
                                MusicLibrary.getMediaItems().get(0).getMediaId());
                updateMetadata(mCurrentMetadata);
            }
            MediaControllerCompat.getMediaController(MainActivity.this)
                    .getTransportControls()
                    .playFromMediaId(
                            mCurrentMetadata.getDescription().getMediaId(), null);
        } else {
            MediaControllerCompat.getMediaController(MainActivity.this)
                    .getTransportControls()
                    .pause();
        }
    }


}
