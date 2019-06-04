package com.example.dipde.digitalcomplainbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class resetpass extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText email;
    Button send;
    String strEmail;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        send = (Button) findViewById(R.id.reset);
        email = (EditText) findViewById(R.id.resetfield);
        mAuth = FirebaseAuth.getInstance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                strEmail = email.getText().toString();
                check();
            }
        });
    }
    public  void check(){
        if (strEmail.isEmpty()) {
            email.setError("Email required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            email.setError("Email is not valid");
            email.requestFocus();
            return;
        }




        Query userNameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(strEmail);
        userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){

                    final ProgressDialog progressDialog = new ProgressDialog(resetpass.this);
                    progressDialog.setTitle("wait...");
                    progressDialog.show();
                    mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Check your Email for Link", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
                else{
                    Toast.makeText(resetpass.this,"This email is not registered",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        }

}
