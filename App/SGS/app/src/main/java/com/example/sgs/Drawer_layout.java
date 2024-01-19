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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private static final String ROOT_FRAGMENT_TAG = "RootFragmentTag";
    TextView txtName,txtStudentClass;
    DrawerLayout drawerLayout ;
    Toolbar toolbar;
    NavigationView navigationView;
    BottomNavigationView bnNavgationView;
    Button btnLogOut ;
    FirebaseFirestore fstore ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bnNavgationView = findViewById(R.id.bnNavigationView);
        toolbar = findViewById(R.id.toolbar);
        btnLogOut = findViewById(R.id.btnLogOut);
        fstore = FirebaseFirestore.getInstance();
//        txtName = findViewById(R.id.txtName1);
//        txtStudentClass = findViewById(R.id.txtStudentClass);

        View headerView = navigationView.getHeaderView(0);
        txtName = headerView.findViewById(R.id.txtName1);
        txtStudentClass = headerView.findViewById(R.id.txtStudentClass);


        setProfile();

        drawerToogle();

       loadFragment(new HomeFragment(),true);

        bnNavgationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.home)
                {
                   loadFragment(new HomeFragment(),true);
                }else {
                    startActivity(new Intent(Drawer_layout.this,MainActivity.class));
                }
                return true;
            }
        });
        bnNavgationView.setSelectedItemId(R.id.home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id==R.id.dashboard)
                {
                    startActivity(new Intent(Drawer_layout.this, MainActivity.class));
                }
                else if (id==R.id.subjectTracker)
                {

                }
                else if (id==R.id.taskScheduler)
                {

                }
                else if (id==R.id.logOut)
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Drawer_layout.this, StartActivity.class));
                    finish();
                }
                else
                {

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void loadFragment(Fragment fragment,boolean b) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(b) {
            ft.add(R.id.containerX, fragment);
            fm.popBackStack(ROOT_FRAGMENT_TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(ROOT_FRAGMENT_TAG);
        }else
        {
            ft.replace(R.id.containerX,fragment);
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void setProfile() {
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

    private void drawerToogle() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder exitDailog = new AlertDialog.Builder(Drawer_layout.this).setTitle("Exit")
                    .setIcon(R.drawable.exit_alert_icon).setMessage("Do you want to exit").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Drawer_layout.super.onBackPressed();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
    }

}