package com.example.dipde.digitalcomplainbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class problemlist extends AppCompatActivity implements ImageAdapter.OnItemClickListener{

    RecyclerView rView;
    DatabaseReference db;
    List<Upload> uploads;
    ProgressBar pbar;
    ImageAdapter iAdapter;
    FirebaseAuth mAuth;
    String userId;
    FirebaseStorage fs;
    ValueEventListener dBListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problemlist);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        rView = findViewById(R.id.recyclerView);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        pbar = findViewById(R.id.pbar4);

        uploads = new ArrayList<>();
        fs = FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance().getReference("uploads");
        iAdapter = new ImageAdapter(problemlist.this, uploads);
        iAdapter.setOnItemClickListener(problemlist.this);
        rView.setAdapter(iAdapter);


        dBListener = db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    if(upload.getId().toString().equals(userId)) {
                        upload.setKey(postSnapshot.getKey());
                        uploads.add(upload);
                    }
                }
                iAdapter.notifyDataSetChanged();
                pbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(problemlist.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
       // Toast.makeText(this, "Long click to see option", Toast.LENGTH_SHORT).show();
        Upload selectedItem = uploads.get(position);
        if(!selectedItem.getImageUrl().equals("none")){
            MainActivity.url=selectedItem.getImageUrl();
            startActivity(new Intent(this,Problemrelatedimage.class));
        }
    }


    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = uploads.get(position);
        final String selectedKey = selectedItem.getKey();
        if(selectedItem.getImageUrl().toString().equals("none") ||  selectedItem.getStatus().equals("solved")){
            db.child(selectedKey).removeValue();
            Toast.makeText(problemlist.this, "Item deleted", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(problemlist.this,problemlist.class));
        }
        else {
            StorageReference imageRef = fs.getReferenceFromUrl(selectedItem.getImageUrl());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.child(selectedKey).removeValue();
                    Toast.makeText(problemlist.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(problemlist.this,problemlist.class));
                }
            });
        }
    }

    @Override
    public void onInProgClick(int position){};

    @Override
    public void onSolvedClick(int position){};
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.removeEventListener(dBListener);
    }
}

