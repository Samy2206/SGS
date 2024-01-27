package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskSchedulerActivity extends AppCompatActivity {

    private ArrayList<String> arrEvent = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private final String userId = FirebaseAuth.getInstance().getUid();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Calendar").child(userId);
    private Calendar calendar = Calendar.getInstance();
    private CalendarView calendarView;
    private EditText edtEvent;
    private FloatingActionButton btnAdd;
    private ListView listEvent;
    private String selectedDate;
    private AppCompatButton btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_scheduler);

        calendarView = findViewById(R.id.calendarView);
        edtEvent = findViewById(R.id.edtEvent);
        btnAdd = findViewById(R.id.btnAdd);
        btnClear = findViewById(R.id.btnClear);
        listEvent = findViewById(R.id.listEvent);

        loadData();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth+"-"+month+"-"+year;
                dateChange();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadEvent();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDateData();
            }
        });

    }


    private void dateChange() {
        arrEvent.clear();
        adapter.notifyDataSetChanged();
        databaseReference.child(selectedDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrEvent.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    arrEvent.add(dataSnapshot.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskSchedulerActivity.this, "Unable to load Events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadEvent() {

        arrEvent.add(edtEvent.getText().toString());
        adapter.notifyDataSetChanged();
        if(selectedDate == null)
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
        else {
            databaseReference.child(selectedDate).setValue(arrEvent);
            edtEvent.setText("");
        }
    }

    private void loadData() {
        adapter = new ArrayAdapter<>(this,R.layout.list_event,arrEvent);
        listEvent.setAdapter(adapter);
        adapter.notifyDataSetChanged();
            selectedDate = calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR);
            Log.d("Date_Value",selectedDate);
            dateChange();
    }

    private void clearDateData() {
        databaseReference.child(selectedDate).removeValue();

    }


}