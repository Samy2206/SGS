package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText txtEmail,txtPassword,txtName,txtPhonenNo;
    private AppCompatButton btnRegister;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtEmail=findViewById(R.id.txtEmail);
        txtPassword=findViewById(R.id.txtPassword);
        btnRegister=findViewById(R.id.btnRegister);
        txtName = findViewById(R.id.txtName);
        txtPhonenNo = findViewById(R.id.txtPhoneNo);
        auth=FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                String phoneno = txtPhonenNo.getText().toString();
                String name = txtName.getText().toString();
                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(name)||TextUtils.isEmpty(phoneno))
                {
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                } else if (password.length()<6)
                {
                    Toast.makeText(RegisterActivity.this, "Password too small", Toast.LENGTH_SHORT).show();
                }else
                {
                    registeruser(email,password,name,phoneno);
                }
            }
        });
    }

    private void registeruser(String email, String password,String name,String phoneno) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    pushUserInfo(email,name,phoneno);
                    Bundle bundle = new Bundle();
                    bundle.putString("email",email);
                    bundle.putString("name",name);
                    bundle.putString("phoneno",phoneno);
                    Intent iNext = new Intent(RegisterActivity.this, studentInfo.class);
                    iNext.putExtras(bundle);
                    startActivity(iNext);
                    finish();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Registration Failded", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pushUserInfo(String email, String name, String phoneno) {

        String userID = auth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(userID);
        Map<String,Object> user = new HashMap<>();
        user.put("name",name);
        user.put("phoneno",phoneno);
        user.put("email",email);
        documentReference.set(user);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(RegisterActivity.this).setTitle("Exit")
                .setIcon(R.drawable.exit_alert_icon).setMessage("Do you want to exit").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegisterActivity.super.onBackPressed();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        exitDialog.show();
    }
}