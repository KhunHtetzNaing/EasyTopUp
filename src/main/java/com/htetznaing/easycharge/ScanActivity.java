package com.htetznaing.easycharge;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        mScannerView.startCamera();
        mScannerView.setResultHandler(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mScannerView.startCamera();
        mScannerView.setResultHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result Result) {
        saveText(Result.getText().toString());
    }

    public void saveText(String text){
        SharedPreferences sharedPreferences = getSharedPreferences("myFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key",text);
        editor.commit();
        startActivity(new Intent(ScanActivity.this,ScannedActivity.class));
        finish();
    }
}