package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class studentInfo extends AppCompatActivity {

    private EditText txtAge,txtClgName,txtBranch,txtStudentClass;
    private Button btnNext;

    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        txtAge = findViewById(R.id.txtAge);
        txtClgName = findViewById(R.id.txtClgName);
        txtBranch = findViewById(R.id.txtBranch);
        txtStudentClass = findViewById(R.id.txtStudentClass);
        btnNext = findViewById(R.id.btnNext);
        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = txtAge.getText().toString();
                String clgname = txtClgName.getText().toString();
                String branch = txtBranch.getText().toString();
                String studentClass = txtStudentClass.getText().toString();
                pushStudentInfo(age,clgname,branch,studentClass);
                startActivity(new Intent(getApplicationContext(),Drawer_layout.class));
                finish();
            }
        });




    }

    private void pushStudentInfo(String age, String clgname, String branch, String studentClass) {
        Intent iBack = getIntent();
        Bundle bundle = iBack.getExtras();

        String name = bundle.getString("name");
        String email = bundle.getString("email");
        String phoneno = bundle.getString("phoneno");

        DocumentReference db = fstore.collection("student info").document(name);
        Map<String,Object> stdData = new HashMap<>();
        stdData.put("name",name);
        stdData.put("email",email);
        stdData.put("phoneno",phoneno);
        stdData.put("age",age);
        stdData.put("clgname",clgname);
        stdData.put("branch",branch);
        stdData.put("class",studentClass);
        stdData.put("userId",fauth.getCurrentUser().getUid());
        db.set(stdData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(studentInfo.this, "Welcome", Toast.LENGTH_SHORT).show();
            }
        });

    }
}