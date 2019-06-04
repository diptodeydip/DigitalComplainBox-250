package com.example.dipde.digitalcomplainbox;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class verification extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        verify = (TextView)findViewById(R.id.verify);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser().isEmailVerified()){
            finish();
            startActivity(new Intent(verification.this,userProfile.class));
        }
        else{
            verify.setText("Account isn't VERIFIED\nVerify first using the link sent to your email");
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Account isn't verified\nVerify first using the link sent to your email"
                            ,Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                }
            });
        }
    }
}
