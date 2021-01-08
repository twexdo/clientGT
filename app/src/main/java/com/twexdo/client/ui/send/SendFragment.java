package com.twexdo.client.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twexdo.client.MainActivity;
import com.twexdo.client.R;

public class SendFragment extends Fragment {

FirebaseDatabase db;
DatabaseReference myRef;
EditText editText;
Button button;
public  boolean asteapta=false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_send, container, false);

        db=FirebaseDatabase.getInstance();
        myRef=db.getReference("Mesaje");

        editText=root.findViewById(R.id.send_edit_text);
        button=root.findViewById(R.id.send_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!asteapta){
                    String key= myRef.push().getKey();
                    myRef.child(key).setValue(editText.getText().toString());
                    asteapta=true;
                    Toast.makeText(MainActivity.getContext(), "Mesaj Trimis", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                }
                else{
                    Toast.makeText(MainActivity.getContext(), "Trebuie sa astepti pentru a trimite inca un mesaj", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
}