package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Drawer_layout extends AppCompatActivity {

    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView txtName,txtStudentClass;
    private ImageView imgProfile;
    private FirebaseFirestore fstore;
    private StorageReference sRef;
    private String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        fstore = FirebaseFirestore.getInstance();

        toolbar.setTitle("Welcome");
        toolbar.setTitleMarginStart(40);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        toggle.syncState();


        startNoticationService();       //Starts notification service
        startNoticationService();

        loadFragment(new HomeFragment(),true);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.dashboard)
                {
                    startActivity(new Intent(Drawer_layout.this, DashboardActivity.class));
                } else if (id==R.id.subjectTracker)
                {
                    startActivity(new Intent(Drawer_layout.this, SubjectTrackerActivity.class));
                } else if(id==R.id.taskScheduler)
                {
                    startActivity(new Intent(Drawer_layout.this, TaskSchedulerActivity.class));
                } else if (id==R.id.feedback) {
                    startActivity(new Intent(Drawer_layout.this, FeedbackActivity.class));
                }else if(id==R.id.notes)
                {
                    startActivity(new Intent(Drawer_layout.this, NotesActivity.class));
                }else if(id==R.id.imgNote)
                {
                    startActivity(new Intent(Drawer_layout.this, Photo_Notes_Activity.class));
                }else if(id==R.id.notification)
                {
                    Intent serviceIntent = new Intent(Drawer_layout.this, NotificationService.class);
                    stopService(serviceIntent);
                }else
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Drawer_layout.this, StartActivity.class));
                    finish();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        setProfile();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_home)
                {
                 loadFragment(new HomeFragment(),false);
                    toolbar.setTitle("Welcome");
                    toolbar.setTitleMarginStart(40);
                }else {
                    loadFragment(new ProfileFragment(),false);
                    toolbar.setTitle("Profile");
                    toolbar.setTitleMarginStart(40);
                }
                return true;
            }
        });


    }

    private void startNoticationService() {
            Intent serviceIntent = new Intent(Drawer_layout.this, NotificationService.class);
            startService(serviceIntent);
    }

    private void loadFragment(Fragment fragment,boolean b) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(b)
        ft.add(R.id.containerX,fragment);
        else
        ft.replace(R.id.containerX,fragment);
        ft.commit();
    }

    private void setProfile() {

        View view = navigationView.getHeaderView(0);
        txtName = view.findViewById(R.id.txtName);
        txtStudentClass = view.findViewById(R.id.txtStudentClass);
        imgProfile = view.findViewById(R.id.imgProfile);

        //set profile picture

        sRef = FirebaseStorage.getInstance().getReference("images/"+userId);
        sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Uri imgUri = task.getResult();
                    Glide.with(Drawer_layout.this)
                            .load(imgUri)
                            .into(imgProfile);
                }
            }
        });

        //set profile name
        String userID = FirebaseAuth.getInstance().getUid();
        DocumentReference ref = fstore.collection("users").document(userID);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.exists())
                {
                    String name = value.getString("name");
                    txtName.setText(name);
                    DocumentReference ref1 = fstore.collection("student info").document(userID);
                    ref1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value != null && value.exists())
                            {
                                String studentClass = value.getString("class");
                                txtStudentClass.setText(studentClass);

                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            AlertDialog.Builder exit = new AlertDialog.Builder(Drawer_layout.this)
                    .setTitle("Exit")
                    .setMessage("Do you want to exit app")
                    .setIcon(R.drawable.exit_alert_icon)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Drawer_layout.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            exit.show();
        }
    }
}