package com.twexdo.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;



public class FMessagingService extends FirebaseMessagingService {
    private final String TAG="FCM:service";
    private static final String SMS_CHANNEL_ID="com.twexdo.client:FCMService";
    int notificationId;
    Notification notification;
    String receiverid,myPhoneNumber,n_from,numeSofer,msg;
    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

         mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

         Log.e(TAG,"Got a message...");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel mNotificationChannel = new NotificationChannel(SMS_CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }

        Log.d(TAG, "From: " + remoteMessage.getData().toString());

        if (remoteMessage.getData()!=null) {


            notificationId = new Random().nextInt();
            getMyPhoneNumber();
            receiverid =remoteMessage.getData().get("n_to");
            try{

                if ((receiverid != null) && receiverid.equals(myPhoneNumber)) {

                    n_from = remoteMessage.getData().get("n_from");
                    numeSofer = remoteMessage.getData().get("n_name");
                    int type = Integer.parseInt(remoteMessage.getData().get("n_type"));
                    if (type == 1) {


                        int time = Integer.parseInt(remoteMessage.getData().get("n_time"));
                        Log.d(TAG,n_from+" \n "+receiverid+" \n "+numeSofer+" \n "+time);


                        if (time > 0) {
                            msg = numeSofer+" a confirmat ca va ajunge in " + time + " minute. \n"+n_from;
                            Intent notificationIntent = new Intent(getApplicationContext(), ClientConfirmare.class);
                            notificationIntent.putExtra("id", n_from);
                            notificationIntent.putExtra("numeSofer", numeSofer);
                            notificationIntent.putExtra("myphNr",receiverid);
                            notificationIntent.putExtra("time", time);
                            notificationIntent.putExtra("smsid",remoteMessage.getData().get("n_childkey"));
                            notificationIntent.putExtra("notificationId",notificationId);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(notificationIntent);
                            pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        } else {

                            msg = "Soferul a refuzat comanda.";
                            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                            notificationIntent.putExtra("id", n_from);
                            notificationIntent.putExtra("smsid",remoteMessage.getData().get("n_childkey"));
                            notificationIntent.putExtra("notificationId",notificationId);
                            pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        }
                    }else if (type == 3) {
                        numeSofer="Sofer";
                        msg =remoteMessage.getData().get("n_mesaj") ;
                        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                        notificationIntent.putExtra("id", n_from);
                        notificationIntent.putExtra("smsid",remoteMessage.getData().get("n_childkey"));
                        notificationIntent.putExtra("notificationId",notificationId);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        FirebaseDatabase.getInstance().getReference("mesaj").child(remoteMessage.getData().get("n_childkey")).setValue(null);
                    }
                    createNotificationChannel();
                    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder = new NotificationCompat.Builder(getApplicationContext(), SMS_CHANNEL_ID)
                            .setContentTitle(numeSofer)
                            .setContentText(msg)
                            .setSound(uri)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.sms);
                    notification = builder.build();
                    Log.e(TAG,numeSofer+"\n"+
                                    msg+"\n");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){



                        startForeground(notificationId, notification);
                    }else{
                        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(notificationId ,builder.build());
                    }

                    PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);

                    PowerManager.WakeLock wl = pm.newWakeLock(805306394 ,"com.adrian:MyLock");
                    wl.acquire(10000);
                    PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"com.adrian:MyCpuLock");

                    wl_cpu.acquire(10000);
                }

            }catch (Exception e){


                Log.e("onChildAdded",e.getMessage());
            }





        }
    }

    private void getMyPhoneNumber() {
        try{

            myPhoneNumber=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            Log.e(TAG,myPhoneNumber);
        }catch (Exception e){
            Toast.makeText(this, "Nema numar", Toast.LENGTH_SHORT).show();
            myPhoneNumber="null";
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("tokens");
        try{
            dbref.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(token);}
        catch (Exception E){}
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    SMS_CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
