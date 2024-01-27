package com.example.sgs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private int rating;
    private ImageView emoji1,emoji2,emoji3,emoji4;
    private EditText edtFeedback;
    private Button btnFeedback,btnReport;
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        emoji1 = findViewById(R.id.emoji_1);
        emoji2 = findViewById(R.id.emoji_2);
        emoji3 = findViewById(R.id.emoji_3);
        emoji4 = findViewById(R.id.emoji_4);
        btnFeedback = findViewById(R.id.btnFeedback);
        btnReport = findViewById(R.id.btnReport);
        edtFeedback = findViewById(R.id.edtFeedback);

        emoji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=4;
                emoji1.setBackgroundColor(Color.WHITE);
                emoji2.setClickable(false);
                emoji3.setClickable(false);
                emoji4.setClickable(false);
            }
        });
        emoji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=4;
                emoji2.setBackgroundColor(Color.WHITE);
                emoji1.setClickable(false);
                emoji3.setClickable(false);
                emoji4.setClickable(false);
            }
        });
        emoji3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=4;
                emoji3.setBackgroundColor(Color.WHITE);
                emoji1.setClickable(false);
                emoji2.setClickable(false);
                emoji4.setClickable(false);
            }
        });
        emoji4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=4;
                emoji4.setBackgroundColor(Color.WHITE);
                emoji1.setClickable(false);
                emoji2.setClickable(false);
                emoji3.setClickable(false);
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFeedback();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
            }
        });


    }

    private void uploadFeedback() {
        String stringFeedback = edtFeedback.getText().toString();
        if (stringFeedback ==null || stringFeedback.isEmpty())
        {
            stringFeedback="";
        }
        else {
            DocumentReference doc = fstore.collection("Feedback").document(userId);
            Map<String, Object> feedback = new HashMap<>();
            feedback.put("rating", rating);
            feedback.put("feedback", stringFeedback);
            doc.set(feedback);
            Toast.makeText(this, "Feedback Submited", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}