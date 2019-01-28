package net.anvisys.xpen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import net.anvisys.xpen.Common.APP_VARIABLES;
import net.anvisys.xpen.Common.Session;
import net.anvisys.xpen.Login.LoginActivity;
import net.anvisys.xpen.Object.Profile;

public class MainActivity extends AppCompatActivity {

    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_STORAGE_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkPermission();


    }


    private class MainProcess extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {

            SystemClock.sleep(5000);
            return "";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.matches("")) {
                    CheckLogin();
                }

            }
            catch (Exception ex)
            {

            }
        }
    }


    private void checkPermission()
    {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, INTERNET_PERMISSION_REQUEST_CODE);
        }

        else

        {
            checkStoragePermission();
        }

    }

    private void checkStoragePermission()
    {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                )
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST_CODE);
        }
        else
        {
            checkCameraPermission();
        }
    }
    private void checkCameraPermission()
    {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
                )
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        else
        {
            checkNetwork();
        }
    }
    private void checkNetwork() {
        NetworkInfo networkInfo = null;
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connManager.getActiveNetworkInfo();
        } catch (Exception ex) {
            Toast.makeText(this, "Network info not Known", Toast.LENGTH_SHORT).show();
        }

        if (networkInfo == null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("Connection not Available, WorkOffline");
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                }
            });

            dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    APP_VARIABLES.NETWORK_STATUS = false;
                    MainProcess route = new MainProcess();
                    route.execute();
                }
            });
            dialog.setCancelable(false);
            AlertDialog alert = dialog.create();
            alert.show();
        } else {
            APP_VARIABLES.NETWORK_STATUS = true;
            MainProcess route = new MainProcess();
            route.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case INTERNET_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkStoragePermission();

                } else {

                    MainActivity.this.finish();

                }
                break;

            case WRITE_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkCameraPermission();

                } else {

                    MainActivity.this.finish();

                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   checkNetwork();

                } else {

                    MainActivity.this.finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void CheckLogin() {
        Profile myProfile = Session.GetUser(this);
        if (myProfile == null || myProfile.E_MAIL.matches("")) {
            Intent LoginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(LoginIntent);
            MainActivity.this.finish();
        } else if (myProfile.Role.matches("Individual") || myProfile.Role.matches("Employee") || myProfile.Role.matches("Manager") || myProfile.Role.matches("Admin")) {

            Intent expenseIntent = new Intent(MainActivity.this, DashboardActivity.class);
            expenseIntent.putExtra("Parent", "Main");
            startActivity(expenseIntent);
            MainActivity.this.finish();

        }
    }

}
