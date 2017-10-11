package com.htetznaing.easycharge;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ScannedActivity extends AppCompatActivity implements View.OnClickListener {
    TeleInfo telephonyInfo;
    boolean isSIM1Ready;
    boolean isSIM2Ready;
    boolean isDualSIM;
    SharedPreferences sharedPreferences;
    String Number,no,Code,what;

    TextView t1;
    EditText edText;
    FloatingActionButton sendEmail;
    ImageView fb,msg,share,github;
    Typeface mm;
    String text,shareText;
    ClipboardManager copy;

    AdRequest adRequest;
    AdView Banner;
    InterstitialAd iAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);

        copy = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        mm = Typeface.createFromAsset(getAssets(),"mm.ttf");
        shareText = "ဖုန္းေငြျဖည့္ကဒ္ေပၚက QR Code ေလးကို\n" +
                "Camera နဲ႔ Scan ဖတ္လိုက္႐ုံနဲ႔ ေငြျဖည့္ေပးႏိုင္တဲ့\n" +
                "Easy Top Up App\n" +
                "Play Store ကေန အခမဲ့ ေဒါင္းယူအသုံးျပဳႏိုင္ပါတယ္ : https://play.google.com/store/apps/details?id=com.htetznaing.easycharge";

        if (ContextCompat.checkSelfPermission(ScannedActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScannedActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(ScannedActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScannedActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        telephonyInfo = TeleInfo.getInstance(this);
        isSIM1Ready = telephonyInfo.isSIM1Ready();
        isSIM2Ready = telephonyInfo.isSIM2Ready();
        isDualSIM = telephonyInfo.isDualSIM();

        sharedPreferences = getSharedPreferences("myFile",MODE_PRIVATE);
        Number = sharedPreferences.getString("key","");
        no = sharedPreferences.getString("no","");
        what = sharedPreferences.getString("what","topup");

        switch (what){
            case "topup":
                tp();
                break;
            case "transfer":
                tf();
                break;
        }

        t1 = (TextView) findViewById(R.id.t1);
        t1.setTypeface(mm);
        t1.setVisibility(View.GONE);
        edText = (EditText) findViewById(R.id.edText);
        edText.setVisibility(View.GONE);
        sendEmail = (FloatingActionButton) findViewById(R.id.sendEmail);
        sendEmail.setVisibility(View.GONE);
        fb = (ImageView) findViewById(R.id.fb);
        msg = (ImageView) findViewById(R.id.msg);
        share = (ImageView) findViewById(R.id.share);
        github = (ImageView) findViewById(R.id.github);

        sendEmail.setOnClickListener(this);
        fb.setOnClickListener(this);
        msg.setOnClickListener(this);
        share.setOnClickListener(this);
        github.setOnClickListener(this);


        adRequest = new AdRequest.Builder().build();
        Banner = (AdView) findViewById(R.id.adView);
        Banner.loadAd(adRequest);

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

    public void tp(){
        Code = "*123*"+Number+"%23";
        if (isDualSIM == true) {
            top();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + Code));
            if (ContextCompat.checkSelfPermission(ScannedActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScannedActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
            startActivity(intent);
        }
    }

    public void tf(){
        Code = "*123*"+Number+"*"+no+"%23";
        if (isDualSIM == true) {
            tran();
        } else {
            t1.setVisibility(View.VISIBLE);
            edText.setVisibility(View.VISIBLE);
            sendEmail.setVisibility(View.VISIBLE);
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + Code));
            if (ContextCompat.checkSelfPermission(ScannedActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScannedActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
            startActivity(intent);
        }
    }

    public void top(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ScannedActivity.this);
        builder.setTitle("ATTENTION Please!");
        builder.setMessage("Choose your SIM 1 or 2 ;)");
        builder.setPositiveButton("SIM 1", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simOne(no);
            }
        });
        builder.setNegativeButton("SIM 2", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simTwo(no);
            }
        });

        AlertDialog a = builder.create();
        a.show();
    }

    public void tran(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ScannedActivity.this);
        builder.setTitle("ATTENTION Please!");
        TextView t = new TextView(this);
        t.setTypeface(mm);
        t.setTextColor(Color.rgb(0, 200, 83));
        t.setText(" သင္ "+no+" သို႔ \n Bill ျဖည့္ေပးမွာ ေသခ်ာပါသလား ?\n" +
                " SIM 1 or SIM 2 ေ႐ြးခ်ယ္ေပးပါ!\n" +
                " သို႔မဟုတ္ Cancel ကိုႏွိပ္ၿပီး ပ်ယ္ဖ်က္ႏိုင္ပါသည္။");
        builder.setView(t);
        builder.setPositiveButton("SIM 1", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simOne(no);
            }
        });
        builder.setNegativeButton("SIM 2", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simTwo(no);
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                t1.setVisibility(View.VISIBLE);
                edText.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.VISIBLE);
            }
        });

        AlertDialog a = builder.create();
        a.show();
    }

    public void simOne(String Number){
        Context context = getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Code));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        for (String s : simSlotName)
            intent.putExtra(s, 0);
        if (ContextCompat.checkSelfPermission(ScannedActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScannedActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        context.startActivity(intent);
        t1.setVisibility(View.VISIBLE);
        edText.setVisibility(View.VISIBLE);
        sendEmail.setVisibility(View.VISIBLE);
    }

    public void simTwo(String Number){
        Context context = getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Code));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        for (String s : simSlotName)
            intent.putExtra(s, 1);
        if (ContextCompat.checkSelfPermission(ScannedActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScannedActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        context.startActivity(intent);
        t1.setVisibility(View.VISIBLE);
        edText.setVisibility(View.VISIBLE);
        sendEmail.setVisibility(View.VISIBLE);
    }

    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };

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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendEmail:
                sentemail();
                break;
            case R.id.fb:
                copy.setText(shareText);
                Intent fsendIntent = new Intent();
                fsendIntent.setAction(Intent.ACTION_SEND);
                fsendIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                fsendIntent.setType("text/plain");
                fsendIntent.setPackage("com.facebook.katana");
                try {
                    startActivity(fsendIntent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ScannedActivity.this,"Please Install Facebook", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.msg:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ScannedActivity.this,"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,shareText);
                startActivity(new Intent(Intent.createChooser(intent,"Easy Top Up")));
                break;
            case R.id.github:
                showAD();
                Intent git = new Intent(Intent.ACTION_VIEW);
                git.setData(Uri.parse("https://github.com/KhunHtetzNaing/EasyTopUp"));
                startActivity(git);
                break;
        }
    }

    public void sentemail(){
        showAD();
        text = edText.getText().toString();
        if (text.isEmpty()){
            text = "I Like Easy Top Up";
        }
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","khunhtetznaing@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Easy Top Up");
        emailIntent.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
