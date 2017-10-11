package com.htetznaing.easycharge;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class About extends AppCompatActivity implements View.OnClickListener {

    ImageView fb, msg, sharebtn;
    String shareText;

    ClipboardManager copy;

    AdView Banenr;
    AdRequest adRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (ContextCompat.checkSelfPermission(About.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(About.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(About.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(About.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        shareText = "ဖုန္းေငြျဖည့္ကဒ္ေပၚက QR Code ေလးကို\n" +
                "Camera နဲ႔ Scan ဖတ္လိုက္႐ုံနဲ႔ ေငြျဖည့္ေပးႏိုင္တဲ့\n" +
                "Easy Top Up App\n" +
                "Play Store ကေန အခမဲ့ ေဒါင္းယူအသုံးျပဳႏိုင္ပါတယ္ : https://play.google.com/store/apps/details?id=com.htetznaing.easycharge";

        fb = (ImageView) findViewById(R.id.fb);
        msg = (ImageView) findViewById(R.id.msg);
        sharebtn = (ImageView) findViewById(R.id.share);

        fb.setOnClickListener(this);
        msg.setOnClickListener(this);
        sharebtn.setOnClickListener(this);

        adRequest = new AdRequest.Builder().build();
        Banenr = (AdView) findViewById(R.id.adView);
        Banenr.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fb:
                copy.setText(shareText);
                Intent fsendIntent = new Intent();
                fsendIntent.setAction(Intent.ACTION_SEND);
                fsendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                fsendIntent.setType("text/plain");
                fsendIntent.setPackage("com.facebook.katana");
                try {
                    startActivity(fsendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(About.this, "Please Install Facebook", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.msg:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(About.this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(new Intent(Intent.createChooser(intent, "Easy Top Up")));
                break;
        }
    }

    public void icon(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://profile/100011731722479"));
            startActivity(intent);
        }catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://m.facebook.com/senior.critical.1"));
            startActivity(intent);
        }
    }
}
