package com.example.sgs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Drawer_layout extends AppCompatActivity {

    NavigationView navigationView;
    BottomNavigationView bottomNavigation;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView txtName,txtStudentClass;

    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        fstore = FirebaseFirestore.getInstance();


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        toggle.syncState();

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
                }else {
                    loadFragment(new ProfileFragment(),false);
                }
                return true;
            }
        });


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


        String userID = FirebaseAuth.getInstance().getUid();
        DocumentReference ref = fstore.collection("users").document(userID);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.exists())
                {
                    String name = value.getString("name");
                    txtName.setText(name);
                    DocumentReference ref1 = fstore.collection("student info").document(name);
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

}