package com.twexdo.client;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private static final String TAG ="StartActivityTAGG" ;
    private  Button send_to_this_phone,check_code;
    private  EditText phone_number,code;
    private String  txt_phoneNumber,txt_code;
    private String verificationId;
    private FirebaseAuth mAuth;
    private Context context;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context=(Activity)this;
        send_to_this_phone=findViewById(R.id.send_to_this_phone);
        check_code=findViewById(R.id.check_code);
        phone_number=findViewById(R.id.phone_number);
        code=findViewById(R.id.code);
        progressBar=findViewById(R.id.progress_bar);
        mAuth=FirebaseAuth.getInstance();

        phone_number.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                progressBar.setVisibility(View.GONE);
                send_to_this_phone.setVisibility(View.VISIBLE);
            }
        });
        send_to_this_phone.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               progressBar.setVisibility(View.VISIBLE);
               send_to_this_phone.setVisibility(View.GONE);
               txt_phoneNumber="+4"+phone_number.getText().toString();
               if(txt_phoneNumber.length()>9){
                   PhoneAuthProvider.getInstance().verifyPhoneNumber(
                           txt_phoneNumber,        // Phone number to verify
                           60,                 // Timeout duration 2 seconds
                           TimeUnit.SECONDS,   // Unit of timeout
                           LogInActivity.this,              // Activity (for callback binding)
                           mCallbacks);        // OnVerificationStateChangedCallbacks



                   Toast.makeText(LogInActivity.this, "Se trimite codul...", Toast.LENGTH_SHORT).show();
               }
               else{
                   Toast.makeText(LogInActivity.this, "Numar de telefon incorect", Toast.LENGTH_SHORT).show();
               }
               code.requestFocus();
           }
       });

        check_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_code=code.getText().toString();
                if(txt_code.length()!=6){

                    code.setError("cod gresit...");
                    code.requestFocus();
                    return;
                }
                verifyCode(txt_code);

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                progressBar.setVisibility(View.GONE);
                send_to_this_phone.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Inncearca din nou ...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG,"onVerificationCompleted");
                String codee=credential.getSmsCode();
                if(codee !=null){
                    verifyCode(codee);
                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                send_to_this_phone.setVisibility(View.VISIBLE);
                AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(LogInActivity.this).create();
                alertDialog.setTitle("Eroare");
                alertDialog.setMessage(e.getMessage());

                alertDialog.show();
            }

            @Override
            public void onCodeSent(@NonNull String s,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG,"onCodeSent");
                verificationId=s;
            }
        };


    }
    public void verifyCode(String codee){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,codee);
        sighInWithCredential(credential);
    }

    private void sighInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Felicitari , de acum poti chema soferul doar printr-o apasare de buton!", Toast.LENGTH_LONG).show();
                    finish();
                    //continua cu credentialele
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                }
                else{
                    Toast.makeText(LogInActivity.this, "Am intampinat o problema, te rog incearca din nou...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null ){
            Toast.makeText(context, "Felicitari , de acum poti chema soferul doar printr-o apasare de buton!", Toast.LENGTH_LONG).show();
            finish();
        }

    }
}
