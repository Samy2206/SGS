package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Photo_Notes_Activity extends AppCompatActivity implements AdapterPhotoNotes.OnItemClickListener{

    private RecyclerView recPhoto;
    private ImageView btnAddPhoto;
    private StorageReference sRef;
    private final String userId = FirebaseAuth.getInstance().getUid();
    private ArrayList<ModelPhoto> arrPhoto = new ArrayList<>();
    private AdapterPhotoNotes adapter;
    private ImageView imgNewPhoto;
    private EditText edtImgName;
    private Uri uriShow;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_notes);

        recPhoto = findViewById(R.id.recPhoto);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        sRef = FirebaseStorage.getInstance().getReference("user_images").child(userId); // Modified path

        retrieveData();


        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            Button btnSelectPhoto, btnUploadPhoto;

            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(Photo_Notes_Activity.this);
                dialog.setContentView(R.layout.dialog_add_photo);

                imgNewPhoto = dialog.findViewById(R.id.imgNewPhoto);
                edtImgName = dialog.findViewById(R.id.edtImgName);
                btnSelectPhoto = dialog.findViewById(R.id.btnSelectPhoto);
                btnUploadPhoto = dialog.findViewById(R.id.btnUploadPhoto);

                imgNewPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iGallery = new Intent(Intent.ACTION_PICK);
                        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(iGallery, 200);
                    }
                });

                btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iGallery = new Intent(Intent.ACTION_PICK);
                        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(iGallery, 200);
                    }
                });

                btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtImgName == null || edtImgName.getText().toString().isEmpty()) {
                            Toast.makeText(Photo_Notes_Activity.this, "Please Enter Image Name", Toast.LENGTH_SHORT).show();
                        } else {
                            sRef.child(edtImgName.getText().toString()).putFile(uriShow).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(Photo_Notes_Activity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    retrieveData();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Photo_Notes_Activity.this, "Error Uploading Image", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
                adapter.notifyDataSetChanged();
                dialog.show();
            }
        });

        recPhoto.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AdapterPhotoNotes(arrPhoto);
        adapter.setOnItemClickListener(this);
        recPhoto.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void retrieveData() {
        arrPhoto.clear();

        sRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // Get the download URL of the image
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    ModelPhoto modelPhoto = new ModelPhoto();
                                    modelPhoto.setImgUri(Uri.parse(downloadUrl.toString()));
                                    modelPhoto.setImgName(item.getName()); // Set image name based on StorageReference name
                                    arrPhoto.add(modelPhoto);
                                    adapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure to get download URL
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Photo_Notes_Activity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                uriShow = data.getData();
                String imgDemo = data.getData().toString();
                imgNewPhoto.setImageURI(Uri.parse(imgDemo));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(int position) {
        String imgUri = arrPhoto.get(position).getImgUri().toString();
        Intent iOpen = new Intent(getApplicationContext(), OpenPhotoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imgUri",imgUri);
        iOpen.putExtras(bundle);
        startActivity(iOpen);
    }
}