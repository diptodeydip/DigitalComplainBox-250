package com.example.dipde.digitalcomplainbox;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class userProfile extends AppCompatActivity {

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

    FirebaseAuth mAuth;
    TextView userName,notification,notification1,notification2;
    Intent intent;
    ValueEventListener dBListener,dBListener1,dBListener2;
    DatabaseReference db,db1,db2;
    List<count> counts,counts1,counts2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        userName=(TextView)findViewById(R.id.username);
        counts = new ArrayList<>();
        counts1 = new ArrayList<>();
        counts2 = new ArrayList<>();
        final  Button clr = findViewById(R.id.clrnoti);
        notification = findViewById(R.id.notific);
        notification1 = findViewById(R.id.notific1);
        notification2 = findViewById(R.id.notific2);


       loaduserinfo();

        findViewById(R.id.Logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                FirebaseAuth.getInstance().signOut();
                finish();
                intent = new Intent(userProfile.this,userloginpage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        findViewById(R.id.forum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                intent = new Intent(userProfile.this,Forum.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        findViewById(R.id.submitproblem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                startActivity(new Intent(userProfile.this,problemSubmissionPage.class));
            }
        });

        findViewById(R.id.listofproblem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.flag = "user";
                v.startAnimation(buttonClick);
                startActivity(new Intent(userProfile.this,problemlist.class));
            }
        });
        //Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        findViewById(R.id.userprofilepic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.url = mAuth.getCurrentUser().getPhotoUrl().toString();
                startActivity(new Intent(userProfile.this,Problemrelatedimage.class));
            }
        });

    // notification part
        db = FirebaseDatabase.getInstance().getReference().child("solvecount").child(mAuth.getCurrentUser().getUid().toString());
            dBListener = db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    counts.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        count cnt = postSnapshot.getValue(count.class);
                        counts.add(cnt);

                   if(counts.size()!=0){
                            if(counts.size()==1)
                                notification.setText("1 problem is Solved");
                            else
                                notification.setText(counts.size()+" problems are Solved");
                            notification.setVisibility(View.VISIBLE);
                            clr.setVisibility(View.VISIBLE);
                        }

                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        db1 = FirebaseDatabase.getInstance().getReference().child("Inqueuecount").child(mAuth.getCurrentUser().getUid().toString());
        dBListener1 = db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                counts1.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    count cnt = postSnapshot.getValue(count.class);
                    counts1.add(cnt);

                    if(counts1.size()!=0){
                        if(counts1.size()==1)
                            notification1.setText("1 problem is in Progress");
                        else
                            notification1.setText(counts1.size()+" problems are in Progress");
                        notification1.setVisibility(View.VISIBLE);
                        clr.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        db2 = FirebaseDatabase.getInstance().getReference().child("Deletecount").child(mAuth.getCurrentUser().getUid().toString());
        dBListener2 = db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                counts2.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    count cnt = postSnapshot.getValue(count.class);
                    counts2.add(cnt);

                    if(counts2.size()!=0){
                        if(counts2.size()==1)
                            notification2.setText("1 problem is Ignored");
                        else
                            notification2.setText(counts2.size()+" problems are Ignored");
                        notification2.setVisibility(View.VISIBLE);
                        clr.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.setVisibility(View.GONE);
                notification1.setVisibility(View.GONE);
                notification2.setVisibility(View.GONE);
                clr.setVisibility(View.GONE);
                db.removeValue();
                db1.removeValue();
                db2.removeValue();
            }
        });



    }


    private void loaduserinfo(){
        FirebaseUser user = mAuth.getCurrentUser();
        String regNo ;
        String photoUrl ;
        try {
             regNo = user.getDisplayName();
             photoUrl = user.getPhotoUrl().toString();
            Glide.with(this).load(photoUrl).into((ImageView) findViewById(R.id.userprofilepic));
            userName.setText(regNo);
        }
        catch (Exception e){
            finish();
            startActivity(new Intent(userProfile.this,profileActivity.class));
        }
    }


/*    protected void onDestroy(){
        super.onDestroy();
        db.removeEventListener(dBListener);
    }*/

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(userProfile.this,userloginpage.class));
                break;
        }
        return true;
    }*/
}
