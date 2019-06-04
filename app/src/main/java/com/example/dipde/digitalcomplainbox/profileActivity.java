package com.example.dipde.digitalcomplainbox;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class profileActivity extends AppCompatActivity {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
    ImageView iv;
    private  final int CHOOSE_IMAGE = 71;
    private Uri uriprofileimage;
    ProgressBar pbar;
    String profileImageUrl;
    FirebaseAuth mAuth;
    EditText regNo,dept;
    StorageReference profileImageRef;
    Intent intent;
    String profilename;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        iv = (ImageView) findViewById(R.id.ivprofilepic);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                showImageChooser();
            }
        });

        intent = new Intent(profileActivity.this,userProfile.class);

        pbar = (ProgressBar) findViewById(R.id.pbar2);
        findViewById(R.id.saveinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(buttonClick);
                saveUserInfo();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user_id= mAuth.getCurrentUser().getUid();
        regNo = (EditText) findViewById(R.id.regNo);
        dept = findViewById(R.id.deptnamme);
        findViewById(R.id.Logoutfromprofileactivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                mAuth.signOut();
                finish();
                startActivity(new Intent(profileActivity.this,userloginpage.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriprofileimage = data.getData();


            /*try {

            We can add image to imageview with this code too

               Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriprofileimage);
                iv.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }*/

            iv.setImageURI(uriprofileimage);
        }

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE);
    }

    private void saveUserInfo() {
        profilename = regNo.getText().toString();
        if (profilename.isEmpty()) {
            regNo.setError("Name required");
            regNo.requestFocus();
            return;
        }
        if (dept.getText().toString().isEmpty()) {
            dept.setError("Department required");
            dept.requestFocus();
            return;
        }
        if(uriprofileimage == null){
            Toast.makeText(getApplicationContext(),"Please choose a picture first",Toast.LENGTH_SHORT).show();
            return ;
        }

//database part

        Query userNameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("Name").equalTo(profilename);
        userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    Toast.makeText(getApplicationContext(),"Username exists",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    DatabaseReference user_db= FirebaseDatabase.getInstance().getReference("Users").child(user_id);
                    Map map = new HashMap();
                    map.put("Name",profilename);
                    map.put("uid",user_id);
                    user_db.setValue(map);

//Imageuploadingpart
                    uploadImageToFirebase();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void uploadImageToFirebase() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        profileImageRef = FirebaseStorage.getInstance().getReference().child("profilepics/"
                + user_id+"/" +System.currentTimeMillis()+"."+getFileExtension(uriprofileimage));
        if(uriprofileimage != null) {
            profileImageRef.putFile(uriprofileimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(profileActivity.this, "Done", Toast.LENGTH_LONG).show();
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            userInfoSaveFinal();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(profileActivity.this, "failed " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        }
    }

    private void userInfoSaveFinal(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(profilename+"("+dept.getText().toString()+")")
                    .setPhotoUri(Uri.parse(profileImageUrl)).build();
            pbar.setVisibility(View.VISIBLE);
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pbar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                finish();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    });


        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
