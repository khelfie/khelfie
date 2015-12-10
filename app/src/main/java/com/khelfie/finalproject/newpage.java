package com.khelfie.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.FacebookSdk;

/**
 * Created by hp on 11/8/2015.
 */
public class newpage extends Activity {


    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.newlayout);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        if(!filePath.equals("")) {                    //string name is defined as null
            // boolean flag to identify the media type, image or video
            boolean isImage = i.getBooleanExtra("isImage", true);

            TextView videopath = (TextView) findViewById(R.id.textView3);
            videopath.setText(filePath);
        }
    }
}
