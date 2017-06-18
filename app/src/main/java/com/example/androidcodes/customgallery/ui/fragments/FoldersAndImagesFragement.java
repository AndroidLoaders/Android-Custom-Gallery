package com.example.androidcodes.customgallery.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidcodes.customgallery.R;
import com.example.androidcodes.customgallery.SpacesItemDecoration;
import com.example.androidcodes.customgallery.adapters.FoldersAdapter;
import com.example.androidcodes.customgallery.models.Folders;
import com.example.androidcodes.customgallery.models.Images;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Mehta on 6/17/2017.
 */

public class FoldersAndImagesFragement extends Fragment {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ArrayList<Folders> foldersList = null;
    private ArrayList<Images> imagesList = null;
    private Thread folderThread = null;
    private FoldersAdapter foldersAdapter = null;

    private Activity activity;
    private SharedPreferences preferences = null;

    private Button btnCheckPermissions;
    private RecyclerView rvImagesList, rvFoldersList;
    private GridLayoutManager gmFoldersManager, gmImagesManager;

    private static final String[] projection = new String[]{MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};

    public FoldersAndImagesFragement() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        preferences = activity.getSharedPreferences("StoragePreference", Activity.MODE_PRIVATE);
        foldersAdapter = new FoldersAdapter(activity);
        return inflater.inflate(R.layout.folders_and_images_fragement, container, false);
    }

    @Override
    public void onViewCreated(View foldersImagesView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(foldersImagesView, savedInstanceState);

        gmFoldersManager = new GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false);
        gmImagesManager = new GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false);

        getOrientation(getResources().getConfiguration().orientation);

        btnCheckPermissions = (Button) foldersImagesView.findViewById(R.id.btnCheckPermissions);
        btnCheckPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkForPermission();
                } else {
                    startThread(folderThread, new FoldersLoaderRunnable());
                }
            }
        });
        btnCheckPermissions.performClick();

        rvFoldersList = (RecyclerView) foldersImagesView.findViewById(R.id.rvFoldersList);
        rvFoldersList.setLayoutManager(gmFoldersManager);
        rvFoldersList.addItemDecoration(new SpacesItemDecoration(5));
        rvFoldersList.setAdapter(foldersAdapter);

        rvImagesList = (RecyclerView) foldersImagesView.findViewById(R.id.rvImagesList);
        rvImagesList.setLayoutManager(gmImagesManager);
    }

    private void checkForPermission() {
        if (ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, permissionsRequired[1]) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permissionsRequired[0]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            permissionsRequired[1])) {
                //Show Information about why you need the permission
                showPermissionAlert(false);
            } else if (preferences.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                showPermissionAlert(true);
            } else {
                //just request the permission
                requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.apply();
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
            startThread(folderThread, new FoldersLoaderRunnable());
        }
    }

    private void showPermissionAlert(final boolean isGotoSetting) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permission");
        builder.setMessage("This app needs Stprage Read-Wrirte permission.");
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                btnCheckPermissions.setVisibility(View.VISIBLE);
                if (!isGotoSetting) {
                    requestPermissions(permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                } else {
                    sentToSettings = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    Toast.makeText(activity, "Go to Permissions to Grant Phone", Toast.LENGTH_LONG).
                            show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnCheckPermissions.setVisibility(View.VISIBLE);
                dialog.cancel();
            }
        });
        builder.show();
    }

    private class FoldersLoaderRunnable implements Runnable {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Cursor cursor = activity.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                            MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                return;
            }
            ArrayList<Folders> fList = new ArrayList<>(cursor.getCount());
            HashSet<Long> albumSet = new HashSet<>();
            File file;
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }
                    long albumId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String folderName = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String imagePath = cursor.getString(cursor.getColumnIndex(projection[2]));
                    if (!albumSet.contains(albumId)) {
                        /*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         */
                        file = new File(imagePath);
                        if (file.exists()) {
                            Folders folders = new Folders();
                            folders.setFolderName(folderName);
                            folders.setFolderImagePath(imagePath);
                            albumSet.add(albumId);
                            fList.add(folders);
                        }
                    }
                } while (cursor.moveToPrevious());
            }
            cursor.close();
            if (foldersList == null) {
                foldersList = new ArrayList<>();
            }
            foldersList.clear();
            foldersList.addAll(fList);

            if (foldersAdapter == null) {
                foldersAdapter = new FoldersAdapter(activity);
            }
            foldersAdapter.refreshAlbumsList(foldersList);
        }
    }

    private void startThread(Thread thread, Runnable runnable) {
        btnCheckPermissions.setVisibility(View.GONE);
        stopThread(thread);
        thread = new Thread(runnable);
        thread.start();
    }

    private void stopThread(Thread thread) {
        if (thread == null || !thread.isAlive()) {
            return;
        }
        try {
            thread.interrupt();
            thread.join();
        } catch (Exception e) {
            System.out.println("TAG --> " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allPermissionsGranted = false;
            int size = grantResults.length;
            for (int i = 0; i < size; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = true;
                } else {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                //btnCheckPermissions.setVisibility(View.GONE);
                startThread(folderThread, new FoldersLoaderRunnable());
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permissionsRequired[0]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            permissionsRequired[1])) {
                showPermissionAlert(false);
            } else {
                btnCheckPermissions.setVisibility(View.VISIBLE);
                Toast.makeText(activity, "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) ==
                    PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                btnCheckPermissions.setVisibility(View.GONE);
                startThread(folderThread, new FoldersLoaderRunnable());
            } else {
                btnCheckPermissions.setVisibility(View.VISIBLE);
            }
            }
        } else {
            startThread(folderThread, new FoldersLoaderRunnable());
        }*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getOrientation(newConfig.orientation);
    }

    private void getOrientation(int orientation) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Activity.
                WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (foldersAdapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 2 :
                    metrics.widthPixels / 4;
            foldersAdapter.setLayoutParams(size);
        }

        gmFoldersManager.setSpanCount(orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4);
        gmImagesManager.setSpanCount(orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 4);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(activity, permissionsRequired[0]) ==
                    PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                startThread(folderThread, new FoldersLoaderRunnable());
            } else {
                btnCheckPermissions.setVisibility(View.VISIBLE);
            }
        }
    }

    /*private class ImageLoaderRunnable implements Runnable {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            *//*
            If the adapter is null, this is first time this activity's view is
            being shown, hence send FETCH_STARTED message to show progress bar
            while images are loaded from phone
             *//*
            File file;
            HashSet<Long> selectedImages = new HashSet<>();
            if (images != null) {
                Image image;
                for (int i = 0, l = images.size(); i < l; i++) {
                    image = images.get(i);
                    file = new File(image.path);
                    if (file.exists() && image.isSelected) {
                        selectedImages.add(image.id);
                    }
                }
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{ album }, MediaStore.Images.Media.DATE_ADDED);
            if (cursor == null) {
                sendMessage(Constants.ERROR);
                return;
            }

            *//*
            In case this runnable is executed to onChange calling loadImages,
            using countSelected variable can result in a race condition. To avoid that,
            tempCountSelected keeps track of number of selected images. On handling
            FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
             *//*
            int tempCountSelected = 0;
            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    boolean isSelected = selectedImages.contains(id);
                    if (isSelected) {
                        tempCountSelected++;
                    }

                    file = new File(path);
                    if (file.exists()) {
                        temp.add(new Image(id, name, path, isSelected));
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            sendMessage(Constants.FETCH_COMPLETED, tempCountSelected);
        }
    }*/
}
