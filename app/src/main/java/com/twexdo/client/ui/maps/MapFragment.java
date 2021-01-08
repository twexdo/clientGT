package com.twexdo.client.ui.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twexdo.client.*;
import com.twexdo.client.R;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment {
    HashMap<String, Marker> lista;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    LocationManager locationManager;
    LocationListener locationListener;
    public static int ix = 1;
    MapView mMapView;
    private GoogleMap googleMap2;
    public LatLng userLatLng = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.WAKE_LOCK}, 10);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            loc();
            doIt();
        }


        return rootView;
    }

    public void loc() {
        locationManager = (LocationManager) MainActivity.getContext().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                try {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("LOCATIE DEZACTIVATA", e.toString());
                }
            }
        };
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doIt();
                    loc();
                }


        }
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

    public void doIt() {
        lista = new HashMap<>();
        myRef = db.getReference("soferi");
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap mMap) {

                mMap.setMyLocationEnabled(true);
                googleMap2 = mMap;

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                       // googleMap2.clear();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            try {
                                String sts = String.valueOf(snap.child("status").getValue(Double.class));

                                Double c = Double.parseDouble(sts);

                                    String nrtel = String.valueOf(snap.child("nrtel").getValue(String.class));
                                    String x = String.valueOf(snap.child("x").getValue(Double.class));
                                    String y = String.valueOf(snap.child("y").getValue(Double.class));
                                    String name = String.valueOf(snap.child("nume").getValue(String.class));

                                    Double a = Double.parseDouble(x);
                                    Double b = Double.parseDouble(y);

                                    LatLng location2 = new LatLng(a, b);


                                    float color = 0;
                                    double aux = Double.parseDouble(String.valueOf(snap.child("status").getValue(Double.class)));
                                    int stat = (int) aux;
                                    String statStr = "";
                                    switch (stat) {
                                        default:
                                            color = BitmapDescriptorFactory.HUE_AZURE;
                                            statStr = "ERROR";

                                            break;
                                        case 1:
                                            color = BitmapDescriptorFactory.HUE_GREEN;
                                            statStr = "LIBER";
                                            break;
                                        case 2:
                                            color = BitmapDescriptorFactory.HUE_RED;
                                            statStr = "OCUPAT";
                                            break;
                                        case 3:
                                            color = BitmapDescriptorFactory.HUE_VIOLET;
                                            statStr = "DUBLU OCUPAT";
                                            break;
                                    }

                                    if (!lista.containsKey(nrtel)) {

                                        Marker m = googleMap2.addMarker(new MarkerOptions().position(location2).snippet(nrtel)
                                                .icon(BitmapDescriptorFactory.defaultMarker(color))
                                                .title(name + " (" + statStr + ")"));
                                        if (stat < 1) m.setVisible(false);
                                        lista.put(nrtel, m);
                                    } else {


                                        lista.get(nrtel).setPosition(location2);
                                        lista.get(nrtel).setTitle(name + " (" + statStr + ")");
                                        lista.get(nrtel).setIcon(BitmapDescriptorFactory.defaultMarker(color));
                                        if (stat < 1) lista.get(nrtel).setVisible(false);
                                        else lista.get(nrtel).setVisible(true);
//                                        if (stat != 0) {
//                                            Toast.makeText(getActivity().getApplicationContext(), name, Toast.LENGTH_SHORT).show();
//                                           // Toast.makeText(getActivity().getApplicationContext(), nrtel + "\n" + color + "\n" + location2.toString() + "\n" + "false", Toast.LENGTH_LONG).show();
//                                            animateMarker(lista.get(nrtel), name,stat, location2, false);
//                                        }else {
//
//                                          //  Toast.makeText(getActivity().getApplicationContext(), nrtel + "\n" + color + "\n" + location2.toString() + "\n" + "true", Toast.LENGTH_LONG).show();
//                                            animateMarker(lista.get(nrtel),  name,stat, location2, true);
//
//                                        }
                                    }
                                    googleMap2.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                                        @Override
                                        public void onInfoWindowLongClick(Marker marker) {

                                            for (Marker m : lista.values()) {

                                                if (marker.toString().equals(m.toString())) {

                                                    String telefon = marker.getSnippet();
                                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefon));
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });




                            } catch (Exception e) {
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };
                myRef.addValueEventListener(postListener);
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
        List<String> providers = lm.getProviders(true);

        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        return new LatLng(l.getLatitude(), l.getLongitude());
    }


}

