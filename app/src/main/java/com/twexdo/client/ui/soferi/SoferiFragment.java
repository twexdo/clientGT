package com.twexdo.client.ui.soferi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twexdo.client.LogInActivity;
import com.twexdo.client.MainActivity;
import com.twexdo.client.R;
import com.twexdo.client.Sofer;
import com.twexdo.client.SoferAdapter;

import java.util.ArrayList;


public class SoferiFragment extends Fragment {
    TextView t;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef ,sendMessageReference;
    ArrayList<Sofer> lista;
    SoferAdapter adapter;
    AlertDialog alertDialog;
    ListView listView;
    Intent intentCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_soferi, container, false);

        if ( ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED  ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.CALL_PHONE}, 10222);}
      try{
        t = rootView.findViewById(R.id.textView);
        myRef = db.getReference("soferi");
        listView = rootView.findViewById(R.id.listView);
        lista = new ArrayList<>();
        adapter = new SoferAdapter(getActivity().getApplicationContext(), lista);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String telefon = adapter.getItem(i).getTelefon();
                final String nume = adapter.getItem(i).getNume();
                intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentCall.setData(Uri.parse("tel:" + telefon));

                if (adapter.getItem(i).getStatus() != 0) {

                    alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Cum doriti sa procedati?");
                    alertDialog.setMessage("MESAJ sau APEL");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "APEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intentCall);
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "IESIRE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                } else {
                    Toast.makeText(MainActivity.getContext(), "Ne pare rau , acest sofer nu este disponibil.", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getContext(), nume+" "+telefon, Toast.LENGTH_SHORT).show();

            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                adapter.notifyDataSetChanged();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    try {


                        String name = String.valueOf(snap.child("nume").getValue(String.class));
                        int sts = snap.child("status").getValue(Integer.class);
                        String nrtel = String.valueOf(snap.child("nrtel").getValue(String.class));
                        String lastSignal = String.valueOf(snap.child("lastSignal").getValue(String.class));
                        String url = String.valueOf(snap.child("url").getValue(String.class));
                        boolean exists = false;
                        int index = 0;
                        int savedindex = 0;
                        for (Sofer s : lista) {
                            index++;
                            if (s.contain(nrtel)) {
                                exists = true;
                                savedindex = index;
                            }
                        }
                        if (!exists && nrtel != "null" && name != "null") {
                            Log.e("SoferiFragment", "Adaugare " + name);
                            Sofer newSofer = new Sofer(name, nrtel, sts, url, lastSignal);
                            adapter.add(newSofer);
                        } else if (nrtel != "null" && name != "null") {
                            Log.e("SoferiFragment", "Rescriere " + name);
                            adapter.removeItem(savedindex);
                            adapter.notifyDataSetChanged();
                            Sofer nSofer = new Sofer(name, nrtel, sts, url, lastSignal);
                            adapter.add(nSofer);
                        }


                    } catch (Exception e) {
                        Log.e("EROARE", e.toString());
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
    }catch(Exception e){}

        return rootView;
    }
}
