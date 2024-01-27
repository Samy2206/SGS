package com.example.sgs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private TextView txtName,txtBranch,txtClass,txtEmail,txtPhoneNo,txtAge,txtCollege;
    private ImageView imgProfile,imgEdit;

    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private String userId = FirebaseAuth.getInstance().getUid();
    private StorageReference sRef;
     public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
       ////////////////////////////////////////////////////////////////////////////////////////////

        txtName = view.findViewById(R.id.txtName);
        txtBranch = view.findViewById(R.id.txtBranch);
        txtClass = view.findViewById(R.id.txtClass);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhoneNo = view.findViewById(R.id.txtPhoneNo);
        txtAge = view.findViewById(R.id.txtAge);
        txtCollege = view.findViewById(R.id.txtCollege);
        imgProfile = view.findViewById(R.id.imgProfile);
        imgEdit = view.findViewById(R.id.imgEdit);

        DocumentReference dRef = fstore.collection("student info").document(userId);

        dRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.exists())
                {
                    txtName.setText(value.getString("name"));
                    txtBranch.setText("Branch: "+value.getString("branch"));
                    txtClass.setText("Class: "+value.getString("class"));
                    txtEmail.setText("Email: "+value.getString("email"));
                    txtPhoneNo.setText("Phone No: "+value.getString("phoneno"));
                    txtAge.setText("Age: "+value.getString("age"));
                    txtCollege.setText("College: "+value.getString("clgname"));

                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UpdateProfile_Activity.class));
            }
        });

        imageSet();




        //////////////////////////////////////////////////////////////////////////////////////////
        return view;

    }

    private void imageSet() {

        sRef = FirebaseStorage.getInstance().getReference("images/"+userId);
        sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Uri imgUri = task.getResult();
                    Glide.with(ProfileFragment.this)
                            .load(imgUri)
                            .into(imgProfile);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to load Profile", Toast.LENGTH_SHORT).show();
            }
        });


    }
}