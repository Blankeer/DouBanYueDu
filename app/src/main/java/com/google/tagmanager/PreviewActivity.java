package com.google.tagmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PreviewActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Log.i("Preview activity");
            Uri data = getIntent().getData();
            if (!TagManager.getInstance(this).setPreviewData(data)) {
                String message = "Cannot preview the app with the uri: " + data + ". Launching current version instead.";
                Log.w(message);
                displayAlert("Preview failure", message, "Continue");
            }
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (intent != null) {
                Log.i("Invoke the launch activity for package name: " + getPackageName());
                startActivity(intent);
                return;
            }
            Log.i("No launch activity found for package name: " + getPackageName());
        } catch (Exception e) {
            Log.e("Calling preview threw an exception: " + e.getMessage());
        }
    }

    private void displayAlert(String title, String message, String buttonLabel) {
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(-1, buttonLabel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
