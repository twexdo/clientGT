package com.twexdo.client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    private static Context c;
    private AppBarConfiguration mAppBarConfiguration;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            if(checkSelfPermission(Manifest.permission.WAKE_LOCK)!=PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{
                        Manifest.permission.WAKE_LOCK
                },200);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        c=this.getApplicationContext();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);


            mAppBarConfiguration = new AppBarConfiguration.Builder(
                     R.id.nav_soferi,R.id.nav_home, R.id.nav_info, R.id.nav_send)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            drawer.openDrawer((int) Gravity.LEFT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE
                }, 10);
            } else {
//                Intent i=new Intent(getApplicationContext(),MyService.class);
//                startService(i);
            }
        }

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent i=new Intent(getApplicationContext(),MyService.class);
//        stopService(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Intent i=new Intent(getApplicationContext(),MyService.class);
//                startService(i);
            }
            else{
                Toast.makeText(getApplicationContext(), "Aveti nevoie e aceasta permisiune pentru a putea trimite mesaje", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }



    public static Context getContext(){
        return c;
    }
}
