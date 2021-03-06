package com.example.autoapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.example.autoapp.R;
import com.example.autoapp.adapters.DriversAdapter;
import com.example.autoapp.controller.ObjectsController;
import com.example.autoapp.helpers.ItemClickSupport;
import com.example.autoapp.models.Driver;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rv_drivers;

    private ObjectsController objectsController;
    private SharedPreferences driverPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        objectsController = new ObjectsController();
        bindControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSharedPreferences();
    }

    @Override
    public void onBackPressed() {
        //do nothing on back pressed to make it behave as launcher
    }

    /***
     * set the drivers list for the time being with the count set to the limit of i in the for loop
     */
    private void setDriversList() {

        // TODO: 22-03-2019 Drive name and photos to be fetched from configuration file later
        String[] driverPics = {"https://www.lexellweb.com/wp-content/uploads/2018/08/Planet-Uke-Profile-Image-for-Sidebar-Circular.png",
                "https://www.lexellweb.com/wp-content/uploads/2018/08/Planet-Uke-Profile-Image-for-Sidebar-Circular.png",
                "https://www.morpht.com/sites/morpht/files/styles/landscape/public/dalibor-matura_1.jpg?itok=gxCAhwAV"};
        for (int i = 0; i < driverPics.length; i++) {
            Driver driver = new Driver("Driver " + (i + 1), driverPics[i]);
            objectsController.setDriver(driver);
        }
        objectsController.setDriver(new Driver("Guest", ""));
        DriversAdapter adapter = new DriversAdapter(HomeActivity.this, objectsController.getDriverList());
        rv_drivers.setLayoutManager(new LinearLayoutManager(this));
        rv_drivers.setAdapter(adapter);

        setClickEventForRecycler();
    }

    /***
     * set click event for the drivers list recycler to open the main screen
     */
    private void setClickEventForRecycler() {
        ItemClickSupport.addTo(rv_drivers).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("DriverName", objectsController.getDriver(position).getDriverName());
                intent.putExtra("DriverPhoto", objectsController.getDriver(position).getDriverPhoto());
                driverPreferences = getSharedPreferences("DRIVER_PREFERENCES", MODE_PRIVATE);
                SharedPreferences.Editor editor = driverPreferences.edit();
                editor.putString("DriverName", objectsController.getDriver(position).getDriverName());
                editor.putString("DriverPhoto", objectsController.getDriver(position).getDriverPhoto());
                editor.apply();
                startActivity(intent);
            }
        });
    }

    private void bindControls() {
        rv_drivers = findViewById(R.id.rv_drivers);
        setDriversList();
    }

    private void checkSharedPreferences(){
        driverPreferences = getSharedPreferences("DRIVER_PREFERENCES", MODE_PRIVATE);
        if (driverPreferences.getString("DriverName", null) != null &&
                driverPreferences.getString("DriverPhoto", null) != null) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("DriverName", driverPreferences.getString("DriverName", null));
            intent.putExtra("DriverPhoto", driverPreferences.getString("DriverPhoto", null));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
