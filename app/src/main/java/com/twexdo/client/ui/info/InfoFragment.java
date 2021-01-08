package com.twexdo.client.ui.info;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twexdo.client.R;

public class InfoFragment extends Fragment {
    TextView main,second;
    FirebaseDatabase db;
    DatabaseReference firebaseRef;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_info, container, false);
        main=root.findViewById(R.id.main_Text);
        main.setMovementMethod(new ScrollingMovementMethod());

        second=root.findViewById(R.id.second_text);
        second.setMovementMethod(new ScrollingMovementMethod());
        db=FirebaseDatabase.getInstance();
        firebaseRef=db.getReference("info");
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                main.setText(snapshot.child("maintext").getValue(String.class));
                second.setText(snapshot.child("secondtext").getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        return root;
    }
}