package pk.edu.pucit.smartocr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import pk.edu.pucit.smartocr.utilities.Constants;
import pk.edu.pucit.smartocr.utilities.DatabaseHelper;
import pk.edu.pucit.smartocr.utilities.DirectoryHelper;
import pk.edu.pucit.smartocr.utilities.SharedPreferencesHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (checkPermissions()) {
            DirectoryHelper.initializeDirectoryStructure();
            if (SharedPreferencesHelper.checkForFirstTime(this)) {
                DatabaseHelper.getInstance(this).initializeDatabase(this);
            }
            displaySplashScreen();
        }
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET},
                        Constants.PERMISSION_ALL_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    private void displaySplashScreen() {
        (new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    int defaultScreen = SharedPreferencesHelper.getInt(SplashActivity.this, Constants.DEFAULT_SCREEN_KEY, Constants.DEFAULT_SCREEN_CAMERA);
                    if (defaultScreen == Constants.DEFAULT_SCREEN_CAMERA) {
                        Intent intent = new Intent(SplashActivity.this, CameraActivity.class);
                        startActivity(intent);
                    } else if (defaultScreen == Constants.DEFAULT_SCREEN_FILE_MANAGER) {
                        Intent intent = new Intent(SplashActivity.this, FileManagerActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_ALL_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    DirectoryHelper.initializeDirectoryStructure();
                    if (SharedPreferencesHelper.checkForFirstTime(this)) {
                        DatabaseHelper.getInstance(this).initializeDatabase(this);
                    }
                    displaySplashScreen();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Required.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

}