package com.example.autoapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.autoapp.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getExtras();

    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            if (bundle.get("DriverName")!=null){
                Toast.makeText(this, Objects.requireNonNull(bundle.get("DriverName")).toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
