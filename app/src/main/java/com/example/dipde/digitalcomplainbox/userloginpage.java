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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class userloginpage extends AppCompatActivity implements  View.OnClickListener{

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
    private FirebaseAuth mAuth;
    ProgressBar pbar;
    EditText emailfield,passwordfield;
    TextView signup;
    Intent intent,intent2,intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userloginpage);
        intent = new Intent(this, verification.class);
        intent2 = new Intent(this, MainActivity.class);
        intent3 = new Intent(this, createAccount.class);

        emailfield = (EditText) findViewById(R.id.loginemail);
        passwordfield = (EditText) findViewById(R.id.loginpassword);
        mAuth = FirebaseAuth.getInstance();
        signup = (TextView) findViewById(R.id.signUp);
        signup.setOnClickListener(this);
        findViewById(R.id.backfromuserloginpage).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.forgot).setOnClickListener(this);


    }

    // To check if the user is logged in

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,verification.class));
        }
    }

    private void signIn(){
        String email = emailfield.getText().toString().trim();
        String password = passwordfield.getText().toString().trim();
        if(email.isEmpty()){
            emailfield.setError("Email required");
            emailfield.requestFocus();
            return ;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailfield.setError("Email is not valid");
            emailfield.requestFocus();
            return ;
        }

        if(password.isEmpty()){
            passwordfield.setError("Password required");
            passwordfield.requestFocus();
            return ;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("wait...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Welcome :)",Toast.LENGTH_SHORT).show();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);
                        } else {

                      /*      if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(getApplicationContext(),"Email is already registered",Toast.LENGTH_SHORT).show();
                            else*/
                            Toast.makeText(getApplicationContext(),"Error login",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClick(View v){
        v.startAnimation(buttonClick);
        switch (v.getId()){
            case R.id.backfromuserloginpage:
            {
                finish();
/*                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);*/
                break;
            }
            case R.id.login:
                signIn();
                break;
            case R.id.signUp:
            {
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                break;
            }
            case R.id.forgot:
            {
                startActivity(new Intent(userloginpage.this,resetpass.class));
                break;
            }
        }

    }

}
