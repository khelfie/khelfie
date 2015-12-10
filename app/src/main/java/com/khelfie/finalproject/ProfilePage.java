package com.khelfie.finalproject;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.util.Log;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hp on 11/7/2015.
 */
public class ProfilePage extends Activity {

    private static final String TAG = ProfilePage.class.getSimpleName();
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int VIDEO_CAPTURE = 101;
    public Uri videoUri; // file url to store image/video
    private String videoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.video_button);
            Bundle bundle = getIntent().getExtras();
            TextView name = (TextView) findViewById(R.id.textView2);
            String var = bundle.getString("ProfileName");
            name.setText(var);

        //////////////////////want to check if camera is available or not///////////////////
           // if (!hasCamera())
             //   recordButton.setEnabled(false);

            Button recordButton = (Button) findViewById(R.id.record_button);
            recordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       dispatchTakeVideoIntent();
                   // Intent intent = new Intent(v.getContext(), AndroidVideoCapture.class);
                    // intent.putExtra("ProfileName", profile.getName());
                    //startActivity(intent);
                }
            });

            Button uploadButton = (Button) findViewById(R.id.upload_button);
            uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchUploadActivity(videoPath, true);

            }
        });

    }


    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }


    private void dispatchTakeVideoIntent() {

        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        ///////////////////want to check if SD card is available or not////////////////
      // if(Environment.isExternalStorageEmulated()) {
      //      mediaFile =
      //              new File(Environment.getExternalStorageDirectory().getAbsolutePath()
      //                      + "/myvideo.mp4");
      //  }
      //  else {
             mediaFile = new File(Environment.getDataDirectory().getAbsolutePath()
                    + "/myvideo_" + timeStamp + ".mp4");
      //  }
        videoUri = Uri.fromFile(mediaFile);
        Log.d(TAG, "pulkit - Before starting video recording intent");

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra("android.intent.extra.durationLimit", 120);
        //takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);       //this is giving error

        Log.d(TAG, "pulkit - Video Caprture done");

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            Log.d(TAG, "pulkit - Inside the if condition");
        }


     //   Intent intent = new Intent(v.getContext(), newpage.class);
       // startActivity(intent);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //launchUploadActivity(videoPath, true);
        Log.d(TAG, "pulkit - inactivity Result function " + requestCode);
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {

                    Log.d(TAG, "pulkit - request video capture before upload activity" + resultCode);

                    // if (resultCode == RESULT_OK) {
                    //Toast.makeText(this, "Video saved to:\n" +
                    //        data.getData(), Toast.LENGTH_LONG).show();
                    //Uri fileUri = data.getData();
                    Uri fileUri = data.getData();
                    videoPath = getRealPathFromURI(fileUri);
                    Log.d(TAG, "pulkit - onactivityresult before upload activity");
                    launchUploadActivity(videoPath, true);

            }
        }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void launchUploadActivity(String videoPath,boolean isVideo){

        Intent i = new Intent(ProfilePage.this, UploadActivity.class);

        i.putExtra("filePath", videoPath);
        i.putExtra("isVideo", isVideo);
        startActivity(i);
    }

  /*  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            mVideoView.setVideoURI(videoUri);
        }
    }*/

   @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", videoUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        videoUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}

