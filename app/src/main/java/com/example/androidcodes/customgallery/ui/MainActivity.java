package com.example.androidcodes.customgallery.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.androidcodes.customgallery.R;
import com.example.androidcodes.customgallery.ui.fragments.FoldersAndImagesFragement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences = null;
    private Activity activity;
    private FrameLayout flContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;
        preferences=getSharedPreferences("StoragePreference", MODE_PRIVATE);

        findViewById(R.id.ivRestore).setOnClickListener(this);
        flContainer = (FrameLayout) findViewById(R.id.flContainer);

        addFragment(new FoldersAndImagesFragement(), "FoldersAndImagesFragement");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.flContainer, fragment, tag);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivRestore:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                } else {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
