package com.example.sgs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity {
    private EditText edtName,edtPhoneNo,edtEmail,edtIssue;
    private Button btnSubmit;
    private String issueDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        edtName = findViewById(R.id.edtName);
        edtPhoneNo = findViewById(R.id.edtPhoneNo);
        edtEmail = findViewById(R.id.edtEmail);
        edtIssue = findViewById(R.id.edtIssue);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });


    }

    private void sendMail() {
        String name = edtName.getText().toString();
        String phoneno = edtPhoneNo.getText().toString();
        String email = edtEmail.getText().toString();
        String issue = edtIssue.getText().toString();

        if(name.isEmpty() || email.isEmpty() || issue.isEmpty())
        {
            Toast.makeText(this, "Please fill all the compulsory fields", Toast.LENGTH_SHORT).show();
        }else {
            issueDetails = "Name: " + name + "\nPhone No: " + phoneno + "\nEmail: " + email + "\nIssue: "+issue;
            Intent iReport = new Intent(Intent.ACTION_SEND);
            iReport.setType("message/rfc822");
            iReport.putExtra(Intent.EXTRA_EMAIL, new String[]{"sanketrajuadhav@gmail.com", "bhagatsuvarna533@gmail.com", "tejaskasar90@gmail.com"});
            iReport.putExtra(Intent.EXTRA_SUBJECT, "Issue With the app from" + edtEmail.getText().toString());
            iReport.putExtra(Intent.EXTRA_TEXT, issueDetails);
            startActivity(Intent.createChooser(iReport, "Send Email From"));
        }
    }

//    private void check() {
//        String name = edtName.getText().toString();
//        String phoneno = edtPhoneNo.getText().toString();
//        String email = edtEmail.getText().toString();
//        String issue = edtIssue.getText().toString();
//
//        if(name.isEmpty() || email.isEmpty() || issue.isEmpty())
//        {
//            Toast.makeText(this, "Please fill all the compulsory fields", Toast.LENGTH_SHORT).show();
//        }
//        issueDetails = "Name: "+name+" Phone No"+phoneno+" Email: "+email+" Issue"+issue;
//    }
}