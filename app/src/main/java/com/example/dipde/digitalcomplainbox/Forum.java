package com.example.dipde.digitalcomplainbox;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Forum extends AppCompatActivity implements ChatAdapter.OnItemClickListener{

    RecyclerView rView;
    DatabaseReference db;
    List<ChatUploads> uploads;
    ProgressBar pbar;
    ChatAdapter iAdapter;
    FirebaseAuth mAuth;
    String userId;
    FirebaseStorage fs;
    ValueEventListener dBListener;
    Button send;
    Uri imgURI;
    TextView forumtxt;
    EditText editText;
    private final int CHOOSE_IMAGE = 101;
    ImageView iv;
    String uploadId,URL="none";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getDisplayName();

        rView = findViewById(R.id.recyclerViewf);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(new LinearLayoutManager(this));
        pbar = findViewById(R.id.pbarf);
        db = FirebaseDatabase.getInstance().getReference().child("forum");
        uploads = new ArrayList<>();
       iAdapter = new ChatAdapter(Forum.this, uploads);
       iAdapter.setOnItemClickListener(Forum.this);
        rView.setAdapter(iAdapter);


        dBListener = db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ChatUploads upload = postSnapshot.getValue(ChatUploads.class);
                        uploads.add(upload);
                }
                iAdapter.notifyDataSetChanged();
                pbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Forum.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pbar.setVisibility(View.INVISIBLE);
            }
        });
        send = (Button) findViewById(R.id.sennd);
        send.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                v.startAnimation(buttonClick);
                check();
            }
            }
        );
      editText = findViewById(R.id.messagess);
       forumtxt = findViewById(R.id.forumpicupload);
        forumtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
       iv = findViewById(R.id.forumpic);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgURI = data.getData();
            iv.setImageURI(imgURI);
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE);
    }


    void check(){
        if(editText.getText().toString().isEmpty()){
            editText.setError("Field is empty");
            editText.requestFocus();
            return;
        }

        uploadinfo();
    }

    void uploadinfo(){
        Date date = new Date();
        String strTimeFormat = "hh:mm:ss a";
        DateFormat timeFormat = new SimpleDateFormat(strTimeFormat);
        String formattedTime = timeFormat.format(date);
        String strdateFormat = "dd-MMM-yyyy";
        DateFormat dateFormat = new SimpleDateFormat(strdateFormat);
        String formattedDate = dateFormat.format(date);

        final ChatUploads item = new ChatUploads();
        item.setmDetails(formattedTime+" \n"+formattedDate+" \nBy- "+userId);
        item.setmDes(""+editText.getText().toString());
        item.setmImageURL("none");
      //  db.child("" + System.currentTimeMillis()).setValue(item);
        editText.setText("");
        //
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");

        uploadId = mAuth.getCurrentUser().getUid();

        progressDialog.show();


        if (imgURI != null) {

            StorageReference ImageRef = FirebaseStorage.getInstance().getReference().child("forum/" +
                    uploadId+"/"+ System.currentTimeMillis() + "." + getFileExtension(imgURI));

            ImageRef.putFile(imgURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Forum.this, "Done", Toast.LENGTH_LONG).show();
                            URL = taskSnapshot.getDownloadUrl().toString();
                            item.setmImageURL(URL);
                            db.child("" + System.currentTimeMillis()).setValue(item);
                            startActivity(new Intent(Forum.this,Forum.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Forum.this, "failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }

                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(Forum.this, "Done", Toast.LENGTH_LONG).show();
            db.child("" + System.currentTimeMillis()).setValue(item);
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Long click to see option", Toast.LENGTH_SHORT).show();
       ChatUploads selectedItem = uploads.get(position);
        if(!selectedItem.getImageURL().equals("none")){
            MainActivity.url=selectedItem.getImageURL();
            startActivity(new Intent(this,Problemrelatedimage.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.removeEventListener(dBListener);
    }
}

