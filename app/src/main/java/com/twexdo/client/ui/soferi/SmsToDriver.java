package com.twexdo.client.ui.soferi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twexdo.client.MainActivity;
import com.twexdo.client.R;
import com.twexdo.client.sms;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SmsToDriver extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SmsToDriverDebug";
    EditText reper;
    TextView intampinare,locatieText;
    LatLng clientLocation;
    String s_adresa, s_reper, nrTelSoferCerut, numeSoferCerut;
    Button b_getLocation, b_sendComanda;
    DatabaseReference databaseReference;
    boolean allpermissionGaranted = false;
    String myPhoneNr;
    KeyEvent keyEvent;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_to_driver);

        reper = findViewById(R.id.sms_reper);
        b_sendComanda = findViewById(R.id.sms_send);
        intampinare = findViewById(R.id.intampinare);
        locatieText=findViewById(R.id.locatie);
        getExtra();
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ||
                    !(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                    !(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                    !(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
            } else {
                allpermissionGaranted = true;
                getMyPhoneNumber();
                doIt();
            }
        }

        b_sendComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getMyPhoneNumber();
                    sendMessage(myPhoneNr, getAddressName(clientLocation.latitude, clientLocation.longitude), reper.getText().toString(), clientLocation.latitude, clientLocation.longitude);

                }catch (Exception e){

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyPhoneNumber();
                doIt();
            }
        }
    }

    public void getExtra() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            nrTelSoferCerut = extras.getString("nr_tel_soferCerut");
            numeSoferCerut = extras.getString("nume_soferCerut");
            if (numeSoferCerut.length() > 1) {
                setTitle("Comanda catre " + numeSoferCerut);
                intampinare.setText("Unde doriti ca " + numeSoferCerut + " sa vina?");
            } else {
                Toast.makeText(this, "Nu am putut identifica soferul dorit de dvs.", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }


    private void getMyPhoneNumber() {
                try {
                    myPhoneNr = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                }catch (Exception e){
                    myPhoneNr="";
                    Toast.makeText(this, "Nu v-am putut autentifica...", Toast.LENGTH_SHORT).show();
                }
    }

    public void sendMessage(final String _myPhoneNr, String addresa, String reper, double x, double y) {
        try {
            if (_myPhoneNr.length() < 9)
                Toast.makeText(getApplicationContext(), "Nu am putut identifica nr. dvs. de telefon:"+_myPhoneNr, Toast.LENGTH_SHORT).show();
            else if(addresa.length()<5){
                Toast.makeText(getApplicationContext(), "Nu am putut identifica locatia dvs.:"+_myPhoneNr, Toast.LENGTH_SHORT).show();

            }
            else {
                try {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    String content = "Comanda ceruta pe " + addresa + " . \n Repere : " + reper + " ";
                    //aici voi edita x si y cu  cand voi pune mapa
                    databaseReference.child("mesaj").push().setValue(new sms(nrTelSoferCerut, _myPhoneNr, content, x, y)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Comanda a fost trimisa cu succes!", Toast.LENGTH_SHORT).show();
                            databaseReference.child("tokens").child(_myPhoneNr).setValue(FirebaseInstanceId.getInstance().getToken());
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Comanda esuata!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Te rog oferane locatia ta"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception error) {
            Toast.makeText(getApplicationContext(), "Am intampinat o problema...", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    MapView mMapView;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    HashMap lista;
    GoogleMap googleMap2;
    public void doIt() {
        lista = new HashMap<>();

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                mMap.setMyLocationEnabled(true);
                googleMap2 = mMap;

                googleMap2.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        clientLocation=latLng;
                        googleMap2.clear();
                        googleMap2.addMarker(new MarkerOptions().position(latLng));
                        locatieText.setText(getAddressName(latLng.latitude,latLng.longitude));
                    }
                });
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getGPS(), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(getGPS())      // Sets the center of the map to location user
                            .zoom(15)                   // Sets the zoom
                            //.bearing(-15)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception e) {
                    Log.e("Errrrrrrr", e.toString());
                    Toast.makeText(MainActivity.getContext(), "Activeaza gps mai intai.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    @SuppressLint("MissingPermission")
    private LatLng getGPS() {

        LocationManager lm = (LocationManager) MainActivity.getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers=null;
        try {
            providers = lm.getProviders(true);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }
        if(l==null) {
            Toast.makeText(this, "Nu am putut ", Toast.LENGTH_SHORT).show();
            return new LatLng(0,0);
        }

        return new LatLng(l.getLatitude(), l.getLongitude());
    }
    public String getAddressName(double lat, double lng) {
        String add="";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);

            Log.v("IGA", "Address" + add);
            return add;
        } catch (IOException e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }
}
