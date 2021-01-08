package com.twexdo.client;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientConfirmare extends Activity {

    TextView info;
    int timp;
    String nrTelefonSofer="",numeSofer="";
    Button accept,refuz;
    DatabaseReference databaseReference;
    static String myPhoneNumber,smsid;
    NotificationManager mNotificationManager;
    int notificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_confirmare);
        View someView = findViewById(R.id.viewRootDialog);

        // Find the root view
        View root = someView.getRootView();

        // Set the color
        root.setBackgroundResource(R.drawable.dialogtheme);

        databaseReference= FirebaseDatabase.getInstance().getReference();

        info=findViewById(R.id.info);
        accept=findViewById(R.id.clientAccepta);
        refuz=findViewById(R.id.clientRefuza);

        Bundle extras = getIntent().getExtras();
         mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (extras != null) {
            timp=extras.getInt("time");
            numeSofer=extras.getString("numeSofer");
            nrTelefonSofer=extras.getString("id");
            myPhoneNumber =extras.getString("myphNr");
            smsid=extras.getString("smsid");

            setTitle("Comanda Acceptata");
            info.setText(nrTelefonSofer+" "+numeSofer+" poate ajunge in aproximtiv "+timp+" minute");
            notificationId=extras.getInt("notificationId");

        }


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEEEEE HH:mm:ss");

                Date currentTime = Calendar.getInstance().getTime();
                    if(nrTelefonSofer.length()<9)
                        Toast.makeText(ClientConfirmare.this, "nrTelefonSofer :"+nrTelefonSofer, Toast.LENGTH_SHORT).show();
                    if(myPhoneNumber.length()<9)
                        Toast.makeText(ClientConfirmare.this, "senderId :"+ myPhoneNumber, Toast.LENGTH_SHORT).show();

                    databaseReference.child("mesaj").push().setValue(new sms(nrTelefonSofer, myPhoneNumber,true,timp, simpleDateFormat.format(currentTime)));
                    databaseReference.child("mesaj").child(smsid).setValue(null);
                    Toast.makeText(getApplicationContext(), "Comanda plasata!", Toast.LENGTH_SHORT).show();
                    mNotificationManager.cancel(notificationId);
                    finish();

            }
        });
        refuz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("mesaj").child(smsid).setValue(null);
                databaseReference.child("mesaj").push().setValue(new sms(nrTelefonSofer, myPhoneNumber,false,timp));
                Toast.makeText(getApplicationContext(), "Comanda anulata!", Toast.LENGTH_SHORT).show();
                mNotificationManager.cancel(notificationId);
                finish();
            }
        });




    }
}
