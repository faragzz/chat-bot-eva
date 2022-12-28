package com.example.faragz_bot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.security.Permission;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.app.SearchManager;

import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    private ArrayList<String> msgList = new ArrayList<>();
    private TextToSpeech ttc;
    private int designWidth =409;
    private int  designHeight =644;
    private int dpHieght;
    private int dpWidth;
    private float dDensity;
    public static boolean voice =false;
    public static String called;
    public static String callnow = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setTitle("eva");


        //
        if(ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.CALL_PHONE} ,1);

        }


        // check later
        DisplayMetrics displayMetrics =getResources().getDisplayMetrics();
        dpHieght = (displayMetrics.heightPixels);
        dpWidth = (displayMetrics.widthPixels);
        dDensity = displayMetrics.scaledDensity;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Faragz","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }






        FloatingActionButton fab,fab1,fab2;

        fab =findViewById(R.id.send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg(null,false);

            }
        });

        fab1 = findViewById(R.id.speak);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText getmsG = findViewById(R.id.getmsg);
                String msg = getmsG.getText().toString();
                ttc = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if(i==TextToSpeech.SUCCESS){
                            ttc.setLanguage(Locale.ENGLISH);
                            ttc.speak(msg,TextToSpeech.QUEUE_ADD,null);

                        }
                    }
                });
            }
        });
        fab2=findViewById(R.id.micro);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
    }
    private void speak(){ // speech to text sssssssssssssssssssssssssssssssssssssssssss
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Start Speaking");
        startActivityForResult(intent,100);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 100 && resultCode == RESULT_OK){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String response =result.get(0);//change to lower case data
            sendMsg(response.toLowerCase(Locale.ROOT),true);
        }
    }
    public void sendMsg(String data,boolean x){//okokoko

        ListView listView = (ListView) findViewById(R.id.list);
        EditText getmsG = (EditText) findViewById(R.id.getmsg);
        String msg = getmsG.getText().toString();
        if(x){
            msg=data;
            voice=true;
        }
        else{
            voice=false;
        }
        msgList.add(msg);


        if (msg.contains("search")||msg.contains("Search")){
            String search;
            if(msg.contains("search")){
                search = msg.replace("search","");
                if(search.contains("for")){
                    search = search.replace("for","");
                }
            }
            else{
                search = msg.replace("Search","");
                if(search.contains("for")){
                    search = search.replace("for","");
                }
            }
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);

            intent.putExtra(SearchManager.QUERY, search);
            startActivity(intent);
            msgList.add("searching for"+search);
            if(voice)responseByVoice("searching for"+search);
        }
        else if(msg.contains("2")){
            Intent int2 = new Intent(MainActivity2.this,Reminder.class);
            startActivity(int2);
        }
        else if(msg.contains("call")){
            String call = null;
                call = msg.replace("call","");
            called = call.replaceAll("\\s", "");

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
                }
                else {

                    getcontact();
                }

                try{
                    Intent i = new Intent(Intent.ACTION_CALL);
                    i.setData(Uri.parse("tel:"+callnow));
                    startActivity(i);
                    callnow = null;
                    called = null;

                }catch (Exception e){
                    Toast.makeText(MainActivity2.this, "Name not found", Toast.LENGTH_LONG).show();
                }

        }

        else if(msg.contains("hi")||msg.contains("Hi")||msg.contains("HI")){
            msgList.add("Hi");

            if(voice){
                responseByVoice("hey");

            }
        }

        else if(msg.contains("date")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            String currentDateandTime = sdf.format(new Date());
            if(voice)responseByVoice(currentDateandTime);
            msgList.add(currentDateandTime);
        }
        else if(msg.contains("hello")){
            msgList.add("helloz");
            if(voice)responseByVoice("hello");
            Toast.makeText(MainActivity2.this, "love you dawidar", Toast.LENGTH_LONG).show();
        }
        else if(msg.contains("Bassiony")||msg.contains("bassiony") || msg.contains("beso")){
            msgList.add("Yalhwy 3ala el Jamallllllllllll");
        }

        else if (msg.contains("google")||msg.contains("Google")){
            gotoUrl("https://www.google.com");
            if(voice)responseByVoice("opening google");
        }
        if (msg.contains("open")||msg.contains("Open")) {
            System.out.println("hellozzzzzzzzzzzzzzzzzzzzzz");
            String open;
            if (msg.contains("open")) {
                open = msg.replace("open", "");
            } else {
                open = msg.replace("Open", "");
                open = open.replace("Open", "");
            }
            System.out.println("open = "+open);
            if (open.contains("youtube")){
                choose(1);
            } else if (open.contains("facebook")) {
                choose(2);
            } else if (open.contains("calendar")) {
                choose(3);
            } else if (open.contains("Play Store")) {
                choose(4);
            }
            else if (open.contains("whatsapp")) {
                choose(5);
            }
            else if(open.contains("instgram")){
                choose(6);
            }
            else if (open.contains("messenger")){
                choose(7);
            }
            else if(open.contains("tictok")){
                choose(8);
            }
            else if(open.contains("twitter")){
                choose(9);
            }
            else if(open.contains("snapchat")){
                choose(10);
            }
            msgList.add("opening" + open);
            if(voice)responseByVoice("opening" + open);
        }

        else if(msg.contains("Play store")){
            final String appPackageName = getPackageName();
            if(voice)responseByVoice("opening play store");
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));//cheak again
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        else if(msg.contains("calc")||msg.contains("calculator")){
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.popupcalculator");
            if (launchIntent != null) {
                if(voice)responseByVoice("opening calculator");
                startActivity(launchIntent);
            } else {
                Intent launchIntentt = getPackageManager().getLaunchIntentForPackage("com.android.calculator");
                if (launchIntentt != null) {
                    startActivity(launchIntentt);
                }
                else {
                    Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if(msg.contains("notf")){
            NotificationCompat.Builder builder  =  new NotificationCompat.Builder(MainActivity2.this,"My Notification");
            builder.setContentTitle("Faragz");
            builder.setContentText("Nice to meet you thank you for using the service");
            builder.setSmallIcon(R.drawable.speaker);
            builder.setAutoCancel(true);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity2.this);
            managerCompat.notify(1,builder.build());
        }
        ArrayAdapter<String> msgData = new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1,msgList);
        listView.setAdapter(msgData);
    }

    public void choose(int app) {
            switch (app) {
                case 0:
                    System.out.println("fail");
                    msgList.add("fail");
                    break;
                case 1:
                    Intent y = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    System.out.println("ok");
                    if (y != null) {
                        startActivity(y);
                        responseByVoice("opening" + app);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 2:
                    Intent facebook = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                    if (facebook != null) {
                        startActivity(facebook);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 3:
                    Intent calenderGoogle = getPackageManager().getLaunchIntentForPackage("com.google.android.calendar");
                    if (calenderGoogle != null) {
                        startActivity(calenderGoogle);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    Intent whatsapp = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    if (whatsapp != null) {
                        startActivity(whatsapp);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                case 6:
                    Intent instagram = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    if (instagram != null) {
                        startActivity(instagram);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 7:
                    Intent messenger = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                    if (messenger != null) {
                        startActivity(messenger);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 8:
                    Intent ticktok = getPackageManager().getLaunchIntentForPackage("com.zhiliaoapp.musically");
                    if (ticktok != null) {
                        startActivity(ticktok);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 9:
                    Intent twitter = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                    if (twitter != null) {
                        startActivity(twitter);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 10:
                    Intent snapchat = getPackageManager().getLaunchIntentForPackage("com.snapchat.android");
                    if (snapchat != null) {
                        startActivity(snapchat);
                    } else {
                        Toast.makeText(MainActivity2.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
    }


    public void responseByVoice(String msg){
        ttc = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    ttc.setLanguage(Locale.US);
                    ttc.speak(msg,TextToSpeech.QUEUE_ADD,null);
                }
            }
        });
    }
    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
    // check later
    public int calcHeight(float value){
        return (int) (dpHieght *(value/designHeight));
    }
    public int calcWidth (float value){
        return (int) (dpWidth *(value/designWidth));
    }


    public  void getcontact(){

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(name.toLowerCase().equals(called)){
                callnow = mobile;
                break;
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getcontact();
            }
        }
    }
}