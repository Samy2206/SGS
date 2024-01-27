package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProfile_Activity extends AppCompatActivity {
    private TextView txtName,txtBranch,txtClass,txtPhoneNo,txtAge,txtCollege;
    private ImageView imgProfile;
    private Uri imgUri;
    private StorageReference sRef;
    private ProgressDialog pdialog;
    private String userId = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private Button btnSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        txtName = findViewById(R.id.txtName);
        txtBranch = findViewById(R.id.txtBranch);
        txtClass = findViewById(R.id.txtClass);
        txtPhoneNo =findViewById(R.id.txtPhoneNo);
        txtAge = findViewById(R.id.txtAge);
        txtCollege = findViewById(R.id.txtCollege);
        imgProfile = findViewById(R.id.imgProfile);
        btnSave = findViewById(R.id.btnSave);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        loadPreviousInfo();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
                finish();
            }
        });
    }

    private void loadPreviousInfo() {
        DocumentReference dRef = fStore.collection("student info").document(userId);
        dRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.exists())
                {
                    txtName.setText(value.getString("name"));
                    txtBranch.setText(value.getString("branch"));
                    txtClass.setText(value.getString("class"));
                    txtPhoneNo.setText(value.getString("phoneno"));
                    txtAge.setText(value.getString("age"));
                    txtCollege.setText(value.getString("clgname"));

                }
            }
        });
    }

    private void updateInfo() {

        DocumentReference dRef = fStore.collection("student info").document(userId);
        dRef.update("name",txtName.getText().toString());
        dRef.update("branch",txtBranch.getText().toString());
        dRef.update("class",txtClass.getText().toString());
        dRef.update("phoneno",txtPhoneNo.getText().toString());
        dRef.update("age",txtAge.getText().toString());
        dRef.update("clgname",txtCollege.getText().toString());
    }

    private void selectImage() {

        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery,100);

        pdialog = new ProgressDialog(this);
        pdialog.setTitle("Uploading....");
        pdialog.show();




    }

    private void uploadImage() {
        sRef = FirebaseStorage.getInstance().getReference("images/"+userId);

        sRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgProfile.setImageURI(imgUri);
                Toast.makeText(UpdateProfile_Activity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                if(pdialog.isShowing())
                    pdialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile_Activity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                if(pdialog.isShowing())
                    pdialog.dismiss();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==RESULT_OK)
        {
            if(requestCode==100 & data != null)
            {
                imgUri = data.getData();
                uploadImage();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}