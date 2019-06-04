package com.example.dipde.digitalcomplainbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class judgecodeverify extends AppCompatActivity {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
    EditText code ;
    String code_;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judgecodeverify);
        code = (EditText) findViewById(R.id.code);

        findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                check();
            }
        });
/////
        spinner = (Spinner) findViewById(R.id.spinnner3);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(judgecodeverify.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Jurisdiction)){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.deptflag = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        /////

    }
    private  void check() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("wait...");

        code_ = code.getText().toString();
        if(MainActivity.deptflag.equals("Select Your Jurisdiction")) {
            Toast.makeText(getApplicationContext(), "Select Jurisdiction", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!code_.isEmpty()) {
            Query userNameQuery = FirebaseDatabase.getInstance().getReference("admin").orderByChild(MainActivity.deptflag.toString()
            ).equalTo(code_);
            progressDialog.show();
            userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        finish();
                        progressDialog.dismiss();
                        startActivity(new Intent(judgecodeverify.this, judge.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong code", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        code.setHint("Code");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error occurred \nplease try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Enter Code",Toast.LENGTH_SHORT).show();
        }
        return ;
    }
}
