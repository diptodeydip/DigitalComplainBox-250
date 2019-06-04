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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class createAccount extends AppCompatActivity implements View.OnClickListener {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
    private FirebaseAuth mAuth;
    ProgressBar pbar;
    EditText emailfield, passwordfield;
    Button register;
    Intent intent, intent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        intent = new Intent(this, profileActivity.class);
        intent2 = new Intent(this, userloginpage.class);

        emailfield = (EditText) findViewById(R.id.email);
        passwordfield = (EditText) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        register = (Button) findViewById(R.id.Reg);
        register.setOnClickListener(this);
        findViewById(R.id.backfromcreateuser).setOnClickListener(this);
    }


    private void register() {
        final String email = emailfield.getText().toString().trim();
        final String password = passwordfield.getText().toString().trim();
        if (email.isEmpty()) {
            emailfield.setError("Email required");
            emailfield.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailfield.setError("Email is not valid");
            emailfield.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordfield.setError("Password required");
            passwordfield.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordfield.setError("Password length should be more than 5");
            passwordfield.requestFocus();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("wait...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "registered :)", Toast.LENGTH_SHORT).show();
                            DatabaseReference user_db= FirebaseDatabase.getInstance().getReference("Users").child(System.currentTimeMillis()+"");
                            Map map = new HashMap();
                            map.put("email",email);
                            user_db.setValue(map);
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Verification Email Sent",Toast.LENGTH_SHORT).show();
                                }
                            });
                            mAuth.signOut();
                            finish();
                           } else {

                      /*      if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(getApplicationContext(),"Email is already registered",Toast.LENGTH_SHORT).show();
                            else*/
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.backfromcreateuser: {
/*                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);*/
                finish();
                break;
            }
            case R.id.Reg:

                register();
                break;
        }

    }
}

