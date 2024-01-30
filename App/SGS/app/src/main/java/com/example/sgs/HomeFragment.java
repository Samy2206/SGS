package com.example.sgs;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {
    private LottieAnimationView animStudy;
    private FloatingActionButton btnAddGoal;
    private ArrayList<Model_Goal> arrGoal = new ArrayList<>();
   private RecyclerView recGoalHome;
   private EditText edtGoal,edtDescription;
    private TextView edtDate;
    private Button btnAdd;
    private FirebaseFirestore fstore;
    private AdapterGoal adapter;
    private final String userID = FirebaseAuth.getInstance().getUid();
    private int noGoals;
    SharedPreferences pref;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recGoalHome = view.findViewById(R.id.recGoalHome);
        btnAddGoal = view.findViewById(R.id.btnAddGoal);
        pref =getContext().getSharedPreferences("numberValues",MODE_PRIVATE);


        /////////////////////////////////////////////////////////////////////////////////
        studyAnim(view);
        recGoalHome.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openAddDialog();

            }
        });
        retrievedata();


        adapter = new AdapterGoal(arrGoal,getContext());
        adapter.resetDueTomorrow();
        recGoalHome.setAdapter(adapter);




        ////////////////////////////////////////////////////////////////////////////////
        return view;
    }



    private void openAddDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_goal);

        edtGoal= dialog.findViewById(R.id.edtGoal);
        edtDescription = dialog.findViewById(R.id.edtDescription);
        edtDate = dialog.findViewById(R.id.edtDate);
        btnAdd = dialog.findViewById(R.id.btnAdd);

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdate();
            }

        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            String goal,description,date;
            @Override
            public void onClick(View v) {
              if(edtGoal.getText().toString().isEmpty() || edtDescription.getText().toString().isEmpty() || edtDate.getText().toString().isEmpty())
              {
                  Toast.makeText(getContext(), "Please Enter The Information", Toast.LENGTH_SHORT).show();
              }
              else {
                  goal = edtGoal.getText().toString();
                  description = edtDescription.getText().toString();
                  date = edtDate.getText().toString();
              }
              dialog.dismiss();

              //upload new goal to database
                fstore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = fstore.collection("Goals").document();
                Map<String,Object> goalMap = new HashMap<>();
                goalMap.put("goal",goal);
                goalMap.put("description",description);
                goalMap.put("date",date);
                goalMap.put("id",documentReference);
                goalMap.put("userId",userID);

                documentReference.set(goalMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Goal Added Successfully", Toast.LENGTH_SHORT).show();
                        adapter.resetDueTomorrow();
                        adapter.notifyDataSetChanged();
                        arrGoal.clear();
                        retrievedata();
                    }
                });

            }
        });
        dialog.show();

    }

    private void setdate() {
        DatePickerDialog dateDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }
    private void retrievedata() {
        fstore = FirebaseFirestore.getInstance();
        fstore.collection("Goals").whereEqualTo("userId",userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments(); //all docs from the collection are retrived
                for(DocumentSnapshot d:list)
                {
                    Model_Goal model = d.toObject(Model_Goal.class);
                    model.setDocRef(d.getReference());
                    arrGoal.add(model);
                    Log.d("NoGaols: ", String.valueOf(noGoals));
                    noGoals++;
                }
                noGoals = arrGoal.size(); // Update noGoals with the size of the list
                Log.d("NoGoals: ", String.valueOf(noGoals));
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("noGoals", noGoals);
                editor.apply(); // Use apply() to persist the changes asynchronously
                adapter.resetDueTomorrow();
                adapter.notifyDataSetChanged();
                adapter.deleteDueGoals();
            }
        });


    }


    private void studyAnim(View view) {
        animStudy = view.findViewById(R.id.animStudy);
        animStudy.setAnimation(R.raw.study_animation);
        animStudy.playAnimation();
    }
}