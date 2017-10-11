package com.htetznaing.easycharge;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RadioButton topup, transfer;
    SharedPreferences share;
    SharedPreferences.Editor editor;

    ImageView fb, msg, sharebtn, github;
    String shareText;
    ClipboardManager copy;

    AdView Banenr;
    InterstitialAd iAd;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        shareText = "ဖုန္းေငြျဖည့္ကဒ္ေပၚက QR Code ေလးကို\n" +
                "Camera နဲ႔ Scan ဖတ္လိုက္႐ုံနဲ႔ ေငြျဖည့္ေပးႏိုင္တဲ့\n" +
                "Easy Top Up App\n" +
                "Play Store ကေန အခမဲ့ ေဒါင္းယူအသုံးျပဳႏိုင္ပါတယ္ : https://play.google.com/store/apps/details?id=com.htetznaing.easycharge";

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentemail();
            }
        });

        topup = (RadioButton) findViewById(R.id.topup);
        transfer = (RadioButton) findViewById(R.id.transfer);
        topup.setChecked(true);

        share = getSharedPreferences("myFile", MODE_PRIVATE);
        editor = share.edit();

        fb = (ImageView) findViewById(R.id.fb);
        msg = (ImageView) findViewById(R.id.msg);
        sharebtn = (ImageView) findViewById(R.id.share);
        github = (ImageView) findViewById(R.id.github);

        fb.setOnClickListener(this);
        msg.setOnClickListener(this);
        sharebtn.setOnClickListener(this);
        github.setOnClickListener(this);

        adRequest = new AdRequest.Builder().build();
        Banenr = (AdView) findViewById(R.id.adView);
        Banenr.loadAd(adRequest);

        iAd = new InterstitialAd(this);
        iAd.setAdUnitId("ca-app-pub-4173348573252986/9896129689");
        iAd.loadAd(adRequest);
        iAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadAD();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                loadAD();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                loadAD();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,About.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void start(View view) {
        showAD();
        if (!transfer.isChecked()) {
            editor.putString("what", "topup");
            editor.commit();
            startActivity(new Intent(MainActivity.this, ScanActivity.class));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            builder.setTitle("Transfer to Number ?");
            builder.setView(editText);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String no = editText.getText().toString();
                    if (no.length() >= 9) {
                        editor.putString("no", no);
                        editor.putString("what", "transfer");
                        editor.commit();
                        startActivity(new Intent(MainActivity.this, ScanActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Please fill correct Phone Number :)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alerm = builder.create();
            alerm.show();
        }
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
                    Toast.makeText(MainActivity.this, "Please Install Facebook", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(new Intent(Intent.createChooser(intent, "Easy Top Up")));
                break;
            case R.id.github:
                showAD();
                Intent git = new Intent(Intent.ACTION_VIEW);
                git.setData(Uri.parse("https://github.com/KhunHtetzNaing/EasyTopUp"));
                startActivity(git);
                break;
        }
    }

    public void sentemail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "khunhtetznaing@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Easy Top Up");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Enter Your Feedback Here!");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void dev(View view) {
        showAD();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://profile/100011339710114"));
            startActivity(intent);
        }catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://m.facebook.com/KHtetzNaing"));
            startActivity(intent);
        }
    }
    public void loadAD(){
        if (!iAd.isLoaded()){
            iAd.loadAd(adRequest);
        }
    }

    public void showAD(){
        if (iAd.isLoaded()){
            iAd.show();
        }else{
            iAd.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to Exit ?");
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image,null);
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAD();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAD();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
