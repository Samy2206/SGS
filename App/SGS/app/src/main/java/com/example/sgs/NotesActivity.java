package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesActivity extends AppCompatActivity {

    private FirebaseFirestore fstore;
    private RecyclerView recNotes;
    private FloatingActionButton btnAdd;
    private ArrayList<modelNotes> arrNotes = new ArrayList<modelNotes>();
    private final String userId = FirebaseAuth.getInstance().getUid();
    private AdapterNotes adapter;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        recNotes = findViewById(R.id.recNotes);
        btnAdd = findViewById(R.id.btnAdd);
        fstore = FirebaseFirestore.getInstance();
        pref =getSharedPreferences("numberValues",MODE_PRIVATE);


        recNotes.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            TextView digedtNoteContaint,digedtNoteName;
            ImageView digbtnSave;
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(NotesActivity.this);
                dialog.setContentView(R.layout.dialog_note);

                digedtNoteName = dialog.findViewById(R.id.digedtNoteName);
                digedtNoteContaint = dialog.findViewById(R.id.digedtNoteContaint);
                digbtnSave = dialog.findViewById(R.id.digbtnSave);
                digbtnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = digedtNoteName.getText().toString();
                        String containt = digedtNoteContaint.getText().toString();
                        Log.d("name_value_2",name);
                        Log.d("name_value_2",containt);
                        if(name.isEmpty() || containt.isEmpty())
                        {
                            Toast.makeText(NotesActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            DocumentReference documentReference = fstore.collection("Notes").document();
                            Map<String,Object> note = new HashMap<>();
                            note.put("name",name);
                            note.put("containt",containt);
                            note.put("userId",userId);
                            note.put("id",documentReference);
                            documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(), "Note Added Successfully", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                    arrNotes.clear();
                                    retrievedata();
                                }
                            });
                        }
                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });


        retrievedata();

        adapter = new AdapterNotes(arrNotes);
        recNotes.setAdapter(adapter);





    }



    private void retrievedata() {
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
                adapter.notifyDataSetChanged();
            }
        });
    }
}