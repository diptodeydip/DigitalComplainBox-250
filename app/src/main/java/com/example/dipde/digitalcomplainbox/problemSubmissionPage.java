package com.example.dipde.digitalcomplainbox;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class problemSubmissionPage extends AppCompatActivity {

    String userName,descrip,formattedDate,formattedTime;
    private Uri uriprofileimage;
    StorageReference ImageRef;
    String ImageUrl;
    private final int CHOOSE_IMAGE = 101;
    DatabaseReference image_db;
    EditText description;
    ImageView iv;
    String uploadId, category,dept;
    FirebaseAuth mAuth;
    Spinner spinner,spinner2;
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_submission_page);


      //We can add items in spinner like this but adding items in xml is easier    android:entries="@array/items"
        //Category spinner
        spinner = (Spinner) findViewById(R.id.spinnner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(problemSubmissionPage.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.items)){
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
                category = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
      // Department spinner
        spinner2 = (Spinner) findViewById(R.id.spinnner2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(problemSubmissionPage.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.depts)){
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
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dept = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(myAdapter2);
        ///////


        mAuth = FirebaseAuth.getInstance();
        description = (EditText) findViewById(R.id.Description);
        description.setBackgroundColor(Color.GRAY);
        iv = (ImageView) findViewById(R.id.proofpicture);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                check();
            }
        });
        findViewById(R.id.backfromproblemsubmissionpage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriprofileimage = data.getData();
            iv.setImageURI(uriprofileimage);
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE);
    }


    private void uploadImageAndDescriptionToFirebase() {


        descrip = description.getText().toString();
        userName = mAuth.getCurrentUser().getDisplayName();

        Date date = new Date();
        String strTimeFormat = "hh:mm:ss a";
        DateFormat timeFormat = new SimpleDateFormat(strTimeFormat);
        formattedTime = timeFormat.format(date);
        String strdateFormat = "dd-MMM-yyyy";
        DateFormat dateFormat = new SimpleDateFormat(strdateFormat);
        formattedDate = dateFormat.format(date);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        //uploadId = image_db.push().getKey();

        uploadId = mAuth.getCurrentUser().getUid();
        progressDialog.show();
        image_db = FirebaseDatabase.getInstance().getReference().child("uploads");
       // db2 = FirebaseDatabase.getInstance().getReference().child("uploads2");
        if (uriprofileimage != null) {

            ImageRef = FirebaseStorage.getInstance().getReference().child("uploads/" +
                    uploadId+"/"+ System.currentTimeMillis() + "." + getFileExtension(uriprofileimage));

            ImageRef.putFile(uriprofileimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(problemSubmissionPage.this, "Done", Toast.LENGTH_LONG).show();
                            ImageUrl = taskSnapshot.getDownloadUrl().toString();
                           // Upload upload = new Upload(descrip,ImageUrl,uploadId,formattedDate , userName);
                            Upload upload = new Upload();
                            upload.setDes(descrip);
                            upload.setId(uploadId);
                            upload.setImageUrl(ImageUrl);
                            upload.setDate(formattedDate);
                            upload.setTime(formattedTime);
                            upload.setName(userName);
                            upload.setCat(category);
                            upload.setStatus("In Queue");
                            upload.setDept(dept);
                            upload.setKey("a");

                            image_db.child("" + System.currentTimeMillis()).setValue(upload);

                            finish();
                            //startActivity(new Intent(problemSubmissionPage.this,problemSubmissionPage.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(problemSubmissionPage.this, "failed " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(problemSubmissionPage.this, "Done", Toast.LENGTH_LONG).show();
            ImageUrl = "none";
            //Upload upload = new Upload(descrip , ImageUrl , uploadId, formattedDate , userName , formattedTime);
            Upload upload = new Upload();
            upload.setDes(descrip);
            upload.setId(uploadId);
            upload.setImageUrl(ImageUrl);
            upload.setDate(formattedDate);
            upload.setTime(formattedTime);
            upload.setName(userName);
            upload.setCat(category);
            upload.setStatus("In Queue");
            upload.setDept(dept);
            upload.setKey("a");
            image_db.child("" + System.currentTimeMillis()).setValue(upload);
            finish();
            //startActivity(new Intent(problemSubmissionPage.this,problemSubmissionPage.class));
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void check() {
        String des;
        des = description.getText().toString();
        if (des.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Write something about your problem", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(category.equals("Select Category")){
            Toast.makeText(getApplicationContext(), "Select Category", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(dept.equals("Select Authority")){
            Toast.makeText(getApplicationContext(), "Select Authority", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            uploadImageAndDescriptionToFirebase();
        }
    }


}
