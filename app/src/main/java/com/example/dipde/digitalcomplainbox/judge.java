package com.example.dipde.digitalcomplainbox;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class judge extends AppCompatActivity implements ImageAdapter.OnItemClickListener{

    RecyclerView rView;
    DatabaseReference db,db2,db3;
    List<Upload> uploads;
    ProgressBar pbar;
    ImageAdapter iAdapter;
    StorageReference fs;
    ValueEventListener dBListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge);


        rView = findViewById(R.id.recyclerView2);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        pbar = findViewById(R.id.pbar5);

        uploads = new ArrayList<>();
        db = FirebaseDatabase.getInstance().getReference("uploads");
        db2 = FirebaseDatabase.getInstance().getReference("solved");
        db3 = FirebaseDatabase.getInstance().getReference("solvecount");
        iAdapter = new ImageAdapter(judge.this, uploads);
        iAdapter.setOnItemClickListener(judge.this);
        rView.setAdapter(iAdapter);


        dBListener = db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                        upload.setKey(postSnapshot.getKey());
                        if(!upload.getStatus().equals("solved") && upload.getDept().equals(MainActivity.deptflag) && !upload.getStatus().equals("Ignored"))
                        uploads.add(upload);
                }
                iAdapter.notifyDataSetChanged();
                pbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(judge.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

        count cnt = new count();
        cnt.setmCount("1");
        db3 = FirebaseDatabase.getInstance().getReference("Deletecount");
        db3.child(selectedItem.getId().toString()).child(System.currentTimeMillis()+"").setValue(cnt);

        final String selectedKey = selectedItem.getKey();
            db.child(selectedKey).removeValue();
            selectedItem.setStatus("Ignored");
        db.child(System.currentTimeMillis() + "").setValue(selectedItem);
            Toast.makeText(judge.this, "Item deleted", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onSolvedClick(int position) {
        Upload selectedItem = uploads.get(position);


        count cnt = new count();
        cnt.setmCount("1");
        db3 = FirebaseDatabase.getInstance().getReference("solvecount");
        db3.child(selectedItem.getId().toString()).child(System.currentTimeMillis()+"").setValue(cnt);

        final String selectedKey = selectedItem.getKey();
        db.child(selectedKey).removeValue();
        selectedItem.setStatus("solved");
        db.child(System.currentTimeMillis() + "").setValue(selectedItem);
        db2.child(System.currentTimeMillis() + "").setValue(selectedItem);
        Toast.makeText(judge.this, "Moved to Solved Problems", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(judge.this,judge.class));
        finish();

    }


        @Override
        public  void onInProgClick(int position){
            Upload selectedItem = uploads.get(position);

            if(!selectedItem.getStatus().equals("In Progress")) {
                count cnt1 = new count();
                cnt1.setmCount("1");
                db3 = FirebaseDatabase.getInstance().getReference("Inqueuecount");
                db3.child(selectedItem.getId().toString()).child(System.currentTimeMillis() + "").setValue(cnt1);
                final String selectedKey = selectedItem.getKey();
                db.child(selectedKey).removeValue();
                selectedItem.setStatus("In Progress");
                db.child(System.currentTimeMillis() + "").setValue(selectedItem);
                Toast.makeText(judge.this, "Status changed to In Progress", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(judge.this, "Already In Progress", Toast.LENGTH_SHORT).show();
            }
        }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.removeEventListener(dBListener);
    }



}

