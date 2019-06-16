package com.geekbrains.mymessenger;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int permissionRequestCode = 123;

    String SEND_SMS = "send_sms";
    String DELIVER_SMS ="deliver_sms";

    Intent sendIntent = new Intent(SEND_SMS);
    Intent deliverIntent = new Intent(DELIVER_SMS);

    PendingIntent sendPI;
    PendingIntent deliverPI;

    EditText adress;
    EditText text;
    Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionCheck();
        setContentView(R.layout.activity_main);
        initPI();
        initView();
        setOnClickSendBtn();
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == permissionRequestCode) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Спасибо!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Извините, приложение без данного разрешения может работать неправильно",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initPI() {
        sendPI = PendingIntent.getBroadcast(MainActivity.this, 0, sendIntent, 0);
        deliverPI = PendingIntent.getBroadcast(MainActivity.this, 0, deliverIntent, 0);
    }

    private void initView() {
        adress = findViewById(R.id.adress);
        text = findViewById(R.id.text);
        sendBtn = findViewById(R.id.sendBtn);
    }

    private void setOnClickSendBtn() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = adress.getText().toString().replace("-", "");
                String msg = text.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                try {
                    smsManager.sendTextMessage(phoneNumber,null, msg, sendPI, deliverPI);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    BroadcastReceiver sendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == Activity.RESULT_OK) {
                Toast.makeText(context, "Sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error sending", Toast.LENGTH_LONG).show();
            }
        }
    };

    BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == Activity.RESULT_OK) {
                Toast.makeText(context, "Delivered", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Delivery error", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sendReceiver, new IntentFilter(SEND_SMS));
        registerReceiver(deliverReceiver, new IntentFilter(DELIVER_SMS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(sendReceiver);
        unregisterReceiver(deliverReceiver);
    }
}
