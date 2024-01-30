package com.example.sgs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity{
    int noNotes;
    int noPhotos;
    TextView txtNoGoals,txtDueTommorow,txtNoNotes,txtNoImages;
    private String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences pref = getSharedPreferences("numberValues",MODE_PRIVATE);

        txtNoGoals = findViewById(R.id.txtNoGoals);
        txtDueTommorow = findViewById(R.id.txtDueTommorow);
        txtNoNotes = findViewById(R.id.txtNoNotes);
        txtNoImages = findViewById(R.id.txtNoImages);

        noOfNotes();
        noOfPhotos();
        txtNoGoals.setText(String.valueOf(pref.getInt("noGoals",0)));
        txtDueTommorow.setText(String.valueOf(pref.getInt("dueTomorrow",0)));



    }

    private void noOfNotes() {
       int calc=0;
        FirebaseFirestore fstore;
        String userId = FirebaseAuth.getInstance().getUid();
        ArrayList<modelNotes> arrNotes = new ArrayList<>();
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Notes").whereEqualTo("userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments(); //all docs from the collection are retrived
                for(DocumentSnapshot d:list)
                {
                    modelNotes model = d.toObject(modelNotes.class);
                    model.setDocRef(d.getReference());
                    arrNotes.add(model);
                }
                noNotes = arrNotes.size();
                txtNoNotes.setText(String.valueOf(noNotes));
            }
        });
    }

    private void noOfPhotos()
    {
        StorageReference sRef = FirebaseStorage.getInstance().getReference("user_images").child(userId); // Modified path

        ArrayList<ModelPhoto> arrPhoto = new ArrayList<>();
        sRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item : listResult.getItems())
                {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            ModelPhoto modelPhoto = new ModelPhoto();
                            modelPhoto.setImgUri(Uri.parse(downloadUrl.toString()));
                            modelPhoto.setImgName(item.getName()); // Set image name based on StorageReference name
                            arrPhoto.add(modelPhoto);
                            txtNoImages.setText(String.valueOf(arrPhoto.size()));

                        }
                    });

                }
            }
        });
    }

}