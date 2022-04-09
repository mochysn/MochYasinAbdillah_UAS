package com.moch.mochyasinabdillah_uas;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraSQL extends AppCompatActivity {

    ImageView img1;
    Button btn1;

    VideoView vid1;
    Button btn2;

    MediaController mediaController;

    File filePhotos;
    Uri photoUri;

    File fileVideos;
    Uri videoUri;

    private static final int CAMERA_REQ = 1001;
    private static final int VIDEO_REQ = 1002;

    Intent openCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerasql);

        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        img1 = findViewById(R.id.imageView);
        vid1 = findViewById(R.id.videoView);

        mediaController = new MediaController(this);
        vid1.setMediaController(mediaController);
        vid1.setVisibility(View.INVISIBLE);

        img1.setVisibility(View.INVISIBLE);

        btn1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // cek permission camera
                if (canAccessCamera()) {
                    // request permission
                    requestPermissions(
                            new String[] { Manifest.permission.CAMERA },
                            CAMERA_REQ
                    );
                } else {
                    takePictures();
                }

                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                            CameraSQL.this,
                            new String[] { Manifest.permission.CAMERA },
                            CAMERA_REQ
                    );
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // cek permission camera
                if (canAccessCamera()) {
                    // request permission
                    requestPermissions(
                            new String[] { Manifest.permission.CAMERA },
                            VIDEO_REQ
                    );
                } else {
                    takeVideos();
                }

                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                            CameraSQL.this,
                            new String[] { Manifest.permission.CAMERA },
                            VIDEO_REQ
                    );
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessCamera() {
        return (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.CAMERA));
    }

    private File createImages() throws IOException {
        String timestamps = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());

        String imgFiles = "JPEG_" + timestamps;

        File storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imgFiles,
                ".jpeg",
                storage
        );
    }

    private void deleteUnusedFile(Uri fileUri){
        File fileDelete = new File(fileUri.getPath());
        if(fileDelete.exists()) {
            if(fileDelete.delete()) {
                Log.i("TEMP", "Temporary file is deleted");
            } else {
                Log.i("TEMP", "Temporary file is not deleted");
            }
        }
    }

    private void takePictures() {
        if (ContextCompat.checkSelfPermission(
                CameraSQL.this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(openCamera.resolveActivity(getPackageManager()) != null) {
                filePhotos = null;
                try {
                    filePhotos = createImages();
                } catch (IOException IoEx) {
                    Log.e("CAMS", IoEx.getMessage());
                }

                if(filePhotos != null) {
                    photoUri = FileProvider.getUriForFile(
                            this,
                            getPackageName(),
                            filePhotos
                    );

                    openCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    PictureActivityResultLauncher.launch(openCamera);
                    //startActivityForResult(openCamera, CAMERA_REQ);

                }

            }
        }
    }

    private File createVideos() throws IOException {
        String timestamps = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());

        String vidFiles = "VID_" + timestamps;

        File storage = getExternalFilesDir(Environment.DIRECTORY_MOVIES);

        return  File.createTempFile(
                vidFiles,
                ".mp4",
                storage
        );
    }

    private void takeVideos() {
        if (ContextCompat.checkSelfPermission(
                CameraSQL.this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            openCamera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            if(openCamera.resolveActivity(getPackageManager()) != null) {
                fileVideos = null;
                try {
                    fileVideos = createVideos();
                } catch (IOException IoEx) {
                    Log.e("CAMS", IoEx.getMessage());
                }

                if(fileVideos != null) {
                    videoUri = FileProvider.getUriForFile(
                            this,
                            getPackageName(),
                            fileVideos
                    );

                    openCamera.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    VideoActivityResultLauncher.launch(openCamera);
                    //startActivityForResult(openCamera, VIDEO_REQ);
                }

            }
        }
    }

    ActivityResultLauncher<Intent> PictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri fileUri = FileProvider.getUriForFile(
                            CameraSQL.this,
                            getPackageName(),
                            filePhotos);

                    switch (result.getResultCode()) {
                        case Activity.RESULT_CANCELED:
                            deleteUnusedFile(fileUri);
                            break;
                        case Activity.RESULT_OK:
                            Bitmap bitmap;
                            Intent data = getIntent();
                            try {
                                if (data.hasExtra("data")) {
                                    bitmap = (Bitmap) data.getExtras().get("data");
                                } else {
                                    photoUri = fileUri;
                                    if (Build.VERSION.SDK_INT < 28) {
                                        bitmap = MediaStore.Images.Media.getBitmap(
                                                CameraSQL.this.getContentResolver(), photoUri
                                        );
                                    } else {
                                        ImageDecoder.Source source =
                                                ImageDecoder.createSource(
                                                        CameraSQL.this.getContentResolver(), photoUri
                                                );
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    }
                                }
                                img1.setVisibility(View.VISIBLE);
                                vid1.setVisibility(View.INVISIBLE);
                                img1.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                Log.e("ERR_PIC", "Photo error : " + e.toString());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> VideoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri fileUri = FileProvider.getUriForFile(
                            CameraSQL.this,
                            getPackageName(),
                            fileVideos);

                    switch (result.getResultCode()) {
                        case Activity.RESULT_CANCELED:
                            deleteUnusedFile(fileUri);
                            break;
                        case Activity.RESULT_OK:
                            try {
                                img1.setVisibility(View.INVISIBLE);
                                vid1.setVisibility(View.VISIBLE);
                                vid1.setVideoURI(fileUri);
                            } catch (Exception ex) {
                                Log.e("ERR_VID", "Video error : " + ex.getMessage());
                            }
                        default:
                            break;
                    }
                }
            }
    );


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQ) {
            if(canAccessCamera()) {
                Toast.makeText(CameraSQL.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                takePictures();
            }
        }
    }
}