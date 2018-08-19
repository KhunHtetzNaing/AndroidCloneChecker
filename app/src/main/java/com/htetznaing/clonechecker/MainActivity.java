package com.htetznaing.clonechecker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    EditText editText;
    TelephonyManager telephonyManager;
    String imei;
    int showAds_code = 001;
    AdView banner;
    InterstitialAd interstitialAd;
    AdRequest adRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfigs.getAppName(this)) {
            checkPermissions();
        }else{
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("This is not Official App!")
                    .setCancelable(false)
                    .setMessage("Dear users, this is not Official App. Please download Official App from Google Play Store or Direct Download\nThanks")
                    .setPositiveButton("Direct Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://shwevideo.app")));
                            finish();
                        }
                    })
                    .setNegativeButton("Play Store", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String playstore = "com.mm.cchecker";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + playstore)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + playstore)));
                            }
                            finish();
                        }
                    });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void initApp(){
        setupAds();
        editText = findViewById(R.id.edIMEI);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Checking...");
        progressDialog.setCancelable(false);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        imei = telephonyManager.getDeviceId();
        editText.setText(imei);
    }

    public void checkNow(View view) {
        letCheck();
    }

    public void letCheck(){
        if (checkInternet()) {
            String myIMEI = editText.getText().toString();
            if (myIMEI.length() == 15) {
                imei = myIMEI;
                CloneChecker cloneChecker = new CloneChecker(MainActivity.this, imei);
                progressDialog.show();
                timerDelayRemoveDialog(30000, progressDialog);
                cloneChecker.onFinish(new CloneChecker.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(boolean ogay) {
                        progressDialog.dismiss();
                        if (ogay) {
                            Real();
                        } else {
                            Clone();
                        }
                    }

                    @Override
                    public void onError() {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error!\nPlease try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please input valid IMEI\nIMEI is a 15 digits number!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (d.isShowing()) {
                    d.dismiss();
                    Log.d("LOL", "ERROR");
                    Toast.makeText(MainActivity.this, "Error!\nPlease try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }, time);
    }

    public boolean checkInternet(){
        boolean what = false;
        CheckInternet checkNet = new CheckInternet(this);
        if (checkNet.isInternetOn()){
            what = true;
        }else{
            what = false;
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setCancelable(false)
                    .setMessage("No internet connection!")
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (checkInternet()){
                                progressDialog.show();
                                letCheck();
                            }
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
        return what;
    }

    public void Real(){
        final String brand = "Brand: "+Build.MANUFACTURER+"\n";
        final String model = "Model: "+Build.MODEL+"\n";
        final String version = "Version: "+Build.VERSION.RELEASE+"\n";
        final String imei = "IMEI: "+this.imei+"\n";
        String ok = "Your phone is ORIGINAL.";

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage(brand+model+version+imei+ok)
                .setPositiveButton("OK",null)
                .setNegativeButton("Copy Info", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setText(brand+model+version+imei);
                        if (clipboardManager.hasText()){
                            Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupAds() {
        adRequest = new AdRequest.Builder().build();
        banner = findViewById(R.id.adView);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial));
        interstitialAd.loadAd(adRequest);
        banner.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadAds();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                loadAds();
            }
        });
    }

    public void loadAds(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    public void showAds(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
            interstitialAd.loadAd(adRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==showAds_code){
            showAds();
        }
    }

    public void Clone(){
        final String brand = "Brand: "+Build.MANUFACTURER+"\n";
        final String model = "Model: "+Build.MODEL+"\n";
        final String version = "Version: "+Build.VERSION.RELEASE+"\n";
        final String imei = "IMEI: "+this.imei+"\n";
        String ok = "Your phone is CLONE.";

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Sorry!")
                .setMessage(brand+model+version+imei+ok)
                .setPositiveButton("OK",null)
                .setNegativeButton("Copy Info", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setText(brand+model+version+imei);
                        if (clipboardManager.hasText()){
                            Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void gotoDeveloper(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://profile/100011339710114"));
            startActivityForResult(intent,showAds_code);
        }catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://m.facebook.com/100011339710114"));
            startActivityForResult(intent,showAds_code);
        }
    }

    private boolean checkPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        final List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1000);
            return false;
        }
        initApp();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1000){
            if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initApp();
            } else {
                checkPermissions();
                Toast.makeText(this, "You need to Allow Write Storage Permission!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.icon)
                .setCancelable(false)
                .setTitle("Notice!")
                .setMessage("Do you want to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAds();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAds();
                    }
                })
                .setNeutralButton("Rate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rate();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                share();
                break;
            case R.id.rate:
                rate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"You can check your phone is Original or Clone with this app.\n\nDownload at Google Play Store : play.google.com/store/apps/details?id="+getPackageName()+"\n\nDirect Download : http://bit.ly/BlahBlah\n#CloneChcker");
        startActivityForResult(Intent.createChooser(intent,"Share App..."),showAds_code);
    }

    public void rate(){
        View view = getLayoutInflater().inflate(R.layout.rate_view,null);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("If you like this app please give 5 stars and write some review!");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),showAds_code);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),showAds_code);
                }
            }
        });
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Help Us")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String appPackageName = "com.mm.osplayer";
                        try {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),showAds_code);
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),showAds_code);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAds();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
